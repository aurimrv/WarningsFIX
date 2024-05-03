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

import org.hammurapi.results.AggregatedResults;
import org.hammurapi.results.BasicResults;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.DetailedResults;
import org.hammurapi.results.NamedResults;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class BasicResultsRenderer extends AbstractRenderer {
    
    public BasicResultsRenderer(RenderRequest request) {
        super(request);
    }
    
    public BasicResultsRenderer(RenderRequest request, String profile) {
        super(request, profile);
    }
    
    public Element render(Document document) throws RenderingException {
        Element ret=document.createElement("results");
        BasicResults br=(BasicResults) request.getRenderee();

        ret.setAttribute("violation-level", String.valueOf(br.getViolationLevel()));
        ret.setAttribute("reviews", String.valueOf(br.getReviewsNumber()));
        ret.setAttribute("code-base", String.valueOf(br.getCodeBase()));
        ret.setAttribute("violations", String.valueOf(br.getViolationsNumber()));
        ret.setAttribute("waived-violations", String.valueOf(br.getWaivedViolationsNumber()));
        ret.setAttribute("dpmo", br.getDPMO());
        ret.setAttribute("sigma", br.getSigma());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
        ret.setAttribute("date", sdf.format(br.getDate()));
        if (br.getMaxSeverity()!=null) {
            ret.setAttribute("max-severity", br.getMaxSeverity().toString());
        }
        
        return ret;
    }    
    
//	public static String format(double d) {
//        String ret=String.valueOf(d);
//        int idx=ret.lastIndexOf('.');
//        if (idx==-1) {
//            return ret+".00";
//        } else {
//            return (ret+"000").substring(0, idx+3);
//        }
//    }        
    
    public static BasicResultsRenderer newRenderer(RenderRequest request) {
        if (request.getRenderee() instanceof CompositeResults) {
            return new CompositeResultsRenderer(request);
        } else if (request.getRenderee() instanceof DetailedResults) {
            return new DetailedResultsRenderer(request);
        } else if (request.getRenderee() instanceof NamedResults) {
            return new NamedResultsRenderer(request);
        } else if (request.getRenderee() instanceof AggregatedResults) {
            return new AggregatedResultsRenderer(request);
        } else {
            return new BasicResultsRenderer(request);
        }
    }
}
