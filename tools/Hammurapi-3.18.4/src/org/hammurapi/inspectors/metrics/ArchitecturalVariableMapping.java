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
 *  * Created on Apr 26, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Hashtable;
import java.util.Vector;

/**
 * @author Johannes Bellert
 *
 */
public class ArchitecturalVariableMapping implements ArchitecturalLayerConstants {
	private Hashtable mappings = new Hashtable();
	
	{
		String[] str1 ={"org.apache.commons.logging.Log", "org.apache.log4j.Level",
				"org.apache.commons.logging.LogFactory" ,	"org.apache.log4j.Logger",
				"org.apache.log4j.*"};
		put(LOGGING, str1 );

		String[] str2 = {"org.apache.log4j.Category"};
		put(LOGGING_DEPRECIATED, str2 );		
		
		String[] str3 = {"java.net.URL", "java.net.*" };
		put(NETWORK, str3 );
		
		String[] str4 =  {"javax.naming.*" };
		put(JNDI, str4 );
		
		String[] str5 = {"javax.rmi.*" };
		put(RMI, str5 );
		
		String[] str6 = {"javax.jms.*" };
		put(JMS, str6 );
		
		String[] str7 = {"org.apache.commons.dbutils.*","org.apache.commons.dbutils.handlers.*"};
		put(JAKARTA_DBUTIL, str7 );
						   
		String[] str71 = {"org.apache.commons.beanutils.*"};
		put(JAKARTA_BEANUTIL, str71 );
		
		String[] str72 = {"org.apache.commons.digester.*"};
		put(JAKARTA_DIGESTER, str72 );
		
		String[] str8 = {"com.gecapital.erc.FileNet.FileNetProxy"};
		put(FILENET_ERC_PROXY, str8 );
		
		String[] str9 = {"com.tibco.tibrv.*"};
		put(TIB_CORE, str9 );
		
		String[] str10 = {"com.tibco.inconcert.*", "com.inconcert.icjava.*" };
		put(TIB_INCONCERT, str10 );
		
		String[] str10_1 = {"com.inconcert.xml.sax.IcWebStateBean", 
				"com.inconcert.xml.sax.IcWebStateBean",
				"com.inconcert.xml.sax.*"};
		put(TIB_INCONCERT_WEB, str10_1 );
		
		
		
		String[] str11 = {"org.quartz.CronTrigger", "org.quartz.Trigger", "org.quartz.Scheduler",
				"org.quartz.TriggerListener", "org.quartz.JobExecutionContext"};
		put(QUARTZ, str11 );
		
		String[] str12 = {"javax.mail.internet.InternetAddress", "javax.mail.internet.MimeBodyPart",
				"javax.mail.internet.MimeMessage", "javax.mail.internet.MimeMultipart",
				"javax.mail.Message", "javax.mail.Multipart",  "javax.mail.Session", "javax.mail.Transport",
				"javax.mail.internet.*", "javax.mail.MessagingException", "javax.mail.SendFailedException",
				"javax.mail.Authenticator",	"javax.mail.*"};
		put(MAIL, str12 );

		
		String[] str13 = {"javax.activation.DataHandler", 
				"javax.activation.FileDataSource",
				"javax.activation.DataSource",
				"javax.activation.*"};
		put(ACTIVATION_FRAMEWORK, str13 );

		String[] str14 = {"javax.transaction.UserTransaction", "javax.transaction.*"};
		put(JTA, str14 );

		String[] str15 = {"java.sql.Statement", "java.sql.Connection", "java.sql.ResultSet" };
		put(SQL_COMMON, str15 );

		String[] str16 = {"java.sql.ResultSetMetaData"};
		put(SQL_DEPRECIATED, str16 );

		String[] str17 = {"javax.sql.DataSource"};;
		put(SQL_DATASOURCE, str17 );

		String[] str19 = {"java.sql.PreparedStatement"};
		put(SQL_PREPARED, str19 );

		//!! shall be differentiated: Java vs. Oracle
		String[] str20 ={"java.sql.Blob", "java.sql.Clob", "oracle.sql.BLOB", "oracle.sql.CLOB"};
		put(SQL_LOB, str20 );
		
		String[] str21 ={"oracle.jdbc.driver.OracleCallableStatement", "oracle.sql.ARRAY",
				"oracle.sql.ArrayDescriptor", "oracle.sql.STRUCT", "oracle.sql.StructDescriptor"};
		put(PL_SQL, str21 );

		String[] str22 ={"org.w3c.dom.Document", 
					"org.w3c.dom.Element", "org.w3c.dom.Node", "org.w3c.dom.NodeList",
					"org.w3c.dom.*"};
		put(XML_W3C_DOM, str22 );

		String[] str222 ={"org.xml.sax.*", 
				"org.w3c.dom.Element", "org.w3c.dom.Node", "org.w3c.dom.NodeList"};
		put(XML_SAX, str222 );

		String[] str223 ={"javax.xml.parsers.*"};
		put(XML_PARSER, str223 );
		
		String[] str23 ={"java.io.File","java.io.FileWriter", "java.io.FileReader",
				"java.io.FileInputStream", "java.io.FileInputStream", "java.io.OutputStream", "java.io.InputStream"};
			put(IO_FILE, str23 );

		String[] str24 = {"java.util.PropertyResourceBundle", "java.util.ResourceBundle", "java.util.Property"};
			put(FILE_PROPERTY, str24 );

		String[] str25 ={"net.sf.navigator.menu.MenuComponent", "net.sf.navigator.displayer.MenuDisplayerMapping",
				"net.sf.navigator.displayer.MessageResourcesMenuDisplayer", "net.sf.navigator.*" };
			put(STRUTS_MENU, str25 );
		
		String[] str26 ={"org.apache.struts.action.ActionMapping", "org.apache.struts.action.ActionError",
				"org.apache.struts.action.ActionErrors", "org.apache.struts.util.MessageResources",
				"org.apache.struts.action.ActionServlet",
				"org.apache.struts.action.ActionMessage",
				"org.apache.struts.action.ActionMessages",
				"org.apache.struts.action.ActionForward",
				"org.apache.struts.action.*",
				"org.apache.struts.config.*" };
		put(STRUTS_MVC, str26 );

		String[] str26_1 ={"org.apache.struts.action.ActionForm" , "org.apache.struts.action.DynaActionForm"};
		put(STRUTS_FORM, str26_1 );
		
		String[] str26_2 ={"org.apache.struts.upload.FormFile" };
		put(STRUTS_UPLOAD, str26_2 );
		
		String[] str26_3 ={"org.apache.velocity.*" };
		put(VELOCITY, str26_3 );
		
		
		String[] str27  ={"javax.servlet.http.HttpServletRequest", "javax.servlet.http.HttpServletResponse",
		 "javax.servlet.http.HttpSession", "javax.servlet.http.*", "javax.servlet.jsp.JspFactory",
		 "javax.servlet.jsp.JspWriter", "javax.servlet.jsp.PageContext" };
		put(JSP, str27 );

		String[] str271  ={
				 "javax.servlet.ServletContext",
				  "javax.servlet.ServletConfig",
				 "javax.servlet.ServletOutputStream",
				 "javax.servlet.*"};
		put(SERVLET, str271 );

		String[] str28  ={"net.sf.hibernate.Session", "net.sf.hibernate.SessionFactory",
		 "net.sf.hibernate.cfg.Configuration" , "net.sf.hibernate.Transaction",
		"net.sf.hibernate.type.Type[]", "net.sf.hibernate.HibernateException", "net.sf.hibernate.SessionFactory"};
		put( HIBERNATE, str28 );
		
		String[] str29  ={"org.exolab.castor.xml.Unmarshaller", "org.exolab.castor.mapping.Mapping",
		"org.exolab.castor.xml.Marshaller" };
		put( CASTOR, str29 );
		
		String[] str30  ={"junit.framework.TestSuite"};
		put( JUNIT_TEST, str30 );
		
		String[] str31  ={"org.springframework.transaction.PlatformTransactionManager",
		"org.springframework.transaction.support.TransactionTemplate",
		"org.springframework.transaction.TransactionStatus", 
		"org.springframework.web.context.WebApplicationContext",
		"org.springframework.context.support.ClassPathXmlApplicationContext",
		};
		put( SPRING_TX, str31 );			
		
		String[] str32  ={"com.meterware.httpunit.WebConversation", "com.meterware.httpunit.WebLink[]",
		"com.meterware.httpunit.WebForm" , "com.meterware.httpunit.*"
		};
		put( HTTPUNIT, str32 );			
		
		String[] str33  ={"com.lowagie.*"	};
				put( PDF_GEN_ITEXT, str33 );	

		String[] str34  ={"javax.crypto.*"	};
		put( JCE, str34 );
		
		String[] str35  ={"jxl.write.*", "jxl.format.*", "jxl.*"	};
		put( JXL, str35 );
		
		String[] str36  ={"org.apache.commons.validator.*"	};
		put( APACHE_COMMONS_VALIDATOR, str36 );
		
		String[] str37  ={"javax.ejb.CreateException",
				"javax.ejb.EJBHome", "javax.ejb.EJBObject", 
				"javax.ejb.Handle", "javax.ejb.*"	};
		put( EJB, str37 );
		
		String[] str38  ={ "javax.net.ssl.SSLSocket", "javax.net.ssl.*", "com.sun.net.ssl.*" };
		put( SSL, str38 );
		
		String[] str39  ={ "javax.security.cert.X509Certificate", "javax.security.cert.*", "com.ibm.xml.dsig.*" };
		put( SECURITY_CERTIFICATE, str39 );
	}
	
	public void put ( String key, String [] strArray){
	
		Vector vc = new Vector();
		for( int i=0; i< strArray.length; i++){
			vc.add( (String)strArray[i] );
		}
		this.mappings.put( key, vc);
	}
	/**
	 * @return Returns the mappings.
	 */
	public Hashtable getMappings() {
		return mappings;
	}
	/**
	 * @param mappings The mappings to set.
	 */
	public void setMappings(Hashtable mappings) {
		this.mappings = mappings;
	}
}
