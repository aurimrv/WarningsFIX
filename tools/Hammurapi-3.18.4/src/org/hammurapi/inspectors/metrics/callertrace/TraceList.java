/*
 * Created on Nov 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TraceList extends Vector {
	private String searchMethodSignature = "<not defined>"; 
	private TracedMethod endpoint = null;

	// private static Logger logger = Logger.getLogger(TraceList.class.getName());
	/* (non-Javadoc)
	 * @see java.util.Vector#elementAt(int)
	 */
	
	public TraceList(Object key ){
		super();
		searchMethodSignature = key.toString();
	}
		
	public Trace traceElementAt(int index) {
		return (Trace) super.elementAt(index);
	}

	public void setEndpointMethod ( MethodWrapper aMethods, String key ){
		TracedMethod tm = new TracedMethod(aMethods);
		
		endpoint = tm;
		endpoint.setSearchKey(key);

		}
	
	
	public Trace retrieveTraceFor( Trace searchTrace ){
		
		//-- default
		Trace returnTrace = null;
		for (int i=0; i<this.size(); i++){
			
			Trace t = (Trace)this.elementAt(i);
			if (t.equals(searchTrace) ){
				/// return t;
			} else if ( t.sameNameAndSizeButNotEqual( searchTrace ) ) {
				return t;
			}
		}
		return returnTrace;
	}

	public Trace cloneTraceFor( Trace searchTrace ){
		
		return (Trace)searchTrace.clone();
		
	}

	public Element toDom(Document document){
		
		Element ret=document.createElement("TraceList");
		ret.setAttribute("size", String.valueOf( this.size() ));
		String str = "Not Given";
			if (endpoint != null){ str = endpoint.getSearchKey(); };
		ret.setAttribute("methodName",  str);
		
		
		for (int i = 0; i < this.size(); i++) {
			Element trc=document.createElement("Trace");
			trc.setAttribute("id", String.valueOf( (i+1) ));
			ret.appendChild(trc);
			Trace t = (Trace) this.traceElementAt(i);
			for (int j = t.size() - 1; j > -1; j--) {
				TracedMethod tm = t.traceElementAt(j);
				trc.appendChild( tm.toDom( document, (((t.size()-1)  - j))));
			}
			}
		return ret;
		}

	public String toXml() {
		StringBuffer sb = new StringBuffer();
		String str = "Not Given";
			if (endpoint != null){ str = endpoint.getSearchKey(); };
		sb.append("<TraceList size=\"" + this.size() + "\" methodName=\"" + str + "\">\n");
//!! job unresolved NullPointer
// 		sb.append( this.endpoint.toXml(0) );
		for (int i = 0; i < this.size(); i++) {
			sb.append("<Trace id=\"" + (i+1) + "\">");
			Trace t = (Trace) this.traceElementAt(i);

			for (int j = t.size() - 1; j > -1; j--) {
//				!! index  im XML stimmt nicht
				TracedMethod tm = t.traceElementAt(j);
				sb.append(tm.toXml((((t.size()-1)  - j))));
			}
			sb.append("</Trace>\n");
		}
		sb.append("</TraceList>\n");
		return sb.toString();
	}

}

