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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.logging.Logger;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.8 $
 */
public class InspectorSet {
	private Map descriptors=new HashMap();
	private Collection inspectors;
	private Logger logger;
	private InspectorContextFactory contextFactory;
	private Collection inspectorSourceInfos=new ArrayList();
	
	public InspectorSet(InspectorContextFactory contextFactory, Logger logger) {
		this.contextFactory=contextFactory;
		this.logger=logger;
	}
	
	public void addDescriptors(Collection descriptors) throws ConfigurationException {
		Iterator it=descriptors.iterator();
		while (it.hasNext()) {
			addDescriptor((InspectorDescriptor) it.next());
		}		
	}
	
	public void addInspectorSourceInfo(InspectorSourceInfo info) {
		inspectorSourceInfos.add(info);
	}
	
	public Collection getInspectorSourceInfos() {
		return inspectorSourceInfos;
	}
	
	public InspectorDescriptor getDescriptor(String name) {
		return (InspectorDescriptor) descriptors.get(name);
	}
	
	public Collection getDescriptors() {
		return descriptors.values();
	}
	
	public void addDescriptor(InspectorDescriptor descriptor) throws ConfigurationException {
		SelfDescribingInspectorProxy sdrp=new SelfDescribingInspectorProxy(descriptor);
		String inspectorName=descriptor.getName();
		if (inspectorName==null) {
			inspectorName=sdrp.getName();
		}
		
		if (inspectorName==null) {
			throw new ConfigurationException("Unnamed inspector"); 
		}
		
		InspectorDescriptorStack rds=(InspectorDescriptorStack) descriptors.get(inspectorName);
		if (rds==null) {
			rds=new InspectorDescriptorStack();
			descriptors.put(inspectorName, rds);
		}
		
		rds.addFirst(sdrp);
		rds.addFirst(descriptor);
	}
	
	public Collection getInspectors() throws ConfigurationException, HammurapiException {
		if (inspectors==null) {
			assignOrders();
			inspectors=new LinkedList();
			Iterator it=descriptors.values().iterator();			
			while (it.hasNext()) {
				InspectorDescriptor inspectorDescriptor=(InspectorDescriptor) it.next();
				InspectorContext ic=contextFactory.newContext(inspectorDescriptor, logger);
				if (Boolean.TRUE.equals(inspectorDescriptor.isEnabled())) {
					Inspector inspector=inspectorDescriptor.getInspector();
					if (inspector!=null) {
						inspector.setContext(ic);
						inspectors.add(inspector);

						if (inspector instanceof FilteringInspector) {
							Collection fid=inspectorDescriptor.getFilteredInspectorDesriptors(this, null);
							if (fid!=null) {
								Iterator dit=fid.iterator();
								while (dit.hasNext()) {
									((FilteringInspector) inspector).addTarget(((InspectorDescriptor) dit.next()).getInspector());
								}
							}
						}
					}
				}
			}			
		}		
		return inspectors;
	}
	
	/**
	 * Invokes init() for all inspectors in the set
	 * @throws HammurapiException
	 * @throws ConfigurationException
	 */
	public void initInspectors() throws ConfigurationException, HammurapiException {
	    Iterator it=getInspectors().iterator();
	    while (it.hasNext()) {
	        ((Inspector) it.next()).init();
	    }
	}
	
