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

import java.util.ArrayList;
import java.util.Iterator;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.CompilationUnit;

/**
 * Packages should begin with the root (project/organization) package.
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class RootPackageRule extends InspectorBase implements Parameterizable {
    //Anu 20050525 : added Arraylist of storing multiple packagenames
	private ArrayList rootPackage = new ArrayList();
    
    /** Reviews the node.
     * @return Collection of violated ASTs
     */
    public void visit(CompilationUnit compilationUnit) {
    	String packageName=compilationUnit.getPackage().getName();
//    	Anu 20050525 : updated for matching the package name with the passed parameters 
    	Iterator it = rootPackage.iterator();
    	while (it.hasNext())
    	{
    		String rootPkg = (String)it.next();	
    		if (rootPkg!=null){
    			if(packageName.equals(rootPkg) || packageName.startsWith(rootPkg+".")) {
    				return;
    			}
    		}	
    	    else
    	    {
    	    	return;
    	    }
    	
    	}
    	context.reportViolation(compilationUnit, new Object[] {rootPackage});
    }    
    
    /** Configures rule
     */
    public boolean setParameter(String name, Object value) throws ConfigurationException {
//    	Anu 20050525 : updated for storing the package names passed as parameter
    	if ("root-package".equals(name)) {
        	if (!rootPackage.contains(value.toString())) {
        	 rootPackage.add(value.toString());
        	}
			return true;
        } else {
            throw new ConfigurationException("Parameter '"+name+"' is not supported");
        }
    }                

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Application root package:\n");
		Iterator it=rootPackage.iterator();
		while (it.hasNext()) {
			ret.append("    " + it.next() + "\n");
		}
		return ret.toString();
	}
}
