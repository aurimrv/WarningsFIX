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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Operation;
import com.pavelvlasov.jsel.Package;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.review.Signed;
import com.pavelvlasov.xml.dom.AbstractDomObject;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.10 $
 */
public class DomWaiver extends AbstractDomObject implements Waiver {
	private Set signatures=new HashSet();
	boolean active=true;
	boolean hadSignatures=false;
	
	private LinkedList elements=new LinkedList();
	
	private static class ElementEntry {
		boolean exclude;
		String name;
		String operationSignature;
		Pattern fileNamePattern;
	}
	
	/**
	 * @param parameter
	 * @param entry
	 */
	private static void parseParameter(Object parameter, ElementEntry entry) {
		String value = (String) parameter;
		int pi = value.indexOf('(');
		if (pi==-1) {
			entry.name=value;
		} else {
			int lastDot=value.lastIndexOf('.', pi);
			if (lastDot==-1) {
				entry.name="*";
				entry.operationSignature=value;
			} else {
				entry.name=value.substring(0, lastDot);
				entry.operationSignature=value.substring(lastDot+1);
			}
		}
	}
	
	/**
	 * @param holder
	 */
	public DomWaiver(Element holder) throws HammurapiException {
		super();
		try {
			CachedXPathAPI cxpa=new CachedXPathAPI();
			inspectorName=getElementText(holder, "inspector-name", cxpa);
			
			NodeIterator it=cxpa.selectNodeIterator(holder, "signature");
			Node signatureNode;
			while ((signatureNode=it.nextNode())!=null) {
				if (signatureNode instanceof Element) {
					hadSignatures=true;
					signatures.add(getElementText(signatureNode));
					
				}
			}
			
			it=cxpa.selectNodeIterator(holder, "include-element|exclude-element|include-file|exclude-file");
			Element element;
			while ((element=(Element) it.nextNode())!=null) {
				String elementText = getElementText(element);
				if ("include-element".equals(element.getNodeName())) {
					ElementEntry entry=new ElementEntry();
					entry.exclude=false;
					parseParameter(elementText, entry);
					elements.addFirst(entry);
				} else if ("exclude-element".equals(element.getNodeName())) {
					ElementEntry entry=new ElementEntry();
					entry.exclude=true;
					parseParameter(elementText , entry);
					elements.addFirst(entry);			
				} else if ("include-file".equals(element.getNodeName())) {
					GlobCompiler compiler=new GlobCompiler();
					ElementEntry entry=new ElementEntry();
					entry.exclude=false;
					try {
						entry.fileNamePattern=compiler.compile(elementText);
					} catch (MalformedPatternException e) {
						throw new HammurapiException(e);
					}
					elements.addFirst(entry);			
				} else if ("exclude-file".equals(element.getNodeName())) {
					GlobCompiler compiler=new GlobCompiler();
					ElementEntry entry=new ElementEntry();
					entry.exclude=true;
					try {
						entry.fileNamePattern=compiler.compile(elementText);
					} catch (MalformedPatternException e) {
						throw new HammurapiException(e);
					}
					elements.addFirst(entry);						
				}
			}
			
			reason=getElementText(holder, "reason", cxpa);
			
			String expirationDateVal=getElementText(holder, "expiration-date", cxpa);
			if (expirationDateVal!=null) {
				SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT);				
				expirationDate=sdf.parse(expirationDateVal);
			}
		} catch (TransformerException e) {
			throw new HammurapiException(e);
		} catch (ParseException e) {
			throw new HammurapiException(e);
		}
	}	
	
	private String inspectorName;
	
	public String getInspectorName() {
		return inspectorName;
	}

	private Date expirationDate;
	
	public Date getExpirationDate() {
		return expirationDate;
	}

	private String reason;
	
	public String getReason() {
		return reason;
	}

	public boolean waive(Violation violation, boolean peek) {
		if (inspectorName.equals(violation.getDescriptor().getName())) {
			if (hadSignatures) {
				if (violation.getSource() instanceof Signed) {
					String signature = ((Signed) violation.getSource()).getSignature();
					if (signatures.contains(signature)) {
						if (!peek) {
							signatures.remove(signature);
						}
						active=!signatures.isEmpty();
						return true;
					}
				}
			}
			
			if (elements.isEmpty()) {
				return true;
			}
				
			if (violation.getSource() instanceof LanguageElement) {
				for (LanguageElement element=(LanguageElement) violation.getSource(); element!=null; element=element.getParent()) {
					if (_waive(element)) {
						return true;
					}
				}
				
				if (_waive(((LanguageElement) violation.getSource()).getCompilationUnit().getPackage())) {
					return true;
				}
			} else if (violation.getSource() instanceof com.pavelvlasov.jsel.Package) {
				if (_waive(violation.getSource())) {
					return true;
				}
			}
		}
		return false;
	}

	private Perl5Matcher matcher=new Perl5Matcher();
	
	/**
	 * @param source
	 */
	private boolean _waive(Object o) {
		if (o instanceof Package) {
			Iterator it=elements.iterator();
			while (it.hasNext()) {
				ElementEntry entry=(ElementEntry) it.next();
				Package pkg = (Package) o;
				if (entry.name!=null && entry.operationSignature==null && 
						("*".equals(entry.name) 
								|| pkg.getName().equals(entry.name)
								|| (entry.name.endsWith(".*") && pkg.getName().startsWith(entry.name.substring(0, entry.name.length()-2))))) {
					return !entry.exclude;
				}
			}
		} else if (o instanceof CompilationUnit) {
			Iterator it=elements.iterator();
			while (it.hasNext()) {
				ElementEntry entry=(ElementEntry) it.next();
				if (entry.fileNamePattern!=null && matcher.matches(((CompilationUnit) o).getName(), entry.fileNamePattern)) {  
					return !entry.exclude;
				}
			}
		} else if (o instanceof TypeBody) {
			// only if package included ???
			Iterator it=elements.iterator();
			while (it.hasNext()) {
				ElementEntry entry=(ElementEntry) it.next();
				try {
					TypeBody typeBody = (TypeBody) o;
					if (entry.name!=null && entry.operationSignature==null && typeBody.isKindOf(entry.name)) {
						return !entry.exclude;
					}
				} catch (JselException e) {
					if (!(e.getCause() instanceof ClassNotFoundException)) {
						return false;
					}
				}
			}
		} else if (o instanceof Operation) {
			// only if type included ???
			Iterator it=elements.iterator();
			while (it.hasNext()) {
				ElementEntry entry=(ElementEntry) it.next();
				try {
					Operation operation = (Operation) o;
					if (entry.name!=null && entry.operationSignature!=null && ("*".equals(entry.name) || operation.getEnclosingType().isKindOf(entry.name)) && operation.getInfo().isArgumentCompatible(entry.operationSignature)) {						
						return !entry.exclude;
					}
				} catch (JselException e) {
					throw new HammurapiRuntimeException(e);
				}
			}
		}
		
		return false;
	}

	public boolean isActive() {
		return active;
	}

	public Collection getSignatures() {
		return signatures;
	}
	
}
