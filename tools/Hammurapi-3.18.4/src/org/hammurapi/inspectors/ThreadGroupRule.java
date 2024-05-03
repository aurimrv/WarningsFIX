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
import com.pavelvlasov.jsel.VariableDefinition;


/**
 * ER-105
 * Avoid using variables of type 'java.lang.ThreadGroup'
 * @author  Janos Czako
 * @version $Revision: 1.2 $
 */
public class ThreadGroupRule extends InspectorBase  {
	
	/**
	 * The full qualified name of the class java.lang.ThreadGroup
	 */
	private static final String TG_CLASS_NAME = "java.lang.ThreadGroup";
	
	/**
	 * Reviews if the variable definition has the type ThreadGroup.
	 * 
	 * @param element the variable definition to be reviewed.
	 */
	public void visit(VariableDefinition element) {
		try {
			if (element.getTypeSpecification().getType().isKindOf(TG_CLASS_NAME)) {
				context.reportViolation(element); 			
			}
		} catch (JselException e) {
			context.warn(element, e);
		}
	}	
}
