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
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.This;


/**
 * ER-083
 * Avoid passing the "this" reference as an argument
 * @author  Janos Czako
 * @version $Revision: 1.2 $
 */
public class ThisReferenceAsArgumentInEjbRule extends InspectorBase  {
	
	/**
	 * The full qualified name of the class javax.ejb.EnterpriseBean.
	 */
	private static final String EB_CLASS_NAME = "javax.ejb.EnterpriseBean";

	/**
	 * Visits the methodcalls if they has this as parameter.
	 * 
	 * @param element the methodcall to be reviewed.
	 */	
	public void visit(MethodCall element) {
		LanguageElement lelement = ((LanguageElement) element);
		Repository repository =	lelement.getCompilationUnit().getPackage().getRepository();

		try {
			if (((LanguageElement) element).getEnclosingType().isKindOf(EB_CLASS_NAME)) {
			 	java.util.Iterator iter = element.getParameters().iterator();
			 	while (iter.hasNext()) {
			 		if (iter.next() instanceof This) {
						context.reportViolation(lelement); 			
			 		}
			 	}
			 }
		} catch (JselException e) {
			context.warn(lelement, e);
		}
	}
	
}
