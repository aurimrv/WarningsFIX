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

*/package org.hammurapi.inspectors.testcases.fixes;

import java.io.FileInputStream;
import java.io.InputStream;

import org.hammurapi.inspectors.testcases.HammurapiTestCasesException;

/**
 * ThrowTooGeneralExceptionTypeRule
 * @author  Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class ThrowTooGeneralExceptionTypeRuleFixTestCase {

	private static org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getRootLogger();

	private static final String FILE_ERROR_TXT = "File error";

	/** Java doc automaticaly generated by Hammurapi */
	public int getFirstByte(final String fName)
	
		// --- FIX --- 
		throws HammurapiTestCasesException {
			
		try {
			InputStream is = new FileInputStream(fName);
			return is.read();

		} catch (java.io.IOException e) {
			logger.fatal(FILE_ERROR_TXT, e);
			throw new HammurapiTestCasesException(e);
		}
	}
}

