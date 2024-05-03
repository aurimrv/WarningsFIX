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

import org.hammurapi.HammurapiMeasurement;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import com.pavelvlasov.metrics.MetricRenderer;
import com.pavelvlasov.metrics.Metric.Measurement;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.review.SourceMarkerRenderer;

/**
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.1 $
 */
public class HammurapiMetricRenderer extends MetricRenderer {

	/**
	 * @param request
	 */
	public HammurapiMetricRenderer(RenderRequest request) {
		super(request);
	}
	
	

	/**
	 * @param request
	 * @param profile
	 */
	public HammurapiMetricRenderer(RenderRequest request, String profile) {
		super(request, profile);
	}
	
	protected void renderMeasurement(Measurement ms, Element me) throws DOMException, RenderingException {
		super.renderMeasurement(ms, me);
		if (ms instanceof HammurapiMeasurement && ((HammurapiMeasurement) ms).getSource()!=null) {
			me.appendChild(new SourceMarkerRenderer(new RenderRequest(((HammurapiMeasurement) ms).getSource())).render(me.getOwnerDocument()));
		}
	}
}
