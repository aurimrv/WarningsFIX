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
import java.util.TreeSet;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Element;

import com.pavelvlasov.xml.dom.AbstractDomObject;
import com.pavelvlasov.xml.dom.DomSerializable;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class BasicDescriptor extends AbstractDomObject implements DomSerializable {
    private String key=getClass().getName()+":"+Long.toString(counter++, Character.MAX_RADIX);;
    private String name;
    private String description;
    private String url;
    private String category;
    private static long counter=System.currentTimeMillis();
        
    public BasicDescriptor() {
    }    
    
    public BasicDescriptor(Element holder, CachedXPathAPI cxpa) throws TransformerException {
        if (holder.hasAttribute("key")) {
            key=holder.getAttribute("key");
        }
        name=getElementText(holder, "name", cxpa);
        description=getElementText(holder, "description", cxpa);
        url=getElementText(holder, "url", cxpa);
        this.category=getElementText(holder, "category", cxpa);
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public void toDom(Element holder) {
        if (key!=null) {
            holder.setAttribute("key", key);
        }
        
        addTextElement(holder, "name", name);
        addTextElement(holder, "description", description);
        addTextElement(holder, "url", url);
        addTextElement(holder, "category", category);
        
        Iterator it=clients.iterator();
        while (it.hasNext()) {
            addTextElement(holder, "client", it.next().toString());
        }        
    }

    private Collection clients = new TreeSet();

    /**
     * @param client Compilation unit name with path.
     */
    public void addClient(String client) {
        clients.add(client);
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
