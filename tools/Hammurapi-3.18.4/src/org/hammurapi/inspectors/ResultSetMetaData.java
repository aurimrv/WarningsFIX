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
import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.review.SourceMarker;


/**
 * ER-070
 * ResultSetMetaData is banned. The disclosure of DB internals (here Column Names) on Business/Service layer is bad design
 * @author  Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class ResultSetMetaData extends InspectorBase  {

	/**
	 * The not allowed class
	 */
	private static final String VIOLATION_CLASS = "java.sql.ResultSetMetaData";
	
	/**
	 * Reviews if the not allowed class is used.
	 * 
	 * @param element the parameter to be reviewed.
	 */
	public void visit(Parameter element) {
		try {
			String fcn =
				element.getTypeSpecification().getType().getName();
			if (fcn!=null && VIOLATION_CLASS.equals(fcn)) {
				context.reportViolation((SourceMarker) element);
			}
		} catch (JselException e) {
			context.warn((SourceMarker) element, e);
		}
	}
	
	/**
	 * Reviews if the not allowed class is used.
	 * 
	 * @param element the variable definition to be reviewed
	 */
	public void visit(VariableDefinition element) {
		try {
			String fcn = element.getTypeSpecification().getType().getName();
			if (fcn!=null && VIOLATION_CLASS.equals(fcn)) {
				context.reportViolation((SourceMarker) element);
			}
		} catch (JselException e) {
			context.warn((SourceMarker) element, e);
		}
	}
	
	/**
	 * Reviews if the not allowed class is used.
	 * 
	 * @param element the identifier to be reviewed.
	 */
	public void visit(Ident element) {
		try {
			Object obj = element.getProvider();
			String fcn = null;
			if (obj instanceof VariableDefinition) {
				fcn =
					((VariableDefinition)obj).getTypeSpecification().getType().getName();
			} else if (obj instanceof Parameter) {
				fcn =
					((Parameter)obj).getTypeSpecification().getType().getName();
			}
			if (fcn!=null && VIOLATION_CLASS.compareTo(fcn)==0) {
				context.reportViolation((SourceMarker) element);
			}
		} catch (JselException e) {
			context.warn((SourceMarker) element, e);
		}
	}
	
}
