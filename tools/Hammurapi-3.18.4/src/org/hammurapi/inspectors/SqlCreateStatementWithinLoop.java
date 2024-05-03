/*
 * Created on Oct 21, 2004
 *
 */
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.Code;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Operation;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.statements.DoStatement;
import com.pavelvlasov.jsel.statements.ForStatement;
import com.pavelvlasov.jsel.statements.WhileStatement;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author Johannes Bellert
 *
 */
public class SqlCreateStatementWithinLoop extends InspectorBase {
        
    public boolean visit(MethodCall target) throws JselException {
		if (context.getVisitorStack().isIn(new Class[] { ForStatement.class, DoStatement.class, WhileStatement.class })) {
			context.verbose((SourceMarker) target, " target " + target.toString());
			if ("createStatement".equals(target.getMethodName()) || "prepareStatement".equals(target.getMethodName())) {
				Code code = ((LanguageElement) target).getEnclosingCode();
				if (code != null) {
					Operation op = (Operation) code;
					TypeBody tb = code.getEnclosingType();

					String key = tb.getFcn() + ">>"	+ op.getOperationSignature();

					OperationInfo opi = target.getProvider();
					context.verbose((SourceMarker) target, "opi " + opi.getDeclaringType().getName());

					if ("java.sql.Connection".equals(opi.getDeclaringType().getName())) {
						context.reportViolation((SourceMarker) target);
					}
				}
			}
		}
		return true;
	}
}
