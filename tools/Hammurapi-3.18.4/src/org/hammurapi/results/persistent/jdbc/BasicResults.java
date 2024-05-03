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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;

import org.hammurapi.results.persistent.jdbc.sql.BasicResultTotal;
import org.hammurapi.results.simple.SimpleAggregatedResults;

import com.pavelvlasov.sql.Parameterizer;


/**
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.5 $
 */
public class BasicResults implements org.hammurapi.results.BasicResults {

	public Number getMaxSeverity() {
		return maxSeverity;
	}

	protected Parameterizer idParameterizer = new Parameterizer() {
				public void parameterize(PreparedStatement ps) throws SQLException {
					ps.setInt(1, getId());
				}					
			};

	public String getSigma() {
		double p=1.0-violationLevel/reviews;
		if (reviews==0) {
			return "No results";
		} else if (p<=0) {
			return "Full incompliance";
		} else if (p>=1) {
			return "Full compliance";
		} else {
			return MessageFormat.format("{0,number,#.###}", new Object[] {new Double(SimpleAggregatedResults.normsinv(p)+1.5)}) + (hasWarnings() ? " (not accurate because of warnings)" : "");
		}
	}

	public String getDPMO() {
		if (reviews==0) {
			return "Not available, no reviews";
		}
		
		return String.valueOf((int) (1000000*violationLevel/reviews)) + (hasWarnings() ? " (not accurate because of warnings)" : "");
	}

	public boolean hasWarnings() {
		return hasWarnings;
	}

	public int getWaivedViolationsNumber() {
		return waivedViolationsNumber;
	}

	public Date getDate() {
		return date;
	}

	protected Date date = new Date();

	public int getViolationsNumber() {
	    return violationsNumber; 
	}

	public long getCodeBase() {
	    return codeBase; 
	}

	public long getReviewsNumber() {
	    return reviews;
	}

	public double getViolationLevel() {
	    return violationLevel;
	}

	protected int getId() {
		return id;
	}

	protected int id;
	protected ResultsFactory factory;
	protected boolean hasWarnings = false;
	protected Number maxSeverity;
	protected int waivedViolationsNumber = 0;
	protected int violationsNumber = 0;
	protected double violationLevel = 0;
	protected long reviews = 0;
	protected long codeBase = 0;
	
	protected BasicResults(ResultsFactory factory) {
		this.factory=factory;
	}

    public BasicResults(int id, ResultsFactory factory) throws SQLException {
    	this(factory);
    	this.id=id;
    	factory.addToCache(this);
    	BasicResultTotal data = factory.getResultsEngine().getBasicResultTotal(id);
    	codeBase=data.getCodebase();
    	date=data.getResultDate();
    	reviews=data.getReviews();
    	violationLevel=data.getViolationLevel();
    	violationsNumber=(int) data.getViolations();
		waivedViolationsNumber=(int) data.getWaivedViolations();
		maxSeverity=data.getMaxSeverity();						
		hasWarnings=data.getHasWarnings()>0;
    }
	
}
