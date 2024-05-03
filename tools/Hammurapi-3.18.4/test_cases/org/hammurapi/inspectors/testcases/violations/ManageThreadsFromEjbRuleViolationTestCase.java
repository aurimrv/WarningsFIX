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
package org.hammurapi.inspectors.testcases.violations;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/**
 * ManageThreadsFromEjbRule
 * @author  Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class ManageThreadsFromEjbRuleViolationTestCase implements SessionBean {
		
	private static final org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getRootLogger();

	private SessionContext context;

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbCreate() {
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbPostCreate() {
	}
  
	/** Java doc automaticaly generated by Hammurapi */
	public void setSessionContext(final SessionContext pContext){
		this.context = pContext;
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbActivate() {
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbPassivate() {
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbRemove() {
	}
	
	/** Java doc automaticaly generated by Hammurapi */
	public void businessMethod1() {
		Thread myThread = new Thread(){
			/** Java doc automaticaly generated by Hammurapi */
			public void run() {
				while (true) {
					businessMethod2();
				}
			}
		};

		// --- VIOLATION ---
		myThread.start();
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void businessMethod2() {
	}
}

