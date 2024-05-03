/*
 * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Hammurapi Group
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * URL: http://www.hammurapi.org
 * e-Mail: support@hammurapi.biz

 */
package org.hammurapi.inspectors;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Initializer;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.PlainAssignment;
import com.pavelvlasov.jsel.statements.CompoundStatement;
import com.pavelvlasov.jsel.statements.Statement;
import com.pavelvlasov.jsel.statements.TryBlock;

/**
 * The rule checks if allocated resource is properly diposed.
 * It follows try/finally pattern.
 * It is configurable via parameters.
 * Example configuration:
 * <pre>
 * <parameter type="java.lang.String" name="resource-def">javax.sql.DataSource:getConnection:java.sql.Connection:close</parameter>
 * </pre>
 * Where:
 *  - "javax.sql.DataSource" is a full class name for the resource allocator
 *  - "getConnection" is a method name of the class that allocates resource
 *  - "java.sql.Connection" is a full class name for the resource DE-allocator
 *  - "close" is a method name of the class that DE-allocates resource
 *
 * The following code will be checked by the paramemeter above:
 * <pre>
 *   javax.sql.DataSource ds;
 *   // connection is allocated here...
 *   java.sql.Connection conn = ds.getConnection();
 *   ...
 *   // connection is "returned" back to data source...
 *   conn.close();
 * </pre> 
 * @author zorror8080
 * @version $Revision: 1.9 $
 */
