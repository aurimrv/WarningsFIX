/*
 * Created on May 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author MUCBJ0
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JspXref {
	//!! could/should be SourceMarker 
	public String base = "NA";

	public String invocation = "NA";
	//!! could/should be ref JspDescriptor
	public String ref = "NA";

	public JspXref ( JspDescriptor jspD, String invoc, String _ref){
		super();
		base = jspD.codeMetric.source_url ;
		invocation = invoc;
		ref = _ref;
	}
	
	public Element toDom(Document document){

		Element ret=document.createElement("JspXref");
		ret.setAttribute("base", base);
		ret.setAttribute("invocation", this.invocation);
		ret.setAttribute("ref", this.ref);
		return ret; 
	}
}
