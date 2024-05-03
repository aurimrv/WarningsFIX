/*
 * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Johannes Bellert
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
 * URL: http://www.hammurapi.com
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Apr 25, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Vector;

import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPathAPI;
import org.hammurapi.HammurapiException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
	
/**
 * @author Johannes Bellert
 *
 */
public class TechStackEntity {
	
	private String name = "";
	private String rating = "<undefined>";
	private String complexity = "";
	private String homeUrl = "NA";
	
	
	
	public boolean markIfPartOfJarFileList(String jarName){
	    boolean ret = false;
	    int i=0;
	    while( !ret && i<getJarFileList().size()){
	        JarFile jf = (JarFile)getJarFileList().elementAt(i);
	        System.out.println( jarName +" <> " +jf.getName());
	        if(jarName.equals(jf.getName()) ){
	            jf.setIsUsed(true);
	            ret =true;
	        }
	        i++;
	    }
	    return ret;
	}
	
    /**
     * @return Returns the jarFileList.
     */
    public Vector getJarFileList() {
        return jarFileList;
    }
    /**
     * @param jarFileList The jarFileList to set.
     */
    public void setJarFileList(Vector jarFileList) {
        this.jarFileList = jarFileList;
    }
    /**
     * @return Returns the licenseList.
     */
    public Vector getLicenseList() {
        return licenseList;
    }
    /**
     * @param licenseList The licenseList to set.
     */
    public void setLicenseList(Vector licenseList) {
        this.licenseList = licenseList;
    }
	private Vector licenseList = new Vector();
	private Vector jarFileList = new Vector();
	private Vector variableMapping = new Vector();
	private Vector extensionMapping = new Vector();
	
	/**
	 * @return Returns the extensionMapping.
	 */
	public Vector getExtensionMapping() {
		return extensionMapping;
	}
	/**
	 * @param extensionMapping The extensionMapping to set.
	 */
	public void setExtensionMapping(Vector extensionMapping) {
		this.extensionMapping = extensionMapping;
	}
	/**
	 * @return Returns the variableMapping.
	 */
	public Vector getVariableMapping() {
		return variableMapping;
	}
	/**
	 * @param variableMapping The variableMapping to set.
	 */
	public void setVariableMapping(Vector variableMapping) {
		this.variableMapping = variableMapping;
	}

	public TechStackEntity(Element holder) throws HammurapiException {
		super();
		try {
			CachedXPathAPI cxpa=new CachedXPathAPI();
			
			name = holder.getAttribute("name") ;
	
			rating = holder.getAttribute("rating") ;
			complexity = holder.getAttribute("complexity") ;
			
				NodeIterator itvmNode=XPathAPI.selectNodeIterator(holder, "VariableMapping");
					Node vmNode;
					while ((vmNode=itvmNode.nextNode())!=null) {
						if (vmNode instanceof Element) {
							//System.out.println( " vmNode.getAttribute(name)"  + ((Element)vmNode).getAttribute("name") );
							this.variableMapping.add( (String)((Element)vmNode).getAttribute("name")  );
						}
					}		
				NodeIterator itxmNode=XPathAPI.selectNodeIterator(holder, "ExtensionMapping");
					Node xmNode;
					while ((xmNode=itxmNode.nextNode())!=null) {
						if (xmNode instanceof Element) {
							// System.out.println( " xmNode.getAttribute(name)"  + ((Element)xmNode).getAttribute("name") );
							this.extensionMapping.add( (String)((Element)xmNode).getAttribute("name")  );
						}
					}				
																			    
					NodeIterator itLicNode=XPathAPI.selectNodeIterator(holder, "License");
					Node licNode;
					while ((licNode=itLicNode.nextNode())!=null) {
						if (licNode instanceof Element) {
							 this.licenseList.add( (String)((Element)licNode).getAttribute("name")  );
						}
					}				
					NodeIterator itJarNode=XPathAPI.selectNodeIterator(holder, "JarFile");
					Node jarNode;
					while ((jarNode=itJarNode.nextNode())!=null) {
						if (jarNode instanceof Element) {
						    this.jarFileList.add( new JarFile( (Element)jarNode) );
						}
					}
					NodeIterator itHome=XPathAPI.selectNodeIterator(holder, "Home");
					Node homeNode;
					while ((homeNode=itHome.nextNode())!=null) {
						if (homeNode instanceof Element) {
						    this.homeUrl =  (String)((Element)homeNode).getAttribute("url");
						}
					}
		}catch (Exception e){
			throw new HammurapiException(e);
		}
	}

	public String toString(){
		return name ;
	}
	/**
	 * @return Returns the complexity.
	 */
	public String getComplexity() {
		return complexity;
	}
	/**
	 * @param complexity The complexity to set.
	 */
	public void setComplexity(String complexity) {
		this.complexity = complexity;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the rating.
	 */
	public String getRating() {
		return rating;
	}
	/**
	 * @param rating The rating to set.
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}
	public TechStackEntity( String _name, String _rating){
		super();
		name = _name;
		rating = _rating;
			
	}

	public Element toDom(Document document){

		Element ret=document.createElement("TechStackEntity");
		ret.setAttribute("name", this.name );
		
		ret.setAttribute("complexity", this.complexity );
		ret.setAttribute("rating", this.rating );
	
		for(int i=0; i<licenseList.size(); i++){
		    Element retA=document.createElement("License");
		    retA.setAttribute("name", (String) this.licenseList.elementAt(i));
		    ret.appendChild(  retA);
		}
		
		for(int i=0; i<jarFileList.size(); i++){
		    ret.appendChild( ((JarFile)jarFileList.elementAt(i)).toDom(document) );
		}
		Element retURL=document.createElement("Home");
		retURL.setAttribute("url", this.homeUrl );
		ret.appendChild(  retURL);
		return ret;
	}

}
