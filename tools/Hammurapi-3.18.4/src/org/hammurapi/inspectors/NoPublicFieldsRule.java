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

import java.util.List;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;

/**
 * Checks if the class has only static final, or final public fields. Even the
 * permission for this two kind of fields can be configured.
 * 
 * @author Pavel Vlasov
 * @version $Revision: 1.7 $
 */
public class NoPublicFieldsRule extends InspectorBase implements
		Parameterizable {
	/**
	 * Stores the setting form the configuration if static public final
	 * attributes are allowed in the code.
	 */
	private boolean staticFinalAllowed = true;

	/**
	 * Stores the setting form the configuration if public final attributes are
	 * allowed in the code.
	 */
	private boolean finalAllowed = false;

	/**
	 * Reviews the variable definitions.
	 * 
	 * @param variableDefinition
	 *            the variable definition being reviewed.
	 */
	public void visit(VariableDefinition variableDefinition) {
		LanguageElement parent = variableDefinition.getParent();
		if (!(parent instanceof TypeDefinition)) {
			return; // local variable
		}
		if (((TypeDefinition) parent).getModifiers().contains("private")) {
			return; // private class
		}
		List modifiers = variableDefinition.getModifiers();
		if (modifiers.contains("public")) {
			if (modifiers.contains("final")) {
				if (finalAllowed) {
					return;
				} else if (modifiers.contains("static") && staticFinalAllowed) {
					return;
				} else {
					context.reportViolation(variableDefinition);
				}
			} else {
				context.reportViolation(variableDefinition);
			}
		}
	}

	/**
	 * Configures rule. Reads in the values of the parameters
	 * static-final-allowed and final-allowed.
	 * 
	 * @param name
	 *            the name of the parameter being loaded from Hammurapi
	 *            configuration
	 * @param value
	 *            the value of the parameter being loaded from Hammurapi
	 *            configuration
	 * @exception ConfigurationException
	 *                in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object value)
			throws ConfigurationException {
		if ("static-final-allowed".equals(name)) {
			staticFinalAllowed = "yes".equals(value);
		} else if ("final-allowed".equals(name)) {
			finalAllowed = "yes".equals(value);
		} else {
			throw new ConfigurationException("Parameter " + name
					+ " is not supported.");
		}
		return true;
	}

	/**
	 * Gives back the preconfigured values.
	 */
	public String getConfigInfo() {
		StringBuffer ret = new StringBuffer("Allowed for public fields:\n");
		ret.append("static: " + staticFinalAllowed + "\t");
		ret.append("final: " + finalAllowed + "\t");
		return ret.toString();
	}
}
