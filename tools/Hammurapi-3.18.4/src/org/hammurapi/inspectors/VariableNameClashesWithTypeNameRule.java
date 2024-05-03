/*
@license.text
 */
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;


/**
 * ER-200
 * Instance variables and the declaring type shouldn't have same name
 * @author  Janos Czako
 * @version $Revision: 1.2 $
 */
public class VariableNameClashesWithTypeNameRule extends InspectorBase  {
	
	/**
	 * Reviews the variable definitions, if they have clashing names
	 * with the class name.
	 * 
	 * @param element the method definition to be reviewed.
	 */
	public void visit(VariableDefinition element) {
		
	    TypeBody enclosingType = element.getEnclosingType();
		if (enclosingType instanceof TypeDefinition && ((TypeDefinition) enclosingType).getName().equals(element.getName())) {
			context.reportViolation(element);
		}
	}
	
}
