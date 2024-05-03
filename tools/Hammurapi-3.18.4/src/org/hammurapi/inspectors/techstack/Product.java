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

import java.util.Collection;
import java.util.Iterator;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Element;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class Product extends Licensor {

    private Publisher publisher;
    
   public Product(TechStack stack, Publisher publisher) {
        super(stack);
        this.publisher=publisher;
        publisher.addProduct(this);
    }
    
    /**
     * @param holder
     * @param cxpa
     * @throws TransformerException
     */
    public Product(TechStack stack, Publisher publisher, Element holder, CachedXPathAPI cxpa) throws TransformerException {
        super(stack, holder, cxpa);
        this.publisher=publisher;
        publisher.addProduct(this);
        this.stack=stack;
        ignore="yes".equals(holder.getAttribute("ignore"));
    }
    
    public void addClient(String client) {
        if (!ignore) {
	        super.addClient(client);
	        getLicense().addClient(client);
	        publisher.addClient(client);
        }
    }
    
    private boolean ignore;

    public boolean isIgnore() {
        return ignore;
    }
    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
    
    public void toDom(Element holder) {
        super.toDom(holder);
        holder.setAttribute("ignore", ignore ? "yes" : "no");
    }
    
    /**
     * @param packageName
     * @return Matched package name or null
     */
    public boolean match(String packageName, Collection clients) {
        Iterator it=packages.iterator();
        while (it.hasNext()) {
            String pn=(String) it.next();
            if (pn.equals(packageName) || packageName.startsWith(pn+".")) {
                Iterator cit=clients.iterator();
                while (cit.hasNext()) {
                    addClient((String) cit.next());
                }
                return true;
            }
        }   
        return false;
    }

    public BasicDescriptor getLicense() {
        BasicDescriptor ret=super.getLicense();
        return ret==null ? publisher.getLicense() : ret; 
    }

    public void addClients(Collection clients) {
        Iterator it=clients.iterator();
        while (it.hasNext()) {
            addClient((String) it.next());
        }
        
    }    
}
