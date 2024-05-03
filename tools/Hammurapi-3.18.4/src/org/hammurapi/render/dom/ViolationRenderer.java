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

import org.hammurapi.Violation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;
import com.pavelvlasov.render.dom.DomRenderer;
import com.pavelvlasov.review.Signed;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class ViolationRenderer extends AbstractRenderer {
    
    /** Creates a new instance of ViolationEntryRenderer */
    public ViolationRenderer(RenderRequest request) {
        super(request);
    }
    
    public Element render(Document document) throws RenderingException {
        Element ret=document.createElement("violation");
        Violation ve=(Violation) request.getRenderee();
        
        if (ve.getSource()!=null) {
	        int line = ve.getSource().getLine();
	        if (line!=0) {
	        	ret.setAttribute("line", String.valueOf(line));
	        }
	                
	        int column = ve.getSource().getColumn();
	        if (column!=0) {
	        	ret.setAttribute("col", String.valueOf(column));
	        }
	        
	        String sourceURL = ve.getSource().getSourceURL();
	        if (sourceURL!=null) {
				ret.setAttribute("source-url", sourceURL);
	        }
	        
	        if (ve.getSource() instanceof Signed) {
	        	String signature = ((Signed) ve.getSource()).getSignature();
	        	if (signature!=null) {
	        		ret.setAttribute("signature", signature);
	        	}
	        }
        }
        
        Element me=document.createElement("message");
        ret.appendChild(me);
        me.appendChild(document.createTextNode(ve.getMessage()));
        if (ve.getDescriptor()!=null) {
	        DomRenderer rdr=new InspectorDescriptorRenderer(new RenderRequest(ve.getDescriptor()));
	        ret.appendChild(rdr.render(document));
        }
        return ret;
    }
    
}
