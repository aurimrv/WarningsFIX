/*
 * Created on Nov 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TraceTable extends Hashtable {


	/* (non-Javadoc)
	 * @see java.util.Dictionary#get(java.lang.Object)
	 */
	public synchronized TraceList get(String key) {
		// TODO Auto-generated method stub
		return (TraceList)super.get(key);
	}

	public Element toDom(Document document){
		Element ret=document.createElement("TraceTable");
		ret.setAttribute("size", String.valueOf( this.size() ));
		
		Enumeration enum = this.elements();
		Enumeration enumKey = this.keys();
		while(enum.hasMoreElements()){
			String key = (String)enumKey.nextElement();
			TraceList tList = (TraceList)enum.nextElement();
			
			ret.appendChild(tList.toDom( document ));
		}
		return ret;
	}
	
	
	public String toXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<TraceTable size=\"" + this.size() + "\">\n");
		Enumeration enum = this.elements();
		Enumeration enumKey = this.keys();
		while(enum.hasMoreElements()){
			String key = (String)enumKey.nextElement();
			TraceList tList = (TraceList)enum.nextElement();
			sb.append(tList.toXml() );
		}
		sb.append("</TraceTable>\n");
		return sb.toString();
	}


}
