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

import com.pavelvlasov.jsel.impl.Token;
import org.hammurapi.HammurapiException;


/**
 * Common interface for checking the source code formatting of tokens. Different
 * checkers for different coding styles implement this interface
 * 
 * @author Jochen Skulj
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public interface FormattingChecker {

  /**
   * checks, if a token violates a rule
   * 
   * @param aToken
   *          next token to check
   * @return <code>true<code> if the token violates a rule; otherwise <code>false</code>
   */
  boolean check(Token aToken) throws HammurapiException;
}
