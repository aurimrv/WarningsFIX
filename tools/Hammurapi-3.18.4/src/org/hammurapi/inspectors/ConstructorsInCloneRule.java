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
import org.hammurapi.HammurapiException;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.TypeSpecification;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.NewObject;
import com.pavelvlasov.jsel.statements.CompoundStatement;
import com.pavelvlasov.jsel.statements.Statement;
import com.pavelvlasov.util.AccumulatingVisitorExceptionSink;
import com.pavelvlasov.util.DispatchingVisitor;

/**
 * ER-089
 * Avoid using constructors in the 'clone ()' method
 * @author  Janos Czako
 * @version $Revision: 1.4 $
 */
public class ConstructorsInCloneRule extends InspectorBase  {
	
	/**
	 * The chained visitor class which searches after calling new on the
	 * visited class.
	 */	
	public static class NewSnooper {
		
		/**
		 * The name of the class (not full quailified), the reviewed clone()
		 * method belongs to.
		 */
		private String classNameToReview;
		
		/**
		 * Constructor which takes the name of the visited class.
		 * @param theClassName the name of the class to be reviewed.
		 */
		NewSnooper(String theClassName) {
			classNameToReview = theClassName;
		}
		
		/**
		 * The list of the new calls found.
		 */
		java.util.List returns=new java.util.ArrayList();
		
		/**
		 * Reviews the calls to new, if they are referred to the class to be
		 * reviewed.
		 * 
		 * @param newCall the calls to new.
		 */
		public void visit(NewObject newCall) throws JselException {
			TypeSpecification ts = newCall.getTypeSpecification();
			if (classNameToReview.equals(ts.getType().getName())) {
				returns.add(newCall);
			}
		}
	}
	
	/**
	 * The name of the "clone" method.
	 */
	private static final String CLONE_NAME = "clone";
	
	/**
	 * The error text for exceptions in the chained visitor.
	 */
	private static final String CHAINED_ERRS = 
		"There have been exceptions (see above)";
	
	/**
	 * Reviews the method definitions, if they are "clone()" and call 
	 * constructor.
	 * 
	 * @param element the method definition to be reviewed.
	 * @throws HammurapiException In case of any exception in the chained visitor.
	 */
	public void visit(Method element) throws HammurapiException {
		if (CLONE_NAME.equals(element.getName()) &&	element.getParameters().isEmpty()) {
			CompoundStatement compoundStatement = element.getCompoundStatement();
			if (compoundStatement!=null) {
				java.util.Iterator statements =	compoundStatement.getStatements().iterator();
					
				while (statements.hasNext()) {
					Statement statement = (Statement) statements.next();
					if (statement instanceof VariableDefinition) {
						AccumulatingVisitorExceptionSink es = new AccumulatingVisitorExceptionSink();
						NewSnooper rs = new NewSnooper(element.getEnclosingType().getFcn());
						element.accept(new DispatchingVisitor(rs, es));
			
						if( !rs.returns.isEmpty()) {
							context.reportViolation(element);
						}
						if (!es.getExceptions().isEmpty()) {
							es.dump();
							throw new HammurapiException(CHAINED_ERRS);
						}   	    	
					}
				}
			}
		}
	}
}
