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
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.expressions.This;
import com.pavelvlasov.jsel.statements.ReturnStatement;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.Visitor;


/**
 * ER-084
 * Avoid returning "this" from public methods.
 * @author  Janos Czako
 * @version $Revision: 1.3 $
 */
public class ReturnThisFromEjbRule extends InspectorBase  {
	
	public void visit(Method method) {
		if (method.getModifiers().contains("public")) {
			TypeBody tb = method.getEnclosingType();
			try {
				if (tb.isKindOf("javax.ejb.EnterpriseBean")) {
					method.accept(new Visitor() {
						public boolean visit(Object target) {
							if (target instanceof ReturnStatement) {
								ReturnStatement returnStatement=(ReturnStatement) target;
								if (returnStatement.getExpression() instanceof This) {
									context.reportViolation((SourceMarker) returnStatement);
								}
							}
							return true;
						}							
					});		
				}
			} catch (JselException e) {
				context.warn(method, e);
			}
		}
	}	
}
