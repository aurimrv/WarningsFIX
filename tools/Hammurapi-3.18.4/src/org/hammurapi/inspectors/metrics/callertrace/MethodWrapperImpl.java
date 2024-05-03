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
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author Johannes Bellert
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MethodWrapperImpl {
	private String declaringType = "";
	// private SourceMarker srcMarker = null;
	private String sourceURL = "";
	private String srcLine = "1";
	private String name = "";
	private String signature = "";
	private int line = 0;
	private MethodWrapperDeclaration callerMethod = null;
	private boolean isTraced = false;
	public MethodWrapperImpl(OperationInfo _method, SourceMarker srcMrk) {
		super();
		// aMethod = _method;
		// name = _method.getName();
		signature = _method.getSignature();
		try {
			declaringType = _method.getDeclaringType().getName().toString();
		} catch (JselException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sourceURL = srcMrk.getSourceURL();
		srcLine = String.valueOf(srcMrk.getLine());
		line = srcMrk.getLine();
		// srcMarker = srcMrk;
	}
	
	// only for Test Case usage !!
	public MethodWrapperImpl(String _name, String _declType) {
		super();
		signature = _name;
		declaringType = _declType;
	}
	//-- for initial search and test cases
	public MethodWrapperImpl(String _name) {
		super();
		// aMethod = _method;
		name = _name;
		signature = _name;
		declaringType = "";
	}
	public boolean equals(Object obj) {
		return this.getMethodKey().equals(((MethodWrapper) obj).getMethodKey());
	}
	public boolean isTraced() {
		return isTraced;
	}
	public void setTracedTrue() {
		isTraced = true;
	}
	public String getDeclaringType() {
		return declaringType;
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
	 * @return Returns the line.
	 */
	public int getLine() {
		return line;
	}
	public void setLine(int i) {
		line = i;
		return;
	}
	public String getSrcURL() {
		return sourceURL;
	}
	public String getSrcLine() {
		return srcLine;
	}
	/**
	 * @return Returns the signature.
	 */
	public String getSignature() {
		return signature;
	}
	/**
	 * @return Returns the sourceURL.
	 */
	public String getSourceURL() {
		return sourceURL;
	}
	public String getMethodKey() {
		// return declaringType + ">>"+signature;
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(String.valueOf(this.hashCode()));
		sb.append(")");
		sb.append(declaringType);
		sb.append(">>");
		sb.append(signature);
		return sb.toString();
	}
	
	public String printMethodName() {
		// return declaringType + ">>"+signature;
		StringBuffer sb = new StringBuffer();
		
		sb.append(declaringType);
		sb.append(">>");
		sb.append(signature);
		return sb.toString();
	}
	
	public String toSearchKey() {
		// return name;
		// return declaringType + ">>"+signature;
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(String.valueOf(this.hashCode()));
		sb.append(")");
		sb.append(declaringType);
		sb.append(">>");
		sb.append(signature);
		return sb.toString();
	}
	public String toString() {
		// return this.getMethodKey();
		StringBuffer sb = new StringBuffer();
		sb.append(declaringType);
		sb.append(">>");
		sb.append(signature);
		return sb.toString();
	}
}
