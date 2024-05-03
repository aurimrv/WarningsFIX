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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.Class;
import com.pavelvlasov.jsel.Field;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.TypeIdentifier;
import com.pavelvlasov.jsel.VariableDefinition;

/**
 * ER-092
 * Avoid hiding inherited instance fields
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class HidingInheritedFieldsRule extends InspectorBase  {
	
	public void visit(Class clazz) {
		try {
			TypeIdentifier superclass = clazz.getSuperclass();
			if (superclass!=null) {
				// TODO Need to check superclass and implemented interfaces and their
				// superclasses. Use scopes for that. Create a list of superscopes and
				// for each field in the class check if it is found in one of superscopes
				Class parentClass=(Class) superclass.find();
				if (parentClass!=null) {
					Set parentFields=new HashSet();
					Iterator it=parentClass.getFields().iterator();
					while (it.hasNext()) {
						Field field = (Field) it.next();
						String cPkg = clazz.getCompilationUnit().getPackage().getName();
						String sPkg = superclass.getCompilationUnit().getPackage().getName();
						boolean isVisible=cPkg.equals(sPkg) ? !field.getModifiers().contains("private") : (field.getModifiers().contains("public") || field.getModifiers().contains("protected"));						
						if (isVisible && field instanceof VariableDefinition) {							
							parentFields.add(((VariableDefinition) field).getName());
						}			
					}
					
					it=clazz.getFields().iterator();
					while (it.hasNext()) {
						Field field = (Field) it.next();
						if (field instanceof VariableDefinition && parentFields.contains(((VariableDefinition) field).getName())) {
							VariableDefinition vd=(VariableDefinition) field;
							// TODO make names configurable
							if (!("serialVersionUID".equals(vd.getName()) && vd.getTypeSpecification().isKindOf("long"))) {
								context.reportViolation(field);
							}
						}			
					}
				}
			}
		} catch (JselException e) {
			context.warn(clazz, "Could not resolve parent class: "+e);
		}
	}
}
