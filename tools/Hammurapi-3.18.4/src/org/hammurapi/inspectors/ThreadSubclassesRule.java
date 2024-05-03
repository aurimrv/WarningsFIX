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
import com.pavelvlasov.jsel.OperationNameSpace.OperationNameSpaceEntry;


/**
 * ER-108
 * Give subclasses of Thread a 'run ()' method
 * @author  Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class ThreadSubclassesRule extends InspectorBase  {
	
	private static final String JAVA_LANG_THREAD = "java.lang.Thread";
	public void visit(com.pavelvlasov.jsel.Class clazz) {
		try {
			OperationNameSpaceEntry entry = clazz.getOperationNamespace().find("run()");
			if (clazz.isKindOf(JAVA_LANG_THREAD) && (entry==null || !clazz.getFcn().equals(entry.getInfo().getDeclaringType().getName()))) {
				context.reportViolation(clazz);
			}
		} catch (JselException e) {
			context.warn(clazz, e);
		}
	}
}
