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
 * URL: http://www.pavelvlasov.com/pv/content/menu.show?id=products.jtaste
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Mar 20, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ListOfCategories extends Vector  {
	//!! no hard references to JSel Classes
	//!! refactor 
	private String type = "";
	private String source_url = "";
	private int  source_line = 0;
	private int  source_col = 0;
	
	private String aPackage = null;
	private Vector violations = new Vector();

	public ListOfCategories( String typeName, String source_url, int line, int col){
		super();
		
		this.type = typeName;
		this.source_url = source_url;
		this.source_col = col;	
		this.source_line = line;
	}
	
	public boolean contains(String search){
		boolean found = false;
		for(int i=0; i< this.size(); i++){
				ArchitecturalCategory ac =(ArchitecturalCategory)this.elementAt(i);
				if ( search != null && ac.categoryType!= null && ac.categoryType.equals(search)){
					return true;
				}
				}
		return found;
	}

	public String toKey() {
		if (this.getType() != null ) {
			return this.getType();
		} else {
			return "unDefinedKey";
		}
	}

	public Element toDom(Document document){

		Element ret=document.createElement("ListOfClassCategories");
		Element typ=document.createElement("SourceMarker");
		if(this.type != null){
			try {
				ret.setAttribute("class",this.type );
				
				typ.setAttribute("source-url", source_url);
				typ.setAttribute("line", String.valueOf( source_line ));
				typ.setAttribute("col", String.valueOf(  source_col ));

			} catch (DOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		ret.setAttribute("size", String.valueOf(this.size()));
		ret.appendChild(typ);

		for(int i=0; i< this.size(); i++){
			ArchitecturalCategory ac =(ArchitecturalCategory)this.elementAt(i);
			ret.appendChild(ac.toDom(document));
		}

		Element violaLst=document.createElement("LayerList");
		ret.appendChild(violaLst);

		for (int i = 0; i < this.violations.size(); i++) {
			Element viol=document.createElement("Layer");
			viol.setAttribute("id", String.valueOf( (i+1) ) );
			viol.setAttribute("name", (String) this.violations.elementAt(i) );
			violaLst.appendChild(viol);
		}
		return ret;
	}

	public StringBuffer toXml() {
		StringBuffer sb = new StringBuffer();

			sb.append("<ListOfCategories class=\"");
			sb.append( this.getType()				);

		sb.append("\" size=\"");
		sb.append(this.size());
		sb.append("\">");

		sb.append("<Violations>");
		for (int i = 0; i < this.violations.size(); i++) {
			sb.append("<Violation id=\"" + (i+1) + "\">");
			sb.append((String) this.violations.elementAt(i));
			sb.append("</Violation>");
		}

		sb.append("</Violations>");


		for(int i=0; i< this.size(); i++){
		ArchitecturalCategory ac =(ArchitecturalCategory)this.elementAt(i);
//		sb.append(ac.toXml());
		}
		sb.append("</ListOfCategories>");
		return sb;
	}

	/**
	 * @return
	 */
	public String getType() {

		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public Vector getViolations() {
		return violations;
	}

	/**
	 * @param vector
	 */
	public void setViolations(Vector vector) {
		violations = vector;
	}

	/**
	 * @return Returns the source_url.
	 */
	public String getSourceURL() {
		return source_url;
	}
	/**
	 * @param source_url The source_url to set.
	 */
	public void setSourceURL(String source_url) {
		this.source_url = source_url;
	}
}
