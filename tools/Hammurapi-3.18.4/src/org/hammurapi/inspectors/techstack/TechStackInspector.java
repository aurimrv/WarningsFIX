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
package org.hammurapi.inspectors.techstack;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.hammurapi.ParameterizableInspectorBase;
import org.hammurapi.HammurapiException;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;
import org.hammurapi.results.AnnotationContext.FileEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.XmlSource;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.xml.dom.AbstractDomObject;
import com.pavelvlasov.xml.dom.DOMUtils;

/**
 * Creates annotation, which displays dependencies on external packages.
 * @author Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class TechStackInspector extends ParameterizableInspectorBase {
    
    private XmlSource config;
    private XmlSource style;
    private XmlSource licenseSummaryStyle;
    private XmlSource licenseClientsStyle;
    private XmlSource productClientsStyle;
    private XmlSource publisherClientsStyle;

    public TechStackInspector() {
        config=new XmlSource("config", getClass(), ".xml");
        addConfigurator(config);
        
        style=new XmlSource("style", getClass(), ".xsl");
        addConfigurator(style);
        
        licenseSummaryStyle=new XmlSource("license-summary-style", getClass(), "!licenses.xsl");
        addConfigurator(licenseSummaryStyle);
        
        licenseClientsStyle=new XmlSource("license-clients-style", getClass(), "!license-clients.xsl");
        addConfigurator(licenseClientsStyle);
        
        productClientsStyle=new XmlSource("product-clients-style", getClass(), "!product-clients.xsl");
        addConfigurator(productClientsStyle);
        
        publisherClientsStyle=new XmlSource("publisher-clients-style", getClass(), "!publisher-clients.xsl");
        addConfigurator(publisherClientsStyle);
    }
	
	public void leave(final Repository repo) {
	    try {
            Document configDoc=config.getConfigDocument();
    	    final TechStack techStack = configDoc==null ? new TechStack() : new TechStack(configDoc.getDocumentElement());
			Iterator it=repo.getExternalSuppliers().entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry entry=(Map.Entry) it.next();
			    String packageName=(String) entry.getKey();
			    Collection clients=new TreeSet();
			    Iterator cit=repo.getExternalSupplierClients(packageName).iterator();
			    while (cit.hasNext()) {
			        CompilationUnit cu=(CompilationUnit) cit.next();
			        String cpn = cu.getPackage().getName();
			        if (cpn.length()==0) {
			            clients.add(cu.getName());
			        } else {
			            clients.add(cpn.replace('.','/')+"/"+cu.getName());
			        }
			    }
			    techStack.addPackage(packageName, clients);
			}
			
			final Document techStackDoc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			techStack.toDom(AbstractDomObject.addElement(techStackDoc, "tech-stack"));
    	    			
    		context.annotate(new LinkedAnnotation() {
    			String path;

    			public String getPath() {
    				return path;
    			}

    			public String getName() {
    				return "Technology stack";
    			}

    			public void render(AnnotationContext context) throws HammurapiException {
    			    if (".HTML".equalsIgnoreCase(context.getExtension())) {
        				FileEntry root = context.getNextFile(context.getExtension());
        				path=root.getPath();
        				Element tsRoot = techStackDoc.getDocumentElement();
                        tsRoot.setAttribute("root-path", root.getPath());
        				
        				FileEntry licenseSummary = context.getNextFile(context.getExtension());
        				tsRoot.setAttribute("license-summary", licenseSummary.getPath());
        				
        				CachedXPathAPI cxpa=new CachedXPathAPI();
        				
        				try {
        				    Map params=new HashMap();
	        				NodeIterator nit=cxpa.selectNodeIterator(tsRoot, "publisher/product[client]");
	    			        Element element;	    			        
	    			        while ((element=(Element) nit.nextNode())!=null) {
	    			            FileEntry fe=context.getNextFile(context.getExtension());
	    			            element.setAttribute("path", fe.getPath());
	    			            params.put("product", element.getAttribute("key"));
	    			            DOMUtils.style(techStackDoc, fe.getFile(), productClientsStyle.getStream(), params);
	    			        }
	    			        
	        				nit=cxpa.selectNodeIterator(tsRoot, "publisher[client]");
	    			        while ((element=(Element) nit.nextNode())!=null) {
	    			            FileEntry fe=context.getNextFile(context.getExtension());
	    			            element.setAttribute("path", fe.getPath());
	    			            params.put("publisher", element.getAttribute("key"));
	    			            DOMUtils.style(techStackDoc, fe.getFile(), publisherClientsStyle.getStream(), params);
	    			        }
	    			        
	        				nit=cxpa.selectNodeIterator(tsRoot, "//license[client]");
	    			        while ((element=(Element) nit.nextNode())!=null) {
	    			            FileEntry fe=context.getNextFile(context.getExtension());
	    			            element.setAttribute("path", fe.getPath());
	    			            params.put("license", element.getAttribute("key"));
	    			            DOMUtils.style(techStackDoc, fe.getFile(), licenseClientsStyle.getStream(), params);
	    			        }
	    			        
    			            DOMUtils.style(techStackDoc, root.getFile(), style.getStream(), null);
    			            DOMUtils.style(techStackDoc, licenseSummary.getFile(), licenseSummaryStyle.getStream(), null);
	    			        
	        				DOMUtils.serialize(techStackDoc,new File("techStack.xml"));
        				} catch (IOException e) {
        				    throw new HammurapiException("Could not render tech stack to HTML: "+e, e);
        				} catch (TransformerException e) {
        				    throw new HammurapiException("Could not render tech stack to HTML: "+e, e);
                        } catch (ConfigurationException e) {
        				    throw new HammurapiException("Could not render tech stack to HTML: "+e, e);
                        }
    			    } else {
        				FileEntry root = context.getNextFile(context.getExtension());
        				path=root.getPath();
        				try {
                            DOMUtils.serialize(techStackDoc,root.getFile());
                        } catch (IOException e) {
                            throw new HammurapiException("Could not render tech stack to xml: "+e,e);
                        } catch (TransformerException e) {
                            throw new HammurapiException("Could not render tech stack to xml: "+e,e);
						}        				    			        
    			    }
    			}

    			public Properties getProperties() {
    				return null;
    			}				
    		});
    		
    	    getContext().getSession().setAttribute("tech-stack", techStack);    	    
        } catch (ConfigurationException e) {
            disable("Cannot load tech stack configuration: "+e);
        } catch (TransformerException e) {
            disable("Cannot load tech stack configuration: "+e);
        } catch (JselException e) {        	
            context.warn(null, "Cannot obtain external suppliers information: "+e);
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            context.warn(null, "Could not save tech stack to file: "+e);
        } catch (FactoryConfigurationError e) {
            context.warn(null, "Could not save tech stack to file: "+e);
        }
	}
}
