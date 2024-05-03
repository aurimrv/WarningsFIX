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

/**
 * FormattingCheckerFactory creates depending on the configuration the required
 * FormattingChecker
 * 
 * @author Jochen Skulj
 * @version $Revision: 1.1 $
 */
public class FormattingCheckerFactory {

  /**
   * Parameter value
   */
  public final static String SAME_LINE = "same-line";

  /**
   * Parameter value
   */
  public final static String NEW_LINE = "new-line";

  /**
   * creates the required FormattingChecker
   * 
   * @param codingStyle
   *          coding style to check: <code>same-line</code> or
   *          <code>new-line</code>
   * @return a FormattingChecker or <code>null</code> if the given parameter
   *         doesn't specify a valid coding style
   */
  public static FormattingChecker create(String codingStyle) {
    FormattingChecker instance = null;
    if (codingStyle.equals(SAME_LINE)) {
      instance = new FormattingCheckerSameLine();
    }
    if (codingStyle.equals(NEW_LINE)) {
      instance = new FormattingCheckerNewLine();
    }
    return instance;
  }

}
