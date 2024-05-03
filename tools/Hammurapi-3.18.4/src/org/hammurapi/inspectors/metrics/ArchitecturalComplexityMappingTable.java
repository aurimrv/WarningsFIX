/*
 * Created on Jul 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPathAPI;
import org.hammurapi.HammurapiException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * @author MUCBJ0
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArchitecturalComplexityMappingTable extends Hashtable {

	
	public ArchitecturalComplexityMappingTable(Element holder) throws HammurapiException {
		super();
		try {
			CachedXPathAPI cxpa=new CachedXPathAPI();
			NodeIterator it=XPathAPI.selectNodeIterator(holder, "ComplexityMapping");
		
			Node teNode;
			while ((teNode=it.nextNode())!=null) {
				if (teNode instanceof Element) {
					ArchitecturalComplexityMapping acm = new ArchitecturalComplexityMapping( (Element)teNode );
							this.put( acm.getName(), acm  );
					}				
			}
		}catch (Exception e){
			throw new HammurapiException(e);
		}
	}
	public Element toDom(Document document){

		Element ret=document.createElement("ComplexityMappingTable");
		ret.setAttribute("size", String.valueOf(this.size()));
		Enumeration enum = this.keys();
		while (enum.hasMoreElements()) {
			String key = (String) enum.nextElement();
			Element cmcat=document.createElement("ComplexityMapping");
			ArchitecturalComplexityMapping cat = (ArchitecturalComplexityMapping)get(key);
			cmcat.setAttribute("name", cat.getName() );
			cmcat.setAttribute("rate", String.valueOf(cat.getRate())  );
			ret.appendChild(cmcat);
		}
		return ret;

	}


	
}
