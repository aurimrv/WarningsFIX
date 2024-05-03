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

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.hammurapi.results.InspectorSummary;
import org.hammurapi.results.ResultsFactory;
import org.hammurapi.results.ReviewResults;
import org.hammurapi.results.quick.Inspector;
import org.hammurapi.results.quick.ResultsEngine;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.metrics.Metric;
import com.pavelvlasov.review.Signed;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.sql.SQLProcessor;
import com.pavelvlasov.sql.Transaction;
import com.pavelvlasov.sql.hypersonic.HypersonicDataSource;
import com.pavelvlasov.sql.hypersonic.HypersonicStandaloneDataSource;
import com.pavelvlasov.sql.hypersonic.HypersonicTmpDataSource;
import com.pavelvlasov.util.DispatcherAware;
import com.pavelvlasov.util.DispatchingVisitor;
import com.pavelvlasov.util.OrderedTarget;
import com.pavelvlasov.util.DispatchingVisitor.Stats;


public class QuickResultsCollector implements DispatcherAware, OrderedTarget {
	static final byte RESULT_NEW=0;
	static final byte RESULT_PREV=1;
	static final byte RESULT_UNTOUCHED=2;
	
	private final QuickHammurapiTask task;
	private Collection listeners;
	private long start=System.currentTimeMillis();
	private DispatchingVisitor dispatcher;
	
	DataSource getDataSource() {
		return dataSource;
	}
	
	ResultsEngine getEngine() {
		return engine;
	}
	
	SQLProcessor getProcessor() {
		return processor;
	}
	
	private HypersonicDataSource dataSource;
	private SQLProcessor processor;
	private ResultsEngine engine;
	
