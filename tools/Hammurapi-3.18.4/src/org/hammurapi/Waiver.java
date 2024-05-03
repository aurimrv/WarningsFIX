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

package org.hammurapi;

import java.util.Collection;
import java.util.Date;

/**
 * Waiver is a way to suppress a particular violation.
 * @author Pavel Vlasov	
 * @version $Revision: 1.4 $
 */
public interface Waiver {
	
	static final String DATE_FORMAT = "yyyy/MM/dd";
	
	/**
	 * 
	 * @return Inspector name to which this waiver applies
	 */
	String getInspectorName();
	
	/**
	 * @param violation Violation to waive
	 * @param peek If true waiver doesn't change it state.
	 * @return true if Violation shall be waived
	 */
	boolean waive(Violation violation, boolean peek);
	
	Date getExpirationDate();
	
	/**
	 * @return Description why waiver was given.
	 */
	String getReason();
	
	/**
	 * @return true if waiver can waive violations
	 */
	boolean isActive();

	/**
	 * @return Collection of signatures waived by this inspector. Can be null.
	 */
	Collection getSignatures();	
}
