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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.ReviewResults;

import com.pavelvlasov.ant.ConnectionEntry;
import com.pavelvlasov.convert.CompositeConverter;
import com.pavelvlasov.sql.SQLProcessor;

/**
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.3 $
 */
public class HistoryOutput extends ConnectionEntry implements Listener {
	private String table;
	private String reportUrl;
	private String host;
	private String description;

	public void onReview(ReviewResults reviewResult) {
		// Nothing
	}

	public void onPackage(CompositeResults packageResults) {
		// Nothing
	}

	public void onSummary(final CompositeResults summary, InspectorSet inspectorSet) throws HammurapiException {
		try {
			Connection con = getConnection();
			try {
				HistoryImpl history=new HistoryImpl(false);
				
				//history.setChangeRatio();
				history.setCodebase(summary.getCodeBase());
				history.setCompilationUnits(summary.size()-summary.getChildren().size()); 
				history.setDescription(description); 
				// history.setExecutionTime(  ); 
				history.setHasWarnings(summary.hasWarnings() ? 1 : 0); 
				history.setHost(host); 
				history.setMaxSeverity((Integer) CompositeConverter.getDefaultConverter().convert(summary.getMaxSeverity(), Integer.class, false)); 
				history.setName(summary.getName()); 
				history.setReportDate(new Timestamp(summary.getDate().getTime())); 
				history.setReportUrl(reportUrl); 
				history.setReviews(summary.getReviewsNumber()); 
				history.setViolationLevel(summary.getViolationLevel()); 
				history.setViolations(summary.getViolationsNumber()); 
				history.setWaivedViolations(summary.getWaivedViolationsNumber());
				history.setSigma(summary.getSigma());
				history.setDpmo(summary.getDPMO());
				
				history.insert(new SQLProcessor(con, null), table);			
			} finally {					
				con.close();
			}
		} catch (SQLException e) {
			throw new HammurapiException(e);
		} catch (ClassNotFoundException e) {
			throw new HammurapiException(e);			
		}
	}

	public void onBegin(InspectorSet inspectorSet) {
		// Nothing
	}
	
	/**
	 * Table name
	 * @ant.required
	 * @param table
	 */
	public void setTable(String table) {
		this.table = table;
	}
	
	/**
	 * Report url
	 * @ant.non-required
	 * @param reportUrl
	 */
	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
	
	/**
	 * Name of the host to differentiate reports from different hosts.
	 * @ant.non-required
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Review description
	 * @ant.non-required
	 * @param description
	 */
	public void setDescription(String description) {
		this.description=description;
	}	
}
