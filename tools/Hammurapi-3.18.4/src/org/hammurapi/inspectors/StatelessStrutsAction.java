/*
 *  * Hammurapi
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
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;

/**
 * @author MUCBJ0
 * @author Pavel Vlasov.
 *
 * FIX 1: changed package name from org.apache.struts.actions.Action to org.apache.struts.action.Action
 */
public class StatelessStrutsAction extends InspectorBase {

    public boolean isOfInterest(VariableDefinition element) throws JselException {
    	LanguageElement parent=element.getParent();
    	if (parent instanceof TypeDefinition) {
	    	return ((TypeDefinition) parent).isKindOf("org.apache.struts.action.Action");
    	} else {
    		return false;
    	}
    }

    public void visit(VariableDefinition element) {
        try {
			if (isOfInterest(element) && !(/*element.getModifiers().contains("static") || */ element.getModifiers().contains("final"))) {
				context.reportViolation(element);
			}
		} catch (JselException e) {
			context.warn(element.getEnclosingType(), e);
		}
	}

}
