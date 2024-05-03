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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ListOfPackageCategories extends Hashtable {
	private String aPackage = "<not-defined>";
	private Vector violations = new Vector();
	private HashSet layers = new HashSet();


	public ListOfPackageCategories(String _aPackage ){
	super();
	if( "".equals(_aPackage)){
		this.aPackage = "(default package)";
	}else{
		this.aPackage = _aPackage;
	}
	}

	public void addAll( Hashtable ht ){
		Enumeration enumk = ht.keys();
		Enumeration enumv = ht.elements();
		while (enumv.hasMoreElements()) {
			Object k = enumk.nextElement();
			com.pavelvlasov.jsel.Package p = (com.pavelvlasov.jsel.Package)enumv.nextElement();
			this.put( k , p);


	}}
	public boolean contains(String search){
		return  super.containsKey(search);
	}

	public ArchitecturalCategoryPackage get(String key){
		if (key != null){
			return (ArchitecturalCategoryPackage)super.get(key);
		} else {
			throw new NullPointerException();
			// return null;
		}

	}

	public static String getWithClassFcn(String keyFcn) {
		if (keyFcn != null) {
			int lastI = keyFcn.lastIndexOf(".");
			if (lastI == -1) {
				return "<default>";
			} else {
				String l = keyFcn.substring(0,lastI);
				return l;
			}
		}
		return "";
	}

	public String toKey(){
		if ( this.aPackage != null){

		return this.aPackage  ;
		} else { return "undefinedRoot com.pavelvlasov.jsel.PackageName";
		}
	}

	public Element toDom(Document document){

		Element ret=document.createElement("ListOfPackageCategories");
		ret.setAttribute("package", this.toKey());
		ret.setAttribute("size", String.valueOf(this.size()));

		Enumeration enum = this.elements();
		while (enum.hasMoreElements()) {
			ArchitecturalCategoryPackage ac = (ArchitecturalCategoryPackage) enum.nextElement();
			ret.appendChild(ac.toDom(document));
		}

		Element layerLst=document.createElement("LayerList");
		ret.appendChild(layerLst);
		Iterator it = this.layers.iterator();

		int j = 1;
		while(it.hasNext()){
			Element layer=document.createElement("Layer");
			layer.setAttribute("id", String.valueOf( j ) );
			layer.setAttribute("name", (String) it.next() );
			layerLst.appendChild(layer);
			j++;
		}

		Element violaLst=document.createElement("Violation");
		ret.appendChild(violaLst);


		for (int i = 0; i < this.violations.size(); i++) {
			Element viol=document.createElement("Layer");
			viol.setAttribute("id", String.valueOf( (i+1) ) );
			viol.setAttribute("name", (String) this.violations.elementAt(i) );
			violaLst.appendChild(viol);
		}


		return ret;

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
	 * @return
	 */
	public HashSet getLayers() {
		return layers;
	}

	/**
	 * @param set
	 */
	public void setLayers(HashSet set) {
		layers = set;
	}

	/**
	 * @return Returns the aPackage.
	 */
	public String getAPackage() {
		return aPackage;
	}
}
