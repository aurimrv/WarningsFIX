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
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class Licensor extends BasicDescriptor {

    private BasicDescriptor license;
    protected List packages=new ArrayList();

    public Licensor(TechStack stack) {
        super();
        this.stack=stack;
    }
    
    public Licensor(TechStack stack, Element holder, CachedXPathAPI cxpa) throws TransformerException {
        super(holder, cxpa);
        this.stack=stack;
        this.licenseRef=getElementText(holder, "license-ref", cxpa);
        Element licenseEl=(Element) cxpa.selectSingleNode(holder, "license");
        if (licenseEl!=null) {
            license=new BasicDescriptor(licenseEl, cxpa);
        }
        NodeIterator nit = cxpa.selectNodeIterator(holder, "package");
        Node n;
        while ((n=nit.nextNode())!=null) {
            packages.add(getElementText(n));
        }
    }

    private String licenseRef;

    public BasicDescriptor getLicense() {
        return license==null ? stack.findLicense(licenseRef) : license;        
    }

    protected TechStack stack;
    
    public void toDom(Element holder) {
        super.toDom(holder);
        if (license!=null) {
            license.toDom(addElement(holder, "license"));
        }
        
        addTextElement(holder, "license-ref", licenseRef);
        
        Iterator it=packages.iterator();
        while (it.hasNext()) {
            addTextElement(holder, "package", it.next().toString());
        }
    }
    
    public void setLicense(BasicDescriptor license) {
        this.license = license;
    }
    
    public void setLicenseRef(String licenseRef) {
        this.licenseRef = licenseRef;
    }

    public void addPackage(String packageName) {
        packages.add(packageName);
    }
}
