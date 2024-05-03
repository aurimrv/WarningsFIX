/*
 * Hammurapi
 * Automated Java code review system.
 * Copyright (C) 2004  Johannes Bellert
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
 * URL: http://www.pavelvlasov.com/pv/content/menu.show?id=products.jtaste
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Apr 12, 2004
 *
 */
package org.hammurapi.inspectors.metrics.callertrace;


/**
 * @author Johannes Bellert
 */
public interface MethodWrapper {


	public boolean equals(Object obj );
	public String toSearchKey();

	public boolean isTraced();
	public void setTracedTrue();
	
	public String getMethodKey(  );
	// public String getName();
	public String printMethodName();
	public String getSignature();
	
	public String toString();
	/**
	 * @return Returns the line.
	 */
	public int getLine();

	public void setLine(int i) ;
	

	
	public String getSrcURL();
	public String getSrcLine();
}
