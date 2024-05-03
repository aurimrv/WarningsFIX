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
import com.pavelvlasov.jsel.Code;

/**
 * ER-048
 * Method is too long
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class CodeTooLongRule extends InspectorBase  implements Parameterizable  {
	
	/**
	 * Reviews the method definitions if they are longer than the preconfigured
	 * maximum.
	 * 
	 * @param element the method definition to be reviewed.
	 */	
	public void visit(Code element) {
		int firstLine = element.getAst().getFirstToken().getLine();
		int lastLine = element.getAst().getLastToken().getLine();
		
		int length = lastLine-firstLine;
		context.addMetric(element, "Code length", length);
		
		if (maxLine!=null && length>maxLine.intValue()) {
			context.reportViolation(element);
		}
	}
	
	/**
	 * Stores the setting form the configuration for the maximum allowed
	 * linenumber inside of a method.
	 */
	private Integer maxLine;
	
	/**
	 * Configures the rule. Reads in the values of the parameter maximum-line.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("max-lines".equals(name)) {
			maxLine = (Integer) parameter;
			return true;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName());
		} 
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		if (maxLine==null) {
			return super.getConfigInfo();
		} else {
			StringBuffer ret=new StringBuffer("Allowed maximum line length of the methods:\n");
			ret.append("max-lines: " + maxLine + "\n");
			return ret.toString();
		}
	}
}
