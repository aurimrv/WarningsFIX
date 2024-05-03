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

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.TypeSpecification;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.StringConstant;
import com.pavelvlasov.jsel.statements.CasesGroup;


/**
 * ER-101
 * Avoid using text labels in "switch" statements
 * @author  Janos Czako
 * @version $Revision: 1.2 $
 */
public class TextLabelsInSwitchStatementRule extends InspectorBase  {
	
	/**
	 * Type name for String.
	 */
	private static final String STRING_TYPE = "java.lang.String";
		
	/**
	 * Reviews the cases of the switch statements, if they have String labels.
	 * 
	 * @param element the case to be reviewed.
	 */
	public void visit(CasesGroup element) {		
		java.util.Iterator iter = element.getCases().iterator();
		while (iter.hasNext()) {
			LanguageElement le = (LanguageElement) iter.next();
			if (le instanceof StringConstant) {
				context.reportViolation(le);
	}
			else if (le instanceof Ident) {				
				try {
					Object r = 
						le.getEnclosingScope().getVariableNamespace().
							find(((Ident) le).getText()).getTypeName();
					if (r instanceof VariableDefinition) {
						VariableDefinition vd=(VariableDefinition) r;
						TypeSpecification typeSpecification = vd.getTypeSpecification();
							
						if (STRING_TYPE.equals(typeSpecification.getName())) {	
							context.reportViolation(le);
						}
					}
				} catch (JselException e) {
					context.warn(element, e);
				}
			}
		}
	}
}
