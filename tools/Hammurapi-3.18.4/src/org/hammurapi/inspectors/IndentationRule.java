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

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Class;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.Interface;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Package;
import com.pavelvlasov.jsel.impl.JavaTokenTypes;
import com.pavelvlasov.jsel.impl.Token;
import com.pavelvlasov.review.SimpleSourceMarker;

/**
 * <p>
 * Hammurapi inspector for checking the indentation of the source code.
 * </p>
 * <p>
 * The parameter <i>standard-indentation-level </i> specifies the default
 * indentation for blocks in curly braces. There must be no difference in the
 * indentation of statements without sorrounding curly braces. Examples (with
 * <i>standard-indentation-level </i>= 2):
 * </p>
 * 
 * <pre>
 * if (current.getType() == JavaTokenTypes.LCURLY) {
 *   requiredColumn += indentLevel;
 * }
 * 
 * while (parent != null &amp;&amp; parent instanceof Class) {
 *   lassLevel++;
 *   parent = parent.getParent();
 * }
 * </pre>
 * 
 * <p>
 * If large expressions (e.g. in if-Statements) are longer than a single line it
 * is recommended to use a different indentation. This is useful for
 * distinguishing these expressions from the following Java blocks. In the same
 * way the indentation of long assignment values, parameter, lists, long chains
 * of method calls and throws-statement should be handled. This different
 * indentation is specified by the parameter <i>expression-indentation-level
 * </i>. Examples (with <i>expression-indentation-level </i>= 4):
 * </p>
 * 
 * <pre>
 * if (current.getType() == JavaTokenTypes.LPAREN &amp;&amp; parenthesisLevel++ == 0) {
 *   requiredColumn += exprIndentLevel;
 * }
 * 
 * check((Token) aClass.getAst().getFirstToken(), getClassLevel(aClass),
 *     (Token) aClass.getAst().getLastToken());
 * </pre>
 * 
 * @author Jochen Skulj
 * @version $Revision: 1.4 $
 */
public class IndentationRule extends InspectorBase implements Parameterizable {

  /**
   * parameter name for the standard indentation
   */
  public static final String PARAMETER_INDENT = "standard-indentation-level";

  /**
   * parameter name for the expression indentation
   */
  public static final String PARAMETER_EXPR_INDENT = "expression-indentation-level";

  /**
   * standard indentation
   */
  private int indentLevel;

  /**
   * expression indentation
   */
  private int exprIndentLevel;

  /**
   * source marker for reporting violating tokens
   */
  private SimpleSourceMarker sourceMarker;

  /**
   * constructor
   */
  public IndentationRule() {
    indentLevel = 2;
    exprIndentLevel = 4;
  }

  /**
   * checks indentation of tokens of a class
   * 
   * @param aClass
   *          class to inspect
   */
  public void visit(Class aClass) {
    initSourceMarker(aClass);
    check((Token) aClass.getAst().getFirstToken(), getClassLevel(aClass),
        (Token) aClass.getAst().getLastToken());
  }

  /**
   * checks indentation of tokens of an interface
   * 
   * @param anInterface
   *          interface to inspect
   */
  public void visit(Interface anInterface) {
    check((Token) anInterface.getAst().getFirstToken(),
        getClassLevel(anInterface), (Token) anInterface.getAst().getLastToken());
  }

  /**
   * initializes the source marker for the current language element
   * 
   * @param anElement
   *          current language element
   */
  protected void initSourceMarker(LanguageElement anElement) {
    sourceMarker = new SimpleSourceMarker(anElement);
    CompilationUnit unit = anElement.getCompilationUnit();
    Package pack = unit.getPackage();
    if (pack.getName().length() == 0) {
      sourceMarker.setSourceURL(unit.getName());
    } else {
      sourceMarker.setSourceURL(pack.getName().replace('.', File.separatorChar)
          + File.separator + unit.getName());
    }
  }

  /**
   * counts the parent classes
   * 
   * @param anElement
   *          current class or interface
   * @return number of parent classes
   */
  protected int getClassLevel(LanguageElement anElement) {
    int classLevel = 0;
    LanguageElement parent = anElement.getParent();
    while (parent != null && parent instanceof Class) {
      classLevel++;
      parent = parent.getParent();
    }
    return classLevel;
  }

  /**
   * checks the indentation of a set of tokens
   * 
   * @param firstToken
   *          first token to inspect
   * @param classLevel
   *          number of surrounding classes (if current language element is an
   *          inner class or interface
   * @param lastToken
   *          last token to inspect
   */
  protected void check(Token firstToken, int classLevel, Token lastToken) {
    Token current = firstToken;
    if (current != null) {
      int line = current.getLine();
      int requiredColumn = 1 + (classLevel * indentLevel);
      int parenthesisLevel = 0;
      boolean assignment = false;
      do {
        // check, if a RCURLY affects the required indentation
        if (current.getType() == JavaTokenTypes.RCURLY) {
          requiredColumn -= indentLevel;
        }
        // check, if the begin of an assignment affects the possible indentation
        if (current.getType() == JavaTokenTypes.ASSIGN && !assignment) {
          assignment = true;
          requiredColumn += exprIndentLevel;
        }
        // check, if the begin of an assignment affects the possible indentation
        if (current.getType() == JavaTokenTypes.SEMI && assignment) {
          assignment = false;
          requiredColumn -= exprIndentLevel;
        }
        // if a new line is reached check indentation
        if (current.getLine() > line) {
          // continue with standard indentation checks if the current line
          // does not begin with a throws literal or a method call outside of an
          // assignment
          if (current.getType() != JavaTokenTypes.LITERAL_throws
              && current.getType() != JavaTokenTypes.DOT) {
            if (current.getColumn() != requiredColumn) {
              sourceMarker.setLine(current.getLine());
              sourceMarker.setColumn(current.getColumn());
              context.reportViolation(sourceMarker);
            }
          } else {
            // special check for throws literals and a method calls
            int specialRequiredColumn = assignment ? requiredColumn
                : requiredColumn + exprIndentLevel;
            if (current.getColumn() != specialRequiredColumn) {
              sourceMarker.setLine(current.getLine());
              sourceMarker.setColumn(current.getColumn());
              context.reportViolation(sourceMarker);
            }
          }
          line = current.getLine();
        }
        // check, if a RCURLY affects the required indentation
        if (current.getType() == JavaTokenTypes.LCURLY) {
          requiredColumn += indentLevel;
        }
        // check, if an expressions in parenthesises affects the required
        // indentation
        if (current.getType() == JavaTokenTypes.LPAREN
            && parenthesisLevel++ == 0) {
          requiredColumn += exprIndentLevel;
        }
        if (current.getType() == JavaTokenTypes.RPAREN
            && --parenthesisLevel == 0) {
          requiredColumn -= exprIndentLevel;
        }
        current = current.getNextNonWhiteSpaceToken();
      } while (current != null && current != lastToken);
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
  public boolean setParameter(String aName, Object aValue)
      throws ConfigurationException {
    boolean parameterSupported = false;
    if (aName.equals(PARAMETER_INDENT)) {
      parameterSupported = true;
      indentLevel = ((Integer) aValue).intValue();
    }
    if (aName.equals(PARAMETER_EXPR_INDENT)) {
      parameterSupported = true;
      exprIndentLevel = ((Integer) aValue).intValue();
    }
    if (!parameterSupported) {
      throw new ConfigurationException("Parameter '" + aName
          + "' is not supported by " + getClass().getName());
    }
	return true;
  }
}
