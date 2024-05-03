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

import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.expressions.Dot;
import com.pavelvlasov.jsel.expressions.Equal;
import com.pavelvlasov.jsel.expressions.Expression;
import com.pavelvlasov.jsel.expressions.IntegerConstant;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.PrimaryExpression;

/**
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.4 $
 */
public class UseEqualsInsteadOfCompareToInspector extends InspectorBase {
	
	public void visit(MethodCall methodCall) {
		PrimaryExpression name = methodCall.getName();
		if (name instanceof Dot) {
			name=(PrimaryExpression) name.getOperand(1); 
		}
		
		if ("compareTo".equals(name.toString())	&& methodCall.getParameters().size()==1) {		
			LanguageElement parent = ((LanguageElement) methodCall).getParent();
			if (parent instanceof Equal) {
				Object theOtherOperand=((Expression) parent).getOperand(0);
				if (theOtherOperand==methodCall) {
					theOtherOperand=((Expression) parent).getOperand(1);
				}
				
				if (theOtherOperand instanceof IntegerConstant 
						&& ((IntegerConstant) theOtherOperand).getValue()==0) {
					context.reportViolation(parent);
				} 
			}
		}		
	}
}
