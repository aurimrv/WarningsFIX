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
package org.hammurapi.results;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;

import com.pavelvlasov.ant.Param;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class AnnotationConfig {
	private String name;

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Annotation name.
	 * @param name The name to set.
	 * @ant.required
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	private Map parameters=new HashMap();
	
	/**
	 * Configuration parameter. 
	 * @ant.non-required
	 * @param parameter
	 * @throws BuildException
	 */
	public void addConfiguredParameter(Param parameter) throws BuildException {
		if (parameter.getName()==null) {
			throw new BuildException("Unnamed parameter");
		}
		parameters.put(parameter.getName(), parameter);
	}
	
	public Object getParameter(String name) throws BuildException {
		Param parameter=(Param) parameters.get(name);
		if (parameter==null) {
			return null;
		}
		
		return parameter.getObject(null);
	}
}
