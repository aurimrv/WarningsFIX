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

import com.pavelvlasov.jsel.statements.CompoundStatement;
import com.pavelvlasov.jsel.statements.DoStatement;
import com.pavelvlasov.jsel.statements.ForStatement;
import com.pavelvlasov.jsel.statements.IfStatement;
import com.pavelvlasov.jsel.statements.WhileStatement;
import com.pavelvlasov.review.SourceMarker;

/**
 * Complex statements like do, while, for, if, else need a brace enclosed block.
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class BraceRule extends InspectorBase {
        
    /**
     * Reviews the if statements.
     * 
     * @param statement the if statement to be reviewed
     */
    public void visit(IfStatement statement) {
    	if (!(statement.getStatement() instanceof CompoundStatement)) {
    		context.reportViolation((SourceMarker) statement.getStatement());
    	}
    	
    	if (!(statement.getElseStatement() == null
			|| statement.getElseStatement() instanceof CompoundStatement
			|| statement.getElseStatement() instanceof IfStatement)) {
			context.reportViolation((SourceMarker) statement.getElseStatement());
		}
    }
    
    /**
     *  Reviews the do loops.
     * 
     * @param statement the do statement to be reviewed.
     */    
    public void visit(DoStatement statement) {
    	if (!(statement.getStatement() instanceof CompoundStatement)) {
    		context.reportViolation((SourceMarker) statement.getStatement());
    	}
    }

    /**
     * Reviews the for loops.
     * 
     * @param statement the for statement to be reviewed.
     */
    public void visit(ForStatement statement) {
    	if (!(statement.getStatement() instanceof CompoundStatement)) {
    		context.reportViolation((SourceMarker) statement.getStatement());
    	}
    }

    /**
     * Reviews the while loops.
     * 
     * @param statement the while statement to be reviewed.
     */
    public void visit(WhileStatement statement) {
    	if (!(statement.getStatement() instanceof CompoundStatement)) {
    		context.reportViolation((SourceMarker) statement.getStatement());
    	}
    }

    
}
