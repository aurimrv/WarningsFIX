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
package org.hammurapi.inspectors.formatting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.pavelvlasov.jsel.impl.Token;
import org.hammurapi.HammurapiException;


/**
 * Base class for FormattingCheckers implements invocation of
 * dispatch methods by using reflection
 * 
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class FormattingCheckerBase implements FormattingChecker {
  
	private static final String CHECK_PREFIX = "check_";

	private Map checkers=new HashMap();
	
	protected int indentationLevel=4;
	
	{
		Class thisClass=this.getClass();
		for (int i=0, mc=thisClass.getMethods().length; i<mc; i++) {
			Method m=thisClass.getMethods()[i];
			if (!m.getName().equals(CHECK_PREFIX) 
					&& Modifier.isPublic(m.getModifiers())
					&& m.getName().startsWith(CHECK_PREFIX) 
					&& m.getParameterTypes().length==1 
					&& Token.class.isAssignableFrom(m.getParameterTypes()[0])
					&& boolean.class.equals(m.getReturnType())) {
				checkers.put(m.getName().substring(CHECK_PREFIX.length()), m);
			}
		}
	}
	
	public boolean check(Token aToken) throws HammurapiException {
		Method m=(Method) checkers.get(aToken.getTypeName());
		if (m==null) {
			return false;
		} else {
			try {
				return ((Boolean) m.invoke(this, new Object[] {aToken})).booleanValue();
			} catch (IllegalArgumentException e) {
				throw new HammurapiException(e);
			} catch (IllegalAccessException e) {
				throw new HammurapiException(e);
			} catch (InvocationTargetException e) {
				throw new HammurapiException(e);
			}
		}
	}
	
	public void setIndentationLevel(int indentationLevel) {
		this.indentationLevel = indentationLevel;
	}
}


