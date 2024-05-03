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

package org.hammurapi.results.persistent.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hammurapi.HammurapiException;
import org.hammurapi.HammurapiMeasurement;
import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.Inspector;
import org.hammurapi.InspectorDescriptor;
import org.hammurapi.SimpleViolation;
import org.hammurapi.Violation;
import org.hammurapi.Waiver;
import org.hammurapi.WaiverSet;
import org.hammurapi.results.Annotation;
import org.hammurapi.results.InspectorSummary;
import org.hammurapi.results.persistent.jdbc.sql.AggregatedResultsInspectorViolations;
import org.hammurapi.results.persistent.jdbc.sql.AggregatedResultsMetric;
import org.hammurapi.results.persistent.jdbc.sql.AggregatedResultsMetricMeasurement;
import org.hammurapi.results.persistent.jdbc.sql.MeasurementImpl;
import org.hammurapi.results.persistent.jdbc.sql.ResultsEngine;
import org.hammurapi.results.persistent.jdbc.sql.ViolationImpl;
import org.hammurapi.results.persistent.jdbc.sql.ViolationJoined;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.convert.Converter;
import com.pavelvlasov.metrics.Metric;
import com.pavelvlasov.persistence.PersistenceException;
import com.pavelvlasov.review.Signed;
import com.pavelvlasov.review.SimpleSourceMarker;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.sql.Parameterizer;
import com.pavelvlasov.sql.RowProcessor;
import com.pavelvlasov.sql.RowProcessorEx;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.19 $
 */
public class AggregatedResults extends BasicResults implements org.hammurapi.results.AggregatedResults {
	protected static final byte VIOLATION = 0;
	protected static final byte WAIVED_VIOLATION = 1;
	protected static final byte WARNING = 2;

	AggregatedResults(WaiverSet waiverSet, ResultsFactory factory) throws SQLException {
    	super(factory);
    	this.waiverSet=waiverSet;
    	id=factory.nextPK("RESULT");
    	factory.addToCache(this);
    	date=new Date();
    	factory.getSQLProcessor().processUpdate(
    			"INSERT INTO RESULT (ID, TYPE, RESULT_DATE, CODEBASE) VALUES (?, ?, ?, 0)",
				new Parameterizer() {
					public void parameterize(PreparedStatement ps) throws SQLException {
						ps.setInt(1, id);
						ps.setString(2, AggregatedResults.this.getClass().getName());
						ps.setDate(3, new java.sql.Date(date.getTime()));
					}
    			});
    	
    	// Self parent for Oracle
		factory.getSQLProcessor().processUpdate(
				"INSERT INTO RESULT_NET (PARENT, CHILD) VALUES (?,?)",
				new Parameterizer() {
					public void parameterize(PreparedStatement ps) throws SQLException {
						ps.setInt(1, id);
						ps.setInt(2, id);
					}					
				});
    	
    	// Self kindred
    	factory.getSQLProcessor().processUpdate(
    			"INSERT INTO KINDRED (ANCESTOR, DESCENDANT) VALUES (?, ?)",
				new Parameterizer() {
					public void parameterize(PreparedStatement ps) throws SQLException {
						ps.setInt(1, id);
						ps.setInt(2, id);
					}
    			});    	    	    	
    }
        
    /**
     * @param id
     * @param factory
     * @throws SQLException
     */
    public AggregatedResults(int id, ResultsFactory factory) throws SQLException {
    	super(id, factory);
    }
    
