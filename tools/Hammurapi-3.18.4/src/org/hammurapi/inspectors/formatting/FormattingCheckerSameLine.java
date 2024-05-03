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
 * braces in the same line
 * 
 * @author Jochen Skulj
 * @version $Revision: 1.2 $
 */
class FormattingCheckerSameLine extends FormattingCheckerBase {

  /**
   * constructor
   */
  public FormattingCheckerSameLine() {
  }

  // Dispatch methods
  public boolean check_catch(Token aToken) {
    return checkPrevRCurly(aToken) || checkNextLParen(aToken);
  }

  public boolean check_LITERAL_do(Token aToken) {
    return checkNextLCurly(aToken);
  }

  public boolean check_LITERAL_else(Token aToken) {
    boolean violation = checkPrevRCurly(aToken);
    if (violation != true) {
      // if the else-Token is followed by an if-token both tokens
      // must be in the same line
      Token next = nextNonWhitespace(aToken);
      if (next.getType() == JavaTokenTypes.LITERAL_if) {
        if (next.getLine() != aToken.getLine()) {
          violation = true;
        }
      } else {
        violation = checkNextLCurly(aToken);
      }
    }
    return violation;
  }

  public boolean check_LITERAL_finally(Token aToken) {
    return (checkPrevRCurly(aToken) || checkNextLCurly(aToken));
  }

  public boolean check_LITERAL_for(Token aToken) {
    return checkNextLParen(aToken);
  }

  public boolean check_LITERAL_if(Token aToken) {
    return checkNextLParen(aToken);
  }

  public boolean check_LITERAL_while(Token aToken) {
    return checkWhile(aToken);
  }

  public boolean check_LITERAL_switch(Token aToken) {
    return checkNextLParen(aToken);
  }

  public boolean check_LITERAL_synchronized(Token aToken) {
    boolean violation = false;
    Token next = nextNonWhitespace(aToken);
    if (next.getType() == JavaTokenTypes.LPAREN) {
      violation = checkNextLParen(aToken);
    }
    return violation;
  }

  public boolean check_LITERAL_try(Token aToken) {
    return checkNextLCurly(aToken);
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
    // This expression is followed by a LCURLY. The last token of the
    // expression and the LCURLY must be in the same line.
    Token expressionFirst = nextNonWhitespace(aToken);
    boolean violation = false;
    if (expressionFirst.getLine() != aToken.getLine()) {
      violation = true;
    }
    Token expressionLast = skipExpressionTokens(expressionFirst);
    Token lcurly = nextNonWhitespace(expressionLast);
    if (lcurly.getType() == JavaTokenTypes.LCURLY) {
      if (expressionLast.getLine() != lcurly.getLine()) {
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
    // The token must be followed by a LCURLY in the same line
    boolean violation = false;
    Token lcurly = nextNonWhitespace(aToken);
    if (lcurly.getType() == JavaTokenTypes.LCURLY) {
      if (lcurly.getLine() != aToken.getLine()) {
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
    // the token must have e previous RCURLY in the same line
    Token rcurly = previousNonWhitespace(aToken);
    boolean violation = false;
    if (rcurly.getType() == JavaTokenTypes.RCURLY) {
      if (rcurly.getLine() != aToken.getLine()) {
        violation = true;
      }
    } else {
      violation = true;
    }
    return violation;
  }

  /**
   * checks formatting for while-Tokens
   * 
   * @param aToken
   *          current Token
   * @return <code>true<code> if the token violates a rule; otherwise <code>false</code>
   */
  protected boolean checkWhile(Token aToken) {
    // while can be used in two ways: as the beginning of a while-loop or
    // the end of a do-loop. This is checked by inspecting previous tokens
    boolean whileLoop = true;
    Token previous = previousNonWhitespace(aToken);
    if (previous.getType() == JavaTokenTypes.RCURLY) {
      // get the matching LCURLY
      int open = 1;
      while (open > 0) {
        previous = previousNonWhitespace(previous);
        if (previous.getType() == JavaTokenTypes.LCURLY) {
          --open;
        }
        if (previous.getType() == JavaTokenTypes.RCURLY) {
          ++open;
        }
      }
      // get the token previous to the matching LCURLY
      previous = previousNonWhitespace(previous);
      if (previous.getType() == JavaTokenTypes.LITERAL_do) {
        whileLoop = false;
      }
    }
    boolean violation = true;
    if (whileLoop) {
      // in a while-Loop the while is a token followed by a
      // paranthesis
      violation = checkNextLParen(aToken);
    } else {
      // in a do-Loop the while is a token that is a
      // successor of a closing curly brace and that is
      // followed by a expression in parenthesis and semicolon
      violation = checkPrevRCurly(aToken);
      if (!violation) {
        Token expressionFirst = nextNonWhitespace(aToken);
        if (expressionFirst.getLine() != aToken.getLine()) {
          violation = true;
        }
        Token expressionLast = skipExpressionTokens(expressionFirst);
        Token semicolon = nextNonWhitespace(expressionLast);
        if (semicolon.getType() != JavaTokenTypes.SEMI
            || semicolon.getLine() != aToken.getLine()) {
          violation = true;
        }
      }

    }
    return violation;
  }

  /**
   * skips all tokens of a expression in parenthesis
   * 
   * @param currentToken
   *          first opening parenthesis of expression
   * @return last closing parenthesis of expression
   */
  protected Token skipExpressionTokens(Token currentToken) {
    int open = 0;
    Token token = currentToken;
    do {
      if (token.getType() == JavaTokenTypes.LPAREN) {
        ++open;
      }
      if (token.getType() == JavaTokenTypes.RPAREN) {
        --open;
      }

      if (open > 0) {
        token = nextNonWhitespace(token);
      }
    } while (token != null && open > 0);
    return token;
  }

  /**
   * skip all next tokens that are whitespaces
   * 
   * @param aToken
   *          current token
   * @return next non-whitespace token
   */
  protected Token nextNonWhitespace(Token aToken) {
    Token current = (Token) aToken.getNextToken();
    while (current.getType() == JavaTokenTypes.WS
        || current.getType() == JavaTokenTypes.NEW_LINE) {
      current = (Token) current.getNextToken();
    }
    return current;
  }

  /**
   * skip all previous tokens that are whitespaces
   * 
   * @param aToken
   *          current token
   * @return next previous non-whitespace token
   */
  protected Token previousNonWhitespace(Token aToken) {
    Token current = (Token) aToken.getPrevToken();
    while (current.getType() == JavaTokenTypes.WS
        || current.getType() == JavaTokenTypes.NEW_LINE) {
      current = (Token) current.getPrevToken();
    }
    return current;
  }
}
