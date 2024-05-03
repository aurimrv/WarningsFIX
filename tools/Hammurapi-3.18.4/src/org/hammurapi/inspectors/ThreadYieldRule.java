/*
 * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Hammurapi Group
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
 * URL: http://www.hammurapi.org
 * e-Mail: support@hammurapi.biz

 */
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.review.SourceMarker;

/**
 * ER-106
 * Avoid using 'Thread.yield'
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class ThreadYieldRule extends InspectorBase  {

	/**
	 * yield belongs to this class
	 */
	private static final String VIOLATION_CLASS = "java.lang.Thread";
	
	/**
	 * The method yield is the subject of this rule.
	 */
	private static final String VIOLATION_METHOD = "yield";
	
	/**
	 * Reviews the methodcalls to yield, since they are violations.
	 * 
	 * @param element the methodcall to be reviewed.
	 */
	public void visit(MethodCall element) {
		try {
			OperationInfo provider=element.getProvider();
			if (provider==null) {
				context.warn((SourceMarker) element, "Provider is null for "+element+" at "+((LanguageElement) element).getLocation());
			} else if (VIOLATION_METHOD.equals(provider.getName()) && provider.getParameterTypes().length==0 && provider.getDeclaringType().isKindOf(VIOLATION_CLASS)) {
				context.reportViolation((SourceMarker) element);
			}					
		} catch (JselException e) {
			context.warn((SourceMarker) element, e);
		}		
	}

}
