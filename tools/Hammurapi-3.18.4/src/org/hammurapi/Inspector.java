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

import java.sql.SQLException;
import java.util.Properties;

import com.pavelvlasov.sql.SQLProcessor;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.4 $
 */
public interface Inspector {
	void setContext(InspectorContext context);
	void unSetContext();
	InspectorContext getContext();
	void init() throws HammurapiException;
	void destroy();
	
	/**
	 * @return Configuration string.  
	 */
	String getConfigInfo();
	
	/**
	 * This method is invoked when a new database is 
	 * created. In this method inspector can create tables 
	 * to store its proprietary information 
	 * @param processor Processor to perform initialization
	 * @param dbProperties Database properties, e.g. database type - 
	 * "Hypersonic", "Cloudscape", ...
	 */
	void initDb(SQLProcessor processor, Properties dbProperties) throws SQLException;
}
