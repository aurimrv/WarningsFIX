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

import java.util.Iterator;

import org.hammurapi.Inspector;
import org.hammurapi.InspectorDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class InspectorDescriptorRenderer extends AbstractRenderer {
    
    /** Creates a new instance of InspectorDescriptorRendrerer */
    public InspectorDescriptorRenderer(RenderRequest request) {
        super(request);
    }
    
    public Element render(Document document) throws RenderingException {
        Element ret=document.createElement("inspector-descriptor");
        InspectorDescriptor descriptor=(InspectorDescriptor) request.getRenderee();

        if (descriptor.getDescription()!=null) {
            ret
			.appendChild(document.createElement("description"))
			.appendChild(document.createTextNode(descriptor.getDescription()));
        }
        
        if (descriptor.getMessage()!=null) {
        	ret
			.appendChild(document.createElement("message"))
			.appendChild(document.createTextNode(descriptor.getMessage()));
        }
        
        try {
	        if (descriptor.getInspector()!=null) {
	        	Element inspectorElement=document.createElement("inspector");
	        	inspectorElement.setAttribute("type", descriptor.getInspector().getClass().getName());
	        	ret.appendChild(inspectorElement);
	        }
        } catch (ConfigurationException e) {
        	throw new RenderingException(e);
        }
        
        if (descriptor.isEnabled()!=null) {
        	ret
			.appendChild(document.createElement("enabled"))
			.appendChild(document.createTextNode(Boolean.TRUE.equals(descriptor.isEnabled()) ? "yes" : "no"));
        }
        
        if (descriptor.isWaivable()!=null) {
        	ret
			.appendChild(document.createElement("waivable"))
			.appendChild(document.createTextNode(Boolean.TRUE.equals(descriptor.isWaivable()) ? "yes" : "no"));
        }
        
        if (descriptor.getName()!=null) {
        	ret
			.appendChild(document.createElement("name"))
			.appendChild(document.createTextNode(descriptor.getName()));
        }
        
        if (descriptor.getSeverity()!=null) {
        	ret
			.appendChild(document.createElement("severity"))
			.appendChild(document.createTextNode(descriptor.getSeverity().toString()));
        }
        
        if (descriptor.getOrder()!=null) {
        	ret
			.appendChild(document.createElement("order"))
			.appendChild(document.createTextNode(descriptor.getOrder().toString()));
        }
        
        if (descriptor.getRationale()!=null) {
        	ret
			.appendChild(document.createElement("rationale"))
			.appendChild(document.createTextNode(descriptor.getRationale()));
        }
        
        if (descriptor.getViolationSample()!=null) {
        	ret
			.appendChild(document.createElement("violation-sample"))
			.appendChild(document.createTextNode(descriptor.getViolationSample()));
        }

        if (descriptor.getFixSample()!=null) {
        	ret
			.appendChild(document.createElement("fix-sample"))
			.appendChild(document.createTextNode(descriptor.getFixSample()));
        }
        
        if (descriptor.getResources()!=null) {
        	ret
			.appendChild(document.createElement("resources"))
			.appendChild(document.createTextNode(descriptor.getResources()));
        }

        if (descriptor.getCategory()!=null) {
        	ret
			.appendChild(document.createElement("category"))
			.appendChild(document.createTextNode(descriptor.getCategory()));
        }
        
//        Iterator it=descriptor.getParameters().entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry entry=(Map.Entry) it.next();
//            Element e=document.createElement("parameter");
//            ret.appendChild(e);
//            e.setAttribute("name", (String) entry.getKey());
//            e.appendChild(document.createTextNode((String) entry.getValue()));
//        }
        
        Iterator it=descriptor.getWaiveCases().iterator();
		while (it.hasNext()) {			
			Element e=document.createElement("waive-case");
			ret.appendChild(e);			
			e.appendChild(document.createTextNode((String) it.next()));
		}        
        
        try {
	        Inspector inspector=descriptor.getInspector();
	        if (inspector!=null) {
	        	String inspectorConfigInfo=inspector.getConfigInfo();
	        	if (inspectorConfigInfo!=null) {
	            	ret
					.appendChild(document.createElement("config-info"))
					.appendChild(document.createTextNode(inspectorConfigInfo));        		
	        	}
	        }
        } catch (ConfigurationException e) {
        	throw new RenderingException("Unable to read inspector configuration info", e);
        }
        
        return ret;
    }    
}
