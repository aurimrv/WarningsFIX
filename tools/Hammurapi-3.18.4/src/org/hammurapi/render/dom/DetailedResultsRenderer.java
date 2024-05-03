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

package org.hammurapi.render.dom;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

import org.hammurapi.Waiver;
import org.hammurapi.WaiverEntry;
import org.hammurapi.results.DetailedResults;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class DetailedResultsRenderer extends NamedResultsRenderer {
    
    public DetailedResultsRenderer(RenderRequest request) {
        super(request);
    }
    
    public DetailedResultsRenderer(RenderRequest request, String profile) {
        super(request, profile);
    }
    
    public Element render(Document document) throws RenderingException {
        Element ret=super.render(document);
        Iterator it=((DetailedResults) request.getRenderee()).getViolations().iterator();
        while (it.hasNext()) {
            ViolationRenderer ver=new ViolationRenderer(new RenderRequest(it.next()));
            ret.appendChild(ver.render(document));
        }
        
        it=((DetailedResults) request.getRenderee()).getWaivedViolations().iterator();
        while (it.hasNext()) {
        	WaiverEntry waiverEntry=(WaiverEntry) it.next();
        	Element waivedViolationElement=document.createElement("waived-violation");
        	ret.appendChild(waivedViolationElement);
        	ViolationRenderer ver=new ViolationRenderer(new RenderRequest(waiverEntry.getViolation()));
        	waivedViolationElement.appendChild(ver.render(document));        	
        	waivedViolationElement.appendChild(renderWaiver(waiverEntry.getWaiver(), document));
        }
        
//        String[] im=((SimpleDetailedResults) renderee).getIncompleteMessages();
//        for (int i=0; i<im.length; i++) {
//            Element ie=document.createElement("incomplete");
//            ie.appendChild(document.createTextNode(im[i]));
//            ret.appendChild(ie);
//        }
//        
        return ret;
    }

	public static Node renderWaiver(Waiver waiver, Document document) {
		Element ret=document.createElement("waiver");
		
		appendTextElement("inspector-name", waiver.getInspectorName(), ret);
		Collection signatures = waiver.getSignatures();
		if (signatures!=null) {
			Iterator it=signatures.iterator();
			while (it.hasNext()) {
				appendTextElement("signature", (String) it.next(), ret);
			}
		}
		
		appendTextElement("reason", waiver.getReason(), ret);
		if (waiver.getExpirationDate()!=null) {
			SimpleDateFormat sdf=new SimpleDateFormat(Waiver.DATE_FORMAT);
			appendTextElement("expiration-date", sdf.format(waiver.getExpirationDate()), ret);
		}		
		
		return ret;
	}
}
