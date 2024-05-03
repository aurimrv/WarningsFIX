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

package org.hammurapi.results.simple;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.hammurapi.HammurapiException;
import org.hammurapi.Violation;
import org.hammurapi.Waiver;
import org.hammurapi.WaiverSet;
import org.hammurapi.results.AggregatedResults;
import org.hammurapi.results.Annotation;
import org.hammurapi.results.BasicResults;
import org.hammurapi.results.InspectorSummary;

import com.pavelvlasov.metrics.Metric;
import com.pavelvlasov.metrics.SimpleMetric;
import com.pavelvlasov.metrics.Metric.Measurement;
import com.pavelvlasov.review.SourceMarker;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.10 $
 */
public class SimpleAggregatedResults implements AggregatedResults, Serializable {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3469850853510240052L;
	protected long codeBase=0;
    protected long reviews=0;
    protected double violationLevel=0;
    private int violationsNumber=0;
    private int waivedViolationsNumber=0;
    private Map metrics=new HashMap();
    private Set warnings=new TreeSet();
    
    public SimpleAggregatedResults(WaiverSet waiverSet) {
    	getContext().waiverSet=waiverSet;
    }
    
    /**
     * Map( severity (Integer) -> Map(string -> InspectorSummary))
     * 
     */
    protected Map severitySummary=new TreeMap();
    
    /**
     * Add violation to severity summary
     * @param violation
     */
    public Waiver addViolation(Violation violation) throws HammurapiException {
		Context context = getContext();
		Waiver ret = context.waiverSet==null ? null : context.waiverSet.requestWaiver(violation, false);
		if (ret==null) {
	    	Integer severity = violation.getDescriptor().getSeverity();
			Integer s=severity;
	    	Map rsm=(Map) severitySummary.get(s);
	    	if (rsm==null) {
	    		rsm=new TreeMap();
	    		severitySummary.put(s, rsm);    		
	    	}
	    	
	    	SimpleInspectorSummary rsEntry=(SimpleInspectorSummary) rsm.get(violation.getDescriptor().getName());
	    	if (rsEntry==null) {
	    		rsEntry=new SimpleInspectorSummary(violation.getDescriptor());
	    		rsm.put(violation.getDescriptor().getName(), rsEntry);
	
	    	}
	    	rsEntry.addLocation(violation.getSource());
	    	
	    	/**
	    	 * Violations with severity >=5 considered as hints.
	    	 */
	    	if (severity.intValue()>0 && severity.intValue()<5) {
	    		violationLevel+=Math.pow(10, 1-severity.doubleValue());
	    	}
		} else {
			waivedViolationsNumber++;
		}
		
    	return ret;
    }
    
    public Map getSeveritySummary() {
        return Collections.unmodifiableMap(severitySummary);
    }    
    
    public Number getMaxSeverity() {
        Integer ret=null;
        Iterator it=severitySummary.keySet().iterator();
        while (it.hasNext()) {
            Integer severity=(Integer) it.next();
            if (ret==null || severity.intValue()<ret.intValue()) {
                ret=severity;
            }
        }
        return ret;
    }

    public Collection getWarnings() {
    	return Collections.unmodifiableCollection(warnings);
    }
    
    public void addWarning(Violation warning) {
    	warnings.add(warning);
    }        
    
    public void addMetric(SourceMarker source, String name, double value) {
    	class SourceAwareMetric extends SimpleMetric {
    		SourceAwareMetric(String name) {
    			super(name);
    		}
    		
    		SourceAwareMetric(String name, boolean keepMeasurements) {
    			super(name, keepMeasurements);
    		}
    		
            void add(final SourceMarker source, final double value, final long time) {
                addMeasurement(new Measurement() {
        			public double getValue() {
        				return value;
        			}
        		
        			public long getTime() {
        				return time;
        			}        	
        		});
            }
    	}
    	
    	SourceAwareMetric metric=(SourceAwareMetric) metrics.get(name);
        if (metric==null) {
            metric=new SourceAwareMetric(name);
            metrics.put(name, metric);
        }
        metric.add(source, value, 0);
    }
    
    public double getViolationLevel() {
        return violationLevel;
    }
    
    public long getReviewsNumber() {
        return reviews;
    }
    
    public long getCodeBase() {
        return codeBase; 
    }
    
    public int getViolationsNumber() {
        return violationsNumber; 
    }
    
//    public double getPerfectionAffinityIndex() {
//        return 1-violationLevel/reviews;
//    }       
//    
    public Map getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }
    
