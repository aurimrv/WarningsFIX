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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Set;

import org.hammurapi.HammurapiException;
import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.InspectorDescriptor;
import org.hammurapi.InspectorSet;
import org.hammurapi.WaiverSet;
import org.hammurapi.results.persistent.jdbc.sql.Report;
import org.hammurapi.results.persistent.jdbc.sql.ReportImpl;
import org.hammurapi.results.persistent.jdbc.sql.ResultsEngine;

import com.pavelvlasov.cache.AbstractProducer;
import com.pavelvlasov.cache.Entry;
import com.pavelvlasov.cache.MemoryCache;
import com.pavelvlasov.convert.CompositeConverter;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.impl.DbRepositoryImpl;
import com.pavelvlasov.metrics.MeasurementCategoryFactory;
import com.pavelvlasov.persistence.Storage;
import com.pavelvlasov.sql.Parameterizer;
import com.pavelvlasov.sql.RowProcessor;
import com.pavelvlasov.sql.RowProcessorEx;
import com.pavelvlasov.sql.SQLProcessor;
import com.pavelvlasov.wrap.WrapperHandler;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.16 $
 */
public class ResultsFactory extends org.hammurapi.results.ResultsFactory {
	private final class Deferrer implements InvocationHandler {
		private final AggregatedResults results;

		private Deferrer(AggregatedResults results) {
			super();
			this.results = results;
		}

		public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
			if (joined || Thread.currentThread()==taskThread) {
				return method.invoke(results, args);
			}
			
