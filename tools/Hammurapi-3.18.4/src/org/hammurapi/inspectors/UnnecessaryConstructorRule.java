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

import com.pavelvlasov.jsel.Constructor;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.statements.Statement;
import com.pavelvlasov.jsel.statements.SuperConstructorCall;
import com.pavelvlasov.util.AccumulatingVisitorExceptionSink;
import com.pavelvlasov.util.DispatchingVisitor;

/**
 * @author Janos Czako
 * 
 * <description>Unnecessary constructor detects when a constructor is not
 * necessary; i.e., when there's only one constructor, it's public, has an
 * empty body, and takes no arguments. (PMD) </description>
 * 
 * Example:
 * 	public DaoX(){
		super();
	}

 */
public class UnnecessaryConstructorRule extends InspectorBase {

	/**
 	* The chained visitor class which searches after constructor definitions.
 	*/	
	public static class ConstructorSnooper {
		
		/**
		 * The list of the constructor definitions found.
		 */
		java.util.List constList = new java.util.ArrayList();
			
		/**
		 * Reviews the constructor definitions and collects them.
		 * 
		 * @param constructor the constructor definition
		 */
		public void visit(Constructor constructor) {
			constList.add(constructor);
		}
	}
	
	/**
	 * The error text for exceptions in the chained visitor.
	 */
	private static final String CHAINED_ERRS = 
		"There have been exceptions (see above)";


	/**
	 * Reviews the type definition if it violates against the rule.
	 * 
	 * @param element the type definition to be reviewed.
	 * @throws HammurapiException in case of any exception in the chained visitor
	 */
	public void visit(TypeDefinition element) throws HammurapiException {
		AccumulatingVisitorExceptionSink es = new AccumulatingVisitorExceptionSink();
		ConstructorSnooper rs= new ConstructorSnooper();
		element.accept(new DispatchingVisitor(rs, es));

		if (rs.constList.size()==1) {
			Constructor constructor =
				(Constructor) rs.constList.get(0);
			
			checkConstructor(constructor);
		}

		if (!es.getExceptions().isEmpty()) {
			es.dump();
			throw new HammurapiException(CHAINED_ERRS);
		}
	}

	/**
	 * "public" modifier of the constructor
	 */	
	private static final String PUBLIC = "public";
	
	/**
	 * Checks if the constructor violates against the rule.
	 */
	private void checkConstructor(Constructor constructor) {
		if (constructor.getModifiers().contains(PUBLIC) &&
			constructor.getParameters().size()==0) {

			if (isEmpty(constructor.getCompoundStatement())) {
				context.reportViolation(constructor);
			}
			else {
				java.util.Iterator statements = 
					constructor.getCompoundStatement().
						getStatements().iterator();
				int cntr = 0;
				while (statements.hasNext()) {
					Statement statement = (Statement) statements.next();
					if (!(statement instanceof SuperConstructorCall)) {
						cntr++; 
					}
				}
				if (cntr==0) {
					context.reportViolation(constructor);
				}
			}
		}
	}
}
