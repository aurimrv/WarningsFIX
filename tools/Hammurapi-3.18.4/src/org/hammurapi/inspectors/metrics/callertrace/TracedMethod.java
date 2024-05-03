/*
 * Created on Nov 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TracedMethod {
	private MethodWrapper method = null;
	private SortedMap keyMethodList = new TreeMap(new TracedMethodLocComparator());

	private String searchKey ="";
	private boolean endpoint = false;
	private boolean key = false;

	public TracedMethod( MethodWrapper mw){
		super();
		method = mw;
	}
	
	
	/**
	 * @return
	 */
	public MethodWrapper getMethod() {
		return method;
	}


	/**
	 * @param method
	 */
	public void setMethod(MethodWrapper method) {
		this.method = method;
	}


	public String toString(){
		
		if ( method != null ){
			return method.toString();
			// return method.getMethodKey();
		} else {
			return searchKey;
		}
	}

	
	public Element toDom(Document document, int id) {
		Element ret = document.createElement("TracedMethod");
		ret.setAttribute("id", String.valueOf(id));
		Set s = this.getKeyMethodList().keySet();
		for (Iterator it = s.iterator(); it.hasNext();) {
			MethodWrapper m = (MethodWrapper) it.next();
			Element ret2 = document.createElement("Inside");
			ret.appendChild(ret2);
			
				
			if (this.getMethod().equals(m)) {
				if (this.isKey()) {
					ret2.setAttribute("type", "key");
				} else {
				ret2.setAttribute("type", "mother");
				}
			} else {
				ret2.setAttribute("type", "sibling");
			};
			
			if (this.getMethod() != null) {
				Element ret3 = document.createElement("Method");
				ret3.setAttribute("key", m.printMethodName());
				if ( m instanceof MethodWrapperDeclaration ){
				    ret3.setAttribute("Ma",  String.valueOf(((MethodWrapperDeclaration) m).afferentMethodCoupling));
				    ret3.setAttribute("Me",  String.valueOf(((MethodWrapperDeclaration) m).getInvokedMethods().size()));
				    } else{
					    ret3.setAttribute("Ma", "");
					    ret3.setAttribute("Me",  "");

			}				
				ret3.setAttribute("source-url", m.getSrcURL());
				ret3.setAttribute("line", String.valueOf(m.getSrcLine()));
				
				ret2.appendChild(ret3);
			}
		}
		return ret;
	}
	
	public String toXml(int id) {
		StringBuffer sb = new StringBuffer();
		Set s = this.getKeyMethodList().keySet();
		sb.append("<TracedMethod id=\"" + id + "\">");

		for (Iterator it=s.iterator(); it.hasNext();){
			MethodWrapper m= (MethodWrapper)	it.next();

		sb.append("<Inside ");

		sb.append(	"type=\"" );
		if ( this.getMethod().equals( m )){
		sb.append( "mother"); }else{ sb.append( "sibling"); };
		sb.append( "\">\n");
		if ( this.getMethod() != null ){
				sb.append(m.getMethodKey());
				sb.append(m.getLine());
				sb.append(m.getSrcURL());
		}
		sb.append("</Inside>\n");
		}
		sb.append("</TracedMethod>\n");
				return sb.toString();
	}



	/**
	 * @return
	 */
	public String getSearchKey() {
		return searchKey;
	}

	/**
	 * @param string
	 */
	public void setSearchKey(String string) {
		searchKey = string;
	}

	/**
	 * @return
	 */
	public boolean isEndpoint() {

		return endpoint;
	}
	/**
	 * @return
	 */
	public boolean isKey() {

		return key;
	}
	/**
	 *
	 */
	public void setIsKeyTrue() {
		key = true;

	}
	/**
	 *
	 */
	public void setEndpointTrue() {
		endpoint = true;

	}

	public String toKey(){
		if ( this.method != null ){
			return ((MethodWrapper)this.method).toSearchKey();
		} else {
			return getSearchKey();
		}
	}
	/**
	 * @return Returns the headKeyMethodList.
	 */
	public SortedMap getKeyMethodList() {
		return keyMethodList;
	}

	/**
	 * @param headKeyMethodList The headKeyMethodList to set.
	 */
	public void setKeyMethodList(SortedMap headKeyMethodList) {
		this.keyMethodList = headKeyMethodList;
	}


	/**
	 * @param key
	 * @param this
	 */
	public void innerCheckForSiblings(String[] key) {
		if (getMethod() != null) {
			getKeyMethodList().put(getMethod(), getMethod());
	
			// System.out.println( "++ " + tm.getMethod().getClass().getName() )
			// ;
			//--loockup for implementor of MethodInvoked
	
			MethodWrapperDeclaration methodImpl = null;
			if (getMethod() instanceof MethodWrapperInvoked) {
				//-- use original Key!
				methodImpl = ((MethodWrapperInvoked) getMethod())
						.getCallerMethod();
			} else {
				methodImpl = (MethodWrapperDeclaration) getMethod();
			}
			if (methodImpl != null && methodImpl.getInvokedMethods() != null) {
				for (Iterator it = methodImpl.getInvokedMethods().iterator(); it
						.hasNext();) {
					MethodWrapperInvoked method = (MethodWrapperInvoked) it
							.next();
	
					for (int j = 0; j < key.length; j++) {
						// System.out.println("method " +
						// method.printMethodName() +" $$ " + key[j]);
						if (method.printMethodName().equals(key[j])) {
							// System.out.println(" ** " + method.toSearchKey()
							// + " ** ");
							getKeyMethodList().put(method, method);
						}
					}
					// System.out.println("tm.getKeyMethodList() " +
					// tm.getKeyMethodList());
				}
			} //-- fi
			//					} //--fi
		} //--fi
	}


}