	QuickResultsCollector(QuickHammurapiTask task, String title, Collection listeners) throws ClassNotFoundException, IOException, SQLException {
		this.task = task;
		this.listeners=listeners;
		
		final String initScript = "org/hammurapi/results/simple/Quickurapi.Hypersonic.sql";
		if (task.database==null) {
			dataSource=new HypersonicTmpDataSource(initScript);
		} else {
			dataSource = new HypersonicStandaloneDataSource(
					task.database.getAbsolutePath(),
					new Transaction() {

						public boolean execute(SQLProcessor processor) throws SQLException {
							try {
								processor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(initScript)));						
							} catch (IOException e) {
								throw new BuildException("Cannot initialize database", e);
							}
							return true;
						}						
					});
		}
		processor=new SQLProcessor(dataSource, null);
		engine=new ResultsEngine(processor);
	}
	
	public void shutdown() throws SQLException {
		dataSource.shutdown();
	}
		
	// Second to visit, second from the end to leave
	private Integer order=new Integer(Integer.MIN_VALUE+1);
	
	public Integer getOrder() {
		return order;
	}
	
	public boolean visit(CompilationUnit compilationUnit) {		
		log(compilationUnit.getRelativeName()+" - reviewing");
		ResultsFactory.pushThreadResults(ResultsFactory.getInstance().newReviewResults(compilationUnit));
		
		if (dispatcher!=null) {
			dispatcher.getThreadStats().reset();
		}
		return true;
	}
	
	private int counter=0;
	
	private static MessageFormat messageFormat=new MessageFormat(
			"{0,date, yyyy/MM/dd HH:mm:ss} Progress: {1} file(s). Elapsed time: {2} min. {3} sec. ");
	
	public void leave(CompilationUnit compilationUnit) throws HammurapiException, SQLException {		
		Iterator it=listeners.iterator();
		ReviewResults results = (ReviewResults) ResultsFactory.popThreadResults();
		results.commit();
		while (it.hasNext()) {
			if (dispatcher!=null) {
				Stats threadStats = dispatcher.getThreadStats();
				results.setCodeBase(threadStats.getVisits());
				results.setReviewsNumber(threadStats.getInvocations());
			}
			((Listener) it.next()).onReview(results);
		}		
		
		// Result
		/*
		 * int Id, 
		 * long Codebase, 
		 * java.sql.Timestamp ResultDate, 
		 * Short MaxSeverity, 
		 * long Reviews, 
		 * double ViolationLevel, 
		 * long Violations, 
		 * long WaivedViolations, 
		 * String Name, 
		 * boolean HasWarnings, 
		 * String PackageName, 
		 * byte State, 
		 * long CuSize, 
		 * long CuChecksum 		 
		 */
		int resultId = processor.nextPK("PRIMARY_KEY", "RESULT");
		String packageName = compilationUnit.getPackage().getName();
		engine.insertResult(
				resultId,
				results.getCodeBase(),
				new Timestamp(results.getDate().getTime()),
				results.getMaxSeverity()==null ? null : new Short(results.getMaxSeverity().shortValue()),
				results.getReviewsNumber(),
				results.getViolationLevel(),
				results.getViolationsNumber(),
				results.getWaivedViolationsNumber(),
				results.getName(),
				results.hasWarnings(),
				packageName,
				RESULT_NEW,
				compilationUnit.getSize(),
				compilationUnit.getCheckSum());
				
		// Metric
		Iterator mit=results.getMetrics().values().iterator();
		while (mit.hasNext()) {
			Metric metric=(Metric) mit.next();
			/*
			 * int ResultId, 
			 * String Name, 
			 * double MinValue, 
			 * double MaxValue, 
			 * double MetricTotal, 
			 * int Measurements 
			 */
			engine.insertMetric(
					resultId,
					metric.getName(),
					metric.getMin(),
					metric.getMax(),
					metric.getTotal(),
					metric.getNumber());
		}
		
		// Inspector
		Iterator sit=results.getSeveritySummary().values().iterator();
		while (sit.hasNext()) {
			Iterator iit=((Map) sit.next()).values().iterator();
			while (iit.hasNext()) {
				InspectorSummary is=(InspectorSummary) iit.next();
				Inspector dis = engine.getInspector(packageName, is.getName(), resultId);
				if (dis==null) {
					/*
					 * String PackageName, 
					 * String Name, 
					 * short Severity, 
					 * String Description, 
					 * String ConfigInfo, 
					 * int Violations, 
					 * int WaivedViolations 
					 */
					engine.insertInspector(
							packageName,
							is.getName(),
							resultId,
							is.getSeverity()==null ? 0 : is.getSeverity().shortValue(),
							is.getDescription(),
							is.getConfigInfo(),
							is.getLocationsCount(),
							0);
				} else {
					engine.addInspectorViolations(is.getLocationsCount(), packageName, is.getName());
				}
			}
		}		
		
		// Warning
		Iterator wit=results.getWarnings().iterator();
		while (wit.hasNext()) {
			Violation warning=(Violation) wit.next();
			InspectorDescriptor descriptor = warning.getDescriptor();
			if (descriptor!=null) {
				Inspector dis = engine.getInspector(packageName, descriptor.getName(), resultId);
				if (dis==null) {
					/*
					 * String PackageName, 
					 * String Name, 
					 * short Severity, 
					 * String Description, 
					 * String ConfigInfo, 
					 * int Violations, 
					 * int WaivedViolations 
					 */
					engine.insertInspector(
							packageName,
							descriptor.getName(),
							resultId,
							descriptor.getSeverity()==null ? 0 : descriptor.getSeverity().shortValue(),
							descriptor.getDescription(),
							null,
							0,
							0);
				}
			}
			
			int warningId = processor.nextPK("PRIMARY_KEY", "WARNING");
			/*
			 * int Id, 
			 * int ResultId, 
			 * String PackageName, 
			 * String Inspector, 
			 * String Message, 
			 * String Source, 
			 * Integer Line, 
			 * Integer Col, 
			 * String SourceSignature 
			 */
			SourceMarker source = warning.getSource();
			engine.insertWarning(
					warningId,
					resultId,
					packageName,
					descriptor.getName(),
					warning.getMessage(),
					source==null ? null : source.getSourceURL(),
					source==null ? null : new Integer(source.getLine()),
					source==null ? null : new Integer(source.getColumn()),
					source instanceof Signed ? ((Signed) source).getSignature() : null);
		}
				
		++counter;
		long now=System.currentTimeMillis();
		
		long elapsedSec = (now-start)/1000;
		long min=elapsedSec/60;
		long sec=elapsedSec % 60;
		
		task.log(messageFormat.format(
				new Object[] {
						new Date(now), 
						new Integer(counter),
						new Long(min),
						new Long(sec)}, 
				new StringBuffer(), null).toString(), Project.MSG_INFO);		
	}
		
	/**
	 * @param string
	 */
	private void log(String message) {
		task.log(message);		
	}

	public void setDispatcher(DispatchingVisitor dispatcher) {
		this.dispatcher=dispatcher;
	}
}
