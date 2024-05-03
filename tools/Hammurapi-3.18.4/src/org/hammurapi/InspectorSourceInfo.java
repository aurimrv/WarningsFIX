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

import org.w3c.dom.Element;

import com.pavelvlasov.xml.dom.DomSerializable;

/**
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.1 $
 */
public class InspectorSourceInfo implements DomSerializable {
	private String name;
	private String location;
	private String revision;
	
	/**
	 * @param name
	 * @param location
	 * @param revision
	 */
	public InspectorSourceInfo(String name, String location, String revision) {
		super();
		this.name = name;
		this.location = location;
		this.revision = revision;
	}
	
	public String getLocation() {
		return location;
	}
	public String getName() {
		return name;
	}
	public String getRevision() {
		return revision;
	}

	public void toDom(Element holder) {
		holder.setAttribute("name", name);
		holder.setAttribute("location", location);
		holder.setAttribute("revision", revision);
	}
}
