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
import java.util.LinkedList;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.RuntimeConfigurationException;

/**
 * Allows to defer rule instantiation till it is actually needed 
 * @author Pavel Vlasov	
 * @version $Revision: 1.5 $
 */
public class SelfDescribingInspectorProxy implements InspectorDescriptor {
	private InspectorDescriptor sourceDescriptor;
	
	private InspectorDescriptor ruleDescriptor=new InspectorDescriptor() {
		public String getDescription() { return null; }
		public Boolean isEnabled() { return null; }
		public String getName() { return null; }
		public Integer getSeverity() { return null; }
		public Integer getOrder() { return null; }
		public String getRationale() { return null; }
		public String getViolationSample() { return null; }
		public String getFixSample() { return null; }
		public String getResources() { return null; }
		public String getMessage() { return null; }
		public Inspector getInspector() { return null; }
		public Collection getParameters() {	return null; }
		public String getMessage(String key) { return null;	}
		public Boolean isWaivable() { return null; }
		public Collection getWaiveCases() {	return new LinkedList(); }
		public String getWaivedInspectorName(String inspectorKey) { return null; }
		public String getWaiveReason(String inspectorKey) { return null; }
		public Collection getWaivedInspectorNames() { return null; }
        public String getCategory() { return null; }
		public Collection getFilteredInspectorDesriptors(InspectorSet inspectorSet, Collection chain) {
			return chain;
		}
		public Collection getAfterInspectorNames() { return null; } 		 
	}; // To avoid if's in every method
	
	private boolean ruleInstantiated=false; 
	
	public SelfDescribingInspectorProxy(InspectorDescriptor descriptor) {
		super();
		sourceDescriptor=descriptor;
	}
	
	private InspectorDescriptor getInspectorDescriptor() {
		if (!ruleInstantiated) {
			try {
				Inspector rule=sourceDescriptor.getInspector();
				if (rule instanceof InspectorDescriptor) {
					ruleDescriptor=(InspectorDescriptor) rule;
				}
				ruleInstantiated=true;
			} catch (ConfigurationException e) {
				throw new RuntimeConfigurationException("Can't instantiate inspector", e);
			}
		}
		return ruleDescriptor;
	}

	public String getCategory() {
		return getInspectorDescriptor().getCategory();
	}

	public String getDescription() {
		return getInspectorDescriptor().getDescription();
	}

	public Boolean isEnabled() {
		return getInspectorDescriptor().isEnabled();
	}

	public String getName() {
		return getInspectorDescriptor().getName();
	}

	public Integer getSeverity() {
		return getInspectorDescriptor().getSeverity();
	}

	public Integer getOrder() {
		return getInspectorDescriptor().getOrder();
	}

	public String getRationale() {
		return getInspectorDescriptor().getRationale();
	}

	public String getViolationSample() {
		return getInspectorDescriptor().getViolationSample();
	}

	public String getFixSample() {
		return getInspectorDescriptor().getFixSample();
	}

	public String getResources() {
		return getInspectorDescriptor().getResources();
	}

	public String getMessage() {
		return getInspectorDescriptor().getMessage();
	}

	public Inspector getInspector() throws ConfigurationException {
		return getInspectorDescriptor().getInspector();
	}

	public Collection getParameters() {		
		return getInspectorDescriptor().getParameters();
	}

	public String getMessage(String key) {
		return getInspectorDescriptor().getMessage(key);
	}

	public Boolean isWaivable() {
		return getInspectorDescriptor().isWaivable();
	}

	public Collection getWaiveCases() {
		return getInspectorDescriptor().getWaiveCases();
	}

	public String getWaivedInspectorName(String inspectorKey) {
		return getInspectorDescriptor().getWaivedInspectorName(inspectorKey);
	}

	public String getWaiveReason(String inspectorKey) {
		return getInspectorDescriptor().getWaiveReason(inspectorKey);
	}

	public Collection getWaivedInspectorNames() {
		return getInspectorDescriptor().getWaivedInspectorNames();
	}

	public Collection getFilteredInspectorDesriptors(InspectorSet inspectorSet, Collection chain) {
		return getInspectorDescriptor().getFilteredInspectorDesriptors(inspectorSet, chain);
	}

	public Collection getAfterInspectorNames() {
		return getInspectorDescriptor().getAfterInspectorNames();
	}
}
