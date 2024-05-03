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

/**
 * Defines connection to a Hypersonic server.
 * @ant.element name="Hypersonic server"
 * @author Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class ServerEntry {

	private String host="localhost";
	private String user="sa";
	private String password="";

	/**
	 * Host name. Defaults to "localhost" 
	 * @param host The host to set.
	 * @ant.non-required
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Password. Defaults to blank string.
	 * @param password The password to set.
	 * @ant.non-required
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * User. Defaults to "sa"
	 * @param user The user to set.
	 * @ant.non-required
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @param host
	 * @param user
	 * @param password
	 */
	public ServerEntry(String host, String user, String password) {
		if (host!=null) {
			this.host=host;
		}
		
		if (user!=null) {
			this.user=user;
		}
		
		if (password!=null) {
			this.password=password;
		}
	}

	/**
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return
	 */
	public String getHost() {
		return host;
	}

}
