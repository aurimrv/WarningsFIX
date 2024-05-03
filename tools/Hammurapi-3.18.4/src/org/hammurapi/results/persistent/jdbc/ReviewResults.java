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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.WaiverSet;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.sql.Parameterizer;
import com.pavelvlasov.sql.RowProcessor;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.5 $
 */
public class ReviewResults extends DetailedResults implements org.hammurapi.results.ReviewResults {
	public ReviewResults(int id, ResultsFactory factory) throws SQLException {
		super(id, factory);
	}
	
	ReviewResults(CompilationUnit cu, WaiverSet waiverSet, ResultsFactory factory) throws SQLException {
		super(cu.getName(), waiverSet, factory);
		final int cuId = cu.getSourceId().intValue();
		factory.getSQLProcessor().processUpdate(
				"UPDATE RESULT SET COMPILATION_UNIT=?, IS_NEW=1 WHERE ID=?",
				new Parameterizer() {
					public void parameterize(PreparedStatement ps) throws SQLException {
						ps.setInt(1, cuId);
						ps.setInt(2, getId());
					}
				});
	}
	
	public CompilationUnit getCompilationUnit() {
		try {
			final int[] ret={-1};
			factory.getSQLProcessor().processSelect(
					"SELECT COMPILATION_UNIT FROM RESULT WHERE ID=?",
					idParameterizer,
					new RowProcessor() {
						public boolean process(ResultSet rs) throws SQLException {
							ret[0]=rs.getInt("COMPILATION_UNIT");
							return false;
						}
					});
			return ret[0]==-1 ? null : factory.getCompilationUnit(ret[0]);
		} catch (JselException e) {
			throw new HammurapiRuntimeException(e);
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}
}
