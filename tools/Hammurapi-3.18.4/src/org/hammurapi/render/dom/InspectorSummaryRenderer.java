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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hammurapi.results.InspectorSummary;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.review.SourceMarkerComparator;
import com.pavelvlasov.review.SourceMarkerRenderer;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.5 $
 */
public class InspectorSummaryRenderer extends AbstractRenderer {
    
    public InspectorSummaryRenderer(RenderRequest request) {
        super(request);
    }
    
    public InspectorSummaryRenderer(RenderRequest request, String profile) {
        super(request, profile);
    }
    
    public Element render(Document document) throws RenderingException {
        Element ret=document.createElement("inspector-summary");
        InspectorSummary is=(InspectorSummary) request.getRenderee();
        
        ret.setAttribute("inspector", is.getName());
        ret.setAttribute("description", is.getDescription());
        ret.setAttribute("severity", is.getSeverity().toString());
        ret.setAttribute("baseline", String.valueOf(is.getBaseLineLocationsCount()));
        ret.setAttribute("count", String.valueOf(is.getLocationsCount()));
        
        if (is.getVersion()!=null) {
        	ret.setAttribute("version", is.getVersion());
        }
        
    	String inspectorConfigInfo=is.getConfigInfo();
    	if (inspectorConfigInfo!=null) {
        	ret
			.appendChild(document.createElement("config-info"))
			.appendChild(document.createTextNode(inspectorConfigInfo));
    	}
        
    	List locations=is.getLocations();
    	if (locations!=null) {
    		locations=new ArrayList(locations);
	        Collections.sort(locations, new SourceMarkerComparator());
	        Iterator smit=locations.iterator();
	        while (smit.hasNext()) {
	        	SourceMarkerRenderer smr=new SourceMarkerRenderer(new RenderRequest((SourceMarker) smit.next()));
	        	ret.appendChild(smr.render(document));
	        }
    	}
        return ret;
	}
}
