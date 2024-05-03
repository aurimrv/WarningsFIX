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
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Parameter;


/**
 * ER-099
 * Avoid using method parameter names that conflict with class member names
 * @author  Janos Czako
 * @version $Revision: 1.2 $
 */
public class OperationParameterNamesRule extends InspectorBase  {
	
	/**
	 * Reviews the method definitions if they have parameters with the same
	 * name as one of the class attribute.
	 * 
	 * @param element the method definition to be reviewed.
	 */
	public void visit(Method element) {
		java.util.Iterator parameters = element.getParameters().iterator();
		
		while (parameters.hasNext()) {
			Parameter parameter = (Parameter) parameters.next();
			String paramName = parameter.getName();
			try {
				if (element.getEnclosingScope().getVariableNamespace().find(paramName)!=null) {
					context.reportViolation(parameter);
				}
			} catch (JselException e) {
				context.warn(parameter, e);
			}
		}
	}
	
}
