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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.pavelvlasov.jsel.TypeDefinition;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CouplingMetricOfPackage extends CouplingMetric {

	public Set listAfferentPackage = new HashSet();
	public Set listEfferentPackage = new HashSet();

	protected String packageName = "";

	private Vector listOfCouplingMetricOfClass = new Vector();

	public CouplingMetricOfPackage( String _packageName){
				super();
				this.packageName = _packageName;
			}

	public void aggregate(CouplingMetric cCmc) {

		this.afferentMethodCounterProjectInternal
			+= cCmc.afferentMethodCounterProjectInternal;
		this.efferentMethodCounterProjectInternal
			+= cCmc.efferentMethodCounterProjectInternal;

		this.afferentMethodCounter += cCmc.afferentMethodCounter;
		this.efferentMethodCounter += cCmc.efferentMethodCounter;
		this.efferentMethodUnresolvedCounter
			+= cCmc.efferentMethodUnresolvedCounter;
		this.reflectiveMethodCounter += cCmc.reflectiveMethodCounter;
		this.afferentVariableCounter += cCmc.afferentVariableCounter;
		this.efferentVariableCounter += cCmc.efferentVariableCounter;
		this.reflectiveVariableCounter += cCmc.reflectiveVariableCounter;

		// this call has to be AFTER all other var aggregation
		this.instability = this.getInstability();
	}

	public String getRootPackageName( String fcn){
		//!! cut class name out of package
		return fcn;
	}
	public void aggregate(CouplingMetricOfClass cCmc) {

		this.aggregate( (CouplingMetric)cCmc );

		for (Iterator it = cCmc.listAfferentTypes.iterator(); it.hasNext();){
			TypeDefinition t = (TypeDefinition)it.next();
			if( t.getFcn() != null  ){
				listAfferentPackage.add(this.getRootPackageName( t.getFcn()));
			}
			}

		for (Iterator it = cCmc.listEfferentTypes.iterator(); it.hasNext();){
			TypeDefinition t = (TypeDefinition)it.next();
			if( t.getFcn() != null  ){
			listEfferentPackage.add( this.getRootPackageName( t.getFcn()) );
			}
			}

		listOfCouplingMetricOfClass.add(cCmc);
	}
/*
	public StringBuffer toXml() {
		StringBuffer sb = new StringBuffer();

		sb.append("<CouplingMetricOfPackage ");
				sb.append(" package=\"");
				sb.append(this.packageName);
				sb.append("\" numberOfClasses=\"");
				sb.append(this.listOfCouplingMetricOfClass.size() );
				sb.append("\">");
		sb.append( super.toXmlCore() );

		sb.append("<ListAfferentPackages>");
		for (Iterator it = listAfferentPackage.iterator(); it.hasNext();){
			String p = (String)it.next();
			if( p != null){
				sb.append("<Package name=\"" );
			sb.append(p);
			sb.append("\"/>" );
			}
		}
		sb.append("</ListAfferentPackages>");

		sb.append("<ListEfferentPackages>");
		for (Iterator it = listEfferentPackage.iterator(); it.hasNext();){
			String p = (String)it.next();
					if( p != null){
						sb.append("<Package name=\"" );
					sb.append(p);
					sb.append("\"/>" );
					}		}
		sb.append("</ListEfferentPackages>");

		sb.append("<ListOfCouplingMetricOfClass>");
		for( int i=0; i< this.listOfCouplingMetricOfClass.size(); i++){
			CouplingMetricOfClass cmc = (CouplingMetricOfClass)listOfCouplingMetricOfClass.elementAt(i);

			sb.append(cmc.toXml());
		}
		sb.append("</ListOfCouplingMetricOfClass>");
		sb.append("</CouplingMetricOfPackage>");
		return sb;
	}
*/	
}

