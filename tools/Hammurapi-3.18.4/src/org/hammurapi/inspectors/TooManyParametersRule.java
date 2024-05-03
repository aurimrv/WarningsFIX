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
 * Too many parameters.
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class TooManyParametersRule extends InspectorBase implements Parameterizable {
    /**
     * Stores the setting form the configuration for the maximum allowed
     * number of parameters for an operation.
     */
    private Integer maxParameters;
    
    /** 
     * Reviews the method and checks if it has too many parameters.
     * 
     * @param operation the method to be reviewed.
     */
    public void visit(Operation operation) {
    	if (maxParameters!=null && operation.getParameters().size()>maxParameters.intValue()) {
    		context.reportViolation(operation, new Object[] {new Integer(operation.getParameters().size()), maxParameters});
    	}
    }
    
    /**
     * Configures the rule. Reads in the values of the parameters max-parameters and
     * class-max-complexity.
     * 
     * @param name the name of the parameter being loaded from Hammurapi configuration
     * @param value the value of the parameter being loaded from Hammurapi configuration
     * @exception ConfigurationException in case of a not supported parameter
     */
    public boolean setParameter(String name, Object value) throws ConfigurationException {
        if ("max-parameters".equals(name)) {
            maxParameters=new Integer(Integer.parseInt(value.toString()));
			return true;
        }
        
		throw new ConfigurationException("Parameter '"+name+"' is not supported");
    }                  

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed nbr of parameters:\n");
		ret.append("max parameters: " + maxParameters + "\n");
		return ret.toString();
	}
}