	/**
     * Add violation to severity summary
     * @param violation
	 * @return Waiver
	 * @throws HammurapiException
     */
    public Waiver addViolation(final Violation violation) throws HammurapiException {
		final Waiver ret = waiverSet==null ? null : waiverSet.requestWaiver(violation, false);		
		try {
    		ResultsEngine resultsEngine = factory.getResultsEngine();
	    	if (ret==null) {
	    		severitySummary=null;
	    		InspectorDescriptor descriptor = insertViolation(violation, VIOLATION, null);
	    		++violationsNumber;
				resultsEngine.updateAggregatedResultsViolations(violationsNumber, id);
	    		
		    	final int severity = descriptor.getSeverity().intValue();
		    	if (maxSeverity==null || maxSeverity.intValue()>severity) {
		    		maxSeverity=new Integer(severity);
		    		resultsEngine.updateAggregatedResultsMaxSeverity((short) severity, id);
		    	}
	    		    		
		    	/**
		    	 * Violations with severity >=5 considered as hints.
		    	 */
				if (severity>0 && severity<5) {
		    		violationLevel+=Math.pow(10, 1-descriptor.getSeverity().doubleValue());		    		
		    		resultsEngine.setViolationLevel(violationLevel, getId());
		    	}
	    	} else {
	    		++waivedViolationsNumber;
	    		resultsEngine.incWaivedViolations(getId());
	    	}	    		    	
		} catch (SQLException e) {
			throw new HammurapiException(e);
		}
    	return ret;		
    }
    
    protected interface ViolationConfigurator {
    	void setViolationInfo(org.hammurapi.results.persistent.jdbc.sql.Violation sqlViolation) throws SQLException;
    }
    
	protected InspectorDescriptor insertViolation(final Violation violation, byte type, ViolationConfigurator configurator) {
		InspectorDescriptor descriptor = violation.getDescriptor();
		try {
			maybeInsertInspector(descriptor);
			
			ViolationImpl sqlViolation=new ViolationImpl(true);
			sqlViolation.setId(factory.getSQLProcessor().nextPK("PRIMARY_KEY", "VIOLATION"));
			sqlViolation.setViolationType(type);

			sqlViolation.setResultId(getId());
			sqlViolation.setReportId(factory.getReportId());
			if (descriptor!=null) {
				sqlViolation.setInspector(descriptor.getName()); // INSPECTOR
			}
			
			String message = violation.getMessage();
			if (message!=null) {
				sqlViolation.setMessageId(new Integer(factory.addMessage(message))); // MESSAGE
			}
			
			SourceMarker source = violation.getSource();
			if (source!=null) {
				sqlViolation.setSourceId(source.getSourceId()); // SOURCE
				sqlViolation.setLine(source.getLine()); // LINE
				sqlViolation.setCol(source.getColumn()); // COL
			
				if (source instanceof Signed) {
					 String signature = ((Signed) source).getSignature();
					 if (signature!=null && signature.startsWith(source.getSourceURL())) {
					 	sqlViolation.setSignaturePostfix(signature.substring(source.getSourceURL().length())); // SOURCE_SIGNATURE
					 }
				}
			}
			
			if (configurator!=null) {
				configurator.setViolationInfo(sqlViolation);
			}
			
			factory.getResultsEngine().insertViolation(sqlViolation);
		} catch (SQLException e1) {
			throw new HammurapiRuntimeException(e1);
		}
		return descriptor;
	}

	public void addWarning(Violation warning) {
		insertViolation(warning, WARNING, null);
    	hasWarnings=true;
    }        
        
    private Set metricIds=new HashSet();
    
    private class MetricEntry {
    	int id;
    	String name;
    	
		public MetricEntry(int id, String name) {
			super();
			this.id = id;
			this.name = name;
		}
		
		public boolean equals(Object obj) {
			return obj instanceof MetricEntry && ((MetricEntry) obj).id==id && ((MetricEntry) obj).name.equals(name);
		}
		
		public int hashCode() {
			return id ^ name.hashCode();
		}
    }
    
    public void addMetric(final SourceMarker source, final String name, final double value) {
    	try {
        	ResultsEngine engine = factory.getResultsEngine();
        	MetricEntry metricEntry = new MetricEntry(getId(), name);
			if (!metricIds.contains(metricEntry)) {
        		org.hammurapi.results.persistent.jdbc.sql.Metric dbMetric = engine.getMetric(getId(), name);
        		if (dbMetric==null) {
        			engine.insertMetric(getId(), name);
        		}
        		metricIds.add(metricEntry);
        	}
        	
        	MeasurementImpl measurement = new MeasurementImpl(true);
			measurement.setId(factory.getSQLProcessor().nextPK("PRIMARY_KEY", "MEASUREMENT"));
        	if (source!=null) {
        		measurement.setCol(source.getColumn());
        		measurement.setLine(source.getLine());
        		measurement.setSource(source.getSourceURL());
        	}
    		measurement.setResultId(getId());
    		measurement.setMeasurementValue(value);
    		measurement.setName(name);
    		engine.insertMeasurement(measurement);
    		
    		metrics=null;
    	} catch (SQLException e) {
    		throw new HammurapiRuntimeException(e);
    	}
    }
    
