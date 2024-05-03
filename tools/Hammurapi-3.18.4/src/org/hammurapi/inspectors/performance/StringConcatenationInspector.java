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
package org.hammurapi.inspectors.performance;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Expression;
import com.pavelvlasov.jsel.expressions.Parenthesis;
import com.pavelvlasov.jsel.expressions.Plus;
import com.pavelvlasov.jsel.expressions.PlusAssignment;
import com.pavelvlasov.jsel.expressions.StringConstant;
import com.pavelvlasov.jsel.expressions.TypeCast;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class StringConcatenationInspector extends InspectorBase {
	
	public void visit(Plus element) throws JselException {
		LanguageElement parent = ((LanguageElement) element).getParent();
		if (parent instanceof VariableDefinition && ((VariableDefinition) parent).getModifiers().contains("static")) {
			return;
		}
		
		if (element.getTypeSpecification().isKindOf("java.lang.String")) {
			analyze(element, (Expression) element.getOperand(0), (Expression) element.getOperand(1));
		}
	}
	
	public void visit(PlusAssignment element) throws JselException {
		if (((Expression) element.getOperand(0)).getTypeSpecification().isKindOf("java.lang.String")) {
			analyze(element, (Expression) element.getOperand(0), (Expression) element.getOperand(1));
		}
	}
	
	private void analyze(Expression e, Expression e1, Expression e2) {
		// Do not report single plus like a+b, only more than one plus.
		// and do not report constants concatenation like "a"+"b"+"c"
		if ((e1 instanceof Plus || e2 instanceof Plus) && !(isConstant(e1) && isConstant(e2))) {
			context.reportViolation((SourceMarker) e);
		}
	}
	
	private boolean isConstant(Expression expr) {
		return peel(expr) instanceof StringConstant
				|| (peel(expr) instanceof Plus
						&& isConstant(expr.getOperand(0)) 
						&& isConstant(expr.getOperand(1)));
	}
	
	private Expression peel(Expression expr) {
		if (expr instanceof TypeCast) {
			return peel(((TypeCast) expr).getExpression());
		} else if (expr instanceof Parenthesis) {
			return peel((Expression) expr.getOperand(0));
		} else {
			return expr;
		}
	}
}
