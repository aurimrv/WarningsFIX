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
package org.hammurapi.results;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.pavelvlasov.wrap.WrapperHandler;

/**
 * Converts CompositeResults to report.
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.1 $
 */
public class ReportMixer {

	public static Report mix(final CompositeResults cr, final String description) {
		Class[] cri=WrapperHandler.getClassInterfaces(cr.getClass());
		Class[] interfaces=new Class[cri.length+1];
		System.arraycopy(cri,0,interfaces,0,cri.length);
		interfaces[cri.length]=Report.class;		
		
		return (Report) Proxy.newProxyInstance(
					cr.getClass().getClassLoader(),
					interfaces,
					new InvocationHandler() {

						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							if ("getDescription".equals(method.getName()) && method.getParameterTypes().length==0) {
								return description;
							}
							
							return method.invoke(cr, args);
						}
						
					}
				);
	}
}
