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
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Operation;
import com.pavelvlasov.jsel.TypeIdentifier;

/**
 * ER-114
 * Declare only predefined set of exceptions in throws clause (application layer specific)
 * @author  Janos Czako
 * @version $Revision: 1.3 $
 */
public class ThrowsClauseRule extends InspectorBase  implements Parameterizable  {
	
	/**
	 * Reviews the operations, if they have items in their throws clause which
	 * is nor predefined.
	 * 
	 * @param element the operation to be reviewed.
	 */
	public void visit(Operation element) {
		java.util.Iterator throwsList = element.getThrows().iterator();
		
		while (throwsList.hasNext()) {
			TypeIdentifier item = (TypeIdentifier) throwsList.next();
			try {
				if (!allowedThrows.contains(item.getName())) {
					context.reportViolation(element, new Object[] {item});
				}
			} catch (JselException e) {
				context.warn(item, e);
			}
	}
	}
	
	/**
	 * Stores the setting form the configuration for the allowed
	 * exceptions in the trows clause of the operations.
	 */
	private java.util.Set allowedThrows = new java.util.HashSet();
	
	/**
	 * Configures the rule. Reads in the values of the parameter allowed-throws.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("allowed-throw".equals(name)) {
			allowedThrows.add(parameter.toString());	
			return true;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName());
		} 
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed exceptions in the thorws clauses:\n");
		java.util.Iterator iter = allowedThrows.iterator();
		while (iter.hasNext()) { 
			ret.append("allowed-throw: " + (String) iter.next() + "\t");
		}
		ret.append("\n");
		return ret.toString();
	}
}
