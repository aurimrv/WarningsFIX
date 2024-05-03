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
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.TypeDefinition;


/**
 * ER-094
 * Avoid more than two levels of nested inner classes
 * @author  Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class InnerClassNestingRule extends InspectorBase implements Parameterizable {
	/**
	 * Stores the setting form the configuration for the allowed
	 * maximal nesting of the inner classes.
	 */
	private Integer maxNesting;
	
	/**
	 * Reviews the type definition if it is an inner class which nesting
	 * is deaper than the configured value.
	 * 
	 * @param element the type definition to be reviewed.
	 */	
	public void visit(TypeDefinition element) {
		if (maxNesting!=null) {
			int i=0;
			for (LanguageElement parent=element.getParent(); parent!=null && parent instanceof TypeDefinition; parent=parent.getParent()) {
				i++;
			}
			if (i>maxNesting.intValue()) {
				context.reportViolation(element);
			}
		}
	}
	
	/**
	 * Configures rule. Reads in the values of the parameter max-nesting.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter name, or value.
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("max-nesting".equals(name)) {
			maxNesting = (Integer) parameter;
			return true;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName());
		} 
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed maximum nesting for inner classes:\n");
		ret.append("max-nesting: " + maxNesting + "\n");
		return ret.toString();
	}
}
