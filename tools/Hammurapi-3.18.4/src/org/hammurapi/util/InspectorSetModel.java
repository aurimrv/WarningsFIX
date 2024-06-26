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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pavelvlasov.util.Visitable;
import com.pavelvlasov.util.Visitor;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class InspectorSetModel implements Visitable {
	private List inspectors=new LinkedList();
		
	/**
	 * 
	 */
	public InspectorSetModel(String fileName) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader(fileName));
		String line;
		while ((line=br.readLine())!=null) {
			if (line.trim().length()!=0 && !line.trim().startsWith("#")) {
				inspectors.add(new InspectorModel(line));
			}
		}
	}

	public boolean accept(Visitor visitor) {
		Iterator it=inspectors.iterator();
		while (it.hasNext()) {
			((Visitable) it.next()).accept(visitor);
		}
		return true;
	}
}
