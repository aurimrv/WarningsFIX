/*
 * Created on Sep 15, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.jsel.Method;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MethodMap extends Hashtable {

	public void addAll( Hashtable ht ){
		Enumeration enumk = ht.keys();
		Enumeration enumv = ht.elements();
		while (enumv.hasMoreElements()) {
			Object k = enumk.nextElement();
			Method m = (Method)enumv.nextElement();
			this.put( k , m);
	}}

	public Vector selectMethodsByName(String methodName) {
		Vector vc = new Vector();
		Enumeration enumxx = this.elements();
		while (enumxx.hasMoreElements()) {
			Method mi = (Method) enumxx.nextElement();
			if (mi.getName().equals(methodName)) {
				vc.add(mi);
			}
		}
		return vc;
	}

	public MethodWrapper selectMethodsWithoutHashkey(String methodName) {
		
		Enumeration enumxx = this.elements();
		while (enumxx.hasMoreElements()) {
			MethodWrapper mi = (MethodWrapper) enumxx.nextElement();
			String myKey = AdjacencyMatrix.nodeNameWithoutHashcode(mi.getMethodKey());
			if (myKey.equals(methodName)) {
			return mi;
			}
		}
		return null;
		}

	public MethodWrapper selectMethodsBothWithoutHashkey(String methodName) {
		
		Enumeration enumxx = this.elements();
		while (enumxx.hasMoreElements()) {
			MethodWrapper mi = (MethodWrapper) enumxx.nextElement();
			String myKey = AdjacencyMatrix.nodeNameWithoutHashcode(mi.getMethodKey());
			if (myKey.equals(AdjacencyMatrix.nodeNameWithoutHashcode(methodName))) {
			return mi;
			}
		}
		return null;
		}
	
	public Vector selectConstructors() {
		Vector vc = new Vector();
		Enumeration enumxx = this.elements();
		while (enumxx.hasMoreElements()) {
			Method mi = (Method) enumxx.nextElement();
		//	if (mi instanceof Constructor) {
		//		vc.add(mi);
		//	}
		}
		return vc;
	}
	
	public Element toDom(Document document){
		Element ret=document.createElement("ImplementedMethodList");
		ret.setAttribute("size", String.valueOf( this.size() ));
		
		Enumeration enum = this.elements();
		Enumeration enumKey = this.keys();
		while(enum.hasMoreElements()){
			String key = (String)enumKey.nextElement();
			MethodWrapperDeclaration mwd = (MethodWrapperDeclaration)enum.nextElement();
			
			ret.appendChild(mwd.toDom( document ));
		}
		return ret;
	}
}
