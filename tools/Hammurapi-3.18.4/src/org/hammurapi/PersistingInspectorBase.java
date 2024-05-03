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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Properties;

import com.pavelvlasov.sql.SQLExceptionEx;
import com.pavelvlasov.sql.SQLProcessor;

/**
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.2 $
 */
public class PersistingInspectorBase extends ParameterizableInspectorBase {

	/**
	 * Creates history table and establishes foreign key relationship with report.
	 */
	public void initDb(SQLProcessor processor, Properties dbProperties) throws SQLException {
	    try {
	        ClassLoader classLoader = getClass().getClassLoader();
			String prefix = getClass().getName().replace('.','/');
			// Look for specific resource
	        String resourceName=prefix+"."+dbProperties.getProperty("type")+".sql";
			InputStream resourceAsStream = classLoader.getResourceAsStream(resourceName);
			
			// Look for common resource
			if (resourceAsStream==null) {
				resourceName=prefix+".sql";
				resourceAsStream = classLoader.getResourceAsStream(resourceName);
			}
			
	        if (resourceAsStream==null) {
	            throw new SQLException("Initialization script resource not found: "+resourceName);
	        } else {
	            processor.executeScript(new InputStreamReader(resourceAsStream));
	        }
	    } catch (IOException e) {
	        throw new SQLExceptionEx(e);
	    }
	}

}
