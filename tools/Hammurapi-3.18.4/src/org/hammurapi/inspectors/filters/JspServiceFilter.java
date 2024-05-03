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
package org.hammurapi.inspectors.filters;

import org.hammurapi.InspectorBase;
import org.hammurapi.FilteringInspector;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Parameter;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class JspServiceFilter extends InspectorBase implements	FilteringInspector {
	
	public boolean approve(Method method) {
		try {
			return !("_jspService".equals(method.getName()) 
					&& method.getParameters().size()==2 
					&& method.getEnclosingType().isKindOf("org.apache.jasper.runtime.HttpJspBase")
					&& ((Parameter) method.getParameters().get(0)).getTypeSpecification().isKindOf("javax.servlet.http.HttpServletRequest")
					&& ((Parameter) method.getParameters().get(1)).getTypeSpecification().isKindOf("javax.servlet.http.HttpServletResponse"));
		} catch (JselException e) {
			context.warn(method, "Filter failed: "+e.getMessage());
			return true;
		}
	}
}
