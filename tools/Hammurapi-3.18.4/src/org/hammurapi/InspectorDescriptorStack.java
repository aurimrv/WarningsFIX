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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;

/**
 * Contains information from multiple descriptors. Returns first not-null
 * value from the stack.
 * @author Pavel Vlasov	
 * @version $Revision: 1.6 $
 */
public class InspectorDescriptorStack implements InspectorDescriptor {
	private LinkedList stack=new LinkedList();
	
	public String getDescription() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getDescription()!=null) {
				return descriptor.getDescription();
			}
		}
		return null;
	}

	public String getCategory() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getCategory()!=null) {
				return descriptor.getCategory();
			}
		}
		return "Miscellaneous";
	}

	public Boolean isEnabled() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.isEnabled()!=null) {
				return descriptor.isEnabled();
			}
		}
		return null;
	}

	public String getName() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getName()!=null) {
				return descriptor.getName();
			}
		}
		return null;
	}

	Integer defaultSeverity=new Integer(1);
	private Set parameterized=new HashSet();
	
	public Integer getSeverity() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getSeverity()!=null) {
				return descriptor.getSeverity();
			}
		}
		return defaultSeverity;
	}	

	public Integer getOrder() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getOrder()!=null) {
				return descriptor.getOrder();
			}
		}
		return null;
	}

	public String getRationale() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getRationale()!=null) {
				return descriptor.getRationale();
			}
		}
		return null;
	}

	public String getViolationSample() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getViolationSample()!=null) {
				return descriptor.getViolationSample();
			}
		}
		return null;
	}

	public String getFixSample() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getFixSample()!=null) {
				return descriptor.getFixSample();
			}
		}
		return null;
	}

	public String getResources() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getResources()!=null) {
				return descriptor.getResources();
			}
		}
		return null;
	}

	public String getMessage() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getMessage()!=null) {
				return descriptor.getMessage();
			}
		}
		return getDescription();
	}
	
	/**
	 * Adds descriptor at the beginning of the stack (will be used first)
	 * @param descriptor
	 */
	public void addFirst(InspectorDescriptor descriptor) {
		stack.addFirst(descriptor);
	}
	
	public void addLast(InspectorDescriptor descriptor) {
		stack.addLast(descriptor);
	}

	public Inspector getInspector() throws ConfigurationException {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			Inspector inspector=descriptor.getInspector();
			if (inspector!=null) {
				if (!getParameters().isEmpty()) {
					if (inspector instanceof Parameterizable) {
						if (!parameterized.contains(inspector)) {
							parameterized.add(inspector);
							Iterator pit=getParameters().iterator();
							while (pit.hasNext()) {
								ParameterEntry pe=(ParameterEntry) pit.next();
								if (!((Parameterizable) inspector).setParameter(pe.getName(), pe.getValue())) {
								    throw new ConfigurationException(inspector.getClass().getName()+" does not support parameter "+pe.getName());
								}
							}
						}
					} else {
						throw new ConfigurationException(inspector.getClass().getName()+" does not implement "+Parameterizable.class.getName());
					}
				}
				return inspector;
			}
		}
		return null;
	}

	public Collection getParameters() {
		LinkedList ret=new LinkedList();
		LinkedList rstack=new LinkedList(stack);
		Collections.reverse(rstack);
		Iterator it=rstack.iterator();
		while (it.hasNext()) {
			Collection params=((InspectorDescriptor) it.next()).getParameters();
			if (params!=null) {
				ret.addAll(params);
			}
		}
		return ret;
	}

	public String getMessage(String key) {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.getMessage(key)!=null) {
				return descriptor.getMessage(key);
			}
		}
		return null;
	}

	public Boolean isWaivable() {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			if (descriptor.isWaivable()!=null) {
				return descriptor.isWaivable();
			}
		}
		return null;
	}

	public Collection getWaiveCases() {
		LinkedList ret=new LinkedList();
		Iterator it=ret.iterator();
		while (it.hasNext()) {
			Collection waiveCases=((InspectorDescriptor) it.next()).getWaiveCases();
			if (waiveCases!=null) {
				ret.addAll(waiveCases);
			}
		}
		return ret;
	}

	public String getWaivedInspectorName(String inspectorKey) {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			String ret=descriptor.getWaivedInspectorName(inspectorKey);
			if (ret!=null) {
				return ret;
			}
		}
		return null;
	}

	public String getWaiveReason(String inspectorKey) {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			InspectorDescriptor descriptor=(InspectorDescriptor) it.next();
			String ret=descriptor.getWaiveReason(inspectorKey);
			if (ret!=null) {
				return ret;
			}
		}
		return null;
	}

	public Collection getWaivedInspectorNames() {
		LinkedList ret=new LinkedList();
		Iterator it=ret.iterator();
		while (it.hasNext()) {
			Collection win=((InspectorDescriptor) it.next()).getWaivedInspectorNames();
			if (win!=null) {
				ret.addAll(win);
			}
		}
		return ret;
	}

	public Collection getFilteredInspectorDesriptors(InspectorSet inspectorSet, Collection chain) {
		Iterator it=stack.iterator();
		while (it.hasNext()) {
			chain=((InspectorDescriptor) it.next()).getFilteredInspectorDesriptors(inspectorSet, chain);
		}
		return chain;
	}

	public Collection getAfterInspectorNames() {
		LinkedList ret=new LinkedList();
		Iterator it=ret.iterator();
		while (it.hasNext()) {
			Collection ain=((InspectorDescriptor) it.next()).getAfterInspectorNames();
			if (ain!=null) {
				ret.addAll(ain);
			}
		}
		return ret;
	}
}
