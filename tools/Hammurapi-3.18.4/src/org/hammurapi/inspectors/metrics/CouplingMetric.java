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
 *  * Created on Apr 11, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mucbj0
 *
 file:///D:/anwend/java/JDepend-2.6/ltcaemodel.xml.html

	Instability
		The ratio of efferent coupling (Ce) to total coupling (Ce / (Ce + Ca)). This metric is an indicator of the package's resilience to change.

		The range for this metric is 0 to 1, with I=0 indicating a completely stable package and I=1 indicating a completely instable package.


 **/
public class CouplingMetric {

	public float instability = 0;

		public int afferentMethodCounter = 0;
		public int efferentMethodCounter = 0;

	public int afferentMethodCounterProjectInternal = 0;
	public int efferentMethodCounterProjectInternal = 0;

		public int efferentMethodUnresolvedCounter = 0;
		public int reflectiveMethodCounter = 0;

		public int afferentVariableCounter = 0;
		public int efferentVariableCounter = 0;

		public int reflectiveVariableCounter = 0;

	public CouplingMetric(){
				super();
			}

		public StringBuffer toXmlCore() {
			StringBuffer sb = new StringBuffer();

			sb.append("<Instability>");
			sb.append(this.getInstability());
			sb.append("</Instability>");

			sb.append("<ReflectiveVariableCounter>");
			sb.append(reflectiveVariableCounter);
			sb.append("</ReflectiveVariableCounter>");

			sb.append("<AfferentMethodCounterProjectInternal>");
			sb.append(afferentMethodCounterProjectInternal);
			sb.append("</AfferentMethodCounterProjectInternal>");
			sb.append("<EfferentMethodCounterProjectInternal>");
			sb.append(efferentMethodCounterProjectInternal);
			sb.append("</EfferentMethodCounterProjectInternal>");

			sb.append("<AfferentMethodCounter>");
			sb.append(afferentMethodCounter);
			sb.append("</AfferentMethodCounter>");
			sb.append("<EfferentMethodCounter>");
			sb.append(efferentMethodCounter);
			sb.append("</EfferentMethodCounter>");


			sb.append("<AfferentVariableCounter>");
			sb.append(afferentVariableCounter);
			sb.append("</AfferentVariableCounter>");
			sb.append("<EfferentVariableCounter>");
			sb.append(efferentVariableCounter);
			sb.append("</EfferentVariableCounter>");

			sb.append("<EfferentMethodUnresolvedCounter>");
			sb.append(efferentMethodUnresolvedCounter);
			sb.append("</EfferentMethodUnresolvedCounter>");


			return sb;
		}

		
		public Element toDom(Document document, Element root){
			
			Element ret=document.createElement("AfferentMethodCounter");
			ret.setAttribute("number", String.valueOf( afferentVariableCounter ));
			root.appendChild(ret);
			
			Element ret2=document.createElement("EfferentMethodCounter");
			ret2.setAttribute("number", String.valueOf( efferentVariableCounter ));
			root.appendChild(ret2);
			
			return root;
		}

	/**
	 * @return  (Ce / (Ce + Ca))
	 */
	public float getInstability() {
		// (Ce / (Ce + Ca))
		float instability = 0;
		float denominator = (this.efferentMethodCounter + this.afferentMethodCounter );
	// 	System.out.println ("  " + denominator +" = " + this.efferentMethodCounter  +" + " + this.afferentMethodCounter );
		if( denominator != 0 )
		{	instability =		this.efferentMethodCounter /  denominator; }
	//	System.out.println (" instability " + instability );
		return instability;
	}

}
