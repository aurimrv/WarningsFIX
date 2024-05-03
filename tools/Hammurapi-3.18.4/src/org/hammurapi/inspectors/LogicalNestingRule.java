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
import com.pavelvlasov.jsel.Scope;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.statements.CompoundStatement;
import com.pavelvlasov.jsel.statements.ForStatement;
import com.pavelvlasov.jsel.statements.WhileStatement;
import com.pavelvlasov.review.SourceMarker;

/**
 * ER-030
 * Logical nesting limit exceeded
 * @author  Janos Czako
 * @version $Revision: 1.7 $
 */
public class LogicalNestingRule extends InspectorBase  implements Parameterizable  {
	
	/**
	 * Reviews the statements, if their nesting is too deep.
	 * 
	 * @param element the statement to be reviewed.
	 */
	public void visit(CompoundStatement element) {
		if (!isEmpty(element)) {
//			Collection levels=new ArrayList();
			Scope prevScope=null;
			int actNesting = -1;
			for (Scope enclosingScope = element.getEnclosingScope(); !(enclosingScope==null || enclosingScope instanceof TypeBody); enclosingScope=enclosingScope.getEnclosingScope()) {
				if (!((enclosingScope instanceof ForStatement || enclosingScope instanceof WhileStatement) && prevScope instanceof CompoundStatement)) {
					actNesting++;
//					levels.add(enclosingScope);					
				}
				prevScope=enclosingScope;
			}
			
			if (actNesting>maxNesting.intValue()) {
				context.reportViolation((SourceMarker) element, new Object[] {maxNesting, new Integer(actNesting)});
//				Iterator it=levels.iterator();
//				while (it.hasNext()) {
//					LanguageElement le=(LanguageElement) it.next();
//					System.out.println("***\t"+le.getClass()+" "+le.getLocation());
//				}
			}
		}
	}

	/**
	 * Stores the setting form the configuration for the allowed
	 * maximal logical nesting.
	 */
	private Integer maxNesting;

	
	/**
	 * Configures rule. Reads in the values of the parameter max-nesting.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter name, or value.
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("max-nesting".equals(name)) {
			maxNesting = (Integer) parameter;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName());
		} 
		return true;
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		if (maxNesting!=null) {
			StringBuffer ret=new StringBuffer("Allowed maximum logical nesting:\n");
			ret.append("max-nesting: " + maxNesting + "\n");
			return ret.toString();
		} else {
			return "";
		}
	}
}
