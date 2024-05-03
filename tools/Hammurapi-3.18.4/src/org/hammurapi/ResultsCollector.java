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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tools.ant.Project;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.ResultsFactory;
import org.hammurapi.results.ReviewResults;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.Package;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.util.DispatcherAware;
import com.pavelvlasov.util.DispatchingVisitor;
import com.pavelvlasov.util.OrderedTarget;
import com.pavelvlasov.util.DispatchingVisitor.Stats;


public class ResultsCollector implements DispatcherAware, OrderedTarget {
	private final HammurapiTask task;
	private InspectorSet inspectorSet;
	private Collection listeners;
	private Map packageResults=new HashMap();
	private CompositeResults summary;
	private long start;
	private DispatchingVisitor dispatcher;
	private WaiverSet waiverSet;
	
	ResultsCollector(HammurapiTask task, InspectorSet inspectorSet, WaiverSet waiverSet, CompositeResults summary, Collection listeners) {
		this.inspectorSet=inspectorSet;
		this.waiverSet=waiverSet;
		this.task = task;
		this.listeners=listeners;
		this.summary=summary;
	}
	
	/**
	 * @ant:ignore
	 * @param result
	 */
	CompositeResults getPackageResult(String packageName) {			
		synchronized (packageResults) {
			CompositeResults ret=(CompositeResults) packageResults.get(packageName);
			if (ret==null) {
				ret=ResultsFactory.getInstance().newCompositeResults(packageName);
				packageResults.put(packageName, ret);
			}
			return ret;
		}			
	}
	
	// Second to visit, second from the end to leave
	private Integer order=new Integer(Integer.MIN_VALUE+1);
	
	public Integer getOrder() {
		return order;
	}
	
	public boolean visit(CompilationUnit compilationUnit) {		
		ReviewResults result = task.isForce() ? null : ResultsFactory.getInstance().findReviewResults(compilationUnit);
		if (result!=null) {
			if (task.isForceOnWarnings() && result.hasWarnings()) {
				result=null;
			} else if (task.isForceOnWaivers()) {
				// Checking if there are violations to be waived
				Iterator it=result.getViolations().iterator();
				while (it.hasNext()) {
					Violation violation=(Violation) it.next();
					if (waiverSet.requestWaiver(violation, true)!=null) {
						result=null;
						break;
					}
				}
			}
		}
		
		
		if (result==null) {
			ResultsFactory.getThreadResults().addMetric(null, "Change ratio", 1);
			
			log(compilationUnit.getRelativeName()+" - reviewing");
			result = ResultsFactory.getInstance().newReviewResults(compilationUnit);
			ResultsFactory.pushThreadResults(result);
			
			if (dispatcher!=null) {
				dispatcher.getThreadStats().reset();
			}
			return true;
		}
		
		ResultsFactory.getThreadResults().addMetric(null, "Change ratio", 0);
		
		log(compilationUnit.getRelativeName()+" - skipped");
		synchronized (packageResults) {
			getPackageResult(compilationUnit.getPackage().getName()).add(result);
		}
		
		return false;
	}
	
	private static MessageFormat messageFormat=new MessageFormat(
			"{0,date, yyyy/MM/dd HH:mm:ss} Progress: {1,number,integer}% ({2}/{3}). " +
			"Elapsed time: {4} min. {5} sec. Remaining time: {6} min. {7} sec. ");
	
	public void leave(final CompilationUnit compilationUnit) throws HammurapiException {		
		final ReviewResults results = (ReviewResults) ResultsFactory.popThreadResults();
		
		Stats threadStats = dispatcher==null ? null : dispatcher.getThreadStats();
		
		final long visits = threadStats==null ? 0 : threadStats.getVisits();
		final long invocations = threadStats==null ? 0 : threadStats.getInvocations();
		
		final CompositeResults packageResult = getPackageResult(compilationUnit.getPackage().getName());
		ResultsFactory.getInstance().execute(
				new ResultsFactory.Task() {

					public void execute() throws HammurapiException {
						results.commit();
						Iterator it=listeners.iterator();
						while (it.hasNext()) {
							if (dispatcher!=null) {
								results.setCodeBase(visits);
								results.setReviewsNumber(invocations);
							}
							((Listener) it.next()).onReview(results);
						}
						
						packageResult.add(results);
					}						
				});		
		
		++counter;
		double progress= (double) counter/(double) repoSize;
		int percentage =(int) (100*progress);
		long now=System.currentTimeMillis();
		
		long elapsedSec = (now-start)/1000;
		long min=elapsedSec/60;
		long sec=elapsedSec % 60;
		
		long remainingSec = counter==0 ? 0 : (long) (elapsedSec*(1-progress)/progress);
		long rmin= remainingSec/60;
		long rsec= remainingSec % 60;
				
		task.log(messageFormat.format(
				new Object[] {
						new Date(now), 
						new Integer(percentage),
						new Integer(counter),
						new Integer(repoSize),
						new Long(min),
						new Long(sec),
						new Long(rmin),
						new Long(rsec)}, 
				new StringBuffer(), null).toString(), Project.MSG_INFO);		
	}
	
