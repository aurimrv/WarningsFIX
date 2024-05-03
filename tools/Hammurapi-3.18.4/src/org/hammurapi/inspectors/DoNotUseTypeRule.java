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

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.Identifier;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.review.SourceMarker;

/**
 * ER-117
 * Copyrights information should be present in each  file.
 * @author  Janos Czako
 * @version $Revision: 1.4 $
 */
public class DoNotUseTypeRule extends InspectorBase  implements Parameterizable  {

	/**
	 * Reviews the variable definitions if they violate agains the rule.
	 * 
	 * @param element the variable definition to be reviewed.
	 */
	public void visit(VariableDefinition element) {
		// TODO Shall check not only for variable definition but also for
		// parameters, superclasses, casts, and implemented interfaces
		// so it might be better to visit(Identifier) or something like this
		try {
			if (disAllowedIncludes.contains(element.getTypeSpecification().getType().getName())) {
				context.reportViolation(element);
			}
		} catch (JselException e) {
			context.warn(element, e);
		}
	}

	/**
	 * Reviews the compilation unit if it's file violates agaianst the rule.
	 * If a file contains more than one compilation unit, the violation will be 
	 * raised each time.
	 * 
	 * @param element the compilation unit to be reviewed.
	 */	
	public void visit(CompilationUnit element) throws HammurapiException {
		Iterator imports = element.getImports().iterator();
		while (imports.hasNext()) {
			Identifier importItem = (Identifier) imports.next();
			String importName = importItem.getValue();
			if (importName.endsWith(STAR)) {
				importName = 
					importName.substring(0, importName.length()-SUB_STR_LEN);
			}
			if (disAllowedIncludes.contains(importName)) {
				context.reportViolation((SourceMarker) importItem);
			} else {
				// If the whole package is disallowed and the import is exact.
				int pos = importName.lastIndexOf(DOT);
				if (pos>0) {
				importName = importName.substring(0, pos);
				if (disAllowedIncludes.contains(importName)) {
					context.reportViolation((SourceMarker) importItem);
				}
			}
		}
	}
	}
	
	/**
	 * Stores the setting from the configuration of the disallowed types, 
	 * packages. In case of a full package the ending "*" won't be stored.
	 */
	private java.util.Set disAllowedIncludes = new java.util.HashSet();
	
	private static final String STAR = "*";
	private static final String DOT = ".";
	private static final int SUB_STR_LEN = 2; 

    
	/**
	 * Configures the rule. Reads in the values of the parameter copyright.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("include".equals(name)) {
			String s = parameter.toString();
			//cutting the ending "*" makes easier the later checks
			if (s.endsWith(STAR)) {
				s = s.substring(0, s.length()-SUB_STR_LEN);
			}
			disAllowedIncludes.add(s);	
			return true;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName());
		} 
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Disallowed types:");
		java.util.Iterator iter = disAllowedIncludes.iterator();
		while (iter.hasNext()) {
			ret.append("\n\ttype: " + (String) iter.next());
		}
		ret.append("\n");

		return ret.toString();
	}
}
