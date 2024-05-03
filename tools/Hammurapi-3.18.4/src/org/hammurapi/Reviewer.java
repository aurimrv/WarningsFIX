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

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hammurapi.results.Annotation;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.impl.CompilationUnitImpl;
import com.pavelvlasov.logging.Logger;
import com.pavelvlasov.persistence.CompositeStorage;
import com.pavelvlasov.persistence.FileStorage;
import com.pavelvlasov.persistence.MemoryStorage;
import com.pavelvlasov.review.Signed;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.ClassResourceLoader;
import com.pavelvlasov.util.DispatchingVisitor;
import com.pavelvlasov.util.VisitorExceptionSink;
import com.pavelvlasov.util.VisitorStack;
import com.pavelvlasov.util.VisitorStackSource;

/**
 * This class is to be used by IDE plugins
 * @author Pavel Vlasov
 * @version $Revision: 1.8 $
 */
public class Reviewer {

	private Logger logger;
	private InspectorSet inspectorSet;
	private WaiverSet waiverSet=new WaiverSet();
	private CompositeStorage storage=new CompositeStorage();
	private DispatchingVisitor visitor;
	private SessionImpl session=new SessionImpl();
	private int tabSize=8;
	
	private class ReviewerInspectorContext extends InspectorContextBase {

		public ReviewerInspectorContext(InspectorDescriptor descriptor, Logger logger, VisitorStackSource visitorStackSource) {
			super(descriptor, logger, visitorStackSource, session);
		}

		/**
		 * 
		 * @param source
		 * @param message
		 */
		public void reportViolation(SourceMarker source, String message) {
			Violation violation=new SimpleViolation(detach(source), message, descriptor);  
			addViolation(violation);
		}

		public void annotate(Annotation annotation) {
			addAnnotation(annotation);
		}

		public void addMetric(SourceMarker source, String name, double value) {
			// Ignore
		}

		/**
		 * Report warning
		 * @param source
		 * @param message
		 */
		public void warn(SourceMarker source, String message) {
			Violation violation=new SimpleViolation(detach(source), message, descriptor);  
			addWarning(violation);		
			if (source==null) {
			    System.err.println("WARNING: "+message);
			} else {
			    System.err.println("WARNING at "+source.getSourceURL()+" "+source.getLine()+":"+source.getColumn()+" : "+message);			    
			}
		}

		/**
		 * Report warning
		 * @param source
		 */
		public void warn(SourceMarker source, Throwable th) {
			Violation violation=new SimpleViolation(detach(source), th.toString(), descriptor);  
			addWarning(violation);
			// TODO better warning handling here
			if (source==null) {
			    System.err.println("WARNING: "+th);
			} else {
			    System.err.println("WARNING at "+source.getSourceURL()+" "+source.getLine()+":"+source.getColumn()+" : "+th);
			}
			th.printStackTrace();
		}

		/**
		 * Creates a waiver for inspector with a given key
		 * @param inspectorKey 
		 */
		public void waive(Signed signed, final String inspectorKey) {
			final String iName=descriptor.getWaivedInspectorName(inspectorKey);
			if (iName==null) {
				warn(signed instanceof SourceMarker ? (SourceMarker) signed : null, descriptor.getName()+": Inspector with key '"+inspectorKey+"' not found.");
			} else {
				final String signature=signed==null ? null : signed.getSignature();			
				final Set signatures=new HashSet();
				if (signature!=null) {
					signatures.add(signature);
				}
				
				if (Reviewer.this.logger!=null) {
					Reviewer.this.logger.debug(this, "Inspector "+getDescriptor().getName()+" autowaives "+iName+" at "+signature);				
				}
				
				Waiver waiver=new Waiver() {
					boolean active=true;
		
					public String getInspectorName() {
						return iName;
					}
		
					public boolean waive(Violation violation, boolean peek) {
						if (iName.equals(violation.getDescriptor().getName())) {
							if (signature==null) {
								return true;
							} else if (violation.getSource() instanceof Signed && signature.equals(((Signed) violation.getSource()).getSignature())) {
								if (!peek) {
									active=false;
								}
								return true;								
							}
						}
						return false;
					}
					
					public Date getExpirationDate() {
						return null;
					}
		
					public String getReason() {
						return descriptor.getWaiveReason(inspectorKey);
					}
		
					public boolean isActive() {
						return active;
					}
		
					public Collection getSignatures() {
						return signatures;
					}
				};
				waiverSet.addWaiver(waiver, date);
			}				
		}
	}

