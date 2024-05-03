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

import java.util.Iterator;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.expressions.DoubleConstant;
import com.pavelvlasov.jsel.expressions.FloatConstant;
import com.pavelvlasov.jsel.expressions.IntegerConstant;
import com.pavelvlasov.jsel.expressions.LongConstant;

/**
 * ER-011
 * Avoid hardwired numeric literals
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class HardcodedNumericLiteralsRule extends HardcodedLiteralsRule  implements Parameterizable  {
	
	/**
	 * Reviews the double constant literals, if they are allowed.
	 * 
	 * @param element the literal to be reviewed.
	 */	
	public void visit(DoubleConstant element) {
		if (!isAllowed(element.getValue())) {
			analyze((LanguageElement) element);
		}
	}
	
	/**
	 * Reviews the float constant literals, if they are allowed.
	 * 
	 * @param element the literal to be reviewed.
	 */	
	public void visit(FloatConstant element) {
		if (!isAllowed(element.getValue())) {
			analyze((LanguageElement) element);
		}
	}
	
	/**
	 * Reviews the int constant literals, if they are allowed.
	 * 
	 * @param element the literal to be reviewed.
	 */	
	public void visit(IntegerConstant element) {
		if (!isAllowed(element.getValue())) {
			analyze((LanguageElement) element);
		}
	}
	
	/**
	 * Reviews the long constant literals, if they are allowed.
	 * 
	 * @param element the literal to be reviewed.
	 */	
	public void visit(LongConstant element) {
		if (!isAllowed(element.getValue())) {
			analyze((LanguageElement) element);
		}
	}
	
	/**
	 * Stores the setting form the configuration for the allowed
	 * number literals. They are acceptable as double.
	 */
	private java.util.Set allowedLiterals = new java.util.HashSet();

	/**
	 * Checks if a double value is in the allowed list.
	 * It looks through the allowed list.
	 * 
	 * @param value the value to be checked.
	 * @return true if the value is in the allowed list.
	 */	
	private boolean isAllowed(double value) {
		return allowedLiterals.contains(new Double(value));
	}

	
	/**
	 * Configures rule. Reads in the values of the parameters containing the
	 * allowed numeric literals. It checks them if they can be parsed as double.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter name, or value.
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("allowed-literal".equals(name)) {
			allowedLiterals.add(parameter);	
			return true;
		} else {
			throw new ConfigurationException("Parameter '" + name + 
				"' is not supported by "+getClass().getName());
		} 
	}
	

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed numeric literals:\n");
		Iterator it=allowedLiterals.iterator();
		while (it.hasNext()) {
			ret.append("\t");
			ret.append(it.next());
			ret.append("\n");
		}
		return ret.toString();
	}
}
