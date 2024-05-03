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
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Dot;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.This;
import com.pavelvlasov.review.SourceMarker;


/**
 * ER-088
 * Avoid using an object to access "static" fields or methods
 * @author  Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class AccessToStaticMembersRule extends InspectorBase  {

	/**
	 * The modifier static
	 */
	private static final String STATIC = "static";

	/**
	 * Reviews if the two side of the dot violates against the rule.
	 * (non static on the left and static on the right.)
	 * 
	 * @param dot the dot to be reviewed
	 */
	public void visit(Dot dot) {
		Object dot1 = dot.getOperand(1);
		if (dot1 instanceof Ident) {
			try {
				Object provider = ((Ident) dot1).getProvider();
				boolean isStatic = false;
				if (provider instanceof Method) {
					if (((Method) provider).getModifiers().contains(STATIC)) {
						isStatic = true;
					}
				} else if (provider instanceof VariableDefinition) {
					if (((VariableDefinition) provider).getModifiers().contains(STATIC)) {
						isStatic = true;
					}
				}
	
				if (isStatic) {
					Object dot0 = dot.getOperand(0);
					Ident user = null;
					if (dot0 instanceof This) {
						context.reportViolation((SourceMarker)dot1);
					} else if (dot0 instanceof Ident) {
						user = (Ident) dot0;
					} else if (dot0 instanceof Dot) {
						user = (Ident) ((Dot)dot0).getOperand(1);
					}
					if (user!=null) {
						provider = user.getProvider();
						if (provider instanceof VariableDefinition) {
							if (!((VariableDefinition)provider).getModifiers().contains(STATIC)) {
								context.reportViolation((SourceMarker)dot1);
							}
						}
					}
				}
			} catch (JselException e) {
				context.warn((SourceMarker)dot, e);
			} 
		}
	}
}