	public Reviewer(
				final Logger logger,
				boolean embeddedInspectors,
				Collection inspectors,
				Collection waivers) throws HammurapiException {
		this.logger=logger;
		final VisitorStack[] visitorStack={null};
		
		final VisitorStackSource delegatingVisitorStackSource = new VisitorStackSource() {

			public VisitorStack getVisitorStack() {
				return visitorStack[0];
			}
		}; 
		
		inspectorSet=new InspectorSet(
			new InspectorContextFactory() {
				public InspectorContext newContext(final InspectorDescriptor descriptor, final Logger logger) {
					return new ReviewerInspectorContext(descriptor, logger, delegatingVisitorStackSource);
				}
			},
			logger);
		
		if (embeddedInspectors) {
			InputStream inspectorStream=HammurapiTask.class.getResourceAsStream("inspectors.xml");
			if (inspectorStream==null) {
				throw new HammurapiException("Cannot load embedded inspectors");
			}
			
			DomInspectorSource source=new DomInspectorSource(inspectorStream, "Hammurapi.jar");
			source.loadInspectors(inspectorSet);
		}
		
		if (inspectors!=null) {
			Iterator it=inspectors.iterator();
			while (it.hasNext()) {
				try {
					inspectorSet.addDescriptor((InspectorDescriptor) it.next());
				} catch (ConfigurationException e) {
					throw new HammurapiException(e);
				}
			}
		}

		if (waivers!=null) {
			Date now=new Date();
			Iterator it=waivers.iterator();
			while (it.hasNext()) {
				waiverSet.addWaiver((Waiver) it.next(), now);
			}
		}		
		
		storage.addStorage("file", new FileStorage(new File(System.getProperties().getProperty("java.io.tmpdir"))));
		storage.addStorage("memory", new MemoryStorage());	
		session.setStorage(storage);
		
		try {
			Collection ic = inspectorSet.getInspectors();
			
			visitor = new DispatchingVisitor(
					ic, 
					new VisitorExceptionSink() {
						public void consume(DispatchingVisitor dispatcher, Object visitor, Method method, Object visitee, Exception e) {
							if (logger!=null) {
								logger.warn(visitee, "Exception: "+e);
							}
							
							e.printStackTrace();
			
							getRequest().onWarning(new SimpleViolation(visitee instanceof SourceMarker ? (SourceMarker) visitee : null, "Exception: "+e, null));
						}			
					}, 
					null);
			session.setInspectors(inspectorSet);
			visitorStack[0]=visitor.getVisitorStack();
		} catch (ConfigurationException e) {
			throw new HammurapiException(e);
		}	
		
		session.setVisitor(visitor);
	}
	
