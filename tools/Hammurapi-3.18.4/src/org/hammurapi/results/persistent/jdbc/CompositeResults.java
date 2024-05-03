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
import java.util.ArrayList;
import java.util.Collection;

import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.WaiverSet;
import org.hammurapi.results.AggregatedResults;

import com.pavelvlasov.convert.Converter;
import com.pavelvlasov.util.ConvertingCollection;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.6 $
 */
public class CompositeResults extends DetailedResults implements org.hammurapi.results.CompositeResults {
	public CompositeResults(int id, ResultsFactory factory) throws SQLException {
		super(id, factory);
	}
	
	private Collection children;
	
    public Collection getChildren() {
    	if (children==null) {
	    	try {
	    		Collection childrenKeys=factory.getResultsEngine().getResultChildren(getId(),new ArrayList());
	    		
	    		children=new ConvertingCollection(
	    				childrenKeys,
						new Converter() {
							public Object convert(Object source) {
								return factory.getResult(source);
							}							
	    				}, 
						new Converter() {
							public Object convert(Object source) {
								return new Integer(((BasicResults) source).getId());
							}							
	    				});
	    	} catch (SQLException e) {
	    		throw new HammurapiRuntimeException(e);
	    	}
    	}
    	return children;
    }
    
    public void add(AggregatedResults child) {
        aggregate(child);
        children=null;
    }        
    
    CompositeResults(String name, WaiverSet waiverSet, ResultsFactory factory) throws SQLException {
        super(name, waiverSet, factory);
    }

	public int size() {
		try {
			return factory.getResultsEngine().getCompositeResultSize(getId())-1;
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
	}
}
