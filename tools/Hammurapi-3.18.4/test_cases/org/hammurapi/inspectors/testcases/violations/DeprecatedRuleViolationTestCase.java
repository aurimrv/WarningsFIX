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

*/package org.hammurapi.inspectors.testcases.violations;

/**
 * DeprecatedRule
 * @author  Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class DeprecatedRuleViolationTestCase {
	private static org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getRootLogger();

	private static final int YEAR = 2000;
	private static final int MONTH = 2000;
	private static final int DAY = 2000;

	// --- VIOLATION ---		
	private java.util.Date dateOfBeginning = 
		new java.util.Date(YEAR, MONTH, DAY);
	
}

