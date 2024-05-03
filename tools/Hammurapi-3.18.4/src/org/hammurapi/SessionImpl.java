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
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hammurapi.results.ResultsFactory;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.persistence.Storage;
import com.pavelvlasov.sql.ConnectionPerThreadDataSource;
import com.pavelvlasov.sql.SQLProcessor;
import com.pavelvlasov.sql.hypersonic.HypersonicTmpDataSource;
import com.pavelvlasov.util.DispatchingVisitor;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.6 $
 */
public class SessionImpl implements Session {
			
	private DispatchingVisitor visitor;

	private Map contexts=new HashMap();
	
	private Collection inspectors;
	
	private Storage storage;

	private boolean inspectorsSet;

	/**
	 * @return Returns the storage.
	 */
	public Storage getStorage() {		
		return storage==null ? ResultsFactory.getInstance().getStorage() : storage;
	}
	/**
	 * @param storage The storage to set.
	 */
	void setStorage(Storage storage) {
		this.storage = storage;
	}
	/**
	 * @param inspectorSet The inspectors to set.
	 * @throws HammurapiException
	 * @throws ConfigurationException
	 */
	public void setInspectors(InspectorSet inspectorSet) throws ConfigurationException, HammurapiException {
		this.inspectorsSet=true;
		this.inspectors = new ArrayList(inspectorSet.getInspectors());
		inspectorSet.initInspectors();
	}
	
	public InspectorContext getContext(String inspectorName) {
		InspectorContext ret = (InspectorContext) contexts.get(inspectorName);
		if (ret==null) {
			throw new HammurapiRuntimeException("Inspector '"+inspectorName+"' does not exist");
		}
		return ret;
	}
	
	void addContext(String inspectorName, InspectorContext context) {
		contexts.put(inspectorName, context);
	}

	private Map attributes=new HashMap();
	
	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}
	
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}
	
	public Object removeAttribute(Object key) {
		return attributes.remove(key);
	}

	public void disable(Inspector inspector) {
		if (visitor!=null) {
			visitor.remove(inspector);
		}
	}
	
	/**
	 * @param visitor The visitor to set.
	 */
	public void setVisitor(DispatchingVisitor visitor) {
		this.visitor = visitor;
	}

	private SQLProcessor processor;

	private ConnectionPerThreadDataSource datasource;
	
	private boolean scheduleInitDb;

	private String[] classPath;
	
	void setClassPath(String[] classPath) {
		this.classPath=classPath;
	}
	
	public void scheduleInitDb() {
		this.scheduleInitDb = true;
	}
	
	public SQLProcessor getProcessor() {
		try {
			if (processor==null) {
				try {
					datasource=new HypersonicTmpDataSource((Reader) null);
					processor=new SQLProcessor(datasource, null);
					scheduleInitDb=true;
				} catch (ClassNotFoundException e) {
					throw new HammurapiRuntimeException(e);
				} catch (IOException e) {
					throw new HammurapiRuntimeException(e);
				}
			}
			if (scheduleInitDb) {
				scheduleInitDb=false;
				initDb();
			}
		} catch (SQLException e) {
			throw new HammurapiRuntimeException(e);
		}
		
		return processor;
	}
	
	/**
	 * @throws SQLException
	 */
	private void initDb() throws SQLException {
		if (inspectorsSet) {
			if (inspectors!=null) {
				Iterator it=inspectors.iterator();
				while (it.hasNext()) {
					Object next = it.next();
					if (next instanceof Inspector) {
						((Inspector) next).initDb(processor, dbProperties);
					} 
				}
			}
		} else {
			throw new IllegalStateException("getProcessor() called before inspectors were set");
		}
	}

	void setDatasource(DataSource datasource) {
		if (processor==null) {
			processor=new SQLProcessor(datasource,null);
		} else {
			throw new HammurapiRuntimeException("Illegal state: processor is not null");
		}
	}
	
	void shutdown() throws SQLException {
		if (datasource != null) {
			datasource.shutdown();
		}
	}

	public String[] getClassPath() {
		return classPath;
	}
	
	private Properties dbProperties=new Properties();
	
	void setDbProperty(String name, String value) {
		dbProperties.setProperty(name, value);
	}
}
