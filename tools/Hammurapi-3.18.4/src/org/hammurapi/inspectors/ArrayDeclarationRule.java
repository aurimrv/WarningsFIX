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
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.impl.AST;


/**
 * ER-018
 * Array declarators should be placed next to the type, not the variable name
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class ArrayDeclarationRule extends InspectorBase  {
	
	/**
	 * @param element
	 */
	public void visit(VariableDefinition element) {
        AST arrayNode=null;
        AST identNode=null;
        
        for (AST node=(AST) element.getAst().getFirstChild(); node!=null; node=(AST) node.getNextSibling()) {
        	String typeName=node.getTypeName();
            if ("TYPE".equals(typeName)) {
                AST node2=(AST) node.getFirstChild();
                if (node2!=null && "ARRAY_DECLARATOR".equals(node2.getTypeName())) {
                    arrayNode=node2;
                } 
            } else if ("IDENT".equals(typeName)) {
                identNode=node;
                break;
            }
        }
        
        if (arrayNode!=null && identNode!=null && identNode.getColumn()<arrayNode.getColumn()) {
           context.reportViolation(element);
        }
	}	
}
