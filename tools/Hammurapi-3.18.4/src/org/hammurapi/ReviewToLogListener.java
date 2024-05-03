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

import java.text.MessageFormat;
import java.util.Iterator;

import org.apache.tools.ant.Project;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.ReviewResults;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class ReviewToLogListener implements Listener {
    private Project project;
    
    /** Creates a new instance of ReviewToLogListener */
    public ReviewToLogListener(Project project) {
        this.project=project;
    }
    
    public void onReview(ReviewResults reviewResult) throws HammurapiException {
        if (reviewResult.getViolations().size()!=0) {
            project.log("Violations in "+reviewResult.getCompilationUnit().getRelativeName()+":", Project.MSG_VERBOSE);
            Iterator vit=reviewResult.getViolations().iterator();
            while (vit.hasNext()) {
                Violation violation=(Violation) vit.next();
                Object[] objs = {
                    new Integer(violation.getSource().getLine()),
                    new Integer(violation.getSource().getColumn()),
                    violation.getDescriptor().getName(),
                    violation.getDescriptor().getSeverity(),
                    violation.getMessage()
                };
                
                project.log(MessageFormat.format("{0}:{1} - {2}[{3}] {4}", objs), Project.MSG_VERBOSE);
            }
        }

        project.log("Codebase: "+reviewResult.getCodeBase(), Project.MSG_VERBOSE);
        project.log("Reviews: "+reviewResult.getReviewsNumber(), Project.MSG_VERBOSE);
        project.log("Violations: "+reviewResult.getViolations().size(), Project.MSG_VERBOSE);
//        project.log(MessageFormat.format("PAI {0, number,###.00}%", new Object[] { new Double(reviewResult.getPerfectionAffinityIndex()*100) }), Project.MSG_VERBOSE);
        project.log("DPMO: "+reviewResult.getDPMO(), Project.MSG_VERBOSE);
        project.log("Sigma: "+reviewResult.getSigma(), Project.MSG_VERBOSE);
        project.log(" ", Project.MSG_VERBOSE);
    }
    
    public void onSummary(CompositeResults summary, InspectorSet ruleSet) throws HammurapiException {
        project.log("=== Summary ===");
        project.log("Codebase: "+summary.getCodeBase());
        project.log("Reviews: "+summary.getReviewsNumber());
        project.log("Violations: "+summary.getViolationsNumber());
//        project.log(MessageFormat.format("PAI {0, number,###.00}%", new Object[] { new Double(summary.getPerfectionAffinityIndex()*100) }));
        project.log("DPMO: "+summary.getDPMO());
        project.log("Sigma: "+summary.getSigma());
    }    
    
    public void onPackage(CompositeResults packageResults) {
        project.log("=== Package ===", Project.MSG_VERBOSE);
        project.log("Package: "+packageResults.getName(), Project.MSG_VERBOSE);
        project.log("Codebase: "+packageResults.getCodeBase(), Project.MSG_VERBOSE);
        project.log("Reviews: "+packageResults.getReviewsNumber(), Project.MSG_VERBOSE);
        project.log("Violations: "+packageResults.getViolationsNumber(), Project.MSG_VERBOSE);
//        project.log(MessageFormat.format("PAI {0, number,###.00}%", new Object[] { new Double(packageResults.getPerfectionAffinityIndex()*100) }), Project.MSG_VERBOSE);
        project.log("DPMO: "+packageResults.getDPMO(), Project.MSG_VERBOSE);
        project.log("Sigma: "+packageResults.getSigma(), Project.MSG_VERBOSE);
}

	public void onBegin(InspectorSet inspectorSet) {
		project.log("Review started with "+inspectorSet.size()+" inspectors");		
	}    
}
