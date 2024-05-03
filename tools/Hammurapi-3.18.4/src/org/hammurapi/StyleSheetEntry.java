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

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tools.ant.Project;

import com.pavelvlasov.ant.Param;
import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.2 $
  * @ant.element parent="output" name="stylesheet" display-name="Stylesheet nested element of output"
 * @ant.non-required
 */
public class StyleSheetEntry {

	private String name;
	private File file;
	private String url;
	private Map parameters=new HashMap();

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Stylesheet 'logical' name. Supported names are:
	 * <UL>
	* <LI>compilation-unit</LI>
	* <LI>summary</LI>
	* <LI>left-panel</LI>
	* <LI>package</LI>
	* <LI>inspector-set</LI>
	* <LI>inspector-descriptor</LI>
	* <LI>inspector-summary</LI>
	* <LI>metric-details</LI>
	 * </UL>
	 * @return
	 * @ant.required
	 */
	public void setName(String name) {
		this.name=name;
	}

	/**
	 * @return
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Stylesheet to use. Mutually exclusive with URL
	 * @param file
	 * @ant.non-required
	 */
	public void setFile(File file) {
		this.file=file;
	}

	/**
	 * URL to download stylesheet from. Mutually exclusive with File.
	 * @param url
	 * @ant.non-required
	 */
	public void setUrl(String url) {
		this.url=url;
	}

	/**
	 * @return
	 */
	public Map getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 * @ant.ignore
	 */
	public void setParameters(Map parameters) {
		this.parameters.putAll(parameters);
	}
	
	/**
	 * Parameter which will be passed to transformer.
	 * @param param
	 * @ant.non-required
	 */
	public void addConfiguredParameter(Param param) {
		parameters.put(param.getName(), param);
	}
	
    void setParameters(Project project, Parameterizable p) throws ConfigurationException {
        Iterator pit=parameters.values().iterator();
        while (pit.hasNext()) {
            Param param=(Param) pit.next();
            
            param.setProject(project);
            param.execute();
            
            Object value=param.getObject(null);
            if (value!=null) {
                p.setParameter(param.getName(), value);
            }
        }
    }	
}
