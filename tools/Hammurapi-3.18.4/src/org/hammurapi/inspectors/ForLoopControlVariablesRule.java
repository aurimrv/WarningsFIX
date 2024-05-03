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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorContext;

import com.pavelvlasov.jsel.Declaration;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.AssignmentExpression;
import com.pavelvlasov.jsel.expressions.ConditionalExpression;
import com.pavelvlasov.jsel.expressions.Expression;
import com.pavelvlasov.jsel.expressions.ExpressionList;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.LogicalOr;
import com.pavelvlasov.jsel.statements.ForStatement;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.AccumulatingVisitorExceptionSink;
import com.pavelvlasov.util.DispatchingVisitor;
import com.pavelvlasov.util.Visitable;

/**
 * ER-098
 * Do not assign loop control variables in the body of a "for" loop
 * @author  Janos Czako
 * @author Pavel Vlasov
 * @version $Revision: 1.5 $
 */
public class ForLoopControlVariablesRule extends InspectorBase  {
	
	/**
	 * The chained visitor class which searches after assigning 
	 * the loop control variable.
	 */	
	public static class Snooper {

		/**
		 * The name of the loop control variable, searched for.
		 */
		private Collection names;
		private InspectorContext context;		
		
		/**
		 * Constructor, takes the name searched for.
		 * 
		 * @param theIdentName the name of the variable searched for.
		 */
		public Snooper(final Collection names, InspectorContext context) {
			this.names=names;
			this.context=context;
		}

		/**
		 * Reviews the assignments.
		 * 
		 * @param element the assignment to be reviewed.
		 */
		public void visit(AssignmentExpression element) {
			if (!(element instanceof LogicalOr || element instanceof ConditionalExpression)) {
				if (element.getOperand(0) instanceof Ident) {
					Ident ident = (Ident) element.getOperand(0);
					if (names.contains(ident.getText())) {
						context.reportViolation((SourceMarker) element);
					}
				}
			}
		}
	}
	
	/**
	 * The error text for exceptions in the chained visitor.
	 */
	private static final String CHAINED_ERRS = "There have been exceptions (see above)";

	/**
	 * Reviews the for statement if it violates agains the rule
	 * 
	 * @param element the fro statement
	 * @throws HammurapiException in case of any exception in the chained visitor
	 */	
	public void visit(ForStatement element) throws HammurapiException {
		Collection names=new HashSet();
		if (element.getInitializer()!=null) {
			if (element.getInitializer() instanceof Declaration) {
				getNames((Declaration) element.getInitializer(), names);
			} else if (element.getInitializer() instanceof ExpressionList) {
				getNames((ExpressionList) element.getInitializer(), names);
			}
		}
		
		if (!names.isEmpty()) {
			Snooper rs = new Snooper(names, context);
			// TODO Don't use DispatchingVisitor, use plain visitor
			// TODO Use Scope.getVariableNamespace().find() to make sure that
			// first operand of assignment expression is control variable
			AccumulatingVisitorExceptionSink es = new AccumulatingVisitorExceptionSink();
			((Visitable) element.getStatement()).accept(new DispatchingVisitor(rs, es));
	
			if (!es.getExceptions().isEmpty()) {
				es.dump();
				throw new HammurapiException(CHAINED_ERRS);
			}   	    	
		}
	}
	
	/**
	 * Gets the control variable name in for loop 
	 * if it is declared in the for statement
	 * 
	 * @param element the declaration
	 * @return the name of the variable
	 */
	private void getNames(Declaration element, Collection names) {
		Iterator it=element.iterator();
		while (it.hasNext()) {
			VariableDefinition vd = (VariableDefinition) it.next();
			names.add(vd.getName());
		}
	}
	
	/**
	 * Gets the control variable name in for loop 
	 * if it is declared not in the for statement
	 * 
	 * @param element the assigment expression from the for statement
	 * @return the assignment expression
	 */
	private void getNames(ExpressionList element, Collection names) {
		Iterator it=element.iterator();
		while (it.hasNext()) {
			Object o=it.next();
			if (o instanceof AssignmentExpression
					&& !(o instanceof LogicalOr || o instanceof ConditionalExpression)
					&& ((Expression) o).getOperand(0) instanceof Ident) {
				names.add(((Ident) ((Expression) o).getOperand(0)).getText());
			}
		}
	}
}
