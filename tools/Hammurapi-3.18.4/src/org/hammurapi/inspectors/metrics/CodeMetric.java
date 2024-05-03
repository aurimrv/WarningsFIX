/*
 * Created on Mar 14, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.hammurapi.inspectors.metrics;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CodeMetric {

    // private SourceMarker srcCodeMarker;
	public String source_url = "";
	public int  source_line = 0;
	public int  source_col = 0;

	private String descriptonEntity = "NA";
	private String name = "NA";
	private String sourceURL;
	private int number = 0;
	private int ncss = 0;
	private int function = 0;
	private double functionAverage = 0.0;
	private double classAverage = 0.0;
	private Vector children = new Vector();


	public Element toDom(Document document){

		Element ret=document.createElement("SourceCodeMetric"+descriptonEntity);
		ret.setAttribute("name", name);
		try{
		if ( source_url != null && !"".equals (source_url) ){

			// packageName.replace('.','/')+compilationUnit.getName()
			ret.setAttribute("source-url", source_url);
			ret.setAttribute("line", String.valueOf(source_line));
			ret.setAttribute("col", String.valueOf(source_col));
		} else {
	        ret.setAttribute("line", "0" );
	        ret.setAttribute("col", "0" );

		}
		} catch ( Exception e){
			e.printStackTrace();
		}

		Element entities=document.createElement("Entities");
		ret.appendChild(entities);
		entities.setAttribute("number", String.valueOf(number));

		Element ncssE=document.createElement("NCSS");
		ret.appendChild(ncssE);
		ncssE.setAttribute( "number", String.valueOf( this.ncss ) );


		Element functions=document.createElement("Functions");
		ret.appendChild(functions);
		functions.setAttribute("number",  String.valueOf(function));


		Iterator it=children.iterator();
		while (it.hasNext()){
			CodeMetric cm = (CodeMetric) it.next();
			ret.appendChild(cm.toDom(document));
		}

		return ret;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(descriptonEntity);
		sb.append(" - ");
		sb.append(this.name);
		return sb.toString();
	}

	/**
	 * @return Returns the children.
	 */
	public Vector getChildren() {
		return children;
	}
	/**
	 * @param children The children to set.
	 */
	public void setChildren(Vector children) {
		this.children = children;
	}
	/**
	 * @return Returns the descriptonEntity.
	 */
	public String getDescriptonEntity() {
		return descriptonEntity;
	}
	/**
	 * @param descriptonEntity The descriptonEntity to set.
	 */
	public void setDescriptonEntity(String descriptonEntity) {
		this.descriptonEntity = descriptonEntity;
	}
	/**
	 * @return Returns the function.
	 */
	public int getFunction() {
		return function;
	}
	/**
	 * @param function The function to set.
	 */
	public void setFunction(int function) {
		this.function = function;
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
	 * @return Returns the ncss.
	 */
	public int getNcss() {
		return ncss;
	}
	/**
	 * @param ncss The ncss to set.
	 */
	public void setNcss(int ncss) {
		this.ncss = ncss;
	}
	/**
	 * @return Returns the number.
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * @param number The number to set.
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
	/**
	 * @return Returns the srcCodeMarker.
	 */
	public String getSrcCodeUrl() {
		return source_url;
	}
	
}
