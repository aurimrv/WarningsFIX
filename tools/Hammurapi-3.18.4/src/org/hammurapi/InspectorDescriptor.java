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

import java.util.Collection;

import com.pavelvlasov.config.ConfigurationException;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.3 $
 */
public interface InspectorDescriptor {	
    String getCategory();
	String getDescription();	
	Boolean isEnabled();
	String getName();
	Integer getSeverity();
	Integer getOrder();
	String getRationale();
	String getViolationSample();
	String getFixSample();
	String getResources();
	String getMessage();
	String getMessage(String key);	
	
	/**
	 * Instantiates rule
	 * @return rule instance
	 */
	Inspector getInspector() throws ConfigurationException;
	
	/**
	 * Accumulated parameters. Parameters from lower levels come first;
	 * @return Collection of ParameterEntry instances
	 */
	Collection getParameters();
	
	/**
	 * Some violations can be waived, for example empty catch block
	 * might be justifiable in some situations. But other violations
	 * can not. For example there is no reason to waive Upper L Rule.
	 * @return TRUE if violation can be waived.  
	 */
	Boolean isWaivable();

	/**
	 * @return Collection of cases in which a violation can be waived. 
	 */
	Collection getWaiveCases();
	
	/**
	 * @param inspectorKey
	 * @return
	 */
	String getWaivedInspectorName(String inspectorKey);
	
	Collection getWaivedInspectorNames();
	
	/**
	 * @param inspectorKey
	 * @return
	 */
	String getWaiveReason(String inspectorKey);
	
	/**
	 * @param inspectorSet Inspector set to read descriptors from
	 * @param chain filtered inspector descriptors selected by previous descriptor in stack.
	 * @return Collection of inspector descriptors which this inspector 
	 * descriptor declares as filtered.
	 */
	Collection getFilteredInspectorDesriptors(InspectorSet inspectorSet, Collection chain);
	
	/**
	 * 
	 * @return Collection of names of inspectors whose visit() methods shall be invoked after this 
	 * inspector's visit() methods and whose leave() methods shall be invoked before this inspector 
	 * leave() method.
	 */
	Collection getAfterInspectorNames();
}