	private Map metrics;
	
	public Map getMetrics() {
    	if (metrics==null) {
    		metrics=new TreeMap();
    		Iterator it=factory.getResultsEngine().getAggregatedResultsMetric(id).iterator();
    		while (it.hasNext()) {
    			final AggregatedResultsMetric arm=(AggregatedResultsMetric) it.next();    			
				metrics.put(arm.getName(), new Metric() {
					
						public int getNumber() {
							return arm.getMeasurements();
						}

						public double getMin() {
							return arm.getMinValue();
						}

						public double getMax() {
							return arm.getMaxValue();
						}

						public double getAvg() {
							return arm.getTotalValue()/arm.getMeasurements();
						}

						public double getTotal() {
							return arm.getTotalValue();
						}

						public void add(double measurement, long time) {
							throw new UnsupportedOperationException("Read-only");
						}

						public void add(Metric metric) {
							throw new UnsupportedOperationException("Read-only");
						}

                        public double getDeviation() {
                            // TODO - Calcualte deviation
                            return 0;
                        }
						
						private Collection measurements;
						
						public Collection getMeasurements() {
							if (measurements==null) {
//								System.out.println("getMeasurements: "+measurementsCounter++);
								try {
									//System.out.println("Loading measurements for "+getId()+" "+arm.getName());
									measurements=factory.getResultsEngine().getAggregatedResultsMetricMeasurement(
											id, 
											arm.getName(),
											new ArrayList(),
											new Converter() {

												public Object convert(Object source) {
													final AggregatedResultsMetricMeasurement armm=(AggregatedResultsMetricMeasurement) source;
													return new HammurapiMeasurement() {									
														SourceMarker source=new SourceMarker() {
															
															public int getLine() {
																return armm.getLine();
															}
						
															public int getColumn() {
																return armm.getCol();
															}
						
															public String getSourceURL() {
																return armm.getSource();
															}
					
															public Integer getSourceId() {
																return null;
															}
															
														};
														
														public SourceMarker getSource() {
															return source;
														}
					
														public double getValue() {
															return armm.getMeasurementValue();
														}

														public long getTime() {
															return 0;
														}									
													};
												}
												
											});
								} catch (SQLException e) {
									throw new HammurapiRuntimeException("Cannot create metric measurement: "+e, e);
								}
							}
							return measurements;								
						}

						public String getName() {
							return arm.getName();
						}
						
					});
    		}
    	}
    	return metrics;
    }
        	
    public void aggregate(final org.hammurapi.results.AggregatedResults agregee) {
    	final AggregatedResults unWrapped = factory.unWrap(agregee);
        codeBase+=agregee.getCodeBase();
        reviews+=agregee.getReviewsNumber();
        violationsNumber+=agregee.getViolationsNumber();
        waivedViolationsNumber+=agregee.getWaivedViolationsNumber();
        violationLevel+=agregee.getViolationLevel();   
        hasWarnings|=agregee.hasWarnings();
                
        try {
        	Number ams=agregee.getMaxSeverity();
        	if (ams!=null && (maxSeverity==null || maxSeverity.intValue()>ams.intValue())) {
        		maxSeverity=ams;
        		factory.getResultsEngine().updateAggregatedResultsMaxSeverity(ams.shortValue(), id);
        	}
                    	
			final Parameterizer parameterizer = new Parameterizer() {
				public void parameterize(PreparedStatement ps) throws SQLException {
					idParameterizer.parameterize(ps);
					ps.setInt(2, unWrapped.getId());
				}
			};
			
        	factory.getSQLProcessor().processSelect(
        			"SELECT * FROM RESULT_NET WHERE PARENT=? AND CHILD=?",
        			parameterizer,
					new RowProcessorEx() {
						public void onEmptyResultSet() throws SQLException {
							factory.getSQLProcessor().processUpdate(
									"INSERT INTO RESULT_NET (PARENT, CHILD) VALUES (?,?)",
									parameterizer);
						}

						public boolean process(ResultSet rs) {
							// Doing nothing - already set.
							return false;
						}            				
        			});
        
			factory.getSQLProcessor().processUpdate(
					"INSERT INTO KINDRED (ANCESTOR, DESCENDANT) " +
					"SELECT A.ANCESTOR, D.DESCENDANT FROM KINDRED A, KINDRED D WHERE A.DESCENDANT=? AND D.ANCESTOR=? ",
					//"AND NOT EXISTS (SELECT * FROM KINDRED C WHERE C.ANCESTOR=A.ANCESTOR AND C.DESCENDANT=D.DESCENDANT)",
					new Parameterizer() {
						public void parameterize(PreparedStatement ps) throws SQLException {
							ps.setInt(1, getId());
							ps.setInt(2, unWrapped.getId());
						}
					});
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}		
    }
                    
