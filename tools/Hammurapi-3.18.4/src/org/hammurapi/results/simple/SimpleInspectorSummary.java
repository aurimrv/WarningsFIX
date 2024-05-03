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
import org.hammurapi.Inspector;
import org.hammurapi.InspectorDescriptor;
import org.hammurapi.results.InspectorSummary;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.review.SimpleSourceMarker;
import com.pavelvlasov.review.SourceMarker;


public class SimpleInspectorSummary implements Comparable, InspectorSummary, Serializable {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 823706029595309352L;
	private List locations=new LinkedList();
	private String description;
	private String name;
	private Number severity;
	private String configInfo;
	
	SimpleInspectorSummary(InspectorDescriptor descriptor) throws HammurapiException {
		this.description=descriptor.getDescription();
		this.name=descriptor.getName();
		this.severity=descriptor.getSeverity();
		try {
			Inspector inspector = descriptor.getInspector();
            configInfo = inspector==null ? null : inspector.getConfigInfo();
		} catch (ConfigurationException e) {
			throw new HammurapiException("Unable to obtain inspector config info");
		}
	}
	
	SimpleInspectorSummary(InspectorSummary anotherEntry) {
		this.description=anotherEntry.getDescription();
		this.name=anotherEntry.getName();
		this.severity=anotherEntry.getSeverity();
		this.configInfo=anotherEntry.getConfigInfo();
		this.locations.addAll(anotherEntry.getLocations());
	}
	
	public String getDescription() {
		return description;
	}
	
	public List getLocations() {
		return locations;
	}
	
	public int getLocationsCount() {
		return locations.size();
	}
	
	public void addLocation(SourceMarker location) {
		SimpleSourceMarker ssm=new SimpleSourceMarker(location);
		
		// Bad design, fix in the future
		if (location instanceof LanguageElement) {
			CompilationUnit cu=((LanguageElement) location).getCompilationUnit();
			ssm.setSourceURL(cu.getPackage().getName().replace('.', '/')+'/'+cu.getName());
		}
		locations.add(ssm);
	}
	
	public void addLocations(Collection locations) {
		this.locations.addAll(locations);
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the severity.
	 */
	public Number getSeverity() {
		return severity;
	}

	public int compareTo(Object o) {
		return name.compareTo(((InspectorSummary) o).getName());
	}

	public String getConfigInfo() {
		return configInfo;
	}

	public String getVersion() {
		return null;
	}

	public int getBaseLineLocationsCount() {
		return 0;
	}

}
