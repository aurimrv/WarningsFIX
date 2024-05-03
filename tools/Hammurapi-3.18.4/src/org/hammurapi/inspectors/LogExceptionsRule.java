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
package org.hammurapi.inspectors;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.JselRuntimeException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.statements.Handler;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.Visitor;


/**
 * ER-120
 * Catch-blocks should log the exeption with Log4J.fatal( "Context String"  , exception )
 * @author  Pavel Vlasov
 * @version $Revision: 1.6 $
 */
public class LogExceptionsRule extends InspectorBase implements Parameterizable {

	/**
	 * Reviews the exception handlers if they violate against the rule
	 * 
	 * @param handler the exception handler
	 */
	public void visit(Handler handler) {
		class LoggedException extends com.pavelvlasov.RuntimeException {

			/**
			 * Comment for <code>serialVersionUID</code>
			 */
			private static final long serialVersionUID = -1481467503705295388L;
				
		};

		try {
			handler.accept(new Visitor() {
				public boolean visit(Object target) {
					if (target instanceof MethodCall) {
						MethodCall mc = (MethodCall) target;
						try {
							OperationInfo operationInfo = mc.getProvider();
							if (operationInfo==null) {
								context.warn((SourceMarker) mc, "Provider is null for "+mc+" at "+((LanguageElement) mc).getLocation()); 
							} else if (loggerMethods.contains(operationInfo.getName()) && operationInfo.getDeclaringType().isKindOf(loggerClass) && operationInfo.getParameterTypes().length>1) {
								throw new LoggedException();
							}
						} catch (JselException e) {
							context.warn((SourceMarker) mc, e);
						} catch (JselRuntimeException e) {
							context.warn((SourceMarker) mc, e);
						}
					}
					return true;
				}
			});
			context.reportViolation((SourceMarker) handler);
		} catch (LoggedException e) {
			// OK exception has been logged
		}
	}

	/**
	 * Stores the setting form the configuration for the type of the logger class
	 */
	private String loggerClass;

	/**
	 * Stores the setting form the configuration for the logger class methods
	 */
	private java.util.Set loggerMethods = new java.util.HashSet();
    
	/**
	 * Configures the rule. Reads in the values of the parameters logger_class and
	 * logger_method.
	 * 
	 * @param name the name of the parameter being loaded from Hammurapi configuration
	 * @param value the value of the parameter being loaded from Hammurapi configuration
	 * @exception ConfigurationException in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object value) throws ConfigurationException {
		if ("logger_class".equals(name)) {
			loggerClass= value.toString();
		} else if ("logger_method".equals(name)) {
			loggerMethods.add(value.toString());
		} else {
			throw new ConfigurationException("Parameter '"+name+"' is not supported");
		}
		return true;
	}          
	
	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Used logger class: " + loggerClass + "\n");
		java.util.Iterator iter = loggerMethods.iterator();
		while (iter.hasNext()) {
			ret.append("operation: " + iter.next().toString() + "\t");
		}
		ret.append("\n");
		return ret.toString();
	}
}
