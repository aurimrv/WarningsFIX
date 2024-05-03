/*
 * Created on Oct 21, 2004
 *
 */
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author Johannes Bellert
 */
public class SqlStatementAsInstanceVariableRule extends InspectorBase {

	public void visit(final VariableDefinition varDef) {
		try {
			if (varDef.getTypeSpecification().isKindOf("java.sql.Statement")
					|| varDef.getTypeSpecification().isKindOf("java.sql.PreparedStatement")
					|| varDef.getTypeSpecification().isKindOf("java.sql.ResultSet")) {
				LanguageElement le = varDef.getParent();
				if (le instanceof com.pavelvlasov.jsel.impl.ClassImpl) {
					context.reportViolation((SourceMarker) varDef);
				}
			}
		} catch (JselException e) {
			context.warn((SourceMarker) varDef, e);
		}
	}
}
