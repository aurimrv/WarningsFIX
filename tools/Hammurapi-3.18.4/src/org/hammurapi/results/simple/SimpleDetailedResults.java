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

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hammurapi.HammurapiException;
import org.hammurapi.Violation;
import org.hammurapi.Waiver;
import org.hammurapi.WaiverEntry;
import org.hammurapi.WaiverSet;
import org.hammurapi.results.DetailedResults;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class SimpleDetailedResults extends SimpleNamedResults implements DetailedResults {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4368844541348791240L;
	private List violations=new LinkedList();
    private List waivedViolations=new LinkedList();
    private List incompleteMessages=new LinkedList();
    
    SimpleDetailedResults(String name, WaiverSet waiverSet) {    	
        super(name, waiverSet);
    }
    
    public Waiver addViolation(final Violation violation) throws HammurapiException {
    	final Waiver waiver=super.addViolation(violation);
    	if (waiver==null) {
    		violations.add(violation);
    	} else {
    		waivedViolations.add(new WaiverEntry() {
				/**
				 * Comment for <code>serialVersionUID</code>
				 */
				private static final long serialVersionUID = -6057980823743605352L;

				public Waiver getWaiver() {
					return waiver;
				}

				public Violation getViolation() {
					return violation;
				}
    		});
    	}
    	return waiver;
    }
    
    public Collection getViolations() {
        return violations;

    }                
    
    public int getViolationsNumber() {
        return violations.size()+super.getViolationsNumber();
    }

	public Collection getWaivedViolations() {
		return waivedViolations;
	}
    
}
