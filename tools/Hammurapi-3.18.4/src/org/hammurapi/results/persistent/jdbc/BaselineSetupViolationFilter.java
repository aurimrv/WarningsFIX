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

import java.sql.SQLException;

import org.hammurapi.HammurapiException;
import org.hammurapi.Violation;
import org.hammurapi.ViolationFilter;
import org.hammurapi.results.persistent.jdbc.sql.BaselineViolationImpl;
import org.hammurapi.results.persistent.jdbc.sql.ResultsEngine;

import com.pavelvlasov.config.Component;
import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.review.Signed;

/**
 * @author Pavel Vlasov
 * @revision $Revision$
 */
public class BaselineSetupViolationFilter implements ViolationFilter, Component {

	private String reportName;
	private ResultsEngine resultsEngine;

	public boolean accept(Violation violation) throws HammurapiException {
		if (violation.getDescriptor()!=null && violation.getSource() instanceof Signed)
			try {
					BaselineViolationImpl bv=new BaselineViolationImpl(true);
					bv.setInspector(violation.getDescriptor().getName());
					String signature = ((Signed) violation.getSource()).getSignature();
					if (signature!=null) {
						bv.setSignature(signature);					
						bv.setReportName(reportName);
						resultsEngine.insertBaselineViolation(bv);
						return false;
					} 
			} catch (SQLException e) {
				throw new HammurapiException("Cannot store baseline violation", e);
			}
		return true;
	}

	public void start() throws ConfigurationException {
		ResultsFactory resultsFactory = ((ResultsFactory) org.hammurapi.results.ResultsFactory.getInstance());
		try {
			reportName = resultsFactory.getName();
			resultsEngine = resultsFactory.getResultsEngine();
			resultsEngine.deleteBaseline(reportName);
		} catch (SQLException e) {
			throw new ConfigurationException("Cannot delete old baseline", e);
		}		
	}

	public void stop() throws ConfigurationException {
		// Nothing to do.		
	}

	public void setOwner(Object owner) {
		// Ignore		
	}	

}
