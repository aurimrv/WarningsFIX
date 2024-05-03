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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import com.pavelvlasov.jsel.statements.CompoundStatement;
import com.pavelvlasov.jsel.statements.EmptyStatement;
import com.pavelvlasov.jsel.statements.Statement;
import com.pavelvlasov.sql.SQLProcessor;
import com.pavelvlasov.util.OrderedTarget;
import com.pavelvlasov.util.DispatchingVisitor.Filter;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.1 $
 */
public class InspectorBase implements Inspector, OrderedTarget {
	protected InspectorContext context;

	public void setContext(InspectorContext context) {
		this.context=context;
	}

	public void unSetContext() {
		context=null;
	}
	
	public InspectorContext getContext() {
		return context;
	}
	
	public static boolean isEmpty(Statement statement) {
		if (statement==null) {
			return true;
		} if (statement instanceof EmptyStatement) {
			return true;
		} else if (statement instanceof CompoundStatement) {
			Iterator it=((CompoundStatement) statement).getStatements().iterator();
			while (it.hasNext()) {
				if (!isEmpty((Statement) it.next())) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void init() throws HammurapiException {
	}

	public void destroy() {
	}
	
	public Integer getOrder() {
		return getContext().getDescriptor().getOrder();
	}

	public String getConfigInfo() {
		return null;
	}
	
	/**
	 * Method from {@link com.pavelvlasov.util.DispatchingVisitor.Filter}
	 * interface. If inspector implements that interface it doesn't 
	 * need to implement the method, but just provide apporve() methods.
	 * @see com.pavelvlasov.util.DispatchingVisitor.Filter#getTargets()
	 * @return Collection of targets to be filtered.
	 */
	public Collection getTargets() {
		return filterTargets;
	}
	
	private Collection filterTargets=this instanceof Filter ? new LinkedList() : null;
	
	public void addTarget(Object target) {
		if (this instanceof Filter && target!=null) {
			filterTargets.add(target);
		}
	}
	
	/**
	 * Removes this inspector from invocation targets.
	 * Inspector shall invoke this method if it detects 
	 * that it cannot continue functioning properly.
	 */
	protected void disable(String message) {		
		getContext().getSession().disable(this);
		getContext().warn(null, "Inspector "+getContext().getDescriptor().getName()+" disabled itself with message '"+message+"'");
	}

	public void initDb(SQLProcessor processor, Properties dbProperties) throws SQLException {
		
	}
}
