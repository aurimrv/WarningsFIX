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
import com.pavelvlasov.jsel.Interface;
import com.pavelvlasov.jsel.Modifiable;


/**
 * ER-115
 * No need to provide (public, abstract, ) modifiers for interface methods
 * @author  Janos Czako
 * @version $Revision: 1.3 $
 */
public class InterfaceMethodModifiersRule 
	extends InspectorBase implements Parameterizable {
	
	/**
	 * Reviews the interface definition, if it has a declaration with 
	 * not allowed modifier(s). 
	 * The list of the allowed modifiers is configurable.
	 * 
	 * @param element the interface declaration to be reviewed.
	 */	
	public void visit(Interface element) {
		java.util.Iterator fields = element.getFields().iterator();
		
		while (fields.hasNext()) {
			Modifiable item = (Modifiable) fields.next();
			
			if (! allowedModifiers.containsAll(item.getModifiers())) {
				context.reportViolation(item);
			}
		}
	}

	/**
	 * Stores the setting form the configuration for the not allowed
	 * modifiers for the operations of the interface definitions.
	 */
	private java.util.Set allowedModifiers = new java.util.HashSet();
	
    
	/**
	 * Configures the rule. Reads in the values of the parameters operation-max-complexity and
	 * class-max-complexity.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("allowed-modifier".equals(name)) {
			allowedModifiers.add(parameter.toString());
			return true;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported");
		}
	}
	
	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed modifiers in the interface declarations:\n");
		java.util.Iterator iter = allowedModifiers.iterator();
		while (iter.hasNext()) { 
			ret.append("allowed-modifier: " + (String) iter.next() + "\t");
		}
		ret.append("\n");
		return ret.toString();
	}
}
