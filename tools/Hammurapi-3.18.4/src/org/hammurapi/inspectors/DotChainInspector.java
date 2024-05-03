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
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.expressions.Dot;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.1 $
 */
public class DotChainInspector extends InspectorBase implements Parameterizable {

	private Integer maxChain;

	public void visit(Dot dot) {
		if (maxChain==null) {
			disable("Max chain parameter is not set");
			return;
		}
		
		LanguageElement curDot=(LanguageElement) dot;
		
		for (int i=0; i<maxChain.intValue(); i++) {
			if (curDot.getParent()!=null && curDot.getParent().getParent() instanceof Dot) {
				curDot=curDot.getParent().getParent();
			} else {
				return;
			}
		}
		
		context.reportViolation((SourceMarker) dot);		
	}

	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("max-chain".equals(name)) {
			maxChain=(Integer) parameter;
			return true;
		}
		
		return false;
	}

}
