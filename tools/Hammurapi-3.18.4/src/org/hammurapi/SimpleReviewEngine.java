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

import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.hammurapi.results.AggregatedResults;
import org.hammurapi.results.ResultsFactory;

import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.DispatchingVisitor;
import com.pavelvlasov.util.OrderedTarget;
import com.pavelvlasov.util.VisitorExceptionSink;
import com.pavelvlasov.util.VisitorStack;
import com.pavelvlasov.util.VisitorStackSource;

/**
 * Pool of review threads. Delegates review work to threads.
 * @author Pavel Vlasov	
 * @version $Revision: 1.8 $
 */
public class SimpleReviewEngine implements VisitorStackSource {
	private DispatchingVisitor visitor;

	public SimpleReviewEngine(Collection inspectors, final HammurapiTask task) {
		VisitorExceptionSink esink=new VisitorExceptionSink() {

			public void consume(DispatchingVisitor dispatcher, Object visitor, Method method, Object visitee, Exception e) {
				task.log("WARN: Exception in "+visitee, Project.MSG_WARN);
				e.printStackTrace();
				
				AggregatedResults results=ResultsFactory.getThreadResults();
				if (task.failOnFirstException) {
					throw new BuildException("Cause: "+e, e);
				} else if (results==null || e instanceof HammurapiNonConsumableException) {
					task.setHadExceptions();
				} else {
					results.addWarning(new SimpleViolation(visitee instanceof SourceMarker ? (SourceMarker) visitee : null, "Exception "+e, null));
				}
				
				if (task.evictBadInspectors) {
					dispatcher.remove(visitor);
					if (visitor instanceof Inspector) {
						String name=((Inspector) visitor).getContext().getDescriptor().getName();
						results.addWarning(new SimpleViolation(visitee instanceof SourceMarker ? (SourceMarker) visitee : null, "Inspector "+name+" threw "+e+" and has been disabled", null));
					}
				}
			}
		};
		visitor = new DispatchingVisitor(inspectors, esink, getListener(task));			
	}
	
	public void review(Repository repository) {
		repository.accept(visitor);
	}
	
	protected static DispatchingVisitor.Listener getListener(final HammurapiTask task) {
		if (task.getDebugType()==null) {
			return null;
		}
		
		return new DispatchingVisitor.Listener() {
			private boolean isEnabled=true;
			private Class type;
			
			private void log(String message) {
				task.log(message, Project.MSG_INFO);
			}
			
			boolean isListeningFor(Object o) {
				if (isEnabled) {
					if (type==null) {
						try {
							type=o.getClass().getClassLoader().loadClass(task.getDebugType());
						} catch (ClassNotFoundException e) {
							task.log(e.toString(), Project.MSG_WARN);
							isEnabled=false;
							return false;
						}
					}
					return type.isAssignableFrom(o.getClass());
					
				}
				
				return false;					
			}
			
			public void onInvocationRegistration(Object target, Method method) {
				log("\tDispatch invocaton registration");
				log("\t\tTarget type: "+target.getClass());
				log("\t\tTarget method: "+method);
			}
			
			public void onInvocation(Object target, Method method, Object visitable) {
				if (isListeningFor(visitable)) {
					log("Dispatch invocation");
					log("\tTarget type: "+target.getClass());
					log("\tTarget method: "+method);
					log("\tVisitable type: "+visitable.getClass());
					log("\tVisitable: "+visitable);
				}
			}
			
			public void onVisit(Object target) {
				if (isListeningFor(target)) {
					log("Dispatch visit");
					log("\tVisitable type: "+target.getClass());
					log("\tVisitable: "+target);
				}
			}
			
			public void onLeave(Object target) {
				if (isListeningFor(target)) {
					log("Dispatch leave");
					log("\tVisitable type: "+target.getClass());
					log("\tVisitable: "+target);
				}
			}
			
			public void onTargetRegistration(Object target) {
				log("Dispatch type registration");
				log("\tTarget type: "+target.getClass());
				if (target instanceof OrderedTarget) {
					log("\tOrder: "+((OrderedTarget) target).getOrder());
				}
			}

			public void noInvocationsWarning(Object target) {
				log("WARNING: No invocations for type: "+target.getClass());					
			}

			public void onFilterRegistration(Method filter, Method target) {
				log("\tFilter registration");
				log("\t\tFilter method: "+filter);
				log("\t\tTarget method: "+target);
			}
   };
	}

	public VisitorStack getVisitorStack() {
		return visitor.getVisitorStack();
	}

	public DispatchingVisitor getVisitor() {
		return visitor;
	}		
}
