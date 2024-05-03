/*
 * Created on 27.02.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author mucbj0, modified by c8982
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SystemGcMisuseRule extends InspectorBase {
		
	public void visit(MethodCall methodCall) {
		if ("System.gc(".equals(methodCall.toString())) {
			context.reportViolation((SourceMarker) methodCall);
		}
    }	
}

