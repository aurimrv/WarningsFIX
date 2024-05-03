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
package org.hammurapi.inspectors.filters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Matcher;
import org.hammurapi.InspectorBase;
import org.hammurapi.FilteringInspector;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Operation;
import com.pavelvlasov.jsel.Package;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.TypeDefinition;

/**
 * Initial behavior of the filter to approve everything.
 * @author Pavel Vlasov
 * @version $Revision: 1.6 $
 */
public class LanguageElementFilter extends InspectorBase implements
		FilteringInspector, Parameterizable {
	
	private Stack approvals=new Stack();
	
	{
		//Initial state of filter is approve
		approvals.push(Boolean.TRUE);
	}
	
	private static class ElementEntry {
		boolean exclude;
		String name;
		String operationSignature;
		Pattern fileNamePattern;
	}
	
	private LinkedList elements=new LinkedList();
	
	public Boolean approve(Object element) {
		return (Boolean) approvals.peek();
	}
		
	private Perl5Matcher matcher=new Perl5Matcher();
	
	public void visit(CompilationUnit compilationUnit) {
	    Package pkg=compilationUnit.getPackage();
		// Carry over previous state.
		boolean decision=((Boolean) approvals.peek()).booleanValue();
		
		Iterator it=elements.iterator();
		while (it.hasNext()) {
			ElementEntry entry=(ElementEntry) it.next();
			if (entry.name!=null && entry.operationSignature==null && 
					("*".equals(entry.name) 
							|| pkg.getName().equals(entry.name)
							|| (entry.name.endsWith(".*") && pkg.getName().startsWith(entry.name.substring(0, entry.name.length()-2))))) {
				decision = !entry.exclude;
				break;
			}
		}
		approvals.push(decision ? Boolean.TRUE : Boolean.FALSE);						

	    
		// Carry over previous state.
		decision=((Boolean) approvals.peek()).booleanValue();
		
		it=elements.iterator();
		while (it.hasNext()) {
			ElementEntry entry=(ElementEntry) it.next();
			if (entry.fileNamePattern!=null && matcher.matches(compilationUnit.getName(), entry.fileNamePattern)) {  
				decision = !entry.exclude;
				break;
			}
		}
		approvals.push(decision ? Boolean.TRUE : Boolean.FALSE);						
	}
	
	public void leave(CompilationUnit compilaitonUnit) {
		approvals.pop();
		approvals.pop();
	}
	
	/**
	 * Includes/excludes type only if package is included.
	 * @param typeBody
	 */
	public void visit(TypeBody typeBody) {
		// Carry over previous state.
		boolean decision=((Boolean) approvals.peek()).booleanValue();
		
		// Evaluate only if package included
		if (decision) {			
			Iterator it=elements.iterator();
			while (it.hasNext()) {
				ElementEntry entry=(ElementEntry) it.next();
				try {
					if (entry.name!=null && entry.operationSignature==null && typeBody.isKindOf(entry.name)) {
						decision = !entry.exclude;
						break;
					}
				} catch (JselException e) {
					if (!(e.getCause() instanceof ClassNotFoundException)) { 
						context.warn(typeBody, e);						
						break;
					}
				}
			}
		}
		
		approvals.push(decision ? Boolean.TRUE : Boolean.FALSE);						
	}
		
	public void leave(TypeDefinition typeDefinition) {
		approvals.pop();
	}
	
	public void visit(Operation operation) {
		// Carry over previous state.
		boolean decision=((Boolean) approvals.peek()).booleanValue();
		
		// Evaluate only if type included
		if (decision) {
			Iterator it=elements.iterator();
			while (it.hasNext()) {
				ElementEntry entry=(ElementEntry) it.next();
				try {
					if (entry.name!=null && entry.operationSignature!=null && ("*".equals(entry.name) || operation.getEnclosingType().isKindOf(entry.name)) && operation.getInfo().isArgumentCompatible(entry.operationSignature)) {						
						decision = !entry.exclude;
						break;
					}
				} catch (JselException e) {
					context.warn(operation, e);
					break;
				}
			}
		}
		
		approvals.push(decision ? Boolean.TRUE : Boolean.FALSE);						
	}
	
	public void leave(Operation operation) {
		approvals.pop();
	}

	/**
	 * Supports <CODE>include</CODE>, <CODE>exclude</CODE>, <code>include-file</code>, and <code>exclude-file</code>.
	 * parameters. The first two apply to package, type and operation. The last two apply to compilation units. 
	 * Order is significant - includes/excludes are applied
	 * in the same order as they defined. So including org.hammurapi.* and then excluding org.hammurapi.inspectors.* makes sense, but
	 * not vice versa.<BR> 
	 * If package excluded all types and operations in it are excluded. If type is excluded all
	 * operations in this type are excluded.
	 * Operation parameters can contain <CODE>?</CODE> which matches any type.  
	 * <P/>&lt;init&gt; shall be used as operation name for constructors.<P/>
	 * <CODE>include</CODE> and <CODE>exclude</CODE> parameters format:
	 * <UL>
	 * <LI><CODE>*</code> - matches anything</LI>
	 * <LI><code>&lt;string&gt;</code> - matches package, type or operation (if contains '(')</LI>
	 * <LI><code>&lt;string&gt;.*</code> - matches package and its subpackages</LI>
	 * </UL>
	 * Packages are matched literally, types are matched based on isKindOf(), including operation types as well.
	 * If operation doesn't contain type name then any type will match.<BR/>
	 * 
	 * <code>include-file</code> and <code>exclude-file</code> parameter values are file name patterns. ? states for zero or one 
	 * character, * states for zero or more characters. See {@link org.apache.oro.text.GlobCompiler} for more details.<p/>
	 * <DL>
	 * <DT><B>Examples</B></DT>
	 * <DD><code>&lt;parameter name="include"&gt;org.hammurapi&lt;/parameter&gt;</code> - includes package org.hammurapi</DD> 
	 * <DD><code>&lt;parameter name="exclude"&gt;org.hammurapi.*&lt;/parameter&gt;</code> - excludes package org.hammurapi and its subpackages</DD> 
	 * <DD><code>&lt;parameter name="exclude"&gt;org.hammurapi.InspectorDescriptor&lt;/parameter&gt;</code> - excludes interface org.hammurapi.InspectorDescriptor and its implementations</DD> 
	 * <DD><code>&lt;parameter name="include"&gt;org.hammurapi.InspectorDescriptor.getMessage(java.lang.String)&lt;/parameter&gt;</code> - 
	 * includes method interface org.hammurapi.InspectorDescriptor.getMessage(java.lang.String) and its implementations</DD> 
	 * <DD><code>&lt;parameter name="exclude"&gt;toString()&lt;/parameter&gt;</code> - excludes toString() in all classes/interfaces</DD> 
	 * <DD><code>&lt;parameter name="exclude"&gt;org.somepackage.SomeClass.&lt;init&gt;(?, java.util.List)&lt;/parameter&gt;</code> - 
	 * excludes constructors which take two parameters, the first one of any type and the
	 * second one of type <code>java.util.List</code> or its implementations in <code>org.somepackage.SomeClass</code> and its subclasses</DD> 
	 * <DD><code>&lt;parameter name="include-file"&gt;*.bpf&lt;/parameter&gt;</code> - includes .bpf files</DD> 
	 */
	public boolean setParameter(String name, Object parameter)	throws ConfigurationException {
		if ("include".equals(name)) {
			ElementEntry entry=new ElementEntry();
			entry.exclude=false;
			parseParameter(parameter, entry);
			elements.addFirst(entry);
		} else if ("exclude".equals(name)) {
			ElementEntry entry=new ElementEntry();
			entry.exclude=true;
			parseParameter(parameter, entry);
			elements.addFirst(entry);			
		} else if ("include-file".equals(name)) {
			GlobCompiler compiler=new GlobCompiler();
			ElementEntry entry=new ElementEntry();
			entry.exclude=false;
			try {
				entry.fileNamePattern=compiler.compile((String) parameter);
			} catch (MalformedPatternException e) {
				throw new ConfigurationException(e);
			}
			elements.addFirst(entry);			
		} else if ("exclude-file".equals(name)) {
			GlobCompiler compiler=new GlobCompiler();
			ElementEntry entry=new ElementEntry();
			entry.exclude=true;
			try {
				entry.fileNamePattern=compiler.compile((String) parameter);
			} catch (MalformedPatternException e) {
				throw new ConfigurationException(e);
			}
			elements.addFirst(entry);						
		} else {
			throw new ConfigurationException("Not supported parameter: "+name);
		}
		return true;
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
}
