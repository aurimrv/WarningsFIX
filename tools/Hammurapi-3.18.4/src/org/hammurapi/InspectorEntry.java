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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Property;

import com.pavelvlasov.ant.ObjectEntry;
import com.pavelvlasov.ant.Param;
import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;

/**
 * Defines inspector
 * @ant.element name="inspector" display-name="In-line inspector definition"
 * @author Pavel Vlasov	
 * @version $Revision: 1.8 $
 */
public class InspectorEntry extends ObjectEntry implements InspectorDescriptor {
	private String fixSample;
	private String message;
	private String name;
	private Integer order;
	private String rationale;
	private String resources;
	private Integer severity;
	private String violationSample;
	private Boolean isEnabled;
	private Boolean isWaivable;
	private String description;
	private String category;
	private List waiveCases=new LinkedList();
	
	public void addConfiguredWaiveCase(WaiveCaseEntry entry) {
		waiveCases.add(entry.getText());
	}
	
	public Boolean isEnabled() {
		return isEnabled;
	}

	public Boolean isWaivable() {
		return isWaivable;
	}

	public String getName() {
		return name;
	}

	public Integer getSeverity() {
		return severity;
	}

	public Integer getOrder() {
		return order;
	}

	public String getRationale() {
		return rationale;
	}

	public String getViolationSample() {
		return violationSample;
	}

	public String getFixSample() {
		return fixSample;
	}

	public String getResources() {
		return resources;
	}

	public String getMessage() {
		return message;
	}
	
	private List filterEntries=new LinkedList();
	
	/**
	 * Inspector to include to filtering
	 * @ant.non-required
	 * @param entry
	 * @return
	 */
	public FilterEntry createFilterInclude() {
		FilterEntry fe=new FilterEntry();
		filterEntries.add(fe);
		return fe;
	}

	/**
	 * Inspector to exclude from filtering. 
	 * @ant.non-required
	 * @param entry
	 * @return
	 */
	public FilterEntry createFilterExclude() {
		FilterEntry fe=new FilterEntry();
		filterEntries.add(fe);
		fe.exclude=true;
		return fe;
	}
	
	public Inspector getInspector() throws ConfigurationException {
		if (getClassName()==null) {
			return null;
		} else {
			Inspector ret = (Inspector) getObject(null);
			if (!super.getParameters().isEmpty()) {
				if (ret instanceof Parameterizable) {
					Iterator it=getParameters().iterator();
					while (it.hasNext()) {
						ParameterEntry pe=(ParameterEntry) it.next();
						if (!((Parameterizable) ret).setParameter(pe.getName(), pe.getValue())) {
						    throw new ConfigurationException(ret.getClass().getName()+" does not support parameter "+pe.getName());
						}
					}
				} else {
					throw new ConfigurationException(ret.getClass().getName()+" does not implement "+Parameterizable.class.getName());
				}
			}
			return ret;
		}
	}

	protected void validateClass(Class clazz) throws BuildException {
		super.validateClass(clazz);
		if (!Inspector.class.isAssignableFrom(clazz)) {
			throw new BuildException(clazz.getName()+" doesn't implement "+Inspector.class);
		}
	}
	
	/**
	 * @ant:non-required
	 * @param fixSample The fixSample to set.
	 */
	public void setFixSample(String fixSample) {
		this.fixSample = fixSample;
	}

	/**
	 * @ant:non-required
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @ant:non-required
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @ant:non-required
	 * @param order The order to set.
	 */
	public void setOrder(int order) {
		this.order = new Integer(order);
	}

	/**
	 * @ant:non-required
	 * @param rationale The rationale to set.
	 */
	public void setRationale(String rationale) {
		this.rationale = rationale;
	}

	/**
	 * @ant:non-required
	 * @param resources The resources to set.
	 */
	public void setResources(String resources) {
		this.resources = resources;
	}

	/**
	 * @ant:non-required
	 * @param severity The severity to set.
	 */
	public void setSeverity(int severity) {
		this.severity = new Integer(severity);
	}

	/**
	 * @ant:non-required
	 * @param violationSample The violationSample to set.
	 */
	public void setViolationSample(String violationSample) {
		this.violationSample = violationSample;
	}

	/**
	 * @ant:non-required
	 * @param isEnabled The isEnabled to set.
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled ? Boolean.TRUE : Boolean.FALSE;
	}
	
	/**
	 * @ant:non-required
	 * @param isWaivable The isWaivable to set.
	 */
	public void setWaivable(boolean isWaivable) {
		this.isWaivable = isWaivable ? Boolean.TRUE : Boolean.FALSE;
	}
	
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the description.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @ant:non-required
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @ant:non-required
	 * @param category The category to set.
	 */
	public void setCategory(String category) {
		this.category=category;
	}
	
	/**
	 * Convert entries from Param to ParameterEntry
	 * @ant:ignore
	 */
	public Collection getParameters() {
		List ret=new LinkedList();
		Iterator it=super.getParameters().iterator();
		while (it.hasNext()) {
			Param p=(Param) it.next();
			ret.add(new ParameterEntry(p.getName(), p.getObject(null)));
		}
		return ret;
	}
	
	private Map messages=new HashMap();
	
	/**
	 * Keyed message.
	 * @param message
	 * @ant.non-required
	 */
	public void addConfiguredMessage(Property message) {
		messages.put(message.getName(), message);
	}

	public String getMessage(String key) {
		Property property=(Property) messages.get(key);
		return property==null ? null : property.getValue();
	}

	public Collection getWaiveCases() {
		return waiveCases;
	}

	public String getWaivedInspectorName(String inspectorKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getWaiveReason(String inspectorKey) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getWaivedInspectorNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getFilteredInspectorDesriptors(InspectorSet inspectorSet, Collection chain) {
		if (chain==null) {
			chain=new LinkedList();
		}
		
		Iterator it=filterEntries.iterator();
		while (it.hasNext()) {
			FilterEntry fe=(FilterEntry) it.next();
			if (fe.exclude) {
				if (fe.name==null) {
					Iterator dit=chain.iterator();
					while (dit.hasNext()) {
						if (fe.category.equals(((InspectorDescriptor) dit.next()).getCategory())) {
							dit.remove();
						}
					}
				} else {
					if ("*".equals(fe.name)) {
						chain.clear();
					} else {
						Iterator dit=chain.iterator();
						while (dit.hasNext()) {
							if (fe.name.equals(((InspectorDescriptor) dit.next()).getName())) {
								dit.remove();
							}
						}
					}
				}
			} else {
				if (fe.name==null) {
					Iterator dit=inspectorSet.getDescriptors().iterator();
					while (dit.hasNext()) {
						InspectorDescriptor inspectorDescriptor = (InspectorDescriptor) dit.next();
						if (fe.category.equals(inspectorDescriptor.getCategory())) {
							chain.add(inspectorDescriptor);
						}
					}
				} else {
					if ("*".equals(fe.name)) {
						chain.addAll(inspectorSet.getDescriptors());
					} else {
						Iterator dit=inspectorSet.getDescriptors().iterator();
						while (dit.hasNext()) {
							InspectorDescriptor inspectorDescriptor = (InspectorDescriptor) dit.next();
							if (fe.name.equals(inspectorDescriptor.getName())) {
								chain.add(inspectorDescriptor);
							}
						}
					}
				}				
			}
		}
		
		return chain;
	}

	public Collection getAfterInspectorNames() {
		return null;
	}
}
