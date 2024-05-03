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
import com.pavelvlasov.jsel.Field;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;


/**
 * ER-201
 * Discourage usage of instance variables like a, j by enforcing minimal variable name length.
 * @author  Janos Czako
 * @version $Revision: 1.4 $
 */
public class MinimalInstanceVariableLengthRule extends InspectorBase implements Parameterizable {
	
	/**
	 * Reviews the variable definitions, if they have clashing names
	 * with the class name.
	 * 
	 * @param element the method definition to be reviewed.
	 */
	public void visit(TypeDefinition element) {
		java.util.Iterator iter = element.getFields().iterator();
		while (iter.hasNext()) {
			Field field = (Field) iter.next();
			if (field instanceof VariableDefinition) {
				VariableDefinition vd = (VariableDefinition) field;
				String name = vd.getName();
				if (name.length()<minLength.intValue()) {
					context.reportViolation(vd);
				}
			}
		}
	}
	/**
	 * Stores the setting form the configuration for the minimum allowed
	 * length of the name for class variables.
	 */
	private Integer minLength;
	
    
	/**
	 * Configures the rule. Reads in the values of the parameters min-length.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object value) throws ConfigurationException {
		if ("min-length".equals(name)) {
			minLength=  (Integer) value;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported");
		}
		return true;
	}          

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed minimal length for instance variable names:\n");
		ret.append("length: " + minLength + "\n");
		return ret.toString();
	}
}
