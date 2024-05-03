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

import org.hammurapi.WaiverSet;
import org.hammurapi.results.NamedResults;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class SimpleNamedResults extends SimpleAggregatedResults implements Comparable, NamedResults {
    
//    /** Creates a new instance of NamedResults */
//    SimpleNamedResults(AggregatedResults source, String name) {
//        super(source);
//        this.name=name;
//    }
    
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -4420438155869255804L;

	/** Creates a new instance of NamedResults */
    SimpleNamedResults(String name, WaiverSet waiverSet) {
    	super(waiverSet);
        this.name=name;
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
        } else {
            return 2;
        }
    }            
}
