/*
 * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2005  Johannes Bellert
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
 * e-Mail: CraftOfObjects@gmail.com
 */
package org.hammurapi.inspectors;


import java.util.ArrayList;
import java.util.Iterator;

import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Matcher;
import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.impl.JavaTokenTypes;
import com.pavelvlasov.jsel.impl.Token;
import com.pavelvlasov.review.SourceMarker;


/**
 * ER-209 as a variant of ER-100
 * Copyrights information should be present in each  file.
 * @author  Johannes Bellert
 * @version $Revision: 1.3 $
 */
public class VendorNameViolation extends InspectorBase  implements Parameterizable  {
	private Perl5Matcher matcher=new Perl5Matcher();

	/**
	 * Stores the setting from the configuration for the mandatory
	 * copyright text.
	 */
	private ArrayList vendorNames=new ArrayList();
	private ArrayList patterns=new ArrayList();
    
	
	public void visit(Token token) {
		if (token.getType()==JavaTokenTypes.SL_COMMENT || token.getType()==JavaTokenTypes.ML_COMMENT) {
			context.info(token, token.getText() ) ; // + " ---> " + token.getOwner() + " " + token.getOwner().getLocation());
			checkForViolations(token, token.getText() );
		}
	}

	public void checkForViolations( SourceMarker scrMrk, String text){
	Iterator it=patterns.iterator();
	
	while (it.hasNext()) {
	    Pattern pt = (Pattern) it.next();
		if (matcher.contains(text, pt )) {
		    context.reportViolation(scrMrk, "Found Pattern " + pt.getPattern().toString() );
		}
	}
	}
	
	
	/**
	 * Configures the rule. Reads in the values of the parameter vendor-name.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param parameter the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter)
            throws ConfigurationException {
        if ("vendor-name".equals(name)) {
         //   System.out.println("+ " + parameter.toString());

            if (!vendorNames.contains(parameter.toString())) {
                vendorNames.add(parameter.toString());

                GlobCompiler compiler = new GlobCompiler();
                try {
                    patterns.add(compiler.compile(parameter.toString(),
                            GlobCompiler.CASE_INSENSITIVE_MASK));
                } catch (MalformedPatternException e) {
                    throw new ConfigurationException("Malformed pattern: "
                            + parameter, e);
                }
            }
            return true;
        }

        throw new ConfigurationException("Parameter '" + name
                + "' is not supported by " + getClass().getName());
    }

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Configured Vendor Names text:\n");
		Iterator it=vendorNames.iterator();
		
		
		while (it.hasNext()) {
			ret.append("    " + it.next() + "\n");
		}
		return ret.toString();
	}
}