			if (method.getDeclaringClass().getName().equals("java.lang.Object")) {
				return method.invoke(results, args);
			} else if (method.getReturnType().equals(void.class)) {
				execute(
						new Task() {

							public void execute() throws HammurapiException {
								try {
									method.invoke(results, args);
								} catch (IllegalArgumentException e) {
									throw new HammurapiException(e);
								} catch (IllegalAccessException e) {
									throw new HammurapiException(e);
								} catch (InvocationTargetException e) {
									throw new HammurapiException(e);
								}											
							}
							
						});
				return null;
			} else {
				throw new HammurapiException("Non-void method "+method+" can't be executed in a separate thread");
			}
		}
	}

	private WaiverSet waiverSet;
	private SQLProcessor sqlProcessor;
	
	private MemoryCache resultsCache=new MemoryCache(new AbstractProducer() {

		public Entry get(Object key) {
			try {
				int resultId=((Number) key).intValue();
//				System.out.println("Key: "+key.getClass()+" - "+key);
				Class resultClass=getClass().getClassLoader().loadClass(getResultsEngine().getResultType(resultId));
//				System.out.println("Result class: "+resultClass);
				Constructor resultConstructor=resultClass.getConstructor(new Class[]{int.class,ResultsFactory.class});
				final Object result = resultConstructor.newInstance(new Object[]{CompositeConverter.getDefaultConverter().convert(key, Integer.class, false),ResultsFactory.this});				
				return new Entry() {

					public long getExpirationTime() {
						return 0;
					}

					public long getTime() {
						return 0;
					}

					public Object get() {
						return result;
					}
					
				};
				
			} catch (IllegalArgumentException e) {
				throw new HammurapiRuntimeException(e);
			} catch (SecurityException e) {
				throw new HammurapiRuntimeException(e);
			} catch (InstantiationException e) {
				throw new HammurapiRuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new HammurapiRuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new HammurapiRuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new HammurapiRuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new HammurapiRuntimeException(e);
			} catch (SQLException e) {
				throw new HammurapiRuntimeException(e);
			}			
		}

		public Set keySet() {			
			return null;
		}
		
	}, 
	null,
	MeasurementCategoryFactory.getCategory(getClass().getName()+".resultsCache"));
	
	SQLProcessor getSQLProcessor() {
		return sqlProcessor;
	}
	
	private ResultsEngine resultsEngine;
	private Number baseLineId;
	private String name;
	
	ResultsEngine getResultsEngine() {
		if (resultsEngine==null) {
			resultsEngine=new ResultsEngine(getSQLProcessor());
		}
		return resultsEngine;
	}
	
	int nextPK(final String keyName) throws SQLException {
		Connection con=sqlProcessor.getConnection();
		try {
			boolean ac=con.getAutoCommit();
			try {
				con.setAutoCommit(false);
				final Parameterizer parameterizer=new Parameterizer() {
					public void parameterize(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, keyName);
					}					
				};
				
				final int[] value={0};
				
				sqlProcessor.processSelect(
						"SELECT KEY_VALUE FROM PRIMARY_KEY WHERE KEY_NAME=?",
						parameterizer, 
						new RowProcessorEx() {
							public boolean process(ResultSet resultSet) throws SQLException {
								value[0]=resultSet.getInt("KEY_VALUE")+1;
								sqlProcessor.processUpdate("UPDATE PRIMARY_KEY SET KEY_VALUE=KEY_VALUE+1 WHERE KEY_NAME=?", parameterizer);
								return false;
							}

							public void onEmptyResultSet() throws SQLException {
								sqlProcessor.processUpdate("INSERT INTO PRIMARY_KEY (KEY_NAME, KEY_VALUE) VALUES (?, 0)", parameterizer);
							}							
						});
				
				return value[0];
			} finally {
				con.setAutoCommit(ac);
			}
		} finally {
			sqlProcessor.releaseConnection(con);
		}
	}

	public ResultsFactory(final ResultsFactoryConfig config) throws SQLException, HammurapiException {
		this.sqlProcessor=config.getSqlProcessor();
		//sqlProcessor.setMetricConsumer(new StackCountingMetricConsumer(1));				
		this.waiverSet=config.getWaiverSet();
		this.inspectorSet=config.getInspectorSet();
		this.repository=config.getRepository();
		this.storage=config.getStorage();
		this.name=config.getName();
		
		reportId=nextPK("REPORT");
		Report report=new ReportImpl(true);
		report.setId(reportId);
		report.setName(config.getName());		
		report.setDescription(config.getDescription());
		report.setReportNumber(new Integer(config.getReportNumber())); 
		report.setHostId(config.getHostId());
		report.setHostName(config.getHostName());
		getResultsEngine().insertReport(report);
		
		if (config.getBaseLine()!=null) {
			baseLineId=getResultsEngine().getBaseLineIdByDate(config.getName(), new Timestamp(config.getBaseLine().getTime()));
			if (baseLineId==null) {
				throw new HammurapiException("Baseline report not found for "+config.getBaseLine());
			}
		}
	}
	
	public void setSummary(final org.hammurapi.results.AggregatedResults results) {
		try {
			AggregatedResults aggregatedResults = unWrap(results);
			getResultsEngine().setReportResult(aggregatedResults.getId(), reportId);
			aggregatedResults.setBaseLineId((Integer) CompositeConverter.getDefaultConverter().convert(baseLineId, Integer.class, false));
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	/**
	 * @param results
	 * @return Unwrapped result.
	 */
	AggregatedResults unWrap(final org.hammurapi.results.AggregatedResults results) {
		return Proxy.isProxyClass(results.getClass()) ? ((Deferrer) Proxy.getInvocationHandler(results)).results : (AggregatedResults) results;
	}

	public org.hammurapi.results.AggregatedResults newAggregatedResults() {
		try {
			return (org.hammurapi.results.AggregatedResults) proxy(new AggregatedResults(waiverSet, this));
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	public org.hammurapi.results.NamedResults newNamedResults(String name) {
		try {
			return (org.hammurapi.results.NamedResults) proxy(new NamedResults(name, waiverSet, this));
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	public org.hammurapi.results.DetailedResults newDetailedResults(String name) {
		try {
			return (org.hammurapi.results.DetailedResults) proxy(new DetailedResults(name, waiverSet, this));
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	public org.hammurapi.results.CompositeResults newCompositeResults(String name) {
		try {
			return (org.hammurapi.results.CompositeResults) proxy(new CompositeResults(name, waiverSet, this));
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	public org.hammurapi.results.ReviewResults newReviewResults(CompilationUnit compilationUnit) {
		try {
			return (org.hammurapi.results.ReviewResults) proxy(new ReviewResults(compilationUnit, waiverSet, this));
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}
	
	private InspectorSet inspectorSet;
	private int reportId;
	
	/**
	 * @return report Id
	 */
	public int getReportId() {
		return reportId;
	}
	
	private Storage storage;
	
	public Storage getStorage() {		
		return storage;
	}

	public InspectorDescriptor getInspectorDescriptor(String string) {
		return inspectorSet.getDescriptor(string);
	}

	private Repository repository;
	
	public static final String HYPERSONIC_INIT_SCRIPT = "org/hammurapi/results/persistent/jdbc/Hammurapi.Hypersonic.sql";
	public static final String CLOUDSCAPE_INIT_SCRIPT = "org/hammurapi/results/persistent/jdbc/Hammurapi.Cloudscape.sql";
	
	public CompilationUnit getCompilationUnit(int id) throws JselException {
		return repository.getCompilationUnit(id);
	}

	public org.hammurapi.results.ReviewResults findReviewResults(final CompilationUnit cu) {
    	try {
        	final ReviewResults[] ret={null};
	    	getSQLProcessor().processSelect(
	    			"SELECT ID, TYPE FROM RESULT WHERE COMPILATION_UNIT=? AND COMMITED=1",
					new Parameterizer() {
						public void parameterize(PreparedStatement ps) throws SQLException {
							ps.setInt(1, cu.getSourceId().intValue());
						}
	    			},
					new RowProcessor() {
						public boolean process(final ResultSet rs) {
							try {
								Constructor constructor = getClass().getClassLoader().loadClass(
										rs.getString("TYPE")).getConstructor(new Class[]{int.class,ResultsFactory.class});
								ret[0]=(ReviewResults) constructor.newInstance(new Object[]{
										new Integer(rs.getInt("ID")),
										ResultsFactory.this});
								
								sqlProcessor.processUpdate(
										"UPDATE RESULT SET IS_NEW=0 WHERE ID=?",
										new Parameterizer() {
											public void parameterize(PreparedStatement ps) throws SQLException {
												ps.setInt(1, rs.getInt("ID"));
											}											
										});								
							} catch (IllegalArgumentException e) {
								throw new HammurapiRuntimeException(e);
							} catch (SecurityException e) {
								throw new HammurapiRuntimeException(e);
							} catch (InstantiationException e) {
								throw new HammurapiRuntimeException(e);
							} catch (IllegalAccessException e) {
								throw new HammurapiRuntimeException(e);
							} catch (InvocationTargetException e) {
								throw new HammurapiRuntimeException(e);
							} catch (NoSuchMethodException e) {
								throw new HammurapiRuntimeException(e);
							} catch (ClassNotFoundException e) {
								throw new HammurapiRuntimeException(e);
							} catch (SQLException e) {
								throw new HammurapiRuntimeException(e);
							}
							return false;
						}
	    			});
	        return ret[0];
    	} catch (SQLException e) {
    		throw new HammurapiRuntimeException(e);
    	}
	}
	
	public void commit(long executionTime) {
		try {
			getResultsEngine().setReportExecutionTime(executionTime, reportId);
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}

	/**
	 * Adds a message to message table if it is not exists.
	 * Returns message id.
	 */
	int addMessage(String message) {
		return ((DbRepositoryImpl) repository).addMessage(message);
	}
	
	public void shutdown() {
		resultsCache.shutdown();
	}

	/**
	 * @param results
	 */
	void addToCache(BasicResults results) {
		resultsCache.put(new Integer(results.getId()), results, 0, 0);		
	}
	
	BasicResults getResult(Object id) {
		Entry entry = resultsCache.get(id);
		return (BasicResults) (entry==null ? null : entry.get());
	}

	/**
	 * @throws HammurapiException
	 * 
	 */
	public void cleanupOldReports() throws HammurapiException {
		try {
			getResultsEngine().setOldReports(getReportId(), name);
			getResultsEngine().deleteOldResults();
		} catch (SQLException e) {
			throw new HammurapiException("Could not delete old results: "+e, e);
		}
		
	}
	
	private LinkedList tasks=new LinkedList();
	private static final int MAX_QUEUE=1000;
	
	private boolean terminated;
	
	private void checkTerminated() throws HammurapiException {
	    synchronized (tasks) {
	        if (terminated) {
	            throw new HammurapiException("Results processing thread was terminated prematurely");
	        }
	    }
	}
	
	private Thread taskThread=new Thread() {
		{
			setName("Results processing thread");
			start();
		}

		public void run() {
			try {
				while (true) {
					Task task;
					synchronized (tasks) {
						while (tasks.isEmpty()) {
							try {
								tasks.wait();
							} catch (InterruptedException e) {
								return;
							}
						}
						
						task=(Task) tasks.removeFirst();
						if (tasks.size()<MAX_QUEUE) {
							tasks.notifyAll();
						}
					}
					
					if (task==null) {
						return;
					} 
					
					task.execute();
				}
			} catch (Exception e) {
			    e.printStackTrace();
			} finally {
			    synchronized (tasks) {
			        terminated=true;
			        tasks.notifyAll();
			    }
			}
		}
	};

	/**
	 * Executes task in a separate thread
	 */
	public void execute(Task task) throws HammurapiException {
		if (task!=null) {
			synchronized (tasks) {
			    checkTerminated();
			    
				if (tasks.size()>=MAX_QUEUE) {
					try {
						tasks.wait();
						checkTerminated();
					} catch (InterruptedException e) {
						throw new HammurapiException(e);
					}
				}

				tasks.add(task);
				tasks.notifyAll();
			}	
		}
	}
		
	private Object proxy(final AggregatedResults results) {
		return Proxy.newProxyInstance(
				results.getClass().getClassLoader(),
				WrapperHandler.getClassInterfaces(results.getClass()),
				new Deferrer(results));
	}
	
	private boolean joined;

	public void join() throws HammurapiException {
		checkTerminated();
		
		synchronized (tasks) {
			tasks.add(null);
			tasks.notifyAll();
		}
		
		try {
			taskThread.join();
		} catch (InterruptedException e) {
			throw new HammurapiException(e);
		}
		
		joined=true;
	}
	
	String getName() {
		return name;
	}
}
