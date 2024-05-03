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

import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;

/**
 * Classes, methods and variables should be named according to Sun's naming convention. 
 * 
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class NamingStandardRule extends InspectorBase {
    
    /**
     * Reviews the name of the classes.
     * 
     * @param typeDefinition the typedefinition to be reviewed.
     */ 
    public void visit(TypeDefinition typeDefinition) {
        if (typeDefinition.getName().indexOf('_')!=-1 || !Character.isUpperCase(typeDefinition.getName().charAt(0))) {
            context.reportViolation(typeDefinition);
        }
    }   
    
    
    /**
     * Reviews the name of the methods.
     * 
     * @param method the method to be reviewed
     */
    public void visit(Method method) {
    	if (method.getName().indexOf('_')!=-1 || !Character.isLowerCase(method.getName().charAt(0))) {
    		context.reportViolation(method);
    	}
    }
    
    /**
     * Reviews the name of the attributes.
     * 
     * @param variableDefinition the variable definition to be reviewed.
     */
    public void visit(VariableDefinition variableDefinition) {
    	if (variableDefinition.getModifiers().contains("static") && variableDefinition.getModifiers().contains("final")) {
    		if (!"serialVersionUID".equals(variableDefinition.getName()) && !variableDefinition.getName().toUpperCase().equals(variableDefinition.getName())) {
    			context.reportViolation(variableDefinition);
    		}
    	} else if (variableDefinition.getName().indexOf('_')!=-1 || !Character.isLowerCase(variableDefinition.getName().charAt(0))) {
    		context.reportViolation(variableDefinition);
    	}
    }
    
    /**
     * Reviews the name of the parameters.
     * 
     * @param parameter the parameter declaration to be reviewed.
     */
    public void visit(Parameter parameter) {
    	if (parameter.getName().indexOf('_')!=-1 || !Character.isLowerCase(parameter.getName().charAt(0))) {
    		context.reportViolation(parameter);
    	}
    }    
    
    public void visit(com.pavelvlasov.jsel.Package pkg) {
    	if (pkg.getName().toLowerCase()!=pkg.getName()) {
    		context.reportViolation(null, "Package name shall be in lower case: "+pkg.getName());
    	}        
    	
    	if (pkg.getName().indexOf('_')!=-1) {
    		context.reportViolation(null, "Packages name shall not contain underscore character: "+pkg.getName());    	    
    	}
    }
}
