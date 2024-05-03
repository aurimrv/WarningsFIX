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

import com.pavelvlasov.jsel.Field;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.VariableDefinition;


/**
 * ER-087
 * Avoid "static" collections; they can grow without bounds
 * @author  Janos Czako
 * @version $Revision: 1.2 $
 */
public class StaticCollectionsRule extends InspectorBase  {
	
	/**
	 * static Collection attribute is a violation
	 */
	private static final String STATIC = "static";
	
	/**
	 * Reviews the type definition, if it has static Collection attributes.
	 * 
	 * @param type the type definition to be reviewed.
	 */
	public void visit(com.pavelvlasov.jsel.Class clazz) {
		java.util.Iterator iter = clazz.getFields().iterator();
		
		while (iter.hasNext()) {
			Field field = (Field) iter.next();
			if (field instanceof VariableDefinition && field.getModifiers().contains(STATIC)) {
				VariableDefinition vd = (VariableDefinition) field;
				try {
					if (vd.getTypeSpecification().getType().isKindOf("java.util.Collection")) {
						context.reportViolation(field);
					}
				} catch (JselException e) {
					context.warn(field, e);
				}
			}
		}
	}
}