	public Reviewer(
			final Logger logger,
			boolean embeddedInspectors,
			InspectorSource inspectorSource,
			WaiverSource waiverSource) throws HammurapiException {
		this.logger=logger;
		final VisitorStack[] visitorStack={null};
		
		final VisitorStackSource delegatingVisitorStackSource = new VisitorStackSource() {
	
			public VisitorStack getVisitorStack() {
				return visitorStack[0];
			}
		}; 
		
		inspectorSet=new InspectorSet(
			new InspectorContextFactory() {
				public InspectorContext newContext(final InspectorDescriptor descriptor, final Logger logger) {
					return new ReviewerInspectorContext(descriptor, logger, delegatingVisitorStackSource);
				}
			},
			logger);
		
		if (embeddedInspectors) {
			ClassResourceLoader crl=new ClassResourceLoader(TaskBase.class);
			InputStream inspectorStream=crl.getResourceAsStream(null,null,"xml");
			if (inspectorStream==null) {
				throw new HammurapiException("Cannot load embedded inspectors");
			}
			
			DomInspectorSource source=new DomInspectorSource(inspectorStream, "Hammurapi.jar");
			source.loadInspectors(inspectorSet);
		}
		
		if (inspectorSource!=null) {
			inspectorSource.loadInspectors(inspectorSet);
		}
	
		if (waiverSource!=null) {
			Date now=new Date();
			waiverSource.loadWaivers(waiverSet, now);
		}		
		
		storage.addStorage("file", new FileStorage(new File(System.getProperties().getProperty("java.io.tmpdir"))));
		storage.addStorage("memory", new MemoryStorage());	
		session.setStorage(storage);
		
		try {
			Collection ic = inspectorSet.getInspectors();
			
			visitor = new DispatchingVisitor(
					ic, 
					new VisitorExceptionSink() {
						public void consume(DispatchingVisitor dispatcher, Object visitor, Method method, Object visitee, Exception e) {
							if (logger!=null) {
								logger.warn(visitee, "Exception: "+e);
							}
							
							e.printStackTrace();
			
							getRequest().onWarning(new SimpleViolation(visitee instanceof SourceMarker ? (SourceMarker) visitee : null, "Exception: "+e, null));
						}			
					}, 
					null);
			session.setInspectors(inspectorSet);
			visitorStack[0]=visitor.getVisitorStack();
		} catch (ConfigurationException e) {
			throw new HammurapiException(e);
		}	
		
		session.setVisitor(visitor);
	}
	
	private ThreadLocal requestTL=new ThreadLocal();
	
	private ReviewRequest getRequest() {
		return (ReviewRequest) requestTL.get();
	}
	
	public void process(final ReviewRequest request) throws HammurapiException {
		requestTL.set(request);
		try {
			CompilationUnit cu=new CompilationUnitImpl(request.getSource(), request.getRootDir(), request.getName(), tabSize, request.getClassLoader(), logger);
			cu.accept(visitor);											
		} catch (JselException e) {
			throw new HammurapiException(e);
		} finally {
			requestTL.set(null);
		}		
	}

	private void addViolation(Violation violation) {
		getRequest().onViolation(violation);		
	}

	/**
	 * @param annotation
	 */
	private void addAnnotation(Annotation annotation) {
		// Annotations are ignored
	}

	/**
	 * @param violation
	 */
	private void addWarning(Violation violation) {
		getRequest().onWarning(violation);		
	}
		
	public void shutdown() throws HammurapiException {
		try {
			session.shutdown();
		} catch (SQLException e) {
			throw new HammurapiException("Cannot shutdown session", e);
		}
	}
	
	public static void main(String[] arg) throws Exception {
		final File source=new File("..\\UmlApi\\src\\com\\pavelvlasov\\uml\\Operation.java");
		System.out.println(source.getAbsolutePath());
		ClassLoader classLoader=new URLClassLoader(new URL[] { new File("..\\UmlApi\\bin").toURL()}, Reviewer.class.getClassLoader());
		
		final Reader sr=new FileReader(source);
		Reviewer reviewer=new Reviewer(null, true, (InspectorSource) null, null);
		reviewer.process(new ReviewRequestBase(classLoader) {

			public Reader getSource() {
				return sr;
			}

			public String getName() {
				return "com/pavelvlasov/uml/Operation.java";
			}

			public void onViolation(Violation violation) {
				System.out.println("VIOLATION: "+violation.getDescriptor().getName()+": "+violation.getMessage());
			}

			public void onWarning(Violation warning) {
				System.out.println("WARNING: "+warning.getMessage());
			}

			public File getRootDir() {
				return new File("..\\UmlApi\\src");
			}
		});
	}	
	
	/**
	 * @return Returns the tabSize.
	 */
	public int getTabSize() {
		return tabSize;
	}
	
	/**
	 * @param tabSize The tabSize to set.
	 */
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}
}
