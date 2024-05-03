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
package org.hammurapi.results;

import java.util.Collection;
import java.util.Map;

import org.hammurapi.HammurapiException;
import org.hammurapi.Violation;
import org.hammurapi.Waiver;
import org.hammurapi.WaiverSet;

import com.pavelvlasov.review.SourceMarker;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.5 $
 */
public interface AggregatedResults extends BasicResults {
	/**
	 * Add violation to severity summary
	 * @param violation
	 * @return null if violation was added, Waiver if it was waived
	 */
	Waiver addViolation(Violation violation) throws HammurapiException;
	
	Map getSeveritySummary();
	Collection getWarnings();
	boolean hasWarnings();
	void addWarning(Violation warning);
	void addMetric(SourceMarker source, String name, double value);
	Map getMetrics();
	//    public boolean isIncomplete() {
	void aggregate(AggregatedResults agregee);
	/**
	 * Sets number of reviews. 
	 * @param reviews The reviews to set.
	 */
	void setReviewsNumber(long reviews);
	void setCodeBase(long codeBase);
	
	void addAnnotation(Annotation annotation);
	Collection getAnnotations();
	
	WaiverSet getWaiverSet();
	
	/**
	 * Commits results, which means that all calculations
	 * for results was completed successfully.
	 */
	void commit() throws HammurapiException;
	
	/**
	 * @return true this review result or one of its children is
	 * different from the previous run. 
	 */
	boolean isNew();	
	
	/**
	 * 
	 * @return Baseline basic results
	 */
	BasicResults getBaseLine();
}
