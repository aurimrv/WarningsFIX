/*
 * Created on 27.02.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.hammurapi.inspectors;

import java.util.LinkedList;
import java.util.List;

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;

import com.pavelvlasov.jsel.Constructor;
import com.pavelvlasov.jsel.statements.SuperConstructorCall;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.AccumulatingVisitorExceptionSink;
import com.pavelvlasov.util.DispatchingVisitor;

/**
 * @author Johannes
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ConstructorWithoutSuperRule extends InspectorBase {
	
	public static class SuperSnooper {
		List returns=new LinkedList();
		
		public void visit(SuperConstructorCall superCall) {
			returns.add(superCall);
		}
	}
	
	public void visit(Constructor construct )throws HammurapiException { {
    	if (construct!=null) {
    		AccumulatingVisitorExceptionSink es=new AccumulatingVisitorExceptionSink();
    		SuperSnooper rs=new SuperSnooper();
    		construct.accept(new DispatchingVisitor(rs, es));
    		
    		if( rs.returns.isEmpty()) {
    			context.reportViolation((SourceMarker)construct);
    		}
    		if (!es.getExceptions().isEmpty()) {
    		es.dump();
    			throw new HammurapiException("There have been exceptions (see above)");
    		}   	    	
    	}
    }
	}
    
}
