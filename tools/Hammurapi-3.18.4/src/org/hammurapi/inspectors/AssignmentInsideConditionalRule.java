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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.expressions.AssignmentExpression;
import com.pavelvlasov.jsel.expressions.ConditionalExpression;
import com.pavelvlasov.jsel.expressions.LogicalOr;
import com.pavelvlasov.jsel.statements.IfStatement;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.AccumulatingVisitorExceptionSink;
import com.pavelvlasov.util.DispatchingVisitor;
import com.pavelvlasov.util.Visitable;

/**
 * Assignment statements shouldn't be placed inside conditions of if statements.
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class AssignmentInsideConditionalRule extends InspectorBase {
  
  /**
   * Inner class for making chained visit. It searches after
   * assignment statements.
   * 
   * @author Pavel Vlasov
   */
	public static class AssignmentSnooper {
		List assignments=new LinkedList();
		
    /**
     * Reviews the assignment expression.
     * 
     * @param expression the expression to be reviewed.
     */
		public void visit(AssignmentExpression expression) {
			if (!(expression instanceof LogicalOr || expression instanceof ConditionalExpression)) {
				assignments.add(expression);
			}
		}
	}
    
    /**
     * Reviews the if statement, if it contains in its condition
     * an assignment statement.
     * 
     * @param statement the if statement to be reviewed.
     * @throws HammurapiException the exceptions occured in the Hammurapi helper classes.
     */
    public void visit(IfStatement statement) throws HammurapiException {
    	AssignmentSnooper snooper=new AssignmentSnooper();
    	AccumulatingVisitorExceptionSink es=new AccumulatingVisitorExceptionSink();
    	DispatchingVisitor dv=new DispatchingVisitor(snooper, es);
    	((Visitable) statement.getExpression()).accept(dv);
    	
    	Iterator it=snooper.assignments.iterator();
    	while (it.hasNext()) {
    		context.reportViolation((SourceMarker) it.next());
    	}
    	
    	if (!es.getExceptions().isEmpty()) {
    		es.dump();
    		throw new HammurapiException("There have been exceptions (see above)");
    	}   	    	
    }
}
