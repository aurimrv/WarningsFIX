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
package org.hammurapi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.review.SourceMarkerComparator;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.5 $
 */
public class SimpleViolation implements Violation, Serializable {
	private static ThreadLocal inspectorMap=new ThreadLocal() {
		protected Object initialValue() {
			return new HashMap();
		}
	};
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6111796217286959070L;
	private SourceMarker source;
	private String message;

	private String inspectorName;
	
	public SimpleViolation(SourceMarker source, String message, InspectorDescriptor descriptor) {
		super();
		this.source=source;
		
		if (descriptor!=null) {
			Map iMap = (Map) inspectorMap.get();
			if (iMap!=null) {
				iMap.put(descriptor.getName(), descriptor);
			}
			inspectorName=descriptor.getName();
		}
		
		this.message=message;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return Returns the ruleName.
	 */
	public InspectorDescriptor getDescriptor() {
		return inspectorName==null ? null : (InspectorDescriptor) ((Map) inspectorMap.get()).get(inspectorName);
	}

	/**
	 * @return Returns the source.
	 */
	public SourceMarker getSource() {
		return source;
	}

	public int compareTo(Object o) {
		if (o==this) {
			return 0;
		} else if (o instanceof Violation) {
			Violation v=(Violation) o;
			int vline = v.getSource()==null ? 0 : v.getSource().getLine();
			int line = getSource()==null ? 0 : getSource().getLine();
			if (vline==line) {
				int vcolumn = v.getSource()==null ? 0 : v.getSource().getColumn();
				int column = getSource()==null ? 0 : getSource().getColumn();
				if (vcolumn==column) {
					if (message==null) {
						return v.getMessage()==null ? 0 : 1;
					}
					
					if (v.getMessage()==null) {
						return -1;
					}
					
					return message.compareTo(v.getMessage());
				}
				
				return column-vcolumn;
			}
			
			return line-vline;
		} else {
			return 1;
		}
	}
	
	public boolean equals(Object obj) {
		if (obj==this) {
			return true;
		} else if (obj instanceof Violation) {
			Violation v=(Violation) obj;
			if (SourceMarkerComparator._compare(getSource(), v.getSource())==0) {
				// Inspector descriptor is ignored in equality.
				return message==null ? v.getMessage()==null : message.equals(v.getMessage());
			}
			
			return false;
		} else {
			return super.equals(obj);
		}
	}

}
