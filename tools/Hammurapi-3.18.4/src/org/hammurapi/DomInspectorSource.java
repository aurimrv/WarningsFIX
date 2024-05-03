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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import com.pavelvlasov.config.ConfigurationException;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class DomInspectorSource implements InspectorSource {
    private Element holder;
	private String source;
    
    /** Creates a new instance of DomInspectorSource */
    public DomInspectorSource(Element holder, String source) {
        this.holder=holder;
        this.source=source;
    }
    
    public DomInspectorSource(InputStream in, String source) throws HammurapiException {
        load(in);
        this.source=source;
    }
    
    public DomInspectorSource(File f, String source) throws HammurapiException {
        try {
            load(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            throw new HammurapiException("File not found: "+f.getAbsolutePath(), e);
        }
        this.source=source;
    }
    
    private void load(InputStream in) throws HammurapiException {
        try {
            Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            holder=document.getDocumentElement();
        } catch (ParserConfigurationException e) {
            throw new HammurapiException(e.toString(), e);
        } catch (SAXException e) {
            throw new HammurapiException(e.toString(), e);
        } catch (IOException e) {
            throw new HammurapiException(e.toString(), e);
        }        
    }
    
    public void loadInspectors(InspectorSet inspectorSet) throws HammurapiException {
        if (holder.hasAttribute("base")) {
            try {
                URL baseURL=new URL(holder.getAttribute("base"));
                DomInspectorSource base=new DomInspectorSource(baseURL.openStream(), "URL: "+baseURL);
                base.loadInspectors(inspectorSet);
            } catch (MalformedURLException e) {
                throw new HammurapiException("Malformed base URL: "+e, e);
            } catch (IOException e) {
                throw new HammurapiException(e.toString(), e);
            }
        }
        
        try {
            NodeIterator inspectorsIterator=XPathAPI.selectNodeIterator(holder, "inspector-descriptor");
            Element inspectorElement;
            while ((inspectorElement=(Element) inspectorsIterator.nextNode())!=null) {
            	inspectorSet.addDescriptor(new DomInspectorDescriptor(inspectorElement));
            }        
        } catch (TransformerException e) {
            new HammurapiException(e);
        } catch (ConfigurationException e) {
        	new HammurapiException(e);
		}

        inspectorSet.addInspectorSourceInfo(new InspectorSourceInfo(holder.getAttribute("name"), source, holder.getAttribute("revision")));
    }
}
