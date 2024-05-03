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

import org.hammurapi.results.NamedResults;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class NamedResultsRenderer extends AggregatedResultsRenderer {
    
    /** Creates a new instance of NamedResultsRenderer */
    public NamedResultsRenderer(RenderRequest request) {
        super(request);
    }
    
    public NamedResultsRenderer(RenderRequest request, String profile) {
        super(request, profile);
    }
    
    public Element render(Document document) throws RenderingException {
        Element ret=super.render(document);
        ret.setAttribute("name", ((NamedResults) request.getRenderee()).getName());
        return ret;
    }        
}
