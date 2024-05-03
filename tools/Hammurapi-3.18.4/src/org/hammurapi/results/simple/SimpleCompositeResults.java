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

package org.hammurapi.results.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.hammurapi.WaiverSet;
import org.hammurapi.results.AggregatedResults;
import org.hammurapi.results.CompositeResults;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class SimpleCompositeResults extends SimpleDetailedResults implements CompositeResults {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2179448002856899900L;
	private Collection children=new ArrayList();
    
    public Collection getChildren() {
        return Collections.unmodifiableCollection(children);
    }
    
    public void add(AggregatedResults child) {
        aggregate(child);
       	children.add(child);
    }        
    
    SimpleCompositeResults(String name, WaiverSet waiverSet) {
        super(name, waiverSet);
    }

	public int size() {
		int ret=0;
		Iterator it=children.iterator();
		while (it.hasNext()) {
			Object child = it.next();
			ret++;
			if (child instanceof CompositeResults) {
				ret+=((CompositeResults) child).size();
			}			
		}
		return ret;
	}
}
