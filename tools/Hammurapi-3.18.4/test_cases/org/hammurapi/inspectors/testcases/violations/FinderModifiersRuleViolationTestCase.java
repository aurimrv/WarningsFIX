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

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

/**
 * FinderModifiersRule
 * @author  Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class FinderModifiersRuleViolationTestCase implements EntityBean {

	private static final org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getRootLogger();

	private transient EntityContext objcContext;

	private Integer mProdSubmissionID = null;

	/** Java doc automaticaly generated by Hammurapi */
	public void setEntityContext(final EntityContext objaContext) {
		this.objcContext = objaContext;
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void unsetEntityContext() {
		objcContext = null;
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbActivate() {
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbPassivate() {
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbLoad() {
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbStore() {
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbRemove() {
	}

	/** Java doc automaticaly generated by Hammurapi */
	public Integer ejbCreate() throws CreateException, RemoteException {

		this.mProdSubmissionID = new Integer(0);
		return this.mProdSubmissionID;
	}

	/** Java doc automaticaly generated by Hammurapi */
	public void ejbPostCreate() {
	}

	private static final String ERROR_TXT = 
		"Not yet implemented! - findByPrimaryKey";
		
	// --- VIOLATION ---
	/** Java doc automaticaly generated by Hammurapi */
	public final Integer ejbFindByPrimaryKey(final Integer key)
		throws javax.ejb.FinderException {
			
		throw new javax.ejb.FinderException(ERROR_TXT);
	}
}

