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
 *  * Created on Apr 18, 2004
 *
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author Johannes Bellert
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MethodWrapperDeclaration extends MethodWrapperImpl implements MethodWrapper {
	private boolean called = false;
	private HashSet invokedMethods = new HashSet();
	public int afferentMethodCoupling = 0;
	public int efferentMethodCoupling = 0;

	
	public MethodWrapperDeclaration(OperationInfo _method, SourceMarker srcMrk){
		super( _method, srcMrk);
		}

	// only for Test Case usage !!
	public MethodWrapperDeclaration(String _name, String _declType){
		super(_name, _declType);
	}

	public boolean isCalled(){
		return called;
	}
	
	public void setCalled(){
		called = true;
	}
	
	/*
	public OperationInfo getOperationInfo(){
		return aMethod;
	}
	*/
	public boolean equals(Object obj ){
		return this.getMethodKey().equals( ((MethodWrapper)obj).getMethodKey() );
	}

	/**
	 * @return Returns the invokedMethods.
	 */
	public HashSet getInvokedMethods() {
		return invokedMethods;
	}
	
	public Element toDom(Document document){
		Element ret=document.createElement("MethodWrapperDeclaration");
		ret.setAttribute("id", String.valueOf( this.hashCode() ));
		ret.setAttribute("called", String.valueOf(this.isCalled()));
		ret.setAttribute("key", printMethodName());
		ret.setAttribute("source-url", getSrcURL());
		ret.setAttribute("line", String.valueOf(getSrcLine()));
		ret.setAttribute("Ma", String.valueOf(afferentMethodCoupling));
		ret.setAttribute("Me", String.valueOf(this.invokedMethods.size()));
	/*
		Enumeration enum = this.invokedMethods.elements();
		
		while(enum.hasMoreElements()){
			MethodWrapperDeclaration mwd = (MethodWrapperDeclaration)enum.nextElement();
			
			ret.appendChild(mwd.toDom( document ));
		}
		*/
		return ret;
	}	
}
