/*
 * Created on Jul 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.hammurapi.HammurapiException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

/**
 * @author MUCBJ0
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DomArchitecturalMappingSource {
	  private Element holder;
	    
	    public DomArchitecturalMappingSource(InputStream in) throws HammurapiException {
	        load(in);
	    }
	    
	    public DomArchitecturalMappingSource(File f) throws HammurapiException {
	        try {
	            load(new FileInputStream(f));
	        } catch (FileNotFoundException e) {
	            throw new HammurapiException("File not found: "+f.getAbsolutePath(), e);
	        }
	    }
	    
	    private void load(InputStream in) throws HammurapiException {
	        try {
	            Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
	            holder=document.getDocumentElement();
	        } catch (ParserConfigurationException e) {
	            throw new HammurapiException(e.toString(), e);
	        } catch (SAXException e) {
	            throw new HammurapiException(e.toString(), e);
	        } catch (IOException e) {
	            throw new HammurapiException(e.toString(), e);
	        }        
	    }
	    
	    public ArchitecturalLayerMappingTable loadLayerMappings() throws HammurapiException {
	    	ArchitecturalLayerMappingTable architecturalLayerMappingSet  = new ArchitecturalLayerMappingTable();
	        
	        try {
	            NodeIterator inspectorsIterator=XPathAPI.selectNodeIterator(holder, "ArchitecturalLayerMapping");
	            Element inspectorElement;
	            while ((inspectorElement=(Element) inspectorsIterator.nextNode())!=null) {
	            	ArchitecturalLayerMapping te = new ArchitecturalLayerMapping(inspectorElement);
	            	architecturalLayerMappingSet.put( te.getName(), te);
	            }
	    
	        } catch (TransformerException e) {
	            new HammurapiException(e);
	      
			}
	        return   architecturalLayerMappingSet;
		}
	    public ArchitecturalComplexityMappingTable loadComplexityMapping() {

		ArchitecturalComplexityMappingTable aComplexityMappingTable = null;
		try {
			NodeIterator it = XPathAPI.selectNodeIterator(holder,
					"ComplexityMappingTable");
			Node teNode;
			while ((teNode = it.nextNode()) != null) {
				if (teNode instanceof Element) {
					aComplexityMappingTable = new ArchitecturalComplexityMappingTable(
							(Element) teNode);
				}
			}

		} catch (Exception e) {
			new HammurapiException(e);
		}
		return aComplexityMappingTable;
	}    

}
