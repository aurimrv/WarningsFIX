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
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.TypeSpecification;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author mucbj0
*
 *	JDepend
 *
 *	Afferent Couplings (Ca)
 *		The number of other packages that depend upon classes within the package
 *		is an indicator of the package's responsibility.
 *
 *	Efferent Couplings (Ce)
 *		The number of other packages that the classes in the package depend upon
 *		is an indicator of the package's independence.
 *
 * */
public class CouplingMetricOfClass  extends CouplingMetric{
	public SourceMarker srcMrk = null;
	public String name = "";
	public Set listAfferentTypes = new HashSet();
	public Set listEfferentTypes = new HashSet();

	public CouplingMetricOfClass( TypeDefinition c){
				super();
				name = c.getFcn();
				
				srcMrk = (SourceMarker)c;
			}

	public CouplingMetricOfClass( TypeBody c){
				super();
				name = c.getFcn();
				
				srcMrk = (SourceMarker)c;
			}
	public CouplingMetricOfClass( TypeSpecification c)throws JselException{
				super();
				name = c.getName();
				srcMrk = (SourceMarker)c;
			}	
	
	public String toString(){
		return this.name + " CaV: " + afferentVariableCounter + " CeV: " + efferentVariableCounter;
	}
/*	
	public StringBuffer toXml() {
		StringBuffer sb = new StringBuffer();

		sb.append("<CouplingMetricOfClass ");
				sb.append(" type=\"");
				sb.append(type.getClassName());
				sb.append("\" signature=\"");
				sb.append(type.getSignature());
				sb.append("\" isAbstract=\"");
				sb.append(type.isAbstract());
				sb.append("\" isInterface=\"");
				sb.append(type.isInterface());

				sb.append("\">");
		sb.append( super.toXmlCore() );

		sb.append("<ListAfferentTypes>");
		for (Iterator it = this.listAfferentTypes.iterator(); it.hasNext();){
			Type t = (Type)it.next();
			sb.append( t.toXmlSlim() );
		}
		sb.append("</ListAfferentTypes>");
		sb.append("<ListEfferentTypes>");
		for (Iterator it = this.listEfferentTypes.iterator(); it.hasNext();){
				Type t = (Type)it.next();
				sb.append( t.toXmlSlim() );
			}
		sb.append("</ListEfferentTypes>");

		sb.append("</CouplingMetricOfClass>");
		return sb;
	}
*/	
	
	public Element toDom(Document document){

		Element ret=document.createElement("CouplingMetricOfClass");
		ret.setAttribute("type", name );
		ret.setAttribute("sourceMarker", srcMrk.getSourceURL() );
		ret.setAttribute("sourceMarkerLine", String.valueOf( srcMrk.getLine() ) );
		ret.setAttribute("sourceMarkerColumn", String.valueOf( srcMrk.getColumn()) );
//		ret.setAttribute("signature", type.getSignature() );
//		ret.setAttribute("isAbstract", type.isAbstract() );
//		ret.setAttribute("isInterface", type.isInterface() );
		 super.toDom(document, ret);
		

		Element lat=document.createElement("ListAfferentTypes");
		ret.appendChild( lat );

		return ret;
	}

}
