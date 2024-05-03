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
import com.pavelvlasov.jsel.TypeSpecification;
import com.pavelvlasov.jsel.statements.Handler;

/**
 * ER-036
 * Catching too general exception type.
 * @author  Janos Czako
 * @version $Revision: 1.3 $
 */
public class CatchTooGeneralExceptionTypeRule 
	extends InspectorBase  implements Parameterizable  {

	/**
	 * Reviews the exception handlers, if they catch too general exceptions.
	 * 
	 * @param expHandler the handler to be reviewed.
	 */
	public void visit(Handler expHandler) {
		TypeSpecification ts = expHandler.getParameter().getTypeSpecification();
		try {
			if (tooGeneralExceptions.contains(ts.getName())) { 
				context.reportViolation(expHandler);
			}
		} catch (JselException e) {
			context.warn(expHandler, e);
		}
	}

	/**
	 * Stores the setting form the configuration for the exceptions which
	 * are treated as to general to be in a throws claus of a method.
	 */
	private java.util.Set tooGeneralExceptions = new java.util.HashSet();
	
	/**
	 * Configures the rule. Reads in the values of the parameter general-exceptions.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("general-exceptions".equals(name)) {
			tooGeneralExceptions.add(parameter.toString());	
			return true;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName());
		} 
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Too general exceptions to be catched:\n");
		java.util.Iterator iter = tooGeneralExceptions.iterator();
		while (iter.hasNext()) { 
			ret.append("general-exception: " + (String) iter.next() + "\t");
		}
		ret.append("\n");
		return ret.toString();
	}
}
