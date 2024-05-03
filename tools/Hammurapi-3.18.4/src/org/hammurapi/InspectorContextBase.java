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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.Package;
import com.pavelvlasov.logging.Logger;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.VisitorStack;
import com.pavelvlasov.util.VisitorStackSource;
import com.pavelvlasov.wrap.WrapperHandler;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.7 $
 */
public abstract class InspectorContextBase implements InspectorContext {
	protected InspectorDescriptor descriptor;
	public final static String GENERIC_MESSAGE="SimpleViolation. Message not found";
	protected Logger logger;
	private VisitorStackSource visitorStackSource;
	private SessionImpl session;
	
	public InspectorDescriptor getDescriptor() {
		return descriptor;	
	}
	
	/**
	 * Reports violation with a message from descriptor
	 * @param source
	 */
	public void reportViolation(SourceMarker source) {
		reportViolation(source, descriptor.getMessage());
	}
	
	/**
	 * Reports violation with a message from descriptor
	 * @param source
	 */
	public void reportViolationEx(SourceMarker source, String messageKey) {
		reportViolation(source, descriptor.getMessage(messageKey));
	}
	
	/**
	 * @param source
	 */
	public SourceMarker detach(final SourceMarker source) {
	    if (source==null) {
	        return null;
	    }
	    
		Class sourceClass=source.getClass();
		// TODO - real detach through findBySignature
		final String[] sourceUrl = {source.getSourceURL()};
	    List stack = getVisitorStack().getStack(CompilationUnit.class);
	    if (!stack.isEmpty()) {
			CompilationUnit cu=(CompilationUnit) stack.get(0);
			Package pkg=cu.getPackage();
			if (pkg.getName().length()==0) {
				sourceUrl[0]=cu.getName();
			} else {
				sourceUrl[0]=pkg.getName().replace('.', '/')+'/'+cu.getName();
			}
	    }
		
		return (SourceMarker) Proxy.newProxyInstance(
				sourceClass.getClassLoader(), 
				WrapperHandler.getClassInterfaces(sourceClass),
				new InvocationHandler() {

					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (SourceMarker.class.getMethod("getSourceURL", null).equals(method)) {
							return sourceUrl[0];
						}
						
						return method.invoke(source, args);
					}
					
				});				
	}
	
	/**
	 * Formats message taken from InspectorDescriptor with parameters.
	 * @param source
	 * @param params
	 */
	public void reportViolation(SourceMarker source, Object[] params) {
		String message = descriptor.getMessage();
		if (message==null) {
			warn(source, "Message not found for inspector "+getDescriptor().getName());
			message=GENERIC_MESSAGE;
		}
		reportViolation(source, MessageFormat.format(message, params));
	}
	
	/**
	 * Formats message taken from InspectorDescriptor with parameters.
	 * @param source
	 * @param params
	 */
	public void reportViolationEx(SourceMarker source, Object[] params, String messageKey) {
		String message = descriptor.getMessage(messageKey);
		if (message==null) {
			warn(source, "Message with key '"+messageKey+"' not found for inspector "+getDescriptor().getName());
			message=GENERIC_MESSAGE;
		}
		reportViolation(source, MessageFormat.format(message+" for key '"+messageKey+"'", params));
	}
	
	/**
	 * @param descriptor
	 * @param session
	 */
	public InspectorContextBase(
			InspectorDescriptor descriptor, 
			Logger logger,
			VisitorStackSource visitorStackSource, SessionImpl session) {
		super();
		this.descriptor = descriptor;
		this.logger=logger;
		this.visitorStackSource=visitorStackSource;
		this.session=session;
		if (session!=null) {
			session.addContext(descriptor.getName(), this);
		}
	}
	
	/**
	 * Outputs a message to the log
	 * @param source
	 * @param message
	 */
	public void info(SourceMarker source, String message) {
		if (logger!=null) {
			logger.info(this, loggerMessage(source, message));
		}
	}
	
	/**
	 * Outputs a message to the log
	 * @param source
	 * @param message
	 */
	public void debug(SourceMarker source, String message) {
		if (logger!=null) {
			logger.debug(this, loggerMessage(source, message));
		}
	}
	
	/**
	 * Outputs a message to the log
	 * @param source
	 * @param message
	 */
	public void verbose(SourceMarker source, String message) {
		if (logger!=null) {
			logger.verbose(this, loggerMessage(source, message));
		}
	}
	
	/**
	 * @param source
	 * @param message
	 * @return formatted message
	 */
	private String loggerMessage(SourceMarker source, String message) {
		return descriptor.getName()+" "+source.getSourceURL()+" "+source.getLine()+":"+source.getColumn()+" - "+message;
	}

	protected static final Date date = new Date();	
	
	public VisitorStack getVisitorStack() {
		return visitorStackSource==null ? null : visitorStackSource.getVisitorStack();
	}
	
	private Map attributes=new HashMap();
	
	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}
	
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}
	
	public Object removeAttribute(Object key) {
		return attributes.remove(key);
	}
	
	public Session getSession() {
		return session;
	}
}
