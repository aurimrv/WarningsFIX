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

import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.JselRuntimeException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.util.Visitable;
import com.pavelvlasov.util.Visitor;

/**
 * ER-131
 * Unused private variables.
 * @author  Pavel Vlasov
 * @version $Revision: 1.7 $
 */
public class UnusedVariablesRule extends InspectorBase { 
	/**
	 * Reviews variable definition.
	 * @param element the type definition to be reviewed.
	 */	
	public void visit(final VariableDefinition element) throws HammurapiException {
	    
		LanguageElement parent = element.getParent();
		if (element.getModifiers().contains("private") || !(parent instanceof TypeDefinition)) {
			class UsedException extends com.pavelvlasov.RuntimeException {
				
			};
						
			try {
				Visitable toVisit = parent instanceof TypeDefinition ? element.getCompilationUnit() : (Visitable) element.getEnclosingScope();
				toVisit.accept(new Visitor() {
					public boolean visit(Object target) {
						if (target instanceof Ident) {
							Ident ident=(Ident) target;
							try {
								// First comparison is to save time.								
								if (ident.getText().equals(element.getName()) && ((LanguageElement) ident).getParent()!=element) {
									Object provider = ident.getProvider();									
									if (provider==element) {
										throw new UsedException();
									}
								}
							} catch (JselException e) {
								throw new JselRuntimeException(e);
							}
						}
						return true;
					}
					
				});
				context.reportViolation(element);
			} catch (UsedException e) {
				// It's OK. The way to exit.
			}
		}
	}
}
