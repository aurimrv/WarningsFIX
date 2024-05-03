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
import org.hammurapi.results.persistent.jdbc.sql.BaselineViolation;
import org.hammurapi.results.persistent.jdbc.sql.ResultsEngine;

import com.pavelvlasov.config.Component;
import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.review.Signed;

/**
 * @author Pavel Vlasov
 * @revision $Revision$
 */
public class BaselineViolationFilter implements ViolationFilter, Component {

	private String reportName;
	private ResultsEngine resultsEngine;

	public boolean accept(Violation violation) throws HammurapiException {
		if (violation.getDescriptor()!=null && violation.getSource() instanceof Signed)
			try {
					String signature = ((Signed) violation.getSource()).getSignature();
					if (signature!=null) {
						BaselineViolation bv = resultsEngine.getBaselineViolation(
								reportName,
								violation.getDescriptor().getName(),
								signature);
						if (bv!=null) {
							return false;
						}
					} 
			} catch (SQLException e) {
				throw new HammurapiException("Cannot obtain baseline violation", e);
			}
		return true;
	}

	public void start() throws ConfigurationException {
		ResultsFactory resultsFactory = ((ResultsFactory) org.hammurapi.results.ResultsFactory.getInstance());
		reportName = resultsFactory.getName();
		resultsEngine = resultsFactory.getResultsEngine();
	}

	public void stop() throws ConfigurationException {
		// Nothing to do
	}

	public void setOwner(Object owner) {
		// Ignore		
	}	

}
