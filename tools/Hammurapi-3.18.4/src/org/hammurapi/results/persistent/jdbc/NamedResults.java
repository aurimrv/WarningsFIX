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

import org.hammurapi.WaiverSet;

import com.pavelvlasov.sql.Parameterizer;
import com.pavelvlasov.sql.RowProcessor;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class NamedResults extends AggregatedResults implements Comparable, org.hammurapi.results.NamedResults {
    
	/**
	 * @param id
	 * @param factory
	 * @throws SQLException
	 */
	public NamedResults(int id, ResultsFactory factory) throws SQLException {
		super(id, factory);
		factory.getSQLProcessor().processSelect(
				"SELECT NAME FROM RESULT WHERE ID=?",
				idParameterizer,
				new RowProcessor() {
					public boolean process(ResultSet rs) throws SQLException {
						name=rs.getString("NAME");
						return false;
					}
				});
	}
    
    NamedResults(final String name, WaiverSet waiverSet, ResultsFactory factory) throws SQLException {
    	super(waiverSet, factory);
        this.name=name;
        factory.getSQLProcessor().processUpdate(
        		"UPDATE RESULT SET NAME=? WHERE ID=?",
				new Parameterizer() {
					public void parameterize(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, name);
						preparedStatement.setInt(2, getId());
					}
        		});        
    }
    
    private String name;
    
    public String getName() {
        return name;
    }
    
    public int compareTo(Object obj) {
        if (obj instanceof NamedResults) {
            String otherName=((NamedResults) obj).getName();
            if (name==null && otherName==null) {
                return 0;
            } else if (name!=null) {
                return name.compareTo(otherName);
            } else {
                return 1;
            }
        }
        
		return 2;
    }            
}
