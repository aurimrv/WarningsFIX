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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.DomConfigFactory;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.xml.dom.AbstractDomObject;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.9 $
 */
public class DomInspectorDescriptor extends AbstractDomObject implements InspectorDescriptor {
	private String fixSample;
	private String message;
	private String category;
	private String name;
	private Integer order;
	private String rationale;
	private String resources;
	private Integer severity;
	private String violationSample;
	private Boolean isEnabled;
	private Boolean isWaivable;
	private String description;
	private Element inspectorElement;
	private Inspector inspector;
	private List parameters=new LinkedList();
	private List waiveCases=new LinkedList();
	private Map messages=new HashMap();
	
	private class FilterNameEntry {
		boolean exclude;
		String name;
		String category;
	}
	
	private Collection filterEntries=new LinkedList();
	
	public DomInspectorDescriptor(Element holder) throws HammurapiException {
		super();
		try {
			CachedXPathAPI cxpa=new CachedXPathAPI();
			fixSample=getElementText(holder, "fix-sample", cxpa);
			
			NodeIterator it=cxpa.selectNodeIterator(holder, "message");
			Node messageNode;
			while ((messageNode=it.nextNode())!=null) {
				if (messageNode instanceof Element) {
					Element messageElement=(Element) messageNode;
					if (messageElement.hasAttribute("key")) {
						messages.put(messageElement.getAttribute("key"), getElementText(messageElement));
					} else {
						message=getElementText(messageElement);
					}
				}
			}
			name=getElementText(holder, "name", cxpa);
			String orderVal=getElementText(holder, "order", cxpa);
			if (orderVal!=null) {
				order=new Integer(orderVal);
			}
			rationale=getElementText(holder, "rationale", cxpa);
			category=getElementText(holder, "category", cxpa);
			resources=getElementText(holder, "resource", cxpa);
			String severityVal=getElementText(holder, "severity", cxpa);
			if (severityVal!=null) {
				severity=new Integer(severityVal);
			}
			violationSample=getElementText(holder, "violation-sample", cxpa);
			
			String enabledVal=getElementText(holder, "enabled", cxpa);
			if (enabledVal!=null) {
				isEnabled="yes".equalsIgnoreCase(enabledVal) || "true".equalsIgnoreCase(enabledVal) ? Boolean.TRUE : Boolean.FALSE;
			}
			
			String waivableVal=getElementText(holder, "waivable", cxpa);
			if (waivableVal!=null) {
				isWaivable="yes".equalsIgnoreCase(waivableVal) || "true".equalsIgnoreCase(waivableVal) ? Boolean.TRUE : Boolean.FALSE;
			}
			
			description=getElementText(holder, "description", cxpa);
			
			inspectorElement=(Element) cxpa.selectSingleNode(holder, "inspector");
			
			DomConfigFactory factory=new DomConfigFactory();
			NodeIterator nit=cxpa.selectNodeIterator(holder, "parameter");
			Node n;
			while ((n=nit.nextNode())!=null) {
				parameters.add(new ParameterEntry(((Element) n).getAttribute("name"),factory.create(n)));
			}
			
			nit=cxpa.selectNodeIterator(holder, "waive-case");
			while ((n=nit.nextNode())!=null) {
				waiveCases.add(cxpa.eval(n, "text()").toString());
			}	
			
			nit=cxpa.selectNodeIterator(holder, "waives");
			while ((n=nit.nextNode())!=null) {
				if (n instanceof Element) {
					Element waivesElement=(Element) n;
					if (waivesElement.hasAttribute("key")) {
						String wKey=waivesElement.getAttribute("key");
						String iName=cxpa.eval(n, "name/text()").toString();
						if (iName.trim().length()==0) {
							throw new HammurapiException("<waives> must have nested <name> element");
						}
						
						waivedInspectorNames.put(wKey, iName);
						waiveReasons.put(wKey, cxpa.eval(n, "reason/text()").toString());						
					} else {
						throw new HammurapiException("<waives> must have 'key' attribute");
					}
				}
			}	
			
			nit=cxpa.selectNodeIterator(holder, "after");
			while ((n=nit.nextNode())!=null) {
				afterInspectorNames.add(getElementText(n));
			}
			
			NodeList nl=holder.getChildNodes();
			for (int i=0; i<nl.getLength(); i++) {
				Node fn=nl.item(i);
				if (fn instanceof Element) {
					Element el=(Element) fn;
					if ("filter".equals(el.getNodeName())) {
						FilterNameEntry fne=new FilterNameEntry();
						if (el.hasAttribute("name")) {
							fne.name=el.getAttribute("name");
						} else if (el.hasAttribute("category")) {
							fne.category=el.getAttribute("category");
						} else {
							throw new ConfigurationException("<filter> element shall have either name or category attribute");
						}
						filterEntries.add(fne);
					} else if ("filter-exclude".equals(el.getNodeName())) {						
						FilterNameEntry fne=new FilterNameEntry();
						fne.exclude=true;
						if (el.hasAttribute("name")) {
							fne.name=el.getAttribute("name");
						} else if (el.hasAttribute("category")) {
							fne.category=el.getAttribute("category");
						} else {
							throw new ConfigurationException("<filter-exclude> element shall have either name or category attribute");
						}
						filterEntries.add(fne);						
					}
				}
			}
			//System.out.println(name);
		} catch (TransformerException e) {
			throw new HammurapiException(e);
		} catch (ConfigurationException e) {
			throw new HammurapiException(e);
		}
	}
	
