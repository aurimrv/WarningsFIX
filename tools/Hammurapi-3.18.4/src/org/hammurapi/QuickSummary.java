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
package org.hammurapi;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hammurapi.results.AggregatedResults;
import org.hammurapi.results.Annotation;
import org.hammurapi.results.BasicResults;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.InspectorSummary;
import org.hammurapi.results.quick.MetricSummary;
import org.hammurapi.results.quick.Summary;
import org.hammurapi.results.quick.Warning;
import org.hammurapi.results.simple.SimpleAggregatedResults;

import com.pavelvlasov.review.SimpleSourceMarker;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.6 $
 */
public class QuickSummary implements CompositeResults {

	private Summary summary;
	private QuickResultsCollector collector;
	private InspectorSet inspectorSet;
	private String title;

	/**
	 * @param total
	 * @param collector
	 * @throws SQLException
	 */
	public QuickSummary(String title, QuickResultsCollector collector, InspectorSet inspectorSet, Collection children) throws SQLException {
		this.collector=collector;
		this.inspectorSet=inspectorSet;
		this.summary=collector.getEngine().getSummary();
		this.children=children;
		this.title=title;
	}

	private Collection children;
	
	public Collection getChildren() {
		return children;
	}

	public void add(AggregatedResults child) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return summary.getResultSize();
	}

	private Collection violations=new ArrayList();
	
	public Collection getViolations() {
		return violations;
	}

	public Collection getWaivedViolations() {
		return violations;
	}

	public String getName() {
		return title;
	}

	public Waiver addViolation(Violation violation) {
		throw new UnsupportedOperationException();
	}

	private Map severitySummary;
	
	public Map getSeveritySummary() {
		if (severitySummary==null) {
			severitySummary=new TreeMap();
			
			Iterator it=collector.getEngine().getInspectorSummary().iterator();
			while (it.hasNext()) {
				final org.hammurapi.results.quick.InspectorSummary ps=(org.hammurapi.results.quick.InspectorSummary) it.next();
				Integer severity = new Integer(ps.getSeverity());
				Map imap=(Map) severitySummary.get(severity);
				if (imap==null) {
					imap=new TreeMap();
					severitySummary.put(severity, imap);
				}
				imap.put(ps.getName(), 
						new InspectorSummary() {

							public String getDescription() {
								return ps.getDescription();
							}

							public List getLocations() {
								return null;
							}

							public int getLocationsCount() {
								return (int) ps.getViolations();
							}

							public String getName() {
								return ps.getName();
							}

							public String getVersion() {
								return "";
							}

							public Number getSeverity() {
								return new Integer(ps.getSeverity());
							}

							public String getConfigInfo() {
								return ps.getConfigInfo();
							}

							public int getBaseLineLocationsCount() {
								return -1;
							}

							public int compareTo(Object o) {
								if (o instanceof InspectorSummary) {
									return ps.getName().compareTo(((InspectorSummary) o).getName());
								} else if (o instanceof Comparable) {
									return -((Comparable) o).compareTo(this);
								} else {
									return this.hashCode()-o.hashCode();
								}
							}					
				});
			}
		}
		return severitySummary;
	}

	private Collection warnings;
	
	public Collection getWarnings() {
		if (warnings==null) {
			warnings=new ArrayList();
			Iterator it=collector.getEngine().getWarning().iterator();
			while (it.hasNext()) {
				final Warning warning=(Warning) it.next();
				warnings.add(new Violation() {

					public String getMessage() {
						return warning.getMessage();
					}

					public InspectorDescriptor getDescriptor() {
						return inspectorSet.getDescriptor(warning.getInspector());
					}

					private SimpleSourceMarker sourceMarker;
					
					{
						if (warning.getSource()!=null && warning.getLine()!=null && warning.getCol()!=null) {
							sourceMarker=new SimpleSourceMarker(
									warning.getCol().intValue(), 
									warning.getLine().intValue(), 
									warning.getSource(), 
									null);
							
							sourceMarker.setSignature(warning.getSourceSignature());
						}
					}
					
					public SourceMarker getSource() {
						return sourceMarker;
					}

					public int compareTo(Object o) {
						if (o==this) {
							return 0;
						} else if (o instanceof Violation) {
							Violation v=(Violation) o;
							int vline = v.getSource()==null ? 0 : v.getSource().getLine();
							int line = getSource()==null ? 0 : getSource().getLine();
							if (vline==line) {
								int vcolumn = v.getSource()==null ? 0 : v.getSource().getColumn();
								int column = getSource()==null ? 0 : getSource().getColumn();
								if (vcolumn==column) {
									if (warning.getMessage()==null) {
										return v.getMessage()==null ? 0 : 1;
									} else {
										if (v.getMessage()==null) {
											return -1;
										} else {
											return warning.getMessage().compareTo(v.getMessage());
										}
									}
								} else {
									return column-vcolumn;
								}
							} else {
								return line-vline;
							}
						} else {
							return 1;
						}
					}
					
				});
			}
		}
		return warnings;
	}

	public boolean hasWarnings() {
		return summary.getHasWarnings();
	}

	public void addWarning(Violation warning) {
		throw new UnsupportedOperationException();
	}

	public void addMetric(SourceMarker source, String name, double value) {
		throw new UnsupportedOperationException();
	}

	private Map metrics;
	
	public Map getMetrics() {
		if (metrics==null) {
			metrics=new TreeMap();
			Iterator it=collector.getEngine().getMetricSummary().iterator();
			while (it.hasNext()) {
				final MetricSummary metric=(MetricSummary) it.next();
				metrics.put(
						metric.getName(),
						new com.pavelvlasov.metrics.Metric() {
	
							public int getNumber() {
								return (int) metric.getMeasurements();
							}
	
							public double getMin() {
								return metric.getMinValue();
							}
	
							public double getMax() {
								return metric.getMaxValue();
							}
	
							public double getAvg() {
								return metric.getMetricTotal()/metric.getMeasurements();
							}
	
							public double getTotal() {
								return metric.getMetricTotal();
							}
	
							public void add(double value, long time) {
								throw new UnsupportedOperationException();
							}
	
							public void add(com.pavelvlasov.metrics.Metric metric) {
								throw new UnsupportedOperationException();
							}
	
							public Collection getMeasurements() {
								return null;
							}
	
							public String getName() {
								return metric.getName();
							}
							
                            public double getDeviation() {
                                // TODO - Calcualte deviation
                                return 0;
                            }
							
						});
			}			
		}
		return metrics;
	}

	public void aggregate(AggregatedResults agregee) {
		throw new UnsupportedOperationException();
	}

	public void setReviewsNumber(long reviews) {
		throw new UnsupportedOperationException();
	}

	public void setCodeBase(long codeBase) {
		throw new UnsupportedOperationException();
	}

	public void addAnnotation(Annotation annotation) {
		throw new UnsupportedOperationException();
	}

	public Collection getAnnotations() {
		return violations;
	}

	public WaiverSet getWaiverSet() {
		throw new UnsupportedOperationException();
	}

	public void commit() {
	    // No action is required
	}

	public boolean isNew() {
		return summary.getMinState()==QuickResultsCollector.RESULT_NEW;
	}

	public BasicResults getBaseLine() {
		return null;
	}

	public long getCodeBase() {
		return summary.getCodebase();
	}

	public String getDPMO() {
		if (summary.getReviews()==0) {
			return "Not available, no reviews";
		} else {
			return String.valueOf((int) (1000000*summary.getViolationLevel()/summary.getReviews())) + (summary.getHasWarnings() ? " (not accurate because of warnings)" : "");			
		}
	}

	public String getSigma() {
		double p=1.0-summary.getViolationLevel()/summary.getReviews();
		if (summary.getReviews()==0) {
			return "No results";
		} else if (p<=0) {
			return "Full incompliance";
		} else if (p>=1) {
			return "Full compliance";
		} else {
			return MessageFormat.format("{0,number,#.###}", new Object[] {new Double(SimpleAggregatedResults.normsinv(p)+1.5)}) + (summary.getHasWarnings() ? " (not accurate because of warnings)" : "");
		}
	}

	public Number getMaxSeverity() {
		return summary.getMaxSeverity();
	}

	public long getReviewsNumber() {
		return summary.getReviews();
	}

	public double getViolationLevel() {
		return summary.getViolationLevel();
	}

	public int getViolationsNumber() {
		return (int) summary.getViolations();
	}

	public int getWaivedViolationsNumber() {
		return (int) summary.getWaivedViolations();
	}

	public Date getDate() {
		Timestamp resultDate = summary.getResultDate();
		return resultDate==null ? new Date() : new Date(resultDate.getTime());
	}
}
