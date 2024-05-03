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

import java.util.Stack;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.expressions.Dot;
import com.pavelvlasov.jsel.expressions.Expression;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.statements.Handler;
import com.pavelvlasov.review.SourceMarker;


/**
 * ER-069
 * Do not use printStackTrace(), use logger(&lt;Message&gt;, &lt;exception&gt;) instead.
 * @author  Janos Czako
 * @version $Revision: 1.3 $
 */
public class PrintStackTraceRule extends InspectorBase  {
	
	private static final String PRINTSTACKTRACE_NAME = "printStackTrace(";
	
	/*
	 * This method can give a bad result only if a block exists inside a
	 * handler clause and this block hides the exception variable for the 
	 * handler block.
	 */
	
	private ThreadLocal handlerStack=new ThreadLocal() {
		protected Object initialValue() {
			return new Stack();
		}
	};
	
	private Stack getHandlerStack() {
		return (Stack) handlerStack.get();
	}
	
	/**
	 * Reviews the try blocks, if they contain calls 
	 * to the method printStackTrace
	 * 
	 * @param element the try block to be reviewed.
	 */
	public void visit(Handler element) {
		getHandlerStack().push(element.getParameter());
	}
	
	public void leave(Handler element) {
		getHandlerStack().pop();
	}
	
	public void visit(MethodCall methodCall) {
		if (methodCall.getParameters().isEmpty()
			|| methodCall.getParameters().size() == 1) {
			Stack stack = getHandlerStack();
			if (!stack.isEmpty()) {
				Parameter parameter = (Parameter) stack.peek();
				// e.printStackTrace -> Dot
				if (methodCall.getName() instanceof Dot) {
					Expression lastOperand =
						(Expression) methodCall.getName().getOperand(1);
					if ((lastOperand instanceof Ident)
						&& PRINTSTACKTRACE_NAME.equals(
							((Ident) lastOperand).getText())) {
						
						Expression firstOperand =
							(Expression) methodCall
								.getName()
								.getOperand(0);
						
						try {
							if ((firstOperand instanceof Ident)
								&& ((LanguageElement) methodCall)
									.getEnclosingScope()
									.getVariableNamespace()
									.find(
									((Ident) lastOperand).getText())
									== parameter) {
								context.reportViolation((SourceMarker) methodCall);
							}
						} catch (JselException e) {
							context.warn(parameter, e);
						}
					}
				}
			}
		}
	}

/*
 * This way works also in standard situations, but is not so correct,
 * like the above implemented one.
 */
 
/****************************************************************** 
	public void visit(MethodCall method) {
		String methodName = method.getName().toString();
		if ( methodName.indexOf(".printStackTrace")>0) {
			context.reportViolation(method);
		}		
	}
*******************************************************************/
	
}
