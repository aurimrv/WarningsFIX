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

import com.pavelvlasov.jsel.Constructor;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.Visitor;


/**
 * ER-071
 * Avoid calling an "abstract" method from a constructor in an "abstract" class
 * @author  Janos Czako
 * @version $Revision: 1.3 $
 */
public class AbstractMethodFromConstructorRule extends InspectorBase  {

	/**
	 * The abstract modifier.
	 */	
	private static final String ABSTRACT = "abstract";
	
	/**
	 * Reviews the constructor definitions if they violate against
	 * the rule;
	 * 
	 * @param element the constructor definition.
	 */
	public void visit(Constructor element) {
		element.accept(new Visitor() {
			public boolean visit(Object target) {
				if (target instanceof MethodCall) {
					MethodCall mc = (MethodCall) target;
					try {
						OperationInfo oi = mc.getProvider();
						if (oi==null) {
							context.warn((SourceMarker) mc, "Provider is null for "+mc+" at "+((LanguageElement) mc).getLocation());
						} else if (oi.getOperation() instanceof Method) {
							Method method = (Method) oi.getOperation();
							if (method.getModifiers().contains(ABSTRACT) && method.getEnclosingType()==((LanguageElement) mc).getEnclosingType()) {
								context.reportViolation((SourceMarker) mc);
							}
						}
					} catch (JselException e) {
						context.warn((SourceMarker) mc, e);
					}
				}
				return true;
			}
		});
	}
}
