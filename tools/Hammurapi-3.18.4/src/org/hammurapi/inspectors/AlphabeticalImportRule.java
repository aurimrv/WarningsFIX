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

import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.Identifier;
import com.pavelvlasov.review.SourceMarker;

/**
 * Packages should be imported in alphabetical order
 * @author  Pavel Vlasov
 * @version $Revision: 1.6 $
 */
public class AlphabeticalImportRule extends InspectorBase implements Parameterizable {
  
  
	private String [] importOrder ;
	/**
   * Reviews the node.
   * 
   * @param compilationUnit the element under review
   */
	public void visit(CompilationUnit compilationUnit) {
		Comparator importComparator = new Comparator() {

			public int compare(Object importDef1, Object importDef2) {
				
				//20052406 Anu start : Modified to compare the import string with
				//configuartion parameter
				int index1 = getIndex((String) importDef1);
				int index2 = getIndex((String) importDef2);
				if(index1 != -1 && index2 != -1 ){
					if(index1<index2){
						return -1;
					}
					else if(index1>index2) {
						return 1;
					}
				}
				else if(index1 == -1 && index2 != -1)
				{
					return 1;
				}
				else if(index1 != -1 && index2 == -1)
				{
					return -1;
				}
		
				
					
//				if (((String) importDef1).startsWith("java.") && ((String) importDef2).startsWith("com.")) {
//					return -1;
//				} else if (((String) importDef1).startsWith("com.") && ((String) importDef2).startsWith("java.")) {
//					return 1;
				
				
				return ((Comparable) importDef1).compareTo(importDef2);
			}

			private int getIndex(String importDef) {
				
				int retVal = -1;
				int orderLength = importOrder.length -1;
				for(int i=0;i<=orderLength;i++)
				{
					if (importDef.startsWith(importOrder[i]))
					{
						retVal = i;
						break;
					}
				}
							
				return retVal;
			}
			
		};
	
		//20052406 Anu end 
		Iterator it=compilationUnit.getImports().iterator();
		String prevImportValue=null;
		while (it.hasNext()) {
			Identifier ii=(Identifier) it.next();
			if (prevImportValue!=null && importComparator.compare(ii.getValue(), prevImportValue)<0) {
				context.reportViolation((SourceMarker) ii);
			}
			prevImportValue=ii.getValue();
		}
	}
	
//	20050524 Anu Start : updated for storing the import order passed as parameter
	/** Configures rule
     */
    public boolean setParameter(String name, Object value) throws ConfigurationException {
    	
    	String impOrder = "";
    	if ("import-order".equals(name)) {
        	impOrder = value.toString();
        	StringTokenizer st=new StringTokenizer(impOrder, ",");
        	importOrder = new String[st.countTokens()];
        	for (int i=0; st.hasMoreTokens(); i++) {
        		importOrder[i]=st.nextToken();
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
		StringBuffer ret=new StringBuffer("Import Order:\n");
	    int orderSize = importOrder.length -1;
		for(int i=0;i<=orderSize;i++) {
			ret.append("    " + importOrder[i] + "\n");
		}
		return ret.toString();
	}
	//20050524 Anu End
}
