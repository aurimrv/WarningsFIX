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
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.pavelvlasov.xml.dom.DomSerializable;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class Publisher extends Licensor {
    
    public Publisher(TechStack stack) {
        super(stack);
    }
    
    public Publisher(TechStack stack, Element holder, CachedXPathAPI cxpa) throws TransformerException {
        super(stack, holder, cxpa);
        NodeIterator nit = cxpa.selectNodeIterator(holder, "product");
        Node n;
        while ((n=nit.nextNode())!=null) {
            new Product(stack, this, (Element) n, cxpa);
        }        
    }
        
    private Collection products=new ArrayList();
    
    void addProduct(Product product) {
        products.add(product);
        
    }
    
    public void toDom(Element holder) {
        super.toDom(holder);
        Iterator it=products.iterator();
        while (it.hasNext()) {
            ((DomSerializable) it.next()).toDom(addElement(holder, "product"));
        }
    }
    
    /**
     * Matches package name and adds clients if matched
     * @param packageName
     * @param clients
     * @return true if matched.
     */
    public boolean match(String packageName, Collection clients) {
        Iterator it=products.iterator();
        while (it.hasNext()) {
            Product product=(Product) it.next();
            if (product.match(packageName, clients)) {
                return true;
            }
        }
        return false;
    } 
    
    /**
     * Matches package with publisher prefix and returns 'product' part of package.
     * @param packageName
     * @return
     */
    public String[] match(String packageName) {
        Iterator it=packages.iterator();
        while (it.hasNext()) {
            String pn=(String) it.next();
            if (pn.equals(packageName)) {
                return new String[] {packageName, null};
            } else if (packageName.startsWith(pn+".")) {
                int idx=packageName.indexOf('.', pn.length()+1);
                if (idx==-1) {
                    return new String[] {pn, packageName.substring(pn.length()+1)};
                } else {
                    return new String[] {pn, packageName.substring(pn.length()+1, idx)};
                }
            }
        }   
        return null;
    }
}