	/**
	 * Sets number of reviews. 
	 * @param reviews The reviews to set.
	 */
	public void setReviewsNumber(final long reviews) {
		this.reviews = reviews;
		try {
			factory.getSQLProcessor().processUpdate(
					"UPDATE RESULT SET REVIEWS=? WHERE ID=?",
					new Parameterizer() {
						public void parameterize(PreparedStatement ps) throws SQLException {
							ps.setLong(1, reviews);
							ps.setInt(2, getId());
						}
					});
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	public void setCodeBase(final long codeBase) {
		this.codeBase=codeBase;
		try {
			factory.getSQLProcessor().processUpdate(
					"UPDATE RESULT SET CODEBASE=? WHERE ID=?",
					new Parameterizer() {
						public void parameterize(PreparedStatement ps) throws SQLException {
							ps.setLong(1, codeBase);
							ps.setInt(2, getId());
						}
					});
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	public void addAnnotation(Annotation annotation) {
		try {
			final String handle=factory.getStorage().put(annotation);
			factory.getSQLProcessor().processUpdate(
					"INSERT INTO ANNOTATION (RESULT_ID, HANDLE) VALUES (?, ?)",
					new Parameterizer() {
						public void parameterize(PreparedStatement ps) throws SQLException {
							idParameterizer.parameterize(ps);
							ps.setString(2, handle);
						}
					});
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		} catch (PersistenceException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	public Collection getAnnotations() {
		final Collection ret=new LinkedList();
		try {
			factory.getSQLProcessor().processSelect(
					"SELECT HANDLE FROM ANNOTATION WHERE RESULT_ID=?",
					idParameterizer,
					new RowProcessor() {
						public boolean process(ResultSet rs) throws SQLException {
							try {
								ret.add(factory.getStorage().get(rs.getString("HANDLE")));
							} catch (PersistenceException e) {
								throw new HammurapiRuntimeException(e);
							}
							return true;
						}
					});
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
		return ret;
	}

	private WaiverSet waiverSet;

	/**
	 * @param descriptor
	 * @throws SQLException
	 */
	protected void maybeInsertInspector(final InspectorDescriptor descriptor) throws SQLException {
		if (descriptor!=null) {
			factory.getSQLProcessor().processSelect(
					"SELECT * FROM INSPECTOR WHERE REPORT_ID=? AND NAME=?",
					new Parameterizer() {
						public void parameterize(PreparedStatement preparedStatement) throws SQLException {
							preparedStatement.setInt(1, factory.getReportId());
							preparedStatement.setString(2, descriptor.getName());
						}
					},
					
					new RowProcessorEx() {
						public boolean process(ResultSet resultSet) {
							return false;
						}
	
						public void onEmptyResultSet() throws SQLException {
							factory.getSQLProcessor().processUpdate(
									"INSERT INTO INSPECTOR (REPORT_ID, NAME, SEVERITY, DESCRIPTION, CONFIG_INFO) VALUES (?, ?, ?, ?, ?)",
									new Parameterizer() {
										public void parameterize(PreparedStatement ps) throws SQLException {
											ps.setInt(1, factory.getReportId());
											ps.setString(2, descriptor.getName());
											ps.setShort(3, descriptor.getSeverity().shortValue());
											ps.setString(4, descriptor.getDescription());
											try {
												Inspector inspector = descriptor.getInspector();
												if (inspector!=null) {
													ps.setString(5, inspector.getConfigInfo());
												}
											} catch (ConfigurationException e) {
												throw new HammurapiRuntimeException("Can't obtain inspector configuration info", e);
											}
										}
									});
						}					
					});
		}
	}
		
	private Map severitySummary;
	
    public Map getSeveritySummary() {
    	if (severitySummary==null) {
	    	severitySummary=new TreeMap();
	    	Iterator it=factory.getResultsEngine().getAggregatedResultsSeveritySummary(id).iterator();
	    	while (it.hasNext()) {
	    		Number severity=(Number) it.next();
	    		severitySummary.put(severity, createInspectorSummaryMapProxy(severity));
	    	}
    	}
		return severitySummary;
    }    
    
    public Collection getWarnings() {
    	try {    		
			return factory.getResultsEngine().getAllWarningsJoined(getId(), new LinkedList(), violationConverter);
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
    }
    
	private Map createInspectorSummaryMapProxy(final Number severity) {
		return (Map) Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] {Map.class}, 
				new InvocationHandler() {

					private Map target;
					
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (target==null) {
							target=instantiateInspectorSummaryMap(severity);
						}
						return method.invoke(target, args);
					}					
				});
	}
	
	/**
	 * @param severity
	 * @param map
	 */
	private Map instantiateInspectorSummaryMap(final Number severity) {
		class InspectorInfo {
			String name;
			String description;
			String configInfo;			
			
			InspectorInfo(org.hammurapi.results.persistent.jdbc.sql.Inspector arim) {
				name=arim.getName();
				description=arim.getDescription();
				configInfo=arim.getConfigInfo();
			}
			
			public boolean equals(Object obj) {
				if (obj==this) {
					return true;
				} else if (obj instanceof InspectorInfo) {
					InspectorInfo otherInfo=(InspectorInfo) obj;
					return equalStrings(name, otherInfo.name)						
						&& equalStrings(description, otherInfo.description) 
						&& equalStrings(configInfo, otherInfo.configInfo);
				} else {
					return super.equals(obj);
				}
			}
			
			boolean equalStrings(String a, String b) {
				if (a==null) {
					return b==null;
				}
				
				return a.equals(b);
			}
			
			public int hashCode() {
				return (name==null ? 0 : name.hashCode()) ^ (description==null ? 0 : description.hashCode()) ^ (configInfo==null ? 0 : configInfo.hashCode());
			}
		}
		
		final Map infoMap=new HashMap();
		
		Iterator it=factory.getResultsEngine().getAggregatedResultsInspectorSummary(id, severity.shortValue()).iterator();
		while (it.hasNext()) {
			org.hammurapi.results.persistent.jdbc.sql.Inspector arim=(org.hammurapi.results.persistent.jdbc.sql.Inspector) it.next();
			InspectorInfo inspectorInfo=new InspectorInfo(arim);
			Set ids=(Set) infoMap.get(inspectorInfo);
			if (ids==null) {
				ids=new HashSet();
				infoMap.put(inspectorInfo, ids);
			}
			
			ids.add(new Integer(arim.getReportId()));			
		}
				
		final Map map=new TreeMap();
		it=infoMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry=(Map.Entry) it.next();
			InspectorInfo info=(InspectorInfo) entry.getKey();
			Set ids = (Set) entry.getValue();
			StringBuffer sb=new StringBuffer();
			Iterator iit=ids.iterator();
			while (iit.hasNext()) {
				sb.append(iit.next());
				if (iit.hasNext()) {
					sb.append(",");
				}
			}			
			
			map.put(info.name+" ["+sb.toString()+"]", 
				createInspectorSummary(
					severity,
					info.name, 
					info.description, 
					info.configInfo,
					sb.toString()));
		}				
		
		return map;
	}
	
	/**
	 * @param severity
	 * @param inspectorName
	 * @param description
	 * @param configInfo
	 * @return inspector summary
	 */
	private InspectorSummary createInspectorSummary(
			final Number severity, 
			final String inspectorName, 
			final String description, 
			final String configInfo,
			final String inspectorReportIds) 
	{
		return new InspectorSummary() {

			public String getDescription() {
				return description;
			}

			private List locations;
			
			public List getLocations() {
				if (locations==null) {
					locations=new ArrayList();
					Iterator it=factory.getResultsEngine().getAggregatedResultsInspectorViolations(id, inspectorName).iterator();
					while (it.hasNext()) {
						final AggregatedResultsInspectorViolations ariv=(AggregatedResultsInspectorViolations) it.next();
						locations.add(new SourceMarker() {

							public int getLine() {
								return ariv.getLine();
							}

							public int getColumn() {
								return ariv.getCol();
							}

							public String getSourceURL() {
								if (ariv.getCompilationUnitPackage()==null || ariv.getCompilationUnitPackage().length()==0) {
									return ariv.getCompilationUnitName();
								}
								
								return ariv.getCompilationUnitPackage().replace('.','/')+'/'+ariv.getCompilationUnitName();
							}

							public Integer getSourceId() {
								return ariv.getSourceId();
							}
							
						});
					}
				}
				
				return locations;
			}
			
			public int getLocationsCount() {
				return getLocations().size();
			}

			public String getName() {
				return inspectorName;
			}

			public Number getSeverity() {
				return severity;
			}

			public String getConfigInfo() {
				return configInfo;
			}

			public int compareTo(Object o) {
				if (o instanceof InspectorSummary) {
					return inspectorName.compareTo(((InspectorSummary) o).getName());
				} else if (o instanceof Comparable) {
					return -((Comparable) o).compareTo(this);
				} else {
					return this.hashCode()-o.hashCode();
				}
			}

			public String getVersion() {
				return inspectorReportIds;
			}

			private int baseLineLocationsCount=-1;
			
			public int getBaseLineLocationsCount() {
				if (baseLineLocationsCount==-1 && baseLineId!=null) {
					try {
						baseLineLocationsCount=factory.getResultsEngine().getAggregatedResultsInspectorViolationsCount(baseLineId.intValue(), inspectorName);
					} catch (SQLException e) {
						throw new HammurapiRuntimeException(e);
					}
				}
				return baseLineLocationsCount;
			}			
		};
	}

	protected Converter violationConverter=new Converter() {
		public Object convert(Object o) {
			final ViolationJoined vj=(ViolationJoined) o;
			String path;
			if (vj.getCompilationUnitPackage()==null || vj.getCompilationUnitPackage().length()==0) {
				path=vj.getCompilationUnitName();
			} else {
				path=vj.getCompilationUnitPackage().replace('.','/')+'/'+vj.getCompilationUnitName();
			}
			return new SimpleViolation(
					new SimpleSourceMarker(vj.getCol(), vj.getLine(), path, vj.getSourceId()),
					vj.getMessage(),					
					factory.getInspectorDescriptor(vj.getInspector()));
		}
	};
	
	private Integer baseLineId;

	public WaiverSet getWaiverSet() {
		return waiverSet;
	}

	public void commit() throws HammurapiException {
		try {
			factory.getResultsEngine().commit(getId());
		} catch (SQLException e) {
			throw new HammurapiException(e);
		}
	}    
	
	public boolean isNew() {
		try {
			return factory.getResultsEngine().getAggregateResultsIsNew(id)>0;
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}
	
	private BasicResults baseLine;

	public org.hammurapi.results.BasicResults getBaseLine() {
		if (baseLineId!=null && baseLine==null) {
			try {
				baseLine=new BasicResults(baseLineId.intValue(), factory);
			} catch (SQLException e) {
				throw new HammurapiRuntimeException(e);
			}
		}
		
		return baseLine;
	}

	/**
	 * @param baseLineId
	 */
	void setBaseLineId(Integer baseLineId) {
		this.baseLineId=baseLineId;
	}	
}
