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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hammurapi.HammurapiMeasurement;
import org.hammurapi.Violation;
import org.hammurapi.results.AggregatedResults;
import org.hammurapi.results.Annotation;
import org.hammurapi.results.InlineAnnotation;
import org.hammurapi.results.InspectorSummary;
import org.hammurapi.results.LinkedAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.metrics.Metric;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.review.SourceMarker;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.9 $
 */
public class AggregatedResultsRenderer extends BasicResultsRenderer {
    
    public AggregatedResultsRenderer(RenderRequest request) {
        super(request);
    }
    
    public AggregatedResultsRenderer(RenderRequest request, String profile) {
        super(request, profile);
    }
    
    public Element render(Document document) throws RenderingException {    	
        Element ret=super.render(document);
        AggregatedResults ar=(AggregatedResults) request.getRenderee();

        ret.setAttribute("new", ar.isNew() ? "yes" : "no");
        
        Map severitySummary = ar.getSeveritySummary();
        if (severitySummary!=null) {
			Iterator it=severitySummary.entrySet().iterator();
	        while (it.hasNext()) {
	            Map.Entry entry=(Map.Entry) it.next();
	            Element summaryElement=document.createElement("severity-summary");
	            ret.appendChild(summaryElement);            
	            summaryElement.setAttribute("severity", entry.getKey().toString());
	            List srs=new LinkedList(((Map) entry.getValue()).values());
	            Collections.sort(srs);
	            inspectorSummary(srs, summaryElement);
	        }
        }
        
        Collection warnings = ar.getWarnings();
		if (warnings!=null && !warnings.isEmpty()) {
        	Element we=document.createElement("warnings");
        	ret.appendChild(we);
        	Iterator wit=warnings.iterator();
        	while (wit.hasNext()) {
        		Violation warning=(Violation) wit.next();
        		ViolationRenderer vr=new ViolationRenderer(new RenderRequest(warning));
        		we.appendChild(vr.render(document));
        	}
        }  
        
        Collection annotations = ar.getAnnotations();
		if (annotations!=null && !annotations.isEmpty()) {
        	Element annotationsElement=document.createElement("annotations");
        	ret.appendChild(annotationsElement);
        	Iterator ait=annotations.iterator();
        	while (ait.hasNext()) {
        		Annotation annotation=(Annotation) ait.next();
        		Element annotationElement=document.createElement("annotation");
        		annotationsElement.appendChild(annotationElement);
        		annotationElement.setAttribute("name", annotation.getName());
        		if (annotation instanceof LinkedAnnotation) {
        			annotationElement.setAttribute("path", ((LinkedAnnotation) annotation).getPath());
        		}
        		
        		if (annotation instanceof InlineAnnotation) {
        			Element annotationContentElement=document.createElement("content");
        			annotationElement.appendChild(annotationContentElement);
        			annotationContentElement.appendChild(document.createTextNode(((InlineAnnotation) annotation).getContent()));
        		}
        		
        		Properties properties=annotation.getProperties();
        		if (properties!=null) {
        			Iterator prit=properties.entrySet().iterator();
        			while (prit.hasNext()) {
        				Map.Entry entry=(Map.Entry) prit.next();
        				Element propertyElement=document.createElement("property");
        				annotationElement.appendChild(propertyElement);
        				propertyElement.setAttribute("name", (String) entry.getKey());
        				propertyElement.appendChild(document.createTextNode((String) entry.getValue()));
        			}
        		}
        	}
        }  

        Map metrics = ar.getMetrics();
        if (metrics!=null) {
			Iterator it=metrics.entrySet().iterator();
	        while (it.hasNext()) {
	            Map.Entry entry=(Map.Entry) it.next();
	            Element metricElement=document.createElement("metric");            
	            ret.appendChild(metricElement);
	            metricElement.setAttribute("name", (String) entry.getKey());
	            Metric metric=(Metric) entry.getValue();
	            metricElement.setAttribute("number", String.valueOf(metric.getNumber()));
	            metricElement.setAttribute("avg", format(metric.getAvg()));
	            metricElement.setAttribute("min", format(metric.getMin()));
	            metricElement.setAttribute("max", format(metric.getMax()));            
	            metricElement.setAttribute("total", format(metric.getTotal()));
	            if (metric.getMeasurements()!=null) {
	            	metricElement.setAttribute("has-measurements", "yes");
	            }
	        }
        }
        
        if (ar.getBaseLine()!=null) {
        	Element baseLineElement=document.createElement("baseline");
        	ret.appendChild(baseLineElement);
        	baseLineElement.appendChild(new BasicResultsRenderer(new RenderRequest(ar.getBaseLine())).render(document));
        }
        
        return ret;
    }    
    
//    /**
//	 * @param metricElement
//	 * @param metric
//	 */
//	private void renderMeasurement(Element metricElement, String prefix, Metric.Measurement measurement) {
//		if (measurement instanceof HammurapiMeasurement) {
//			SourceMarker source=((HammurapiMeasurement) measurement).getSource();
//			if (source.getSourceURL()!=null) {
//				metricElement.setAttribute(prefix+"-source", source.getSourceURL());
//			}
//			
//			metricElement.setAttribute(prefix+"-line", String.valueOf(source.getLine()));
//			metricElement.setAttribute(prefix+"-col", String.valueOf(source.getColumn()));
//		}
//	}

	private void inspectorSummary(Collection inspectorSummaryList, Element holder) {
		Iterator it=inspectorSummaryList.iterator();
        while (it.hasNext()) {
            Element summaryElement=holder.getOwnerDocument().createElement("inspector-summary");
            holder.appendChild(summaryElement);
            InspectorSummary rsEntry=(InspectorSummary) it.next();
            summaryElement.setAttribute("inspector", rsEntry.getName());
            if (rsEntry.getVersion()!=null) {
            	summaryElement.setAttribute("version", rsEntry.getVersion());
            }
            summaryElement.setAttribute("description", rsEntry.getDescription());
            summaryElement.setAttribute("severity", rsEntry.getSeverity().toString());
            summaryElement.setAttribute("count", String.valueOf(rsEntry.getLocationsCount()));
            summaryElement.setAttribute("baseline", String.valueOf(rsEntry.getBaseLineLocationsCount()));
            if (rsEntry.getLocations()!=null) {
            	summaryElement.setAttribute("has-locations", "yes");
            }
        }
	}

	public static String format(double d) {
        String ret=String.valueOf(d);
        int idx=ret.lastIndexOf('.');
        if (idx==-1) {
            return ret+".00";
        } else {
            return (ret+"000").substring(0, idx+3);
        }
    }            
}
