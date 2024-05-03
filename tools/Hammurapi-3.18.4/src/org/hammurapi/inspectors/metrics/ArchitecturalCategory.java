/*
 * Hammurapi
 * Automated Java code review system.
 * Copyright (C) 2004  Johannes Bellert
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
 * URL: http://www.pavelvlasov.com/pv/content/menu.show?id=products.jtaste
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Mar 20, 2004
 *
 */

/* Copyright (C) 2004  Johannes Bellert
 * e-Mail: Johannes.Bellert@ercgroup.com
 * Created on Mar 20, 2004
 */

package org.hammurapi.inspectors.metrics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ArchitecturalCategory {

	// public SourceMarker srcCodeMarker  = null;
	public String source_url = "";
	public int source_line = 0;
	public int source_col = 0;
	public String categoryType = "";
	public String identicator = "";
	public int occurance = 1;

	public ArchitecturalCategory(String _source_url, int col, int line, String _identicator, String _category){
		super();
		this.categoryType = _category;
		this.identicator =	_identicator;
		this.source_url = _source_url;
		this.source_line = line;
		this.source_col = col;
	}

	public boolean equals(ArchitecturalCategory ac){
		return  ac.categoryType.equals(this.categoryType);
	}

	public boolean equals(String search){
			return  search.equals(this.categoryType);
		}

	public Element toDom(Document document){

		Element ret=document.createElement("ArchitecturalCategory");
		ret.setAttribute("category", this.categoryType );
		ret.setAttribute("identicator", this.identicator );
		ret.setAttribute("occurance", String.valueOf( this.occurance ) );

		Element typ=document.createElement("SourceMarker");

		
		typ.setAttribute("source-url", source_url );
		typ.setAttribute("line", String.valueOf(source_line) );
		typ.setAttribute("col", String.valueOf(source_col ));
		ret.appendChild(typ);
		
		return ret;
	}

}
