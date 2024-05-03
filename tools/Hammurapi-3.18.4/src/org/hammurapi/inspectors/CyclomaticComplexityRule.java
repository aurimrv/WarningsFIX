/*
 * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Pavel Vlasov
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
 * e-Mail: vlasov@pavelvlasov.com

 */
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Operation;

/**
 * Cyclomatic complexity exceeds specified maximum.
 * @author  Pavel Vlasov
 * @version $Revision: 1.5 $
 */
public class CyclomaticComplexityRule extends InspectorBase implements Parameterizable {
    /**
     * Stores the setting form the configuration for the maximum allowed
     * complexity for an operation.
     */
    private Integer operationMaxComplexity;
    /**
     * Stores the setting form the configuration for the maximum allowed
     * complexity for a class.
     */
    private Integer classMaxComplexity;

    //Anu 24th May 05 :added  for setting the upper limit for ER-011(Yellow) inspector
    /**
     * Stores the setting form the configuration for the maximum allowed
     * complexity for the operation ER-011 (Red) .This is the upper range check for
     * ER-011(Yellow).	
     */
    
    private Integer operationMaxRedComplexity;
    /**
     * Stores the setting form the configuration for the maximum allowed
     * complexity for a class ER-011 (Red) .This is the upper range check for
     * ER-011(Yellow).	
     */
    private Integer classMaxRedComplexity;

    //Anu 24th May 05 :End
    /**
     * Reviews the methods and calculates their complexity.
     * 
     * @param operation the method to be reviewed.
     */    
    public void visit(Operation operation) {
    	int complexity=operation.getComplexity();
    	context.addMetric(operation, "Operation complexity", complexity);
    	//Anu 24th May 05 :added check for the upper limit for ER-011(Yellow) inspector
    	if(operationMaxRedComplexity!=null ){
    		if (operationMaxComplexity!=null && complexity>operationMaxComplexity.intValue() && 
    			complexity < operationMaxRedComplexity.intValue() ) { 		
    			context.reportViolation(operation, new Object[] {operationMaxComplexity, new Integer(complexity)});
    		}
    	}
    	else {
    		if (operationMaxComplexity!=null && complexity>operationMaxComplexity.intValue()) {
    			context.reportViolation(operation, new Object[] {operationMaxComplexity, new Integer(complexity)});
    		}
    	}
    }
    
    /**
     * Reviews the class definition and calculates its complexity.
     * 
     * @param clazz the class definition to be reviewed.
     */
    public void visit(com.pavelvlasov.jsel.Class clazz) {
    	int complexity=clazz.getComplexity();
    	context.addMetric(clazz, "Class complexity", complexity);
        //Anu 24th May 05 :added check for the upper limit for ER-011(Yellow) inspector
    	if(classMaxRedComplexity!=null ){
    		if (classMaxComplexity!=null && complexity>classMaxComplexity.intValue() && 
    			complexity < classMaxRedComplexity.intValue() ) {		
    			context.reportViolation(clazz, new Object[] {classMaxComplexity, new Integer(complexity)});
    		}	
    	}
    	else {
    		if (classMaxComplexity!=null && complexity>classMaxComplexity.intValue()) {
    			context.reportViolation(clazz, new Object[] {classMaxComplexity, new Integer(complexity)});
             }   	
       }
    }    
    
    /**
     * Configures the rule. Reads in the values of the parameters operation-max-complexity and
     * class-max-complexity.
     * 
     * @param name the name of the parameter being loaded from Hammurapi configuration
     * @param value the value of the parameter being loaded from Hammurapi configuration
     * @exception ConfigurationException in case of a not supported parameter
     */
    public boolean setParameter(String name, Object value) throws ConfigurationException {
        if ("operation-max-complexity".equals(name)) {
            operationMaxComplexity= (Integer) value;
        } else if ("class-max-complexity".equals(name)) {
        	classMaxComplexity=  (Integer) value;
//        	Anu 24th May 05 :added check for the upper limit for ER-011(Yellow) inspector
        } else if ("class-max-redcomplexity".equals(name)) {
        	classMaxRedComplexity=  (Integer) value;
        } else if ("operation-max-redcomplexity".equals(name)) {
        	operationMaxRedComplexity=  (Integer) value;
        } else {
            throw new ConfigurationException("Parameter '"+name+"' is not supported");
        }
//      Anu 24th May 05 :added check for the upper limit for ER-011(Yellow) inspector should 
//        be more than the max limit
        if(operationMaxRedComplexity!=null && operationMaxComplexity!=null)
        {
        	if (operationMaxRedComplexity.intValue()< operationMaxComplexity.intValue() ){
        		throw new ConfigurationException(
        		"operationMaxRedComplexity should be higher than operationMaxComplexity" );
        	}
        }
        if(classMaxRedComplexity!=null && classMaxComplexity!=null)
        {
        	if (classMaxRedComplexity.intValue()< classMaxComplexity.intValue() ){
        		throw new ConfigurationException(
        		"classMaxRedComplexity should be higher than classMaxComplexity" );
        	}
        }
		return true;
    }          

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed cyclematic complexity:\n");
		ret.append("operation: " + operationMaxComplexity + "\t");
		ret.append("class: " + classMaxComplexity + "\n");
		return ret.toString();
	}
}
