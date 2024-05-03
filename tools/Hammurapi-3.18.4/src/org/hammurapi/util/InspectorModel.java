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
package org.hammurapi.util;

import java.util.StringTokenizer;

import com.pavelvlasov.util.Visitable;
import com.pavelvlasov.util.Visitor;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class InspectorModel implements Visitable {

	public InspectorModel(String line) {
		StringTokenizer st=new StringTokenizer(line, "\t");
		name=st.nextToken();
		className=st.nextToken();
		type=st.nextToken();
		interfaceName=st.nextToken();
		severity=st.nextToken();
		parameterizable="+".equals(st.nextToken());
		description=st.nextToken();		
	}

	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	private String name;
	private String className;
	private String type;
	private String interfaceName;
	private String severity;
	private boolean parameterizable;
	private String description;
	
	public String getClassName() {
		return className;
	}

	public String getDescription() {
		return description;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getName() {
		return name;
	}

	public boolean isParameterizable() {
		return parameterizable;
	}

	public String getSeverity() {
		return severity;
	}

	public String getType() {
		return type;
	}

}
