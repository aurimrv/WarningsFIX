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
 * LogicalNestingRule
 * @author  Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class LogicalNestingRuleFixTestCase {

	private static org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getRootLogger();
		
	private static final int GAP_1 = 10;
	private static final int GAP_2 = 100;
	private static final int GAP_3 = 1000;
	private static final int GAP_4 = 5000;
	private static final int GAP_5 = 10000;

	private static final int PRICE_1 = 10;
	private static final int PRICE_2 = 100;
	private static final int PRICE_3 = 1000;
	private static final int PRICE_4 = 5000;
	private static final int PRICE_5 = 10000;

	/** Java doc automaticaly generated by Hammurapi */
	public int getValue(final int amount) {
		int retVal = PRICE_1;
		
		// --- FIX ---
		if ( amount>GAP_1 && amount<GAP_5) {
			if (amount>GAP_4) {
				retVal = PRICE_2;
			}
			else if (amount>GAP_3) {
				retVal = PRICE_3;
			}
			else if (amount>GAP_2) {
				retVal = PRICE_4;
			}
			else {
				retVal = PRICE_5;
			}
		}
		// --- END FIX ---
		
		return retVal;
	}
}