	public void visit(Package pkg) {
		ResultsFactory.pushThreadResults(getPackageResult(pkg.getName()));
	}
	
	public void leave(Package pkg) throws HammurapiException {
		final CompositeResults packageResult = (CompositeResults) ResultsFactory.popThreadResults();
		packageResult.commit();
		
		ResultsFactory.getInstance().execute(
				new ResultsFactory.Task() {

					public void execute() throws HammurapiException {
						if (!task.skipIntactPackages || packageResult.isNew()) {
							Iterator it=listeners.iterator();
							while (it.hasNext()) {
								((Listener) it.next()).onPackage(packageResult);
							}
							
							synchronized (summary) {
								summary.add(packageResult);
							}
						}
					}
					
				});		
	}
	
	private int counter;
	private int repoSize;
	
	public void visit(Repository repository) {
		start=System.currentTimeMillis();			
		//ResultsFactory.pushThreadResults(summary);
		
		repoSize = repository.size();
		task.log("Review started at "+new Date(start)+", "+repoSize+" files to review", Project.MSG_INFO);
	}
	
	public void leave(Repository repository) throws HammurapiException {
		log(new Date()+" Completing results collection ...");		
		
		ResultsFactory.getInstance().join();
		
		log(new Date()+" Building summary");
		
		if (!task.skipIntactPackages || summary.isNew()) {
			Iterator it=listeners.iterator();
			while (it.hasNext()) {
				((Listener) it.next()).onSummary(summary, inspectorSet);
			}
		}
		
		Iterator it=task.getReviewAcceptorEntries().iterator();
		while (it.hasNext()) {
			((ReviewAcceptor) ((ReviewAcceptorEntry) it.next()).getObject(null)).accept(summary);
		}
		
		long finish=System.currentTimeMillis();			
		
		long elapsedSec = (finish-start)/1000;
		long min=elapsedSec/60;
		long sec=elapsedSec % 60;		
		
		log("Time: "+min+" min. "+sec+" sec.");
		log(
				MessageFormat.format(
						"Performance {0, number,###.000000}",
						new Object[] {
								new Double(
										(double) summary.getCodeBase() * 1000
										/ (finish - start))}));
		
		
		Integer severityThreshold = task.getSeverityThreshold();
		if (severityThreshold!=null) {
			final int sth=this.task.getSeverityThreshold().intValue();
			new ReviewAcceptor() {
				public void accept(CompositeResults summary) throws HammurapiException {
					Number severity=summary.getMaxSeverity();
					if (severity!=null && severity.intValue()<=sth) {
						throw new HammurapiNonConsumableException("Severity threshold ("+sth+") infringed");
					}
				}
			}.accept(summary);
		}
		
		Double sigmaThreshold = task.getSigmaThreshold();
		if (sigmaThreshold!=null) {
			final double cth=sigmaThreshold.doubleValue();				
			new ReviewAcceptor() {
				public void accept(CompositeResults summary) throws HammurapiException {
					try {
						if (Double.parseDouble(summary.getSigma())<cth) {
							throw new HammurapiNonConsumableException("Sigma is below threshold ("+cth+")");
						}
					} catch (NumberFormatException e) {
						throw new HammurapiNonConsumableException("Sigma is not valid");
					}
				}
			}.accept(summary);
		}
		
		Integer dpmoThreshold = task.getDpmoThreshold();
		if (dpmoThreshold!=null) {
			final int cth=dpmoThreshold.intValue();				
			new ReviewAcceptor() {
				public void accept(CompositeResults summary) throws HammurapiException {
					try {
						if (Integer.parseInt(summary.getDPMO())>cth) {
							throw new HammurapiNonConsumableException("DPMO is above threshold ("+cth+")");
						}
					} catch (NumberFormatException e) {
						throw new HammurapiNonConsumableException("DPMO is not valid");
					}
				}
			}.accept(summary);
		}
		
		if (this.task.isFailOnWarnings() && !summary.getWarnings().isEmpty()) {
			throw new HammurapiNonConsumableException("There have been warnings during execution.");				
		}
				
		ResultsFactory.popThreadResults();		
		summary.commit();
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
	/**
	 * @return Returns the summary.
	 */
	CompositeResults getSummary() {
		return summary;
	}

}
