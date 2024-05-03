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
import java.util.Iterator;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Initializer;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Expression;
import com.pavelvlasov.jsel.expressions.MethodCall;

/**
 * ER-066 Unify logging strategy - define individual logger for class
 * 
 * @author Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class DefineLoggerForClassRule 
	extends InspectorBase
	implements Parameterizable {
	
	/**
	 * Reviews the type definition and cheks if it has a logger field.
	 * 
	 * @param type
	 *            the type definition to be reviewed.
	 */
	public void visit(com.pavelvlasov.jsel.Class clazz) {
		if (loggerClassName!=null) {
			Repository repository = clazz.getCompilationUnit().getPackage().getRepository();
			try {
				Class loggerClass=repository.loadClass(loggerClassName); 			
				Iterator it=clazz.getFields().iterator();
				while (it.hasNext()) {
					Object field = it.next();
					if (field instanceof VariableDefinition) {
						VariableDefinition variableDefinition=(VariableDefinition) field;
						try {
							if (variableDefinition.getTypeSpecification().getType().isKindOf(loggerClassName)) {
								context.waive(variableDefinition, "AvoidHidingInheritedInstanceFields");
								
								if (!("logger".equals(variableDefinition.getName()) || "LOGGER".equals(variableDefinition.getName()))) {
									// TODO [0329] Move string to keyed message to descriptor
									context.reportViolation(variableDefinition, "Use unified name 'LOGGER' for loggers");
								}
								
								if (!variableDefinition.getModifiers().contains("static")) {
									// TODO [0329] Move string to keyed message to descriptor
									context.reportViolation(variableDefinition, "Logger must be static");
								}
								
								if (!variableDefinition.getModifiers().contains("private")) {
									// TODO [0329] Move string to keyed message to descriptor
									context.reportViolation(variableDefinition, "Logger must be private");
								}
								
								if (!variableDefinition.getModifiers().contains("final")) {
									// TODO [0329] Move string to keyed message to descriptor
									context.reportViolation(variableDefinition, "Logger must be final");
								}
																
								Initializer initializer = variableDefinition.getInitializer();
                                if (initializer==null) {
									context.reportViolation(variableDefinition, "Logger not initialized");
                                } else {
									if (initializer instanceof MethodCall) {
										MethodCall mc = (MethodCall) initializer;
										if (mc.getParameters().size()==1) {
											Expression expr = (Expression) mc.getParameters().get(0);
											if ("java.lang.Class".equals(expr.getTypeSpecification().getType().getName())) {
												String paramTypeName = expr.toString();
												String classType = clazz.getFcn() + ".class";
												if (!paramTypeName.equals(classType) && !classType.endsWith("." + paramTypeName)) {

													context.reportViolation(variableDefinition, "Parameter is not correct");
												}
											}
										}
									}
								}
								return;
							}
						} catch (JselException e) {
							context.warn(variableDefinition, e);
						}			
					}
				}
				context.reportViolation(clazz);
			} catch (ClassNotFoundException e) {
				context.warn(clazz, e);
			}
		}
	}
	
	/**
	 * The fully quilified name of the Logger class.
	 */
	private String loggerClassName;
	
	/**
	 * Configures rule. Reads in the values of the parameter logger-class-name
	 * 
	 * @param name
	 *            the name of the parameter being loaded from Hammurapi
	 *            configuration
	 * @param value
	 *            the value of the parameter being loaded from Hammurapi
	 *            configuration
	 * @exception ConfigurationException
	 *                in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object value) throws ConfigurationException {
		if ("logger-class-name".equals(name)) {
			loggerClassName=value.toString();
			return true;
		} else {
			throw new ConfigurationException("Parameter " + name
					+ " is not supported.");
		}
	}

	/**
	 * Gives back the preconfigured values. 
	 */	
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Allowed logger class:\n");
		ret.append("logger-class-name: " + loggerClassName + "\n");
		return ret.toString();
	}
}
