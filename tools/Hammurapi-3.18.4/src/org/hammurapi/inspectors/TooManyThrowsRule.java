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
import com.pavelvlasov.jsel.Operation;

/**
 * ER-112
 * Too many exceptions listed in throws clause
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class TooManyThrowsRule extends InspectorBase  implements Parameterizable  {
	/**
	 * Stores the setting form the configuration if static public final
	 * attributes are allowed in the code.
	 */
	private Integer maxThrows;
	
	/**
	 * Reviews the operations, if they have in their throws clause more 
	 * item than the preconfigured allowed maximum.
	 * 
	 * @param element the operation to be reviewed.
	 */
	public void visit(Operation element) {
		if (element.getThrows().size()>maxThrows.intValue()) {
			context.reportViolation(element);
		}
	}
	
	/**
	 * Configures the rule. Reads in the values of the parameters max-parameters and
	 * class-max-complexity.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param parameter the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("max-throws".equals(name)) {
			maxThrows = (Integer) parameter;
			return true;
		}
		
		throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName()); 
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
//		if ()
		StringBuffer ret=new StringBuffer("Allowed maximum item in the throws clause:\n");
//!!! Check for null
		ret.append("max-throws: " + maxThrows + "\n");
		return ret.toString();
	}
}