//    public boolean isIncomplete() {
//        return isIncomplete;
//    }
    
    public void aggregate(AggregatedResults agregee) {
        codeBase+=agregee.getCodeBase();
        reviews+=agregee.getReviewsNumber();
        violationsNumber+=agregee.getViolationsNumber();
        waivedViolationsNumber+=agregee.getWaivedViolationsNumber();
        violationLevel+=agregee.getViolationLevel();   
        warnings.addAll(agregee.getWarnings());
        
        Iterator it=agregee.getSeveritySummary().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry=(Map.Entry) it.next();
            Map ss=(Map) severitySummary.get(entry.getKey());
            if (ss==null) {
                ss=new TreeMap();
                severitySummary.put(entry.getKey(), ss);
            }
            
            Iterator rsit=((Map) entry.getValue()).values().iterator();
            while (rsit.hasNext()) {
            	InspectorSummary rse=(InspectorSummary) rsit.next();
            	SimpleInspectorSummary masterRSE=(SimpleInspectorSummary) ss.get(rse.getName());
            	if (masterRSE==null) {
            		masterRSE=new SimpleInspectorSummary(rse);
            		ss.put(rse.getName(), masterRSE);            		
            	} else {
            		masterRSE.addLocations(rse.getLocations());
            	}
            }
        }       
        
        it=agregee.getMetrics().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry=(Map.Entry) it.next();
            Metric metric=(Metric) metrics.get(entry.getKey());
            if (metric==null) {
                metric=new SimpleMetric((String) entry.getKey());
                metrics.put(entry.getKey(), metric);
            }
            metric.add((SimpleMetric) entry.getValue());
        }
    }
    
	/**
	 * Sets number of reviews. 
	 * @param reviews The reviews to set.
	 */
	public void setReviewsNumber(long reviews) {
		this.reviews = reviews;
	}

	public void setCodeBase(long codeBase) {
		this.codeBase=codeBase;
	}

	private static class Context {
		private List annotations=new ArrayList();
		private WaiverSet waiverSet;
	}
	
	private static ThreadLocal contextMap=new ThreadLocal() {
		protected Object initialValue() {
			return new HashMap();
		}
	};
	
	private static int counter;
	
	private Integer id;
	
	{
		synchronized (this.getClass()) {
			id=new Integer(counter++);
		}
	}
	
	private Context getContext() {
		Map map = (Map) contextMap.get();
		Context ret = (Context) map.get(id);
		if (ret==null) {
			ret=new Context();
			map.put(id, ret);
		}
		return ret;		
	}
	
	private Date date=new Date();
	
	public void addAnnotation(Annotation annotation) {
		getContext().annotations.add(annotation);		
	}

	public Collection getAnnotations() {
		return Collections.unmodifiableCollection(getContext().annotations);
	}

	public Date getDate() {
		return date=new Date();
	}

	public int getWaivedViolationsNumber() {
		return waivedViolationsNumber;
	}

	public String getDPMO() {
		if (reviews==0) {
			return "Not available, no reviews";
		} else {
			return String.valueOf((int) (1000000*violationLevel/reviews)) + (warnings.isEmpty() ? "" : " (not accurate because of warnings)");			
		}
	}

	public String getSigma() {
		double p=1.0-violationLevel/reviews;
		if (reviews==0) {
			return "No results";
		} else if (p<=0) {
			return "Full incompliance";
		} else if (p>=1) {
			return "Full compliance";
		} else {
			return MessageFormat.format("{0,number,#.###}", new Object[] {new Double(normsinv(p)+1.5)}) + (warnings.isEmpty() ? "" : " (not accurate because of warnings)");
		}
	}
	
	public static double normsinv(double probability) {
        // Coefficients in rational approximations
        double[] a = {-3.969683028665376e+01,
				2.209460984245205e+02,
                -2.759285104469687e+02,
				1.383577518672690e+02,
                -3.066479806614716e+01,
				2.506628277459239e+00};

        double[] b = {-5.447609879822406e+01,
        		1.615858368580409e+02,
                -1.556989798598866e+02,
				6.680131188771972e+01,
				-1.328068155288572e+01};

        double[] c = {-7.784894002430293e-03,
        		-3.223964580411365e-01,
                -2.400758277161838e+00,
				-2.549732539343734e+00,
				4.374664141464968e+00,  
				2.938163982698783e+00};

        double[] d = {7.784695709041462e-03,
        		3.224671290700398e-01,
                2.445134137142996e+00,
				3.754408661907416e+00};

        // Define break-points.
        double plow  = 0.02425;
        double phigh = 1 - plow;

        // Rational approximation for lower region:
        if ( probability < plow ) {
                 double q  = Math.sqrt(-2*Math.log(probability));
                 return (((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) /
                                                 ((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1);
        }

        // Rational approximation for upper region:
        if ( phigh < probability ) {
                 double q  = Math.sqrt(-2*Math.log(1-probability));
                 return -(((((c[0]*q+c[1])*q+c[2])*q+c[3])*q+c[4])*q+c[5]) /
                                                        ((((d[0]*q+d[1])*q+d[2])*q+d[3])*q+1);
        }

        // Rational approximation for central region:
        double q = probability - 0.5;
        double r = q*q;
        return (((((a[0]*r+a[1])*r+a[2])*r+a[3])*r+a[4])*r+a[5])*q /
                                 (((((b[0]*r+b[1])*r+b[2])*r+b[3])*r+b[4])*r+1);
}

	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}
	
	public WaiverSet getWaiverSet() {
		return getContext().waiverSet;
	}

	public void commit() {
		// Does nothing because Simple results are not persistent.
	}

	public boolean isNew() {
		return true;
	}

	public BasicResults getBaseLine() {
		return null;
	}    	
}
