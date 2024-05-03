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

import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Operation;
import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.statements.SynchronizedStatement;
import com.pavelvlasov.util.Visitor;


/**
 * ER-103
 * Minimize synchronization in Servlets
 * @author  Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class SynchronizationInServletsRule extends InspectorBase  {
    
	private static final String JAVAX_SERVLET_SERVLETRESPONSE = "javax.servlet.ServletResponse";
    private static final String JAVAX_SERVLET_SERVLETREQUEST = "javax.servlet.ServletRequest";
    private static final String JAVAX_SERVLET_SERVLET = "javax.servlet.Servlet";
    
    public void visit(SynchronizedStatement element) {
        if (((LanguageElement) element).getEnclosingCode() instanceof Method) {
            checkCallers((Method) ((LanguageElement) element).getEnclosingCode());
        }		
	}
	
	private boolean j2eeChecked=false;
	
	public void visit(Method element) {
		if (element.getModifiers().contains("synchronized")) {		    
		    checkCallers(element);		    
		}
	}

    /**
     * @param method
     */
    private void checkCallers(Method method) {
	    if (!j2eeChecked) {
	        j2eeChecked=true;
	        try {
	            method.getRepository().loadClass(JAVAX_SERVLET_SERVLET);
	        } catch (ClassNotFoundException e) {
	            disable(e.getMessage());
	            return;
	        }
	    }
	    
        try {
            /* this buffer will accumulate list of servlet
             * methods invoking this method directly or indirectly 
             */
            final StringBuffer sb=new StringBuffer();
            
            // Check whether this method is invoked from servlet 
            // methods with 2 parameters of type ServletRequest and ServletResponse.
            method.visitCallers(new Visitor() {

                public boolean visit(Object target) {
                    Operation.Invocation inv=(Operation.Invocation) target;
                    if (inv.getCaller() instanceof Method) {
                        Method caller=(Method) inv.getCaller();
                        try {
                            if (caller.getEnclosingType().isKindOf(JAVAX_SERVLET_SERVLET)
                                    && caller.getParameters().size()==2
                                    && ((Parameter) caller.getParameters().get(0)).isTypeOf(JAVAX_SERVLET_SERVLETREQUEST)
                                    && ((Parameter) caller.getParameters().get(0)).isTypeOf(JAVAX_SERVLET_SERVLETRESPONSE)) {
                                if (sb.length()>0) {
                                    sb.append(", ");
                                }
                                sb.append(caller.getEnclosingType().getFcn());
                                sb.append(".");
                                sb.append(caller.getOperationSignature());
                            }
                        } catch (JselException e) {
                            context.warn(caller, e);
                        }
                    }
                    return true;
                }
                
            });
            
        	if (sb.length()>0) {
        	    context.reportViolation(
        	            method, 
        	            context.getDescriptor().getMessage()
        	            +". Synchronized method "
        	            +method.getOperationSignature() 
        	            + " is invoked from servlet method(s) "
        	            + sb.toString());
        	}
        } catch (JselException e) {
            context.warn(method, e);
        }
    }
}
