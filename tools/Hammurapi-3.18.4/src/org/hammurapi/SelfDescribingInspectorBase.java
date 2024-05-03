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
package org.hammurapi;

import java.util.Collection;

/**
 * Base class for self-describing inspectorss. It contains empty methods. Subclass can
 * override only methods of interest instead of implementing all methods from
 * InspectorDescriptor.
 * @author Pavel Vlasov	
 * @version $Revision: 1.1 $
 */
public class SelfDescribingInspectorBase
	extends InspectorBase
	implements InspectorDescriptor {

	public String getDescription() {
		return null;
	}

	public Boolean isEnabled() {
		return null;
	}

	public String getName() {
		return null;
	}

	public Integer getSeverity() {
		return null;
	}

	public Integer getOrder() {
		return null;
	}

	public String getRationale() {
		return null;
	}

	public String getViolationSample() {
		return null;
	}

	public String getFixSample() {
		return null;
	}

	public String getResources() {
		return null;
	}

	public String getMessage() {
		return null;
	}

	public Inspector getInspector() {
		return this;
	}

	public Collection getParameters() {
		return null;
	}

	public String getMessage(String key) {
		return null;
	}

	public Boolean isWaivable() {
		return null;
	}

	public Collection getWaiveCases() {
		return null;
	}

	public String getWaivedInspectorName(String inspectorKey) {
		return null;
	}

	public String getWaiveReason(String inspectorKey) {
		return null;
	}

	public Collection getWaivedInspectorNames() {
		return null;
	}

    public String getCategory() {
        return null;
    }

	public Collection getFilteredInspectorDesriptors(InspectorSet inspectorSet, Collection chain) {
		return chain;
	}

	public Collection getAfterInspectorNames() {
		return null;
	}
}
