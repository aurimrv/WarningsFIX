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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hammurapi.results.Annotation;
import org.hammurapi.results.DetailedResults;
import org.hammurapi.results.ResultsFactory;

import com.pavelvlasov.logging.Logger;
import com.pavelvlasov.review.Signed;
import com.pavelvlasov.review.SimpleSourceMarker;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.VisitorStackSource;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.7 $
 */
public class InspectorContextImpl extends InspectorContextBase {

	private Collection violationFilters;

	/**
	 * @param descriptor
	 * @param logger
	 * @param visitorStackSource
	 */
	public InspectorContextImpl(
			InspectorDescriptor descriptor, 
			Logger logger, 
			VisitorStackSource visitorStackSource,
			SessionImpl session,
			Collection violationFilters) {
		super(descriptor, logger, visitorStackSource, session);
		this.violationFilters=violationFilters;
	}

	/**
	 * 
	 * @param source
	 * @param message
	 */
	public void reportViolation(final SourceMarker source, final String message) {
		try {
			final DetailedResults threadResults = ResultsFactory.getThreadResults();
			final SourceMarker detachedSource = detach(source);
			
			ResultsFactory.getInstance().execute(
					new ResultsFactory.Task() {

						public void execute() throws HammurapiException {
							SimpleViolation violation = new SimpleViolation(detachedSource, message, descriptor);
							
							Iterator filterIterator = violationFilters==null ? null : violationFilters.iterator();
							while (filterIterator!=null && filterIterator.hasNext()) {
								if (!((ViolationFilter) filterIterator.next()).accept(violation)) {
									return;
								}
							}
							
							threadResults.addViolation(violation);						
						}
						
					});
		} catch (HammurapiException e) {
			warn(source, e);
		}
	}

	public void annotate(Annotation annotation) {
		ResultsFactory.getThreadResults().addAnnotation(annotation);
	}

	public void addMetric(SourceMarker source, String name, double value) {
		ResultsFactory.getThreadResults().addMetric(detach(source), name, value);
	}

	/**
	 * Report warning
	 * @param source
	 * @param message
	 */
	public void warn(SourceMarker source, String message) {
		Violation violation=new SimpleViolation(source==null ? null : new SimpleSourceMarker(source), message, descriptor);  
		ResultsFactory.getThreadResults().addWarning(violation);		
		if (source==null) {
			System.err.println("WARNING: "+message);
		} else {
			System.err.println("WARNING at "+source.getSourceURL()+" "+source.getLine()+":"+source.getColumn()+" : "+message);
		}
	}

	/**
	 * Report warning
	 * @param source
	 */
	public void warn(SourceMarker source, Throwable th) {
		Violation violation=new SimpleViolation(detach(source), th.toString(), descriptor);  
		ResultsFactory.getThreadResults().addWarning(violation);
		// TODO better warning handling here
		if (source==null) {
		    System.err.println("WARNING: "+th);		    
		} else {
		    System.err.println("WARNING at "+source.getSourceURL()+" "+source.getLine()+":"+source.getColumn()+" : "+th);
		}
		th.printStackTrace();
	}

	/**
	 * Creates a waiver for inspector with a given key
	 * @param inspectorKey 
	 */
	public void waive(Signed signed, final String inspectorKey) {
		final String iName=descriptor.getWaivedInspectorName(inspectorKey);
		if (iName==null) {
			warn(signed instanceof SourceMarker ? (SourceMarker) signed : null, descriptor.getName()+": Inspector with key '"+inspectorKey+"' not found.");
		} else {
			final String signature=signed==null ? null : signed.getSignature();			
			final Set signatures=new HashSet();
			if (signature!=null) {
				signatures.add(signature);
			}
			
			if (logger!=null) {
				logger.debug(this, "Inspector "+getDescriptor().getName()+" autowaives "+iName+" at "+signature);				
			}
			
			
			final Waiver waiver=new Waiver() {
				boolean active=true;
	
				public String getInspectorName() {
					return iName;
				}
	
				public boolean waive(Violation violation, boolean peek) {
					if (iName.equals(violation.getDescriptor().getName())) {
						if (signature==null) {
							return true;
						}
						
						if (violation.getSource() instanceof Signed && signature.equals(((Signed) violation.getSource()).getSignature())) {
							if (!peek) {
								active=false;
							}
							return true;
						}
					}
					return false;
				}
				
				public Date getExpirationDate() {
					return null;
				}
	
				public String getReason() {
					return descriptor.getWaiveReason(inspectorKey);
				}
	
				public boolean isActive() {
					return active;
				}
	
				public Collection getSignatures() {
					return signatures;
				}
			};
			
			final DetailedResults threadResults = ResultsFactory.getThreadResults();
			try {
				ResultsFactory.getInstance().execute(
						new ResultsFactory.Task() {

							public void execute() {
								threadResults.getWaiverSet().addWaiver(waiver, date);							
							}
							
						});
			} catch (HammurapiException e) {
				throw new HammurapiRuntimeException(e);
			}
		}				
	}
}
