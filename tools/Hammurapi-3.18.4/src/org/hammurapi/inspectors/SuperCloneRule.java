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

import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.util.AccumulatingVisitorExceptionSink;
import com.pavelvlasov.util.DispatchingVisitor;

/**
 * ER-090
 * Call 'super.clone ()' in all 'clone ()' methods
 * @author  Janos Czako
 * @version $Revision: 1.4 $
 */
public class SuperCloneRule extends InspectorBase  {
	

	/**
	 * The chained visitor class which searches after calling super.clone().
	 */	
	public static class MethodCallSnooper {
				
		/**
		 * The list of the calls found.
		 */
		java.util.List returns=new java.util.ArrayList();
	
		/**
		 * The name of the "clone" method.
		 */
		private static final String SUPER_CLONE_CALL = "super.clone";
		
		/**
		 * Reviews the calls to super.clone()
		 * 
		 * @param call the calls to be reviewed.
		 */
		public void visit(MethodCall call) {
			String callTxt = call.getName().toString();
			if (callTxt.indexOf(SUPER_CLONE_CALL)==0 && 
				call.getParameters().size()==0) {

				returns.add(call);
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
	 * "super.clone()".
	 * 
	 * @param element the method definition to be reviewed.
	 * @throws HammurapiException In case of any exception in the chained visitor.
	 */
	public void visit(Method element) throws HammurapiException {
		if (element.getName().compareTo(CLONE_NAME)==0 &&
			element.getParameters().size()==0) {

			AccumulatingVisitorExceptionSink es = new AccumulatingVisitorExceptionSink();
			MethodCallSnooper rs = new MethodCallSnooper();
			element.accept(new DispatchingVisitor(rs, es));
	
			if(rs.returns.size()!=1) {
				context.reportViolation(element);
			}
			if (!es.getExceptions().isEmpty()) {
				es.dump();
				throw new HammurapiException(CHAINED_ERRS);
			}   	    	
		}
	}
	
}
