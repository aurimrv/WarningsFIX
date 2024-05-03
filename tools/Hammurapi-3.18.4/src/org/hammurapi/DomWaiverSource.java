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
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class DomWaiverSource implements WaiverSource {
    private Element holder;
    
    public DomWaiverSource(Element holder) {
        this.holder=holder;
    }
    
    public DomWaiverSource(InputStream in) throws HammurapiException {
        load(in);
    }
    
    public DomWaiverSource(File f) throws HammurapiException {
        try {
            load(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            throw new HammurapiException("File not found: "+f.getAbsolutePath(), e);
        }
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
    
    public void loadWaivers(WaiverSet waiverSet, Date now) throws HammurapiException {
        if (holder.hasAttribute("base")) {
            try {
                URL baseURL=new URL(holder.getAttribute("base"));
                DomWaiverSource base=new DomWaiverSource(baseURL.openStream());
                base.loadWaivers(waiverSet, now);
            } catch (MalformedURLException e) {
                throw new HammurapiException("Malformed base URL: "+e, e);
            } catch (IOException e) {
                throw new HammurapiException(e.toString(), e);
            }
        }
        
        try {
            NodeIterator waiverIterator=XPathAPI.selectNodeIterator(holder, "waiver");
            Element waiverElement;
            while ((waiverElement=(Element) waiverIterator.nextNode())!=null) {
            	waiverSet.addWaiver(new DomWaiver(waiverElement), now);
            }        
        } catch (TransformerException e) {
            new HammurapiException(e);
		}
    }    
}
