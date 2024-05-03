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

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;

/**
 * ER-068
 * Amount of logging code is comparable to the amount of business code.
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class TooMuchLoggingRule extends InspectorBase implements Parameterizable  {
	/*
	 * The idea is to navigate through invocations and calculate number of logging
	 * invocations (to Logger, Category or its subclasses). Then divide number of
	 * logging invocations to code's (method, constructor, static initializer or instance initializer) 
	 * 'size' calculated as getCompoundStatement().getAst().getSize()
	 * 
	 * 
	 */

	// TODO Implement one or more of org.apache.bcel.generic.Visitor methods
	
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("<parameter name>".equals(name)) {
			// TODO set parameter	
			return true;
		}
		
		throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName()); 
	}
}