public class ResourceHandlingRule
  extends InspectorBase
  implements Parameterizable
{
  private static final String TRY_FIN_SHOULD_FOLLOW =
    "try/finally block should follow right after allocated resource \"{0}"
      + "\" to ensure that it is properly deallocated.";
  private static final String FIN_DEALLOC =
    "Allocated resource \"{0}\" should be de-allocated in 'finaly' clause.";
  private static final String FIN_DEALLOC_FIRST =
    "To ensure that resource \"{0}\" is properly deallocated - it should be the first in 'finaly' clause.";

  private Map resources2Check;
  private Set violatedElements;

  public ResourceHandlingRule()
  {
    resources2Check = new HashMap();
    violatedElements = new HashSet();
  }

  private void reportViolation(final LanguageElement element, final String msg)
  {
    if (!violatedElements.contains(element.getLocation()))
    {
      violatedElements.add(element.getLocation());
      context.reportViolation(element, msg);
    }
  }

  private ResInfo checkAllocation(
    final LanguageElement codeFragment,
    final MethodCall mCall)
    throws JselException
  {
    final String methodName = mCall.getMethodName();
    ResInfo res = null;

    if (resources2Check.containsKey(methodName))
    {
      final ResourceDefinition resDef =
        (ResourceDefinition)resources2Check.get(methodName);

      if (resDef
        .obtainClassName
        .equals(mCall.getProvider().getDeclaringType().getName()))
      {
        res = new ResInfo(methodName, codeFragment);
      }
    }
    return res;
  }

  private boolean checkDeAllocation(
    final ResInfo resInfo,
    int index,
    List stms)
    throws JselException
  {
    boolean res = true;
    Statement statement = (Statement)stms.get(index);
    ResourceDefinition resDef =
      (ResourceDefinition)resources2Check.get(resInfo.resourceAllocatedKey);
    // first check if TryFinally follows right after allocation...
    CompoundStatement fSt = null;
    if (statement instanceof TryBlock
      && (fSt = ((TryBlock)statement).getFinallyClause()) != null)
    {
      // = ((TryBlock)statement).getFinallyClause();

      List fSts = fSt.getStatements();
      boolean foundDeAllocation = false;
      boolean firstDeAllocationOperation = false;
      MethodCall releaseMethodCall = null;
      for (int jIndex = 0, jSize = fSts.size(); jIndex < jSize; jIndex++)
      {
        if (fSts.get(jIndex) instanceof CompoundStatement)
        {
          List ffSts = ((CompoundStatement)fSts.get(jIndex)).getStatements();
          for (int kIndex = 0, kSize = ffSts.size(); kIndex < kSize; kIndex++)
          {
            if (ffSts.get(kIndex) instanceof MethodCall
              && resDef.releaseMethodName.equals(
                ((MethodCall)ffSts.get(kIndex)).getMethodName())
              && resDef.releaseClassName.equals(
                ((MethodCall)ffSts.get(kIndex))
                  .getProvider()
                  .getDeclaringType()
                  .getName()))
            {
              foundDeAllocation = true;
              firstDeAllocationOperation = (kIndex == 0);
              releaseMethodCall = (MethodCall)ffSts.get(kIndex);
              break;
            }
          }
        }
        if (!foundDeAllocation)
        {
          reportViolation(
            resInfo.resourceAllocatedBy,
            MessageFormat.format(
              FIN_DEALLOC,
              new Object[] { resInfo.resourceAllocatedKey }));
        }
        else
        {
          if (!firstDeAllocationOperation)
          {
            reportViolation(
              (LanguageElement)releaseMethodCall,
              MessageFormat.format(
                FIN_DEALLOC_FIRST,
                new Object[] {
                  resDef.releaseClassName,
                  resDef.releaseMethodName }));
          }
        }
      }
    }
    else
    {
      if (index < stms.size() - 1)
      {
        checkDeAllocation(resInfo, index + 1, stms);
      }
      reportViolation(
        resInfo.resourceAllocatedBy,
        MessageFormat.format(
          TRY_FIN_SHOULD_FOLLOW,
          new Object[] { resInfo.resourceAllocatedKey }));
    }
    return res;
  }

  public boolean setParameter(String name, Object value)
    throws ConfigurationException
  {
    if ("resource-def".equals(name))
    {
      StringTokenizer tk = new StringTokenizer((String)value, ":");

      String obtainClassName = null;
      String obtainMethodName = null;
      String releaseClassName = null;
      String releaseMethodName = null;

      if (tk.hasMoreTokens())
      {
        obtainClassName = tk.nextToken();
      }
      if (tk.hasMoreTokens())
      {
        obtainMethodName = tk.nextToken();
      }
      if (tk.hasMoreTokens())
      {
        releaseClassName = tk.nextToken();
      }
      if (tk.hasMoreTokens())
      {
        releaseMethodName = tk.nextToken();
      }
      if (tk.hasMoreTokens()
        || obtainClassName == null
        || obtainMethodName == null
        || releaseClassName == null
        || releaseMethodName == null)
      {
        throw new ConfigurationException(
          "Parameter '" + name + "' has invalid value: '" + value + "'");
      }

      resources2Check.put(
        obtainMethodName,
        new ResourceDefinition(
          obtainClassName,
          obtainMethodName,
          releaseClassName,
          releaseMethodName));
    }
    else
    {
      throw new ConfigurationException(
        "Parameter '" + name + "' is not supported by " + getClass().getName());
    }
	return true;
 }

  public void visit(CompoundStatement stmt) throws JselException
  {
    ResourceDefinition resDef;
    ResInfo resInfo = null;
    Statement currentStatement = null;

    List stmts = stmt.getStatements();

    for (int index = 0, size = stmts.size(); index < size; index++)
    {
      currentStatement = (Statement)stmts.get(index);
      if (resInfo != null)
      {
        checkDeAllocation(resInfo, index, stmts);
        resInfo = null;
      }
      if (currentStatement instanceof VariableDefinition)
      {
        Initializer init =
          ((VariableDefinition)currentStatement).getInitializer();

        if (init instanceof MethodCall)
        {
          resInfo =
            checkAllocation(
              (VariableDefinition)currentStatement,
              (MethodCall) ((VariableDefinition)currentStatement)
                .getInitializer());
        }
      }
      else
      {
        if (currentStatement instanceof PlainAssignment)
        {
          PlainAssignment plainAssignment = (PlainAssignment)currentStatement;
		Collection operands = (plainAssignment).getOperands();
          for (int jIndex = 0, jSize = operands.size();
            jIndex < jSize;
            jIndex++)
          {
            if (plainAssignment.getOperand(jIndex) instanceof MethodCall)
            {
              resInfo =
                checkAllocation(
                  (LanguageElement)currentStatement,
                  (MethodCall)plainAssignment.getOperand(jIndex));
              if (resInfo != null)
              {
                break;
              }
            }
          }
        }
        else
        {
          if (currentStatement instanceof MethodCall)
          {
            resInfo =
              checkAllocation(
                (LanguageElement)currentStatement,
                (MethodCall)currentStatement);
          }
        }
      }
    }
    if (resInfo != null)
    {
      checkDeAllocation(resInfo, stmts.size() - 1, stmts);
    }
  }

  private static final class ResourceDefinition
  {
    private String obtainClassName;
    private String obtainMethodName;
    private String releaseClassName;
    private String releaseMethodName;

    private ResourceDefinition(
      String obtainClassName,
      String obtainMethodName,
      String releaseClassName,
      String releaseMethodName)
    {
      this.obtainClassName = obtainClassName;
      this.releaseClassName = releaseClassName;
      this.obtainMethodName = obtainMethodName;
      this.releaseMethodName = releaseMethodName;
    }
  }

  private static final class ResInfo
  {
    private String resourceAllocatedKey;
    private LanguageElement resourceAllocatedBy;

    private ResInfo(
      String resourceAllocatedKey,
      LanguageElement resourceAllocatedBy)
    {
      this.resourceAllocatedKey = resourceAllocatedKey;
      this.resourceAllocatedBy = resourceAllocatedBy;
    }
  }
}
