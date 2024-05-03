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

import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.expressions.Dot;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.PrimaryExpression;
import com.pavelvlasov.jsel.statements.WhileStatement;
import com.pavelvlasov.review.SourceMarker;

/**
 * ER-111
 * Use 'wait ()' and 'notifyAll ()' instead of polling loops
 * @author  Janos Czako
 * @version $Revision: 1.2 $
 */
public class WaitAndNotifyInsteadOfPollingRule extends InspectorBase  {
	
	/**
	 * The name of the operation, we are checking.
	 */
	private static final String SLEEP = "sleep";
	
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
		if (methodName instanceof Ident && SLEEP.equals(((Ident) methodName).getText()) && parameters.size()==1) {
			if (checkForWhileAsParent((LanguageElement) element)) {
				context.reportViolation((SourceMarker) element);
			}
		}
	}
	
	/**
	 * Checks if the element is inside of a while loop (or is one).
	 * 
	 * @param le the element to be checked
	 * @return if it is inside a while loop
	 */	
	private boolean checkForWhileAsParent(LanguageElement le) {
		boolean foundWhile = le instanceof WhileStatement;
		
		while (!foundWhile && le.getParent()!=null) {
			le = le.getParent();
			foundWhile = le instanceof WhileStatement;
	}
	
		return foundWhile;
	}
}
