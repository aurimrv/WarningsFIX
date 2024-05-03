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
package org.hammurapi.inspectors.performance.testcases.fixes;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class StringConcatenationInspectorFixTestCase {
	
	// Examples of + which is not violation
	
	// Static variable initializers are calculated only once during class loading  
	static public String zz="oops ("+System.currentTimeMillis()+" times) I did it again";
	
	{
		// Constants evaluated and compile time
		String z="a"+"b"+"c"+((String) "D");
		
		// One plus is OK.
		String y="z"+z;
	}
	
	
	public String concat(String a, String b, String c) {
		// FIX
		return new StringBuffer(100).append(a).append(b).append(c).toString();
	}

}
