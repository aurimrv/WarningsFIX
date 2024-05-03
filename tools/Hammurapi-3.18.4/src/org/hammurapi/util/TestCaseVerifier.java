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

package org.hammurapi.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hammurapi.HammurapiException;
import org.hammurapi.Inspector;
import org.hammurapi.InspectorSet;
import org.hammurapi.Listener;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.ReviewResults;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.2 $
 */
public class TestCaseVerifier implements Listener, Parameterizable {
	private Map validationResults=new HashMap();
	private Set excludedInspectors=new HashSet();
	private String violationTestCaseFormat="{1}.testcases.violations.{2}ViolationTestCase.java";
	private String fixTestCaseFormat="{1}.testcases.fixes.{2}FixTestCase.java";
	
	private class VerifyEntry {
		Class inspectorClass;
		boolean violationTestCaseExists=false;
		boolean fixTestCaseExists=false;
		boolean violationTestCasePassed=false;
		boolean fixTestCasePassed=false;		
		/**
		 * @param inspectorClass
		 */
		protected VerifyEntry(Class inspectorClass) {
			super();
			this.inspectorClass = inspectorClass;
		}
	}

	public void onReview(ReviewResults reviewResult) throws HammurapiException {
		// TODO Auto-generated method stub
		
	}

	public void onPackage(CompositeResults packageResults) throws HammurapiException {
		// TODO Auto-generated method stub
		
	}

	public void onSummary(CompositeResults summary, InspectorSet inspectorSet) throws HammurapiException {
		// TODO Auto-generated method stub
		
	}

	public void onBegin(InspectorSet inspectorSet) {
		try {
			Iterator it=inspectorSet.getInspectors().iterator();
			while (it.hasNext()) {
				Inspector inspector=(Inspector) it.next();
				validationResults.put(inspector.getContext().getDescriptor().getName(), new VerifyEntry(inspector.getClass()));
			}
		} catch (HammurapiException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		// TODO Auto-generated method stub
		return true;
		
	}
}
