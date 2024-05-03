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

import org.hammurapi.HammurapiException;
import org.hammurapi.Inspector;
import org.hammurapi.InspectorDescriptor;
import org.hammurapi.InspectorSet;
import org.hammurapi.InspectorSourceInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;


/**
 * @author Pavel Vlasov
 * @version $Revision: 1.6 $
 */
public class InspectorSetRenderer extends AbstractRenderer {
    
    public InspectorSetRenderer(RenderRequest request) {
        super(request);
    }
    
    public Element render(Document document) throws RenderingException {
    	try {
	        InspectorSet rs=(InspectorSet) request.getRenderee();
	        Element ret=document.createElement("inspector-set");
	        Iterator it=rs.getInspectors().iterator();
	        while (it.hasNext()) {
	            Inspector r=(Inspector) it.next();
	            InspectorDescriptor descriptor = r.getContext().getDescriptor();
	            if (Boolean.TRUE.equals(descriptor.isEnabled())) {
					InspectorDescriptorRenderer rdr=new InspectorDescriptorRenderer(new RenderRequest(descriptor));
		            ret.appendChild(rdr.render(document));
	            }
	        }
	        
	        it=rs.getInspectorSourceInfos().iterator();
	        while (it.hasNext()) {
	        	Element se=document.createElement("source-info");
	        	ret.appendChild(se);
	        	((InspectorSourceInfo) it.next()).toDom(se);
	        }
	        
	        return ret;
    	} catch (ConfigurationException e) {
    		throw new RenderingException(e);
    	} catch (HammurapiException e) {
    		throw new RenderingException(e);
		}
    }
}
