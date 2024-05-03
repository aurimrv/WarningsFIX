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
 * URL: http://www.pavelvlasov.com/pv/content/menu.show?id=products.jtaste
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Apr 9, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

/**
 * @author Johannes Bellert
 *
 */
public interface ArchitecturalLayerConstants {

	// -- layers
	public static final String PRESENTATION_LAYER = "Presentation Layer";
	public static final String MVC_LAYER = "Model View Controller";
	public static final String CONTROLLER_LAYER = "Controller";
	public static final String BUSINESS_LAYER = "Business Layer";
	public static final String DATATRANSFER_LAYER = "Data Transfer Objects";
	public static final String INTEGRATION_LAYER = "Integration Layer";
	public static final String HELPER_LAYER = "Helpers";
	public static final String TEST_LAYER = "Tests";

	// -- categories
	public static final String FILE_PROPERTY = "File Property";
	public static final String TIB_INCONCERT = "TIB InConcert";
	public static final String TIB_INCONCERT_WEB = "TIB InConcert Web XML";
	public static final String TIB_CORE = "TIB Core";
	public static final String FILENET_ERC_PROXY = "FileNET ERC Proxy";
	public static final String ACTIVATION_FRAMEWORK = "Activation Framework";
	public static final String MAIL = "Mail";
	public static final String STRUTS_PLUGIN = "Struts PlugIn";
	public static final String APACHE_COMMONS_VALIDATOR = "Apache Commons Validator";
	public static final String QUARTZ = "Quartz";
	public static final String EJB_ENTITYBEAN = "EJB EntityBean";
	public static final String EJB_SESSIONBEAN = "EJB SessionBean";
	public static final String EJB = "EJB";
	public static final String SSL = "SSL";
	public static final String SECURITY_CERTIFICATE = "Security Certificate";
	public static final String HTTPSERVLET = "HTTP Servlet";
	public static final String JTA = "JTA";
	public static final String GETTERSETTER = "GetterSetter";
	public static final String IO_FILE = "IO File";
	public static final String XML_W3C_DOM = "XML W3C DOM";
	public static final String XML_PARSER = "XML Parser";
	public static final String XML_SAX = "XML SAX";
	public static final String JXL = "JXL Java Excel API";
	public static final String SQL_DEPRECIATED = "SQL Depreciated";
	public static final String JSP = "JSP";
	public static final String SERVLET = "Servlet";
	public static final String JUNIT_TEST = "JUnit Test";
	public static final String LOGGER_LOG4J = "Logger Log4j";
	public static final String STRUTS_FORM = "Struts Form";
	public static final String STRUTS_MVC = "Struts MVC";
	public static final String STRUTS_MENU = "Struts Menu";
	public static final String STRUTS_UPLOAD = "Struts Upload";
	public static final String VELOCITY = "Velocity";
	
	public static final String SQL_COMMON = "SQL Common";
	public static final String SQL_PREPARED = "SQL Prepared";
	public static final String SQL_DATASOURCE= "SQL Datasource";
	public static final String SQL_LOB = "SQL LOB";
	public static final String JAKARTA_DBUTIL= "Jakarta Common DB Utils";
	public static final String JAKARTA_BEANUTIL= "Jakarta Common Bean Utils";
	public static final String JAKARTA_DIGESTER= "Jakarta Common Digester";
	
	public static final String PL_SQL = "PL/SQL";
	public static final String VALUE_OBJECT = "Value Object";
	public static final String EXCEPTION = "Exception";
	public static final String LOGGING_DEPRECIATED = "Logging_Depreciated";
	public static final String LOGGING = "Logging"; 
	public static final String NETWORK = "Network";
	public static final String JMS = "JMS";
	public static final String RMI ="RMI";
	public static final String JNDI ="JNDI";
	public static final String HIBERNATE ="HIBERNATE";
	public static final String CASTOR ="CASTOR";
	public static final String SPRING_TX ="Spring TX";
	public static final String HTTPUNIT ="HttpUnit";
	public static final String PDF_GEN_ITEXT ="PDF Generator ITEXT" ;
	public static final String JCE ="JCE Crypto" ;
	

	// -- Tech Stack Rating
	public static final String EVALUATION = "Evaluation";
	public static final String DEPRECIATED = "DEPRECIATED";
	public static final String RESTRICTEDUSE = "Restricted Use";
	public static final String OK = "OK";

	

}
