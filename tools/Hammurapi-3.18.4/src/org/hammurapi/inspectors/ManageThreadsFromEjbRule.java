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

import java.util.HashSet;
import java.util.Set;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.NewObject;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.Visitor;


/**
 * ER-085
 * Avoid starting, stopping, or managing threads in any way
 * @author  Janos Czako
 * @version $Revision: 1.3 $
 */
public class ManageThreadsFromEjbRule extends InspectorBase  {

	/**
	 * method which is violations
	 */
	private Set allowedMethods=new HashSet();
	
	/**
	 * Type which's method must not be used.
	 */
	private static final String VIOLATION_CLASS = "java.lang.Thread";

	/**
	 * Reviews the type definition if it is an Enterprise Bean and in that case
	 * if it violates against the rule.
	 * 
	 * @param clazz the class object of the type definition
	 */
	public void visit(final com.pavelvlasov.jsel.Class clazz) {
		try {
			if (clazz.isKindOf("javax.ejb.EnterpriseBean")) {			
				clazz.accept(new Visitor() {
					public boolean visit(Object target) {
						if (target instanceof MethodCall) {
							try {
								OperationInfo provider=((MethodCall) target).getProvider();
								
								if (provider==null) {
									context.warn((SourceMarker) target, "Provider is null for "+target+" at "+((LanguageElement) target).getLocation());
								} else {
									if (provider.getDeclaringType().isKindOf(VIOLATION_CLASS) && !allowedMethods.contains(provider.getSignature())) {
										context.reportViolation((SourceMarker) target);
									}
								}
							} catch (JselException e) {
								context.warn((SourceMarker) target, e);
							}
						} else if (target instanceof NewObject) {
							NewObject newObject=(NewObject) target;
							try {
								if (newObject.getTypeSpecification().getType().isKindOf(VIOLATION_CLASS)) {
									context.reportViolation((SourceMarker) target);																		
								}
							} catch (JselException e) {
								context.warn((SourceMarker) target, e);
							}
						}
						return true;
					}
				});
			}
		} catch (JselException e) {
			context.warn(clazz, e);
		}
	}

	// TODO Make this inspector parameterizable and accept allowed thread methods, e.g. getName()
}
