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

import java.util.Collection;

import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorBase;
import org.hammurapi.results.DetailedResults;
import org.hammurapi.results.ResultsFactory;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.review.SourceMarker;


/**
 * ER-117
 * Copyrights information should be present in each  file.
 * @author  Pavel Vlasov
 * @version $Revision: 1.9 $
 */
public class PrioritizingInspector extends InspectorBase  {
	private int clients;
	private boolean disabled;
	
	public void visit(CompilationUnit cu) {
		clients=0;
	}
	
	public void visit(TypeDefinition td) throws JselException {
		if (!disabled) {
			Collection cc = td.getClients();
			if (cc==null) {
				// Invoked from Quickurappi.
				disabled=true;
			} else {
				clients+=cc.size();
			}
		}
	}
	
	/**
	 * Reviews the compilation unit if it's file violates agaianst the rule.
	 * @param element the compilation unit to be reviewed.
	 * @throws HammurapiException
	 */	
	public void leave(CompilationUnit cu) throws HammurapiException {
		if (!disabled) {
			final SourceMarker sm=context.detach(cu);
			final DetailedResults result = ResultsFactory.getThreadResults();
			
			ResultsFactory.getInstance().execute(new ResultsFactory.Task() {
	
				public void execute() throws HammurapiException {
					result.addMetric(sm, "Work order", 100.0/((Math.log(clients+1)+1)*result.getViolationLevel()+1));
				}
				
			});
		}
	}
}
