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
 * URL: http://www.hammurapi.com
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Apr 26, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Vector;

/**
 * @author Johannes Bellert
 *
 */
public class PackageOccuranceInLayer {
	
	private String packageName = "";
	private int occurance = 1;
	private Vector layerList = new Vector();
	
	public PackageOccuranceInLayer ( String _name){
		super();
		packageName = _name;
	}

	public boolean equals( Object poil){
		
		return packageName.equals(((PackageOccuranceInLayer)poil).packageName);
	}
		
	public String toString(){
		return packageName + " - " + occurance;
	}
	/**
	 * @return Returns the occurance.
	 */
	public int getOccurance() {
		return occurance;
	}
	/**
	 * @param occurance The occurance to set.
	 */
	public void setOccurance(int occurance) {
		this.occurance = occurance;
	}
	/**
	 * @return Returns the packageName.
	 */
	public String getPackageName() {
		return packageName;
	}
	/**
	 * @param packageName The packageName to set.
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	/**
	 * @return Returns the layerList.
	 */
	public Vector getLayerList() {
		return layerList;
	}
	/**
	 * @param layerList The layerList to set.
	 */
	public void setLayerList(Vector layerList) {
		this.layerList = layerList;
	}
}
