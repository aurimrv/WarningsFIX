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
import com.pavelvlasov.jsel.impl.Token;

/**
 * ER-029
 * Line is too long
 * @author  Pavel Vlasov
 * @version $Revision: 1.8 $
 */
public class LineLengthRule extends InspectorBase  implements Parameterizable  {
	/**
	 * Stores the setting form the configuration for the allowed
	 * maximal line length.
	 */
	private Integer maxLineLength;
	
	/**
	 * Reviews the actual line, if it is longer than the allowed maximum.
	 * 
	 * @param element the element to be reviewed.
	 */
	public void visit(Token element) {
		if (maxLineLength == null || lastPosition(element) < maxLineLength.intValue()) {
			return;
		}
		
		Token nextToken = (Token) element.getNextToken();
		if (nextToken==null || (nextToken != null && element.getLine() != nextToken.getLine())) {			
			Token violationToken=element;
			String vtt=violationToken.getTypeName();
			while ("WS".equals(vtt) || "NEW_LINE".equals(vtt)) {
				violationToken=violationToken.getPrevNonWhiteSpaceToken();
				if (violationToken==null || violationToken.getLine()!=element.getLine() || lastPosition(violationToken)<=maxLineLength.intValue()) {
					return;
				}				
				vtt=violationToken.getTypeName();
			}
			
			if ("ML_COMMENT".equals(element.getTypeName())) {
				// TODO break ML_COMMENT into lines and check their length.
			} else {
			    context.reportViolation(violationToken);
			}
		}
	}
	
	private int lastPosition(Token element) {
		return element.getColumn()+(element.getText()==null ? 0 : element.getText().length());
	}

	/**
	 * Configures rule. Reads in the values of the parameter line-max-length.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter name, or value.
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("line-max-length".equals(name)) {
			maxLineLength = (Integer) parameter;
			return true;
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName());
		} 
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed maximum line length:\n");
		ret.append("line-max-length: " + maxLineLength + "\n");
		return ret.toString();
	}
}
