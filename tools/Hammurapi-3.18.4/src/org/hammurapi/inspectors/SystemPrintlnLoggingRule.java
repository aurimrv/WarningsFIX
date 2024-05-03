/*
 * Created on 01.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.hammurapi.inspectors;
import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.impl.expressions.MethodCallImpl;
/**
 * @author Johannes
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class SystemPrintlnLoggingRule extends InspectorBase {
	public void visit(MethodCallImpl method) {
		/*		List l = method.getName().getOperands();
		Expression serviceClass = (Expression) l.get(0);
		Expression serviceVar = (Expression) l.get(1);
		Expression methodName = (Expression) l.get(2);
		if (serviceClass != null
				&& serviceVar != null
				&& methodName != null
				&& "System".equals(serviceClass.toString())
				&& "out".equals(serviceVar.toString())
				&& ("println".equals(methodName.toString()) || "print"
						.equals(methodName.toString()))) {
			context.reportViolation(method);
		}*/
		
		if ( "System.out.println".equals( method.getName().toString() ) ) {
			context.reportViolation(method);
		}		
	}
}