	private void assignOrders() throws HammurapiException {
		final Map dependencyMap=new HashMap();
		Iterator it=descriptors.values().iterator();			
		while (it.hasNext()) {
			InspectorDescriptor inspectorDescriptor=(InspectorDescriptor) it.next();
			if (Boolean.TRUE.equals(inspectorDescriptor.isEnabled())) {
				Collection inspectorsToOrder=new ArrayList();
				Collection waivedInspectorNames = inspectorDescriptor.getWaivedInspectorNames();
				if (waivedInspectorNames!=null) {
					inspectorsToOrder.addAll(waivedInspectorNames);
				}
				
				Collection filteredInspectors = inspectorDescriptor.getFilteredInspectorDesriptors(this, null);
				if (filteredInspectors!=null && !filteredInspectors.isEmpty()) {
					Iterator fit=filteredInspectors.iterator();
					while (fit.hasNext()) {
						inspectorsToOrder.add(((InspectorDescriptor) fit.next()).getName());
					}
				}

				Collection afterInspectors = inspectorDescriptor.getAfterInspectorNames();
				if (afterInspectors!=null) {
					inspectorsToOrder.addAll(afterInspectors);
				}

				dependencyMap.put(inspectorDescriptor.getName(), inspectorsToOrder);						
			}
		}	
		
		class GraphEntry implements Comparable {
			private static final String CICRULAR_REFERENCE_MSG = "Circular <waives> or <filter> reference in inspector '";
			String name;
			private Set dependents=new HashSet();
			private boolean ready=false;
			private int level;
			
			public String toString() {
				StringBuffer ret=new StringBuffer(getClass().getName());
				ret.append("[").append(name);
				if (!dependents.isEmpty()) {
					ret.append(" <- ");
					Iterator it=dependents.iterator();
					while (it.hasNext()) {
						ret.append(it.next());
						if (it.hasNext()) {
							ret.append(", ");
						}
					}
				}
				ret.append("]");
				return ret.toString();
			}
		
			GraphEntry(String name) throws HammurapiException {
				this.name=name;
				Iterator it=((Collection) dependencyMap.get(name)).iterator();
				while (it.hasNext()) {
					String dependent=(String) it.next();
					if (name.equals(dependent)) {
						throw new HammurapiException(CICRULAR_REFERENCE_MSG+name+"'");
					} 
					dependents.add(dependent);
					
					Object o=dependencyMap.get(dependent);
					if (o instanceof Collection) {
						o=new GraphEntry(dependent);
						level=Math.max(level, ((GraphEntry) o).level+1);
					}
					
					if (o!=null) {
						Iterator dit=((GraphEntry) o).getDependents().iterator();
						while (dit.hasNext()) {
							String ddependent=(String) it.next();
							if (name.equals(ddependent)) {
								throw new HammurapiException(CICRULAR_REFERENCE_MSG+name+"'");
							}
							
							dependents.add(ddependent);
						}
					}
				}
				ready=true;
				dependencyMap.put(name, this);
			}

			Set getDependents() throws HammurapiException {
				if (ready) {
					return dependents;
				} 
				
				throw new HammurapiException(CICRULAR_REFERENCE_MSG+name+"'");
			}

			public int compareTo(Object o) {
				if (o==this) {
					return 0;
				} else if (o instanceof GraphEntry) {
					GraphEntry ge=(GraphEntry) o;
					
					try {
						if (getDependents().contains(ge.name)) {
							return -1;
						} else if (ge.getDependents().contains(name)){
							return 1;
						} else if (level<ge.level) {
							return 1;
						} else if (level>ge.level) {
							return -1;
						} else {
							return name.compareTo(ge.name);
						}
					} catch (HammurapiException e) {
						throw new HammurapiRuntimeException(e);
					}
				} else {
					return -1;
				}
			}
			
			public int hashCode() {
				return name.hashCode();
			}
		}						
		
		Iterator kit=dependencyMap.keySet().iterator();
		while (kit.hasNext()) {
			String key=(String) kit.next();
			if (dependencyMap.get(key) instanceof Collection) {
				new GraphEntry(key);
			}
		}
		
		List inspectors=new ArrayList(dependencyMap.values());
		Collections.sort(inspectors);
		
		int counter=0;
		Integer start=null;
		Iterator wit=inspectors.iterator();
		while (wit.hasNext()) {
			String iName=((GraphEntry) wit.next()).name;
			InspectorDescriptor id=(InspectorDescriptor) descriptors.get(iName);
			Integer order = id.getOrder();
			if (order!=null) {
				start=new Integer(order.intValue()-counter);
				break;
			}
			counter++;
		}
		
		counter = start==null ? 0 : start.intValue();
		wit=inspectors.iterator();
		while (wit.hasNext()) {
			String iName=((GraphEntry) wit.next()).name;
			InspectorDescriptor id=(InspectorDescriptor) descriptors.get(iName);
			Integer order = id.getOrder();
			if (order!=null) {
				if (order.intValue()<counter) {
					throw new HammurapiException("Order "+order.intValue()+" conflicts with automatically assigned order "+counter+" for inspector "+iName);
				} 
				
				counter=order.intValue();
			} else {
				final Integer iOrder=new Integer(counter);
				descriptors.put(id.getName(), new InspectorDescriptorFilter(id) {
					public Integer getOrder() {
						return iOrder;
					}
				});
			}
			counter++;
		}			
	}
	
	int size() {
		return descriptors.size();
	}
	
	/**
	 * Calls destroy() for all inspectors
	 */
	public void destroy() {
		if (inspectors!=null) {
			Iterator it=inspectors.iterator();
			while (it.hasNext()) {
				((Inspector) it.next()).destroy();
			}
		}
	}	
}
