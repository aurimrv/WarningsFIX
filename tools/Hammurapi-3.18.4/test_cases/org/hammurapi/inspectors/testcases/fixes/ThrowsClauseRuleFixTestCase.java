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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.hammurapi.inspectors.testcases.HammurapiTestCasesException;

/**
 * ThrowsClauseRule
 * @author  Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class ThrowsClauseRuleFixTestCase {

	private static org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getRootLogger();

	private static final String FILE_ERROR_TXT = "File error";

	private Vector billItems = new Vector();
	private Date today = new Date();
	private int firstByte = 0;

	/** Java doc automaticaly generated by Hammurapi */
	public void setFirstByte(final String fName)

		// --- FIX ---
		throws HammurapiTestCasesException {

		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fName);
			firstByte = fin.read();
			fin.close();
		} catch (IOException e) {
			logger.fatal(FILE_ERROR_TXT, e);
			throw new HammurapiTestCasesException(e);
		}
	}
}

