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

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Package;
import com.pavelvlasov.review.SimpleSourceMarker;

/**
 * ER-059
 * Packages should be neither too lean nor too fat.
 * @author  Janos Czako
 * @version $Revision: 1.3 $
 */
public class FilesPerPackage extends InspectorBase  implements Parameterizable  {
	
	/**
	 * Reviews the package, if it contains more files, than the allowed max.
	 * 
	 * @param element the package to be reviewed.
	 */
	public void visit(Package element) {
		SimpleSourceMarker ssm=new SimpleSourceMarker(0, 0, (element.getName().length()==0 ? "." : element.getName().replace('.','/'))+"/", null);
		
		int packageSize=element.getCompilationUnits().size();
		if (maxPackageSize!=null && packageSize>maxPackageSize.intValue()) {
			context.reportViolationEx(ssm, new Object[] {maxPackageSize, new Integer(packageSize)}, "MAX");
        }

		if (minPackageSize!=null && packageSize<minPackageSize.intValue()) {
			context.reportViolationEx(ssm, new Object[] {minPackageSize, new Integer(packageSize)}, "MIN");
        }
	}
		
	/**
	 * Stores the setting form the configuration for the maximum allowed
	 * number of files in a package.
	 */
	private Integer maxPackageSize;

	/**
	 * Stores the setting form the configuration for the maximum allowed
	 * number of files in a package.
	 */
	private Integer minPackageSize;

	/**
	 * Configures rule. Reads in the values of the parameters containing the
	 * allowed number of files in a package.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter name, or value.
	 */
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("max-files".equals(name)) {
			maxPackageSize = (Integer) parameter;	
		} else if ("min-files".equals(name)) {
			minPackageSize = (Integer) parameter;	
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported by "+getClass().getName());
		} 
		return true;
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed maximum files per package:\n");
		if (maxPackageSize!=null) {
			ret.append("max-files: " + maxPackageSize + "\n");
		}
		
		if (minPackageSize!=null) {
			ret.append("min-files: " + minPackageSize + "\n");
		}
		return ret.toString();
	}
}
