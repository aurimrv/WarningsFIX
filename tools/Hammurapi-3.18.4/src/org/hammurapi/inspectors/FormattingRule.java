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

import java.io.File;

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;
import org.hammurapi.inspectors.formatting.FormattingChecker;
import org.hammurapi.inspectors.formatting.FormattingCheckerFactory;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Package;
import com.pavelvlasov.jsel.impl.Token;
import com.pavelvlasov.review.SimpleSourceMarker;

/**
* Hammurapi inspector for checking code formatting
* 
* @author Jochen Skulj
* @version $Revision: 1.3 $
*/
public class FormattingRule extends InspectorBase implements Parameterizable {

 /**
  * Parameter name for configuring the coding style
  */
 public final static String PARAMETER_STYLE = "coding-style";

 /**
  * Formatting checker instance
  */
 private FormattingChecker checker = null;

 /**
  * inspects the rule on method level
  * 
  * @param aMethod
  *          method to inspect
  * @throws HammurapiException
  */
 public void visit(Method aMethod) throws HammurapiException {
   SimpleSourceMarker source = new SimpleSourceMarker(aMethod);
   CompilationUnit cu = aMethod.getCompilationUnit();
   Package pkg = cu.getPackage();
   if (pkg.getName().length() == 0) {
     source.setSourceURL(cu.getName());
   } else {
     source.setSourceURL(pkg.getName().replace('.', File.separatorChar)
         + File.separator + cu.getName());
   }
   Token token = (Token) aMethod.getAst().getFirstToken();
   while (token != null) {
     if (checker.check(token)) {
       source.setLine(token.getLine());
       source.setColumn(token.getColumn());
       context.reportViolation(source);
     }
     if (token == (Token) aMethod.getAst().getLastToken()) {
       token = null;
     } else {
       token = (Token) token.getNextToken();
     }
   }
 }

 /**
  * sets parameters for the inspector
  * 
  * @param aName
  *          parameter name
  * @param aParameter
  *          parameter value
  */
 public boolean setParameter(String aName, Object aParameter)
     throws ConfigurationException {
   if (aName.equals(PARAMETER_STYLE)) {
     String codingStyle = (String) aParameter;
     checker = FormattingCheckerFactory.create(codingStyle);
     if (checker == null) {
       throw new ConfigurationException("Parameter value '" + codingStyle
           + "' does not specify a supported coding style");
     }
		return true;
   } else {
     throw new ConfigurationException("Parameter '" + aName
         + "' is not supported by " + this.getClass().getName());
   }
 }
}
