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
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.xpath.CachedXPathAPI;
import org.hammurapi.HammurapiException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.pavelvlasov.xml.dom.AbstractDomObject;
/**
 * @author Johannes Bellert
 *
  */
public class ArchitecturalLayerMapping
		extends AbstractDomObject 
		implements ArchitecturalLayerConstants {
	
	private String name = "<undefined>"; 
	public ArchitecturalLayerMapping() {
		super();
	}

	public ArchitecturalLayerMapping(Element holder) throws HammurapiException {
		super();
		try {
			CachedXPathAPI cxpa=new CachedXPathAPI();
			
			this.name = holder.getAttribute("name") ;
			NodeIterator it=cxpa.selectNodeIterator(holder, "TechStackEntity");
			Node teNode;
			while ((teNode=it.nextNode())!=null) {
				if (teNode instanceof Element) {
					
						TechStackEntity tse = new TechStackEntity( (Element) teNode)  ;
						this.mappings.put( tse.getName(), this.name );
						//!! check for configuration bugs: if the list contains this key, thrown a exception.
						this.techStackEntityList.put(tse.getName(), tse);
				}
			}
			//init();
		} catch (Exception e) {
			throw new HammurapiException(e);
		}
	}
	
	//!! job: missing Hibernate, XStream, ..
	private Hashtable mappings = new Hashtable();
	/*
	{
		mappings.put(FILE_PROPERTY, HELPER_LAYER);
		mappings.put(PDF_GEN_ITEXT, HELPER_LAYER);
		mappings.put(JCE, HELPER_LAYER);
		mappings.put(TIB_INCONCERT, INTEGRATION_LAYER);
		mappings.put(TIB_INCONCERT_WEB, PRESENTATION_LAYER );
		mappings.put(TIB_CORE, INTEGRATION_LAYER);
		mappings.put(FILENET_ERC_PROXY, INTEGRATION_LAYER);
		mappings.put(ACTIVATION_FRAMEWORK, INTEGRATION_LAYER);
		mappings.put(MAIL, INTEGRATION_LAYER);
		mappings.put(STRUTS_PLUGIN, MVC_LAYER);
		mappings.put(STRUTS_MENU, MVC_LAYER);
		mappings.put(STRUTS_FORM, MVC_LAYER);
		mappings.put(STRUTS_MVC, MVC_LAYER);
		mappings.put(STRUTS_UPLOAD, HELPER_LAYER);
		
		mappings.put(QUARTZ, CONTROLLER_LAYER);
		mappings.put(EJB_ENTITYBEAN, INTEGRATION_LAYER);
		mappings.put(EJB_SESSIONBEAN, BUSINESS_LAYER);
		mappings.put(EJB, BUSINESS_LAYER);
		mappings.put(HTTPSERVLET, MVC_LAYER);
		mappings.put(JTA, INTEGRATION_LAYER);
		mappings.put(VELOCITY, MVC_LAYER);
		
		mappings.put(GETTERSETTER, BUSINESS_LAYER);
		mappings.put(IO_FILE, INTEGRATION_LAYER);
		mappings.put(XML_W3C_DOM, HELPER_LAYER);
		mappings.put(XML_SAX, HELPER_LAYER);
		mappings.put(XML_PARSER, HELPER_LAYER);
		mappings.put(APACHE_COMMONS_VALIDATOR, PRESENTATION_LAYER);
		mappings.put(SSL, PRESENTATION_LAYER);
		mappings.put(SECURITY_CERTIFICATE, HELPER_LAYER);
		
		mappings.put(JXL, INTEGRATION_LAYER);
		mappings.put(LOGGING, HELPER_LAYER);
		mappings.put(LOGGING_DEPRECIATED, HELPER_LAYER);
		mappings.put(EXCEPTION, HELPER_LAYER);
		mappings.put(SQL_DEPRECIATED, INTEGRATION_LAYER);
		mappings.put(JSP, PRESENTATION_LAYER);
		mappings.put(JUNIT_TEST, TEST_LAYER);
		mappings.put(LOGGER_LOG4J, HELPER_LAYER);
		mappings.put(SQL_COMMON, INTEGRATION_LAYER);
		mappings.put(SQL_PREPARED, INTEGRATION_LAYER);
		mappings.put(SQL_LOB, INTEGRATION_LAYER);
		mappings.put(SQL_DATASOURCE, INTEGRATION_LAYER);
		
		mappings.put(PL_SQL, INTEGRATION_LAYER);
		mappings.put(NETWORK, INTEGRATION_LAYER);
		mappings.put(JMS, INTEGRATION_LAYER);
		mappings.put(RMI, INTEGRATION_LAYER);
		mappings.put(JNDI, HELPER_LAYER);
		mappings.put(PL_SQL, INTEGRATION_LAYER);
		mappings.put(JAKARTA_DBUTIL, HELPER_LAYER);
		mappings.put(JAKARTA_BEANUTIL, BUSINESS_LAYER);
		mappings.put(JAKARTA_DIGESTER, HELPER_LAYER);
		mappings.put(VALUE_OBJECT, BUSINESS_LAYER);
		mappings.put(HIBERNATE, INTEGRATION_LAYER);
		mappings.put(SPRING_TX, INTEGRATION_LAYER);
		mappings.put(CASTOR, HELPER_LAYER);
		mappings.put(HTTPUNIT, TEST_LAYER );
	}
	
	*/
	private Hashtable techStackEntityList = new Hashtable();
	/*
	{
		techStackAppreciation.put(FILE_PROPERTY, new TechStackEntity(FILE_PROPERTY, OK ));
		techStackAppreciation.put(PDF_GEN_ITEXT, new TechStackEntity(PDF_GEN_ITEXT, OK ));
		
		techStackAppreciation.put(TIB_INCONCERT, new TechStackEntity(TIB_INCONCERT , OK));
		techStackAppreciation.put(TIB_INCONCERT_WEB, new TechStackEntity(TIB_INCONCERT_WEB, OK));
		techStackAppreciation.put(TIB_CORE, new TechStackEntity(TIB_CORE , OK));
		techStackAppreciation.put(FILENET_ERC_PROXY, new TechStackEntity( FILENET_ERC_PROXY, OK));
		techStackAppreciation.put(ACTIVATION_FRAMEWORK, new TechStackEntity(ACTIVATION_FRAMEWORK , OK));
		techStackAppreciation.put(MAIL, new TechStackEntity( MAIL, OK));
		techStackAppreciation.put(STRUTS_PLUGIN, new TechStackEntity( STRUTS_PLUGIN, OK));
		techStackAppreciation.put(STRUTS_MENU, new TechStackEntity( STRUTS_MENU, EVALUATION));
		techStackAppreciation.put(STRUTS_FORM, new TechStackEntity( STRUTS_FORM, OK));
		techStackAppreciation.put(STRUTS_MVC, new TechStackEntity( STRUTS_MVC, OK));
		techStackAppreciation.put(STRUTS_UPLOAD, new TechStackEntity( STRUTS_UPLOAD, OK));
		techStackAppreciation.put(VELOCITY, new TechStackEntity( VELOCITY, OK));
		
		techStackAppreciation.put(QUARTZ, new TechStackEntity(QUARTZ , RESTRICTEDUSE));
		techStackAppreciation.put(EJB_ENTITYBEAN, new TechStackEntity( EJB_ENTITYBEAN, OK));
		techStackAppreciation.put(EJB_SESSIONBEAN, new TechStackEntity(EJB_SESSIONBEAN , OK));
		techStackAppreciation.put(EJB, new TechStackEntity(EJB , OK));
		techStackAppreciation.put(HTTPSERVLET, new TechStackEntity( HTTPSERVLET, OK));
		techStackAppreciation.put(JTA, new TechStackEntity( JTA, OK));
		techStackAppreciation.put(GETTERSETTER, new TechStackEntity( GETTERSETTER, OK));
		techStackAppreciation.put(IO_FILE, new TechStackEntity(IO_FILE , OK));
		techStackAppreciation.put(XML_W3C_DOM, new TechStackEntity( XML_W3C_DOM, OK));
		techStackAppreciation.put(XML_SAX, new TechStackEntity( XML_SAX, OK));
		techStackAppreciation.put(XML_PARSER, new TechStackEntity( XML_PARSER, OK));
		techStackAppreciation.put(APACHE_COMMONS_VALIDATOR, new TechStackEntity( APACHE_COMMONS_VALIDATOR, OK));
		
		techStackAppreciation.put(JXL, new TechStackEntity( JXL, OK));
		techStackAppreciation.put(SQL_DEPRECIATED, new TechStackEntity( SQL_DEPRECIATED, DEPRECIATED));
		techStackAppreciation.put(JSP, new TechStackEntity( JSP, OK));
		techStackAppreciation.put(SERVLET, new TechStackEntity( SERVLET, OK));
		
		techStackAppreciation.put(JUNIT_TEST, new TechStackEntity( JUNIT_TEST, OK));
		techStackAppreciation.put(LOGGER_LOG4J, new TechStackEntity( LOGGER_LOG4J, OK));
		techStackAppreciation.put(SQL_COMMON, new TechStackEntity( SQL_COMMON, OK));
		techStackAppreciation.put(SQL_PREPARED, new TechStackEntity(SQL_PREPARED , OK));
		techStackAppreciation.put(SQL_LOB, new TechStackEntity( SQL_LOB, OK));
		techStackAppreciation.put(SQL_DATASOURCE, new TechStackEntity( SQL_DATASOURCE, OK));
		techStackAppreciation.put(PL_SQL, new TechStackEntity(PL_SQL , RESTRICTEDUSE));
		techStackAppreciation.put(VALUE_OBJECT, new TechStackEntity( VALUE_OBJECT, OK));
		techStackAppreciation.put(EXCEPTION, new TechStackEntity( EXCEPTION, OK));
		techStackAppreciation.put(JAKARTA_DBUTIL, new TechStackEntity( JAKARTA_DBUTIL, RESTRICTEDUSE));
		techStackAppreciation.put(JAKARTA_BEANUTIL, new TechStackEntity( JAKARTA_BEANUTIL, OK));
		techStackAppreciation.put(JAKARTA_DIGESTER, new TechStackEntity( JAKARTA_DIGESTER, OK));
		techStackAppreciation.put(LOGGING, new TechStackEntity( LOGGING, OK));
		techStackAppreciation.put(LOGGING_DEPRECIATED, new TechStackEntity( LOGGING_DEPRECIATED, DEPRECIATED));
		techStackAppreciation.put(NETWORK, new TechStackEntity( NETWORK, OK));
		techStackAppreciation.put(JMS, new TechStackEntity( JMS, OK));
		techStackAppreciation.put(RMI, new TechStackEntity( RMI, OK));
		techStackAppreciation.put(JNDI, new TechStackEntity( JNDI, OK));
		techStackAppreciation.put(JCE, new TechStackEntity( JCE, OK));
		
		techStackAppreciation.put(HIBERNATE, new TechStackEntity( HIBERNATE, EVALUATION));
		techStackAppreciation.put(CASTOR, new TechStackEntity( CASTOR, OK));
		techStackAppreciation.put(SPRING_TX, new TechStackEntity( SPRING_TX, EVALUATION));
		techStackAppreciation.put(HTTPUNIT, new TechStackEntity( HTTPUNIT, OK));
		techStackAppreciation.put(SSL, new TechStackEntity( SSL, OK));
		techStackAppreciation.put(SECURITY_CERTIFICATE, new TechStackEntity( SECURITY_CERTIFICATE, OK));
		}
	*/
	
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


	public Element toDom(Document document){

		Element ret=document.createElement("LayerMappings");
		ret.setAttribute("tier", String.valueOf(this.name));
		ret.setAttribute("size", String.valueOf(this.mappings.size()));
		
		Enumeration enum = this.mappings.keys();
		while (enum.hasMoreElements()) {
			String layerKey = (String) enum.nextElement();
			Element layer=document.createElement("Mapping");
			String tier = (String)mappings.get(layerKey);
			layer.setAttribute("identificator", layerKey );
			layer.setAttribute("tier", tier  );
			ret.appendChild(layer);
			
		/*	Enumeration enumTe = this.techStackEntityList.elements();
			while (enumTe.hasMoreElements()) {
				TechStackEntity te = (TechStackEntity) enumTe.nextElement();
				layer.appendChild( te.toDom(document)) ;
			}
			*/
		}
		return ret;

	}
	/**
	 * @return Returns the techStackAppreciation.
	 */
	public Hashtable getTechStackEntityList() {
		return techStackEntityList;
	}
	/**
	 * @return Returns the techStackAppreciation.
	 */
	public Collection getTechStackEntityListValues() {
		return techStackEntityList.values();
	}
	/**
	 * @param techStackAppreciation The techStackAppreciation to set.
	 */
	public void setTechStackEntityList(Hashtable techStackAppreciation) {
		this.techStackEntityList = techStackAppreciation;
	}
	
	//!! test purpose only
	public static void main(String[] args) {
		
		 System.out.println("ArchitecturalLayerMapping load");
	
	try {
		InputStream inspectorStream=ArchitecturalLayerMapping.class.getResourceAsStream("ArchitecturalLayerMapping.xml");
		
		DomArchitecturalMappingSource source=new DomArchitecturalMappingSource(inspectorStream);
		source.loadLayerMappings() ;
	} catch (HammurapiException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	public String toString() {
		return this.name + " "+ this.getMappings().size();
	}
}



