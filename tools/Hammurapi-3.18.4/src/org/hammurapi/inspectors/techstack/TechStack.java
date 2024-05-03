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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.pavelvlasov.xml.dom.AbstractDomObject;
import com.pavelvlasov.xml.dom.DomSerializable;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class TechStack extends AbstractDomObject implements DomSerializable {
    private static final String PUBLISHER_DESCRIPTION = "This publisher was inferred " +
    		"by the technology stack inspector from the package name. " +
    		"You should explicitly add proper information into the technology stack inspector configuration " +
    		"file";
    private static final String PRODUCT_DESCRIPTION = "This product was inferred " +
			"by the technology stack inspector from the package name. " +
			"You should explicitly add proper information into the technology stack inspector configuration " +
			"file";
    
    private static final String LICENSE_DESCRIPTION = "This license was inferred " +
			"by the technology stack inspector from the package name. " +
			"You should explicitly add proper information into the technology stack inspector configuration " +
			"file";
    
    private static final String UNKNOWN_COMMERCIAL_LICENSE = "unknown commercial license";
    private static final String UNKNOWN_OS_LICENSE = "unknown open source license";
    private static final String UNKNOWN_LICENSE = "unknown license";
    private static final String JAVA_LICENSE = "Java license";
    
    private Collection publishers=new ArrayList();
    private Map licenses=new TreeMap();
    
    public TechStack() {
    	// Default constructor
    }
    
    public TechStack(Element holder) throws TransformerException {        
        CachedXPathAPI cxpa=new CachedXPathAPI();
        NodeIterator nit = cxpa.selectNodeIterator(holder, "publisher");
        Node n;
        while ((n=nit.nextNode())!=null) {
            publishers.add(new Publisher(this, (Element) n, cxpa));
        }        
       
        nit = cxpa.selectNodeIterator(holder, "license");
        while ((n=nit.nextNode())!=null) {
            BasicDescriptor baseDescriptor = new BasicDescriptor((Element) n, cxpa);
            licenses.put(baseDescriptor.getKey(), baseDescriptor);
        }        
    }

    public void toDom(Element holder) {
        Iterator it=licenses.values().iterator();
        while (it.hasNext()) {
            ((DomSerializable) it.next()).toDom(addElement(holder, "license"));
        }
        
        it=publishers.iterator();
        while (it.hasNext()) {
            ((DomSerializable) it.next()).toDom(addElement(holder, "publisher"));
        }                
    }
    
    /**
     * Converts first char to uppercase
     * @param str
     * @return
     */
    public static String capitalize(String str) {
        if (str==null) {
            return null;
        }
        
		switch (str.length()) {
			case 0: 
			    return str;
			case 1: 
			    return str.toUpperCase();
			default: 
			    return str.substring(0, 1).toUpperCase()+str.substring(1);
		}
    }
    
    /**
     * 
     * @param packageName
     * @param clients
     */
    public void addPackage(String packageName, Collection clients) {
        Iterator pit=publishers.iterator();
        while (pit.hasNext()) {
            Publisher publisher=(Publisher) pit.next();
            if (publisher.match(packageName, clients)) {
                return;
            }
        }  
        
        // Publisher's root packages
        pit=publishers.iterator();
        while (pit.hasNext()) {
            Publisher publisher=(Publisher) pit.next();
            String[] productMatch=publisher.match(packageName);
            if (productMatch!=null) {
                Product product=new Product(this, publisher);
                if (publisher.getLicense()==null) {
                    product.setLicenseRef(getUnknownLicense().getKey());
                } else {
                    product.setLicenseRef(publisher.getLicense().getKey());
                }
                if (productMatch[1]==null) {
                    product.setName(publisher.getName()+" flagship product");
                    product.setDescription(PRODUCT_DESCRIPTION);
                    product.addPackage(productMatch[0]);
                } else {
                    product.setName(capitalize(productMatch[1]));
                    product.setDescription(PRODUCT_DESCRIPTION);
                    if (publisher.getUrl()!=null) {
                        product.setUrl(publisher.getUrl()+"/products/"+productMatch[1]);
                    }
                    product.addPackage(productMatch[0]+"."+productMatch[1]);
                }
                product.addClients(clients);
                return;
            }
        }  
        
        // AI starts here.
        StringTokenizer st=new StringTokenizer(packageName, ".");
        String[] tokens=new String[st.countTokens()];
        for (int i=0; st.hasMoreTokens(); i++) {
            tokens[i]=st.nextToken();
        }
        
        if (tokens.length>1) {
            if ("com".equals(tokens[0])) {
                // Pattern com.<product>
                if (tokens.length==2) {                    
                    String productName = capitalize(tokens[1]);
                    Publisher publisher=new Publisher(this);
                    publisher.setName("Publisher of "+productName);
                    publisher.setUrl("http://www."+tokens[1]+".com");
                    publisher.setLicenseRef(getUnknownCommercialLicense().getKey());
                    publisher.setDescription(PUBLISHER_DESCRIPTION);
                    publishers.add(publisher);                    
                    
                    Product product=new Product(this, publisher);
                    product.setUrl("http://www."+tokens[1]+".com");
                    product.setName(productName);
                    product.setDescription(PRODUCT_DESCRIPTION);
                    product.setLicenseRef(getUnknownCommercialLicense().getKey());
                    product.addPackage("com."+tokens[1]);
                    product.addClients(clients);
                } else {
                    Publisher publisher=new Publisher(this);
                    publisher.setName(capitalize(tokens[1]));
                    publisher.setDescription(PUBLISHER_DESCRIPTION);
                    publisher.setUrl("http://www."+tokens[1]+".com");
                    publisher.addPackage(tokens[0]+"."+tokens[1]);
                    publisher.setLicenseRef(getUnknownCommercialLicense().getKey());
                    publishers.add(publisher);                
                    
                    Product product=new Product(this, publisher);
                    product.setUrl("http://www."+tokens[1]+".com/products/"+tokens[2]);
                    product.setName(capitalize(tokens[2]));
                    product.setDescription(PRODUCT_DESCRIPTION);
                    product.setLicenseRef(getUnknownCommercialLicense().getKey());
                    product.addPackage("com."+tokens[1]+"."+tokens[2]);                    
                    product.addClients(clients);
                }
                return;
            } else if ("org".equals(tokens[0]) || "net".equals(tokens[0])) {
                if (tokens.length==2) {
                    String productName = capitalize(tokens[1]);
                    Publisher publisher=new Publisher(this);
                    publisher.setName("Publisher of "+productName);
                    publisher.setDescription(PUBLISHER_DESCRIPTION);
                    publisher.setUrl("http://www."+tokens[1]+"."+tokens[0]);
                    publisher.setLicenseRef(getUnknownOpenSourceLicense().getKey());
                    publishers.add(publisher);
                    
                    Product product=new Product(this, publisher);
                    product.setUrl("http://www."+tokens[1]+"."+tokens[0]);
                    product.setName(productName);
                    product.setDescription(PRODUCT_DESCRIPTION);
                    product.setLicenseRef(getUnknownOpenSourceLicense().getKey());
                    product.addPackage(tokens[0]+"."+tokens[1]);
                    product.addClients(clients);
                } else {
                    Publisher publisher=new Publisher(this);
                    publisher.setName(capitalize(tokens[1]));
                    publisher.setDescription(PUBLISHER_DESCRIPTION);
                    publisher.setUrl("http://www."+tokens[1]+"."+tokens[0]);
                    publisher.addPackage(tokens[0]+"."+tokens[1]);
                    publisher.setLicenseRef(getUnknownOpenSourceLicense().getKey());
                    publishers.add(publisher);
                    
                    Product product=new Product(this, publisher);
                    product.setUrl("http://www."+tokens[1]+"."+tokens[0]+"/products/"+tokens[2]);
                    product.setName(capitalize(tokens[2]));
                    product.setDescription(PRODUCT_DESCRIPTION);
                    product.setLicenseRef(getUnknownOpenSourceLicense().getKey());
                    product.addPackage(tokens[0]+"."+tokens[1]+"."+tokens[2]);                    
                    product.addClients(clients);
                }      
                return;
            }
        }
        
        if (tokens.length==0 || "(default)".equals(packageName) || "java".equals(tokens[0]) || "javax".equals(tokens[0])) {
            Publisher publisher=new Publisher(this);
            publisher.setName("Sun Microsystems");
            publisher.setUrl("http://www.sun.com");
            publishers.add(publisher);
            
            Product product=new Product(this, publisher);
            product.setName("Java platform");
            product.setUrl("http://java.sun.com");
            product.setLicenseRef(getJavaLicense().getKey());
            product.addPackage("(default)");                
            product.addPackage("java");                
            product.addPackage("javax");                
            product.addClients(clients);        
            return;
        }
        
        Publisher publisher=new Publisher(this);
        publisher.setName("Publisher of "+packageName);
        publisher.setDescription(PUBLISHER_DESCRIPTION);
        publishers.add(publisher);
        
        Product product=new Product(this, publisher);
        product.setName(packageName);
        product.setDescription(PRODUCT_DESCRIPTION);
        product.setLicenseRef(getUnknownLicense().getKey());
        product.addPackage(packageName);                
        product.addClients(clients);
    }
    
    private BasicDescriptor getUnknownCommercialLicense() {
        if (unknownCommercialLicense==null) {
            unknownCommercialLicense=new BasicDescriptor();
            unknownCommercialLicense.setKey(UNKNOWN_COMMERCIAL_LICENSE);
            unknownCommercialLicense.setName("Unknown commercial license");
            unknownCommercialLicense.setDescription(LICENSE_DESCRIPTION);
            unknownCommercialLicense.setCategory("commercial");
            licenses.put(unknownCommercialLicense.getKey(), unknownCommercialLicense);
        }
        return unknownCommercialLicense;
    }

    private BasicDescriptor getUnknownOpenSourceLicense() {
        if (unknownOpenSourceLicense==null) {
            unknownOpenSourceLicense=new BasicDescriptor();
            unknownOpenSourceLicense.setKey(UNKNOWN_OS_LICENSE);
            unknownOpenSourceLicense.setName("Unknown open source license");
            unknownOpenSourceLicense.setDescription(LICENSE_DESCRIPTION);
            unknownOpenSourceLicense.setCategory("open source");
            licenses.put(unknownOpenSourceLicense.getKey(), unknownOpenSourceLicense);
        }
        return unknownOpenSourceLicense;
    }

    private BasicDescriptor getUnknownLicense() {
        if (unknownLicense==null) {
            unknownLicense=new BasicDescriptor();
            unknownLicense.setKey(UNKNOWN_LICENSE);
            unknownLicense.setName("Unknown license");
            unknownLicense.setDescription(LICENSE_DESCRIPTION);
            licenses.put(unknownLicense.getKey(), unknownLicense);
        }
        return unknownLicense;
    }

    private BasicDescriptor getJavaLicense() {
        if (javaLicense==null) {
            javaLicense=new BasicDescriptor();
            javaLicense.setKey(JAVA_LICENSE);
            javaLicense.setName("Java license");
            licenses.put(javaLicense.getKey(), javaLicense);
        }
        return javaLicense;
    }

    private BasicDescriptor unknownCommercialLicense;
    private BasicDescriptor unknownOpenSourceLicense;
    private BasicDescriptor unknownLicense;
    private BasicDescriptor javaLicense;
    
    /**
     * @param license key
     * @return license
     */
    public BasicDescriptor findLicense(String license) {
        return license==null ? null : (BasicDescriptor) licenses.get(license);
    }

}
