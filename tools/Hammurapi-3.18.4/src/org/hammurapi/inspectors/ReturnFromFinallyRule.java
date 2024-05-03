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

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;

import com.pavelvlasov.jsel.statements.ReturnStatement;
import com.pavelvlasov.jsel.statements.TryBlock;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.AccumulatingVisitorExceptionSink;
import com.pavelvlasov.util.DispatchingVisitor;
import com.pavelvlasov.util.Visitable;

/**
 * Avoid return in finally block.
 * @author  Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class ReturnFromFinallyRule extends InspectorBase {
	
  /**
   * Inner class fro creating a visit chain. It collects the return statements
   * into the LinkedList returns.
   */
	public static class ReturnSnooper {
    /**
     * The LinkedList containing the return statements found.
     */
		List returns=new LinkedList();
		
    /**
     * Reviews the return statement and adds to the list.
     * @param statement the return statement to be reviewed.
     */
		public void visit(ReturnStatement statement) {
			returns.add(statement);
		}
	}
	
	
    /**
     * Reviews the try blokk. 
     * Searches after return statements in the finnally part.
     * 
     * @param tryBlock the try blokk to be reviewed.
     * @throws HammurapiException error when parsing the code in jSel.
     */
    public void visit(TryBlock tryBlock) throws HammurapiException {
    	if (tryBlock.getFinallyClause()!=null) {
    		AccumulatingVisitorExceptionSink es=new AccumulatingVisitorExceptionSink();
    		ReturnSnooper rs=new ReturnSnooper();
    		((Visitable) tryBlock.getFinallyClause()).accept(new DispatchingVisitor(rs, es));
    		
    		Iterator it=rs.returns.iterator();
    		while (it.hasNext()) {
    			context.reportViolation((SourceMarker) it.next());
    		}
    		
    		if (!es.getExceptions().isEmpty()) {
    			es.dump();
    			throw new HammurapiException("There have been exceptions (see above)");
    		}   	    	
    	}
    }
}
