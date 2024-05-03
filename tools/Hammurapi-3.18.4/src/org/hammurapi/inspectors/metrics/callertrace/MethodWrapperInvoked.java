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
 * URL: http://www.hammurapi.com
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Apr 18, 2004
 *
 */
package org.hammurapi.inspectors.metrics.callertrace;

import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MethodWrapperInvoked extends MethodWrapperImpl implements MethodWrapper{
	private MethodWrapperDeclaration callerMethod = null;
	
	public MethodWrapperInvoked( OperationInfo _method, SourceMarker srcMrk){
		super( _method, srcMrk);
	}

	//-- for initial search and test cases
	public MethodWrapperInvoked( String _name){
		super(_name);
		}

	
	public MethodWrapperDeclaration getDeclarationMethod(){
		return null;
	}

	
	/**
	 * @return Returns the callerMethod.
	 */
	public MethodWrapperDeclaration getCallerMethod() {
		return callerMethod;
	}
	/**
	 * @param callerMethod The callerMethod to set.
	 */
	public void setCallerMethod(MethodWrapperDeclaration callerMethod) {
		this.callerMethod = callerMethod;
	}
}
