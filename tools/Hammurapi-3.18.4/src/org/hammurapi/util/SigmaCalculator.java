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
package org.hammurapi.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Iterator;

import org.hammurapi.HistoryImpl;
import org.hammurapi.HistoryOutputEngine;
import org.hammurapi.results.simple.SimpleAggregatedResults;

import com.pavelvlasov.sql.SQLProcessor;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class SigmaCalculator {
    
	private static String sigma(double violationLevel, long reviews) {
		double p=1.0-violationLevel/reviews;
		if (reviews==0) {
			return "No results";
		} else if (p<=0) {
			return "Full incompliance";
		} else if (p>=1) {
			return "Full compliance";
		} else {
			return MessageFormat.format("{0,number,#.###}", new Object[] {new Double(SimpleAggregatedResults.normsinv(p)+1.5)});
		}
	}

	private static String dpmo(double violationLevel, long reviews) {
		if (reviews==0) {
			return "Not available, no reviews";
		}
		
		return String.valueOf((int) (1000000*violationLevel/reviews));
	}
	
	private static Connection getConnection() throws SQLException {
	    // Implement this method
	    throw new UnsupportedOperationException("Implement!!!");
	}


    public static void main(String[] args) throws Exception {
		Connection con = getConnection();
		SQLProcessor processor = new SQLProcessor(con, null);
		HistoryOutputEngine engine=new HistoryOutputEngine(processor);
		try {
		    Iterator it=engine.getHistory().iterator();
		    while (it.hasNext()) {
		        HistoryImpl history=(HistoryImpl) it.next();
		        if (history.getSigma()==null) {
			        history.setSigma(sigma(history.getViolationLevel(), history.getReviews()));
			        history.setDpmo(dpmo(history.getViolationLevel(), history.getReviews()));
			        history.update(processor, "HISTORY");
		        }
		    }
		} finally {					
			con.close();
		}
    }
}
