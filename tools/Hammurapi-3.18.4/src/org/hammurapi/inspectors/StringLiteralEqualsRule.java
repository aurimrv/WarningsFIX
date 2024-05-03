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

import java.util.List;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.TypeSpecification;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Dot;
import com.pavelvlasov.jsel.expressions.Expression;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.PrimaryExpression;
import com.pavelvlasov.jsel.expressions.StringConstant;
import com.pavelvlasov.review.SourceMarker;


/**
 * ER-123
 * If you have to compare with a string do not use  degree.equals("1"))  but "1".equals(degree)
 * @author  Janos Czako
 * @version $Revision: 1.2 $
 */
public class StringLiteralEqualsRule extends InspectorBase  {
	
	/**
	 * The name of the operation, we are checking.
	 */
	private static final String EQUALS = "equals";
	
	/**
	 * Reviews the methodcalls if they violate against the rule.
	 * 
	 * @param element the methodcall to be reviewed.
	 */
	public void visit(MethodCall element) {
		PrimaryExpression methodName = element.getName();
		if (methodName instanceof Dot) {
			List flatOperands = ((Dot) methodName).getFlatOperands();
			methodName=(PrimaryExpression) flatOperands.get(flatOperands.size()-1);
		}
		
		List parameters = element.getParameters();
		if (methodName instanceof Ident && EQUALS.equals(((Ident) methodName).getText()) && parameters.size()==1) {
			Expression parameter = (Expression) parameters.get(0);
			if (parameter instanceof StringConstant) {
				context.reportViolation((SourceMarker) parameter);
			} else if (parameter instanceof Ident) {		
				try {
					// TODO analyze if parameter is a final string constant from another type like MyClass.MY_CONSTANT
					Object r=((LanguageElement) parameter).getEnclosingScope().getVariableNamespace().find(((Ident) parameter).getText());
					if (r instanceof VariableDefinition) {
						VariableDefinition vd=(VariableDefinition) r;
						TypeSpecification typeSpecification = vd.getTypeSpecification();
						try {
							if (vd.getModifiers().contains("final") && vd.getInitializer()!=null && typeSpecification.getDimensions()==0 && typeSpecification.getType().isKindOf("java.lang.String")) {
								context.reportViolation((SourceMarker) parameter);
							}
						} catch (JselException e) {
							context.warn((SourceMarker) typeSpecification, e);
						}
					}
				} catch (JselException e) {
					context.warn((SourceMarker) parameter, e);					
				}
			}
		}
	}
	
}
