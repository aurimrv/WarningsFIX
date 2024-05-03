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
package org.hammurapi.results.persistent.jdbc;

import java.util.Date;

import org.hammurapi.InspectorSet;
import org.hammurapi.WaiverSet;

import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.persistence.Storage;
import com.pavelvlasov.sql.SQLProcessor;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.5 $
 */
public class ResultsFactoryConfig {
	private WaiverSet waiverSet;
	private SQLProcessor sqlProcessor;
	private InspectorSet inspectorSet;
	private Repository repository;
	private Storage storage;
	private int reportNumber;
	private String name;
	private String hostName;
	private String hostId;
	private Date baseLine;
    private String description;
	
	/**
	 * @return Returns the baseLine.
	 */
	public Date getBaseLine() {
		return baseLine;
	}
	
	/**
	 * @param baseLine The baseLine to set.
	 */
	public void setBaseLine(Date baseLine) {
		this.baseLine = baseLine;
	}
	/**
	 * 
	 */
	public ResultsFactoryConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InspectorSet getInspectorSet() {
		return inspectorSet;
	}
	public void setInspectorSet(InspectorSet inspectorSet) {
		this.inspectorSet = inspectorSet;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getReportNumber() {
		return reportNumber;
	}
	public void setReportNumber(int reportNumber) {
		this.reportNumber = reportNumber;
	}
	public Repository getRepository() {
		return repository;
	}
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	public SQLProcessor getSqlProcessor() {
		return sqlProcessor;
	}
	public void setSqlProcessor(SQLProcessor sqlProcessor) {
		this.sqlProcessor = sqlProcessor;
	}
	public Storage getStorage() {
		return storage;
	}
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	public WaiverSet getWaiverSet() {
		return waiverSet;
	}
	public void setWaiverSet(WaiverSet waiverSet) {
		this.waiverSet = waiverSet;
	}
	public String getHostId() {
		return hostId;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
