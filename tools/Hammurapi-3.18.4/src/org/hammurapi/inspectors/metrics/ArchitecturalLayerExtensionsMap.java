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
 *  * Created on Apr 19, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Enumeration;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Johannes Bellert
 *
 */
public class ArchitecturalLayerExtensionsMap extends Properties implements ArchitecturalLayerConstants {
	
	//!! job: use XML based configuration in next Gen
	/*
	{

		this.put( "org.apache.struts.action.Action", STRUTS_MVC);
		this.put( "org.apache.struts.action.ActionForm", STRUTS_FORM);
		this.put( "net.sf.navigator.displayer.MessageResourcesMenuDisplayer", STRUTS_MENU);
		this.put( "org.apache.struts.action.PlugIn", STRUTS_PLUGIN);
		this.put( "org.apache.log4j.Logger", LOGGER_LOG4J);
		this.put( "junit.framework.Test", JUNIT_TEST);
		this.put( "junit.framework.TestCase", JUNIT_TEST);
//		 this.put( "org.apache.jsp.HttpJspBase", JSP);
		this.put( "org.apache.jasper.runtime.HttpJspBase", JSP);
		this.put( "javax.servlet.http.HttpServlet", HTTPSERVLET);


		this.put( "javax.ejb.EJBObject", EJB);
		this.put( "javax.ejb.EJBHome", EJB);
		this.put( "javax.ejb.SessionBean", EJB_SESSIONBEAN);
		this.put( "javax.ejb.SessionContext", EJB_SESSIONBEAN);
		this.put( "javax.ejb.EntityBean", EJB_ENTITYBEAN);
		this.put( "javax.ejb.EntityContext", EJB_ENTITYBEAN);

		this.put( "org.quartz.StatefulJob", QUARTZ);
		this.put( "org.quartz.xml.JobSchedulingDataProcessor", QUARTZ);

		this.put( "java.lang.Exception", EXCEPTION);

		this.put( "org.apache.jasper.runtime.HttpJspBase", JSP);
//		 implements org.apache.jasper.runtime.JspSourceDependent

	}
	

*/
	public Element toDom(Document document){

		Element ret=document.createElement("ExtensionMappings");
		ret.setAttribute("size", String.valueOf(this.size()));
		Enumeration enum = this.keys();
		while (enum.hasMoreElements()) {
			String key = (String) enum.nextElement();
			Element layer=document.createElement("Mapping");
			String cat = (String)get(key);
			layer.setAttribute("identificator", key );
			layer.setAttribute("category", cat  );
			ret.appendChild(layer);
		}
		return ret;

	}

}
