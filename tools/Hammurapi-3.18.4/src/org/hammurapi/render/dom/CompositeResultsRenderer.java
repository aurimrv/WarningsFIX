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

import org.hammurapi.results.CompositeResults;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.DomRenderer;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class CompositeResultsRenderer extends DetailedResultsRenderer {
    public CompositeResultsRenderer(RenderRequest request) {
        super(request);
    }
    
    public CompositeResultsRenderer(RenderRequest request, String profile) {
        super(request, profile);
    }
    
    public Element render(Document document) throws RenderingException {
        Element ret=super.render(document);
        CompositeResults cr=(CompositeResults) request.getRenderee();
        ret.setAttribute("size", String.valueOf(cr.size()));
        Iterator it=cr.getChildren().iterator();
        while (it.hasNext()) {
            DomRenderer renderer=newRenderer(new RenderRequest(it.next()));
            ret.appendChild(renderer.render(document));
        }
        return ret;
    }
}
