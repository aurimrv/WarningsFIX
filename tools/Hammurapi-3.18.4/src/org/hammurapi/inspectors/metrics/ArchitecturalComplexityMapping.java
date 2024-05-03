/*
 * Created on Jul 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import org.hammurapi.HammurapiException;
import org.w3c.dom.Element;

/**
 * @author MUCBJ0
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArchitecturalComplexityMapping {
	String name = "not defined";
	int rate = 1;
	

	public ArchitecturalComplexityMapping(Element holder) throws HammurapiException {
		super();
		try {
			
			
			name = holder.getAttribute("name") ;
			rate = Integer.valueOf(holder.getAttribute("rate")).intValue() ;
			
		}catch (Exception e){
			throw new HammurapiException(e);
		}
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the rate.
	 */
	public int getRate() {
		return rate;
	}
	/**
	 * @param rate The rate to set.
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}
}