	/**
	 * @return Returns the fixSample.
	 */
	public String getFixSample() {
		return fixSample;
	}

	/**
	 * @return Returns the isEnabled.
	 */
	public Boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return Returns the message.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the order.
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @return Returns the rationale.
	 */
	public String getRationale() {
		return rationale;
	}

	/**
	 * @return Returns the resources.
	 */
	public String getResources() {
		return resources;
	}

	/**
	 * @return Returns the severity.
	 */
	public Integer getSeverity() {
		return severity;
	}

	/**
	 * @return Returns the violationSample.
	 */
	public String getViolationSample() {
		return violationSample;
	}

	public String getDescription() {
		return description;
	}

	public Inspector getInspector() throws ConfigurationException {
		if (inspector==null && inspectorElement!=null) {
			DomConfigFactory factory=new DomConfigFactory();
			Object o = factory.create(inspectorElement);
			if (o instanceof Inspector) {
				inspector=(Inspector) o;				
				if (!getParameters().isEmpty()) {
					if (inspector instanceof Parameterizable) {
						Iterator it=getParameters().iterator();
						while (it.hasNext()) {
							ParameterEntry pe=(ParameterEntry) it.next();
							if (!((Parameterizable) inspector).setParameter(pe.getName(), pe.getValue())) {
							    throw new ConfigurationException(o.getClass().getName()+" does not support parameter "+pe.getName());
							}
						}
					} else {
						throw new ConfigurationException(inspector.getClass().getName()+" does not implement "+Parameterizable.class.getName());
					}
				}
			} else {
				throw new ConfigurationException(o.getClass().getName()+" doesn't implement "+Inspector.class.getName());
			}
		}
		return inspector;
	}

	public Collection getParameters() {
		return parameters;
	}

	public String getMessage(String key) {
		return (String) messages.get(key);
	}

	public Boolean isWaivable() {
		return isWaivable;
	}

	public Collection getWaiveCases() {
		return waiveCases;
	}

	private Map waivedInspectorNames=new HashMap();
	
	public String getWaivedInspectorName(String inspectorKey) {
		return (String) waivedInspectorNames.get(inspectorKey);
	}

	private Map waiveReasons=new HashMap();
	private Collection afterInspectorNames = new HashSet();
	
	public String getWaiveReason(String inspectorKey) {
		return (String) waiveReasons.get(inspectorKey);
	}

	public Collection getWaivedInspectorNames() {
		return waivedInspectorNames.values();
	}
	
	public Collection getAfterInspectorNames() {
		return afterInspectorNames;
	}

	public Collection getFilteredInspectorDesriptors(InspectorSet inspectorSet, Collection chain) {
		if (chain==null) {
			chain=new LinkedList();
		}
		
		Iterator it=filterEntries.iterator();
		while (it.hasNext()) {
			FilterNameEntry fne=(FilterNameEntry) it.next();
			if (fne.exclude) {
				if (fne.name==null) {
					Iterator dit=chain.iterator();
					while (dit.hasNext()) {
						if (fne.category.equals(((InspectorDescriptor) dit.next()).getCategory())) {
							dit.remove();
						}
					}
				} else {
					if ("*".equals(fne.name)) {
						chain.clear();
					} else {
						Iterator dit=chain.iterator();
						while (dit.hasNext()) {
							if (fne.name.equals(((InspectorDescriptor) dit.next()).getName())) {
								dit.remove();
							}
						}
					}
				}
			} else {
				if (fne.name==null) {
					Iterator dit=inspectorSet.getDescriptors().iterator();
					while (dit.hasNext()) {
						InspectorDescriptor inspectorDescriptor = (InspectorDescriptor) dit.next();
						if (fne.category.equals(inspectorDescriptor.getCategory())) {
							chain.add(inspectorDescriptor);
						}
					}
				} else {
					if ("*".equals(fne.name)) {
						chain.addAll(inspectorSet.getDescriptors());
					} else {
						Iterator dit=inspectorSet.getDescriptors().iterator();
						while (dit.hasNext()) {
							InspectorDescriptor inspectorDescriptor = (InspectorDescriptor) dit.next();
							if (fne.name.equals(inspectorDescriptor.getName())) {
								chain.add(inspectorDescriptor);
							}
						}
					}
				}				
			}
		}
		
		return chain;
	}
}
