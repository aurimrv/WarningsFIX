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
package org.hammurapi.inspectors.formatting;

import com.pavelvlasov.jsel.impl.JavaTokenTypes;
import com.pavelvlasov.jsel.impl.Token;

/**
 * Implementation of FormattingChecker for formatting style with opening curly
 * braces in a new line
 * 
 * @author Jochen Skulj
 * @version $Revision: 1.1 $
 */
class FormattingCheckerNewLine extends FormattingCheckerSameLine {

  /**
   * constructor
   */
  public FormattingCheckerNewLine() {
  }

  /**
   * checks formatting for tokens that are followed by expression in parenthesis
   * 
   * @param aToken
   *          currentToken
   * @return <code>true<code> if the token violates a rule; otherwise <code>false</code>
   */
  protected boolean checkNextLParen(Token aToken) {
    // The token must be followed by a variously complex braced expression.
    // The expression must start in the same line as the token
    // This expression is followed by a LCURLY. The LCURLY must be in the
    // next line after the last token of the expression and in the same column
    // like the current token
    Token expressionFirst = nextNonWhitespace(aToken);
    boolean violation = false;
    if (expressionFirst.getLine() != aToken.getLine()) {
      violation = true;
    }
    Token expressionLast = skipExpressionTokens(expressionFirst);
    Token lcurly = nextNonWhitespace(expressionLast);
    if (lcurly.getType() == JavaTokenTypes.LCURLY) {
      if ((expressionLast.getLine() + 1) != lcurly.getLine()
          || lcurly.getColumn() != aToken.getColumn()) {
        violation = true;
      }
    } else {
      violation = true;
    }
    return violation;
  }

  /**
   * checks formatting for tokens that are follow opening curly brace (like do)
   * 
   * @param aToken
   *          current Token
   * @return <code>true<code> if the token violates a rule; otherwise <code>false</code>
   */
  protected boolean checkNextLCurly(Token aToken) {
    // The token must be followed by a LCURLY in the same column and the next
    // line
    boolean violation = false;
    Token lcurly = nextNonWhitespace(aToken);
    if (lcurly.getType() == JavaTokenTypes.LCURLY) {
      if (lcurly.getLine() != (aToken.getLine() + 1)
          || lcurly.getColumn() != aToken.getColumn()) {
        violation = true;
      }
    } else {
      violation = true;
    }
    return violation;
  }

  /**
   * checks formatting for tokens that are successors of a closing curly brace
   * like else, catch or finally
   * 
   * @param aToken
   *          current Token
   * @return <code>true<code> if the token violates a rule; otherwise <code>false</code>
   */
  protected boolean checkPrevRCurly(Token aToken) {
    // the token must have e previous RCURLY in the same column and the previous
    // line
    Token rcurly = previousNonWhitespace(aToken);
    boolean violation = false;
    if (rcurly.getType() == JavaTokenTypes.RCURLY) {
      if (rcurly.getLine() != (aToken.getLine() - 1)
          || rcurly.getColumn() != aToken.getColumn()) {
        violation = true;
      }
    } else {
      violation = true;
    }
    return violation;
  }

}
