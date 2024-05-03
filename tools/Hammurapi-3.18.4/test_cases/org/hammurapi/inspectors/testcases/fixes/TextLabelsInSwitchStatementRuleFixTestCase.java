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
package org.hammurapi.inspectors.testcases.fixes;

/**
 * TextLabelsInSwitchStatementRule
 * @author  Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class TextLabelsInSwitchStatementRuleFixTestCase {
  
	private static org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getRootLogger();

	private static final String TXT_1 = "Option 1";
	private static final String TXT_2 = "Option 2";
	private static final String TXT_7 = "Option 3";

	private static final int INT_1 = 1;
	private static final int INT_2 = 2;
	private static final int INT_7 = 7;

	private int getInt1(final String baseVal) {
		int retVal = 0;
		
		// --- FIX ---
		if (TXT_1.compareTo(baseVal)==0) {
			retVal = INT_2;
		} else if (TXT_1.compareTo(baseVal)==0) {
			retVal = INT_7;
		} else {
			retVal = INT_1;
		}
		// --- END FIX ---

		return retVal;
	}
}

