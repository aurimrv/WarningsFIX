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

import com.pavelvlasov.config.ConfigurationException;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class InspectorDescriptorFilter implements InspectorDescriptor {
	private InspectorDescriptor master;

	public InspectorDescriptorFilter(InspectorDescriptor master) {
		this.master=master;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return master.getDescription();
	}

	/**
	 * @return
	 */
	public String getFixSample() {
		return master.getFixSample();
	}

	/**
	 * @return
	 * @throws ConfigurationException
	 */
	public Inspector getInspector() throws ConfigurationException {
		return master.getInspector();
	}

	/**
	 * @return
	 */
	public String getMessage() {
		return master.getMessage();
	}

	/**
	 * @param key
	 * @return
	 */
	public String getMessage(String key) {
		return master.getMessage(key);
	}

	/**
	 * @return
	 */
	public String getName() {
		return master.getName();
	}

	/**
	 * @return
	 */
	public Integer getOrder() {
		return master.getOrder();
	}

	/**
	 * @return
	 */
	public Collection getParameters() {
		return master.getParameters();
	}

	/**
	 * @return
	 */
	public String getRationale() {
		return master.getRationale();
	}

	/**
	 * @return
	 */
	public String getResources() {
		return master.getResources();
	}

	/**
	 * @return
	 */
	public Integer getSeverity() {
		return master.getSeverity();
	}

	/**
	 * @return
	 */
	public String getViolationSample() {
		return master.getViolationSample();
	}

	/**
	 * @return
	 */
	public Collection getWaiveCases() {
		return master.getWaiveCases();
	}

	/**
	 * @param inspectorKey
	 * @return
	 */
	public String getWaivedInspectorName(String inspectorKey) {
		return master.getWaivedInspectorName(inspectorKey);
	}

	/**
	 * @return
	 */
	public Collection getWaivedInspectorNames() {
		return master.getWaivedInspectorNames();
	}

	/**
	 * @param inspectorKey
	 * @return
	 */
	public String getWaiveReason(String inspectorKey) {
		return master.getWaiveReason(inspectorKey);
	}

	/**
	 * @return
	 */
	public Boolean isEnabled() {
		return master.isEnabled();
	}

	/**
	 * @return
	 */
	public Boolean isWaivable() {
		return master.isWaivable();
	}

    /**
     * @return
     */
    public String getCategory() {
        return master.getCategory();
    }

	public Collection getFilteredInspectorDesriptors(InspectorSet inspectorSet, Collection chain) {
		return master.getFilteredInspectorDesriptors(inspectorSet, chain);
	}

	public Collection getAfterInspectorNames() {
		return master.getAfterInspectorNames();
	}
}
