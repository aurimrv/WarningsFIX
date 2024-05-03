/*
 * Hammurapi
 * Automated Java code review system.
 * Copyright (C) 2005  CraftOfObjects
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
 * e-Mail: CraftOfObjects@gmail.com

 */

package org.hammurapi.inspectors;

import java.util.Vector;

import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Operation;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;

/**
 * @author CraftOfObjects
 *
 * This inspector identify singleton pattern by checking for,
 * those class variable type equal to the implementing class.
 * ER-213 is reported.
 * 
 * If the Singleton keeps not final variables > 1, the class is statefull.
 * ER-212 is reported.
 * 
 */
public class StatelessSingleton extends InspectorBase {
  
    private com.pavelvlasov.jsel.Class currentType = null ;
    private boolean currentIsSingleton = false;
    private Vector listOfNotFinalFields = null;
    
    
    public void init (){;
    	currentIsSingleton = false;
    	listOfNotFinalFields = new Vector();
    }
    	
	public void visit(com.pavelvlasov.jsel.Class type) {
	    init();
	    currentType = type;
	}
	
	 public boolean isSingleton(VariableDefinition element) throws JselException {
	    	LanguageElement parent=element.getParent();
	    	if (parent instanceof TypeDefinition) {
	    	    // System.out.println( " parent instanceof TypeDefinition TRUE  " );
	    	    boolean ret = currentType.getFcn().equals( element.getTypeSpecification().getType().getName() ); 
	    	    // System.out.println( " currentType.getFcn().equals( element.getTypeSpecification().getType().getName()  --> " + ret );
		    	return ret ;
	    	} else {
	    	    // System.out.println( " parent instanceof TypeDefinition FALSE  " );
	    		return false;
	    	}
	    }

	    public void visit(VariableDefinition element) {
	        // // System.out.println( "++ " + element.getSignature() );
	        
	        // is this a instance or class variable?
	        LanguageElement el = element.getParent();
	       //  // System.out.println( "** " + el.getClass() );
	        if ( el.getClass().equals(  com.pavelvlasov.jsel.impl.ClassImpl.class) ){
	        try {
				if (el != null && isSingleton(element) && element.getModifiers().contains("static")
				    //    || element.getModifiers().contains("final")) 
				    ) {
					context.info(currentType, "Singleton detected" );
				    context.getSession().getContext("ER-213").reportViolation( element, "Singleton detected" );
				    this.currentIsSingleton = true;
				}
				if ( ! element.getModifiers().contains("final") ){
				    // System.out.println( " added !! " );
				    listOfNotFinalFields.add (element);
				}
			} catch (JselException e) {
				context.warn(element.getEnclosingType(), e);
			}
	        } // fi
		}
	    
	    public void leave(TypeDefinition cu) throws HammurapiException {
	        // System.out.println( "leave: " + this.currentType.getName() );
	        this.checkStatelessSingleton();            
	    }
	    
	    public void checkStatelessSingleton(){
	        
	        //-- the singleton can be NOT final 
	        if ( currentIsSingleton && listOfNotFinalFields.size() > 1){
	        	context.info(currentType, "Singleton has " + listOfNotFinalFields.size() + " non final fields.");
	            context.getSession().getContext("ER-212").reportViolation( currentType, "Singleton contains " + listOfNotFinalFields.size() + " not final fields (together with the Singleton field)" );
	        }        
	    }	
}

