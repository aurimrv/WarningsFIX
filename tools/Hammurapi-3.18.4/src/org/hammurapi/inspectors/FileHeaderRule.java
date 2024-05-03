/*
 * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Pavel Vlasov
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
 * e-Mail: vlasov@pavelvlasov.com

 */
package org.hammurapi.inspectors;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Matcher;
import org.hammurapi.InspectorBase;

import com.pavelvlasov.antlr.AST;
import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.impl.JavaTokenTypes;
import com.pavelvlasov.jsel.impl.Token;


/**
 * ER-100
 * Copyrights information should be present in each  file.
 * @author  Pavel Vlasov
 * @version $Revision: 1.10 $
 */
public class FileHeaderRule extends InspectorBase  implements Parameterizable  {
	private Perl5Matcher matcher=new Perl5Matcher();
	
	/**
	 * Reviews the compilation unit if it's file violates agaianst the rule.
	 * @param element the compilation unit to be reviewed.
	 */	
	public void visit(CompilationUnit element) {
		AST ast = element.getAst();
		if (ast!=null) {
			Token firstToken = (Token) ast.getFirstToken();
			if (firstToken!=null) {
				while (firstToken.getPrevToken()!=null) {
					firstToken=(Token) firstToken.getPrevToken();
				}
//				Anu 24-May-05: Modified code to check for copyright information 
//				before the class declaration with both multiline or singleline comments.				
//				while ((firstToken.getType()!=JavaTokenTypes.ML_COMMENT && firstToken.getType()!=JavaTokenTypes.SL_COMMENT) && firstToken.isWhiteSpace() && firstToken.getNextToken()!=null) {
//					firstToken=(Token) firstToken.getNextToken();
//				}
				
				while(firstToken.getType()!=JavaTokenTypes.LITERAL_class && firstToken.getNextToken()!=null)
				{
					if ((firstToken.getType()==JavaTokenTypes.ML_COMMENT||firstToken.getType()==JavaTokenTypes.SL_COMMENT) && firstToken.getText()!=null ) {
						Iterator it=patterns.iterator();
						while (it.hasNext()) {
							if (matcher.contains(firstToken.getText(), (Pattern) it.next())) {
								return;
							}
						}
					}
					firstToken=(Token) firstToken.getNextToken();
				}
			}
	
//				if ((firstToken.getType()==JavaTokenTypes.ML_COMMENT||firstToken.getType()==JavaTokenTypes.SL_COMMENT) && firstToken.getText()!=null && !classToken) {
//					Iterator it=patterns.iterator();
//					while (it.hasNext()) {
//						if (matcher.contains(firstToken.getText(), (Pattern) it.next())) {
//							return;
//						}
//					}
//				} 
				
				context.reportViolation(element);
			}
		
	}

	/**
	 * Stores the setting from the configuration for the mandatory
	 * copyright text.
	 */
	private ArrayList copyrights=new ArrayList();
	private ArrayList patterns=new ArrayList();
    
	/**
	 * Configures the rule. Reads in the values of the parameter copyright.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param parameter the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("copyright".equals(name)) {	
			if (!copyrights.contains(parameter.toString())) {
				copyrights.add(parameter.toString());
				GlobCompiler compiler=new GlobCompiler();			
				try {
					patterns.add(compiler.compile(parameter.toString()));
				} catch (MalformedPatternException e) {
					throw new ConfigurationException("Malformed pattern: "+parameter, e);
				}
			}
			return true;
		}
		
		throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName()); 
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Configured Copyright text:\n");
		Iterator it=copyrights.iterator();
		while (it.hasNext()) {
			ret.append("    " + it.next() + "\n");
		}
		return ret.toString();
	}
}
