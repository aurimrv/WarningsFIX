/*
 * Hammurapi
 * Automated Java code review system.
 * Copyright (C) 2004  Johannes Bellert
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
 * URL: http://www.hammurapi.com
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Apr 11, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorBase;
import org.hammurapi.inspectors.metrics.callertrace.CallerTraceService;
import org.hammurapi.inspectors.metrics.callertrace.MethodMap;
import org.hammurapi.inspectors.metrics.callertrace.MethodWrapper;
import org.hammurapi.inspectors.metrics.callertrace.MethodWrapperDeclaration;
import org.hammurapi.inspectors.metrics.callertrace.MethodWrapperInvoked;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Code;
import com.pavelvlasov.jsel.Constructor;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Operation;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.TypeBody;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.impl.AbstractRepositoryImpl;
import com.pavelvlasov.jsel.impl.ClassImpl;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.dom.AbstractRenderer;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.Acceptor;

/**
 * @author Johannes Bellert
 *
 */

//!! move class up to caller trace package
public class CallerTrace extends InspectorBase implements  Parameterizable {

	private CallerTraceService callerTraceService = new CallerTraceService();
	//private Repository repository = null;

	public void visit(Operation methodCall) {
		// System.out.println("*if> " + methodCall.toString());
	}
	
public void visit(MethodCall methodCall) {
    
		//System.out.println("*> " + methodCall.toString());
 
		try {
			AbstractRepositoryImpl repository = null;
			OperationInfo opi = methodCall.getProvider();
			MethodWrapperInvoked mwi = new MethodWrapperInvoked( opi, (SourceMarker)methodCall );
		/*	
			if( "java.lang.StringBuffer".equals( mwi.getDeclaringType() )
					|| "java.lang.String".equals( mwi.getDeclaringType() )
					||  ( !mwi.getDeclaringType().startsWith("java.sql.")
							&& mwi.getDeclaringType().startsWith("java.") ) ){
			*/	
			Code code = ((LanguageElement) methodCall).getEnclosingCode();
// *> childNodes.item(i).getNodeName().equalsIgnoreCase(ApplicationConstants.LOGFILENAME			
			if( code != null && code instanceof Operation ){
				Operation op = (Operation) code;
			// OperationInfo provider = new OperationInfo(code);
			TypeBody tb = code.getEnclosingType();
			// String key = provider.getDeclaringType()+ ">>" +op.getOperationSignature();
			String key = tb.getFcn() + ">>" +op.getOperationSignature();
			
			//System.out.println("methodCall " + key );
			
			MethodMap mp =  callerTraceService.getAllMethodsImplemented();
			MethodWrapperDeclaration caller = (MethodWrapperDeclaration)mp.selectMethodsWithoutHashkey( key ) ;
			
			// System.out.println("methodDecl of Caller " + caller.getMethodKey() );
			
			if ( caller != null ){
				caller.getInvokedMethods().add(mwi);
				
			} else {
				// System.out.println("!!!!! methodCall without MethodWrapperDeclaration " + key );	
			}
			
			mwi.setCallerMethod( caller );
			MethodWrapper invokeeDeclarationResolution = mwi;
			MethodWrapperDeclaration invokeeDecla = (MethodWrapperDeclaration)mp.selectMethodsWithoutHashkey( mwi.getMethodKey() ) ;
			
			if ( invokeeDecla != null ){
				// System.out.println("methodDecl of invokee " + caller.getMethodKey() );
				invokeeDecla.setCalled();
				invokeeDecla.afferentMethodCoupling++;
				//System.out.println("Ma " + invokeeDecla.afferentMethodCoupling );
				invokeeDeclarationResolution = invokeeDecla;
				
			} else {
				// System.out.println("!!!!! invokeeDecla without MethodWrapperDeclaration " + mwi.getMethodKey() );	
			}
			//!! 	search for implementor of methodCall and use this for reference
			// 		put only methodcalls as an node, if there is no declaration
			//		issue: depending on scan relationship, the method impl may not accessable
			callerTraceService.getAdjacencyMatrix().put(  invokeeDeclarationResolution.getMethodKey(), caller.getMethodKey() );
			callerTraceService.getAllMethods().put(mwi.getMethodKey(), mwi);
			callerTraceService.getAllInvokedMethodTable().put( mwi.getMethodKey(), mwi);
			}
	// if		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// System.out.println(  methodCall.toString() );
			e.printStackTrace();
		}
	}

public void visit(Constructor aConstructor) {
	try {
		 Repository repository = aConstructor.getCompilationUnit().getPackage().getRepository();
	MethodWrapperDeclaration mwd = new MethodWrapperDeclaration( aConstructor.getInfo(), (SourceMarker)aConstructor);
	
	if( this.callerTraceService.getAllInterfacesAndImplementors().containsKey( mwd.getDeclaringType() )){
		callerTraceService.getAllInterfaceOperations().add( mwd );
		// System.out.println("added 2 allInterfaceOperations "  + mwd.getMethodKey());
	}
	
	callerTraceService.getAllMethods().put(mwd.getMethodKey(),mwd);
	callerTraceService.getAllMethodsImplemented().put(mwd.getMethodKey(),mwd);
	} catch (JselException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


	public void visit(Method methodDecl) {
		try {
			 Repository repository = methodDecl.getCompilationUnit().getPackage().getRepository();
		MethodWrapperDeclaration mwd = new MethodWrapperDeclaration(methodDecl.getInfo(), (SourceMarker)methodDecl);
		
		if( this.callerTraceService.getAllInterfacesAndImplementors().containsKey( mwd.getDeclaringType() )){
			callerTraceService.getAllInterfaceOperations().add( mwd );
			// System.out.println("added 2 allInterfaceOperations "  + mwd.getMethodKey());
		}
		
		callerTraceService.getAllMethods().put(mwd.getMethodKey(),mwd);
		callerTraceService.getAllMethodsImplemented().put(mwd.getMethodKey(),mwd);
		} catch (JselException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void visit(com.pavelvlasov.jsel.Interface ifc) {
		// System.out.println("ifc: " + ifc.toString());
		
		final String searchTerm = ifc.getFcn();
		
		  Repository repository = ifc.getCompilationUnit().getPackage().getRepository();
		Collection classesOfInterest=repository.findAll(new Acceptor() {
            public boolean accept(Object element) {
                try {
                    return (element instanceof TypeDefinition && ((TypeDefinition) element).isKindOf( searchTerm));
                } catch (JselException e) {
                    // TODO You can put more proper handling here, e.g. throw JselRuntimeException.
                    e.printStackTrace();
                    return false;
                }
            }
  });
		  if (classesOfInterest != null && classesOfInterest.size()>0 ){
		  	// System.out.println("<~ " + ifc.getName() );
		  	this.callerTraceService.getAllInterfacesAndImplementors().put( ifc.getName(), classesOfInterest );
		  }
		  
	}
	
	
	//!! should be move to adj matrox 
	/*
	 * side effect: changed left-hand side node (invokees) of adjacence edges
	 */
	
	private void resolveInvokeesImplementors(){
		callerTraceService.resolveInvokeesImplementors();
	}

		/* 
		 * 	Construct a adjacenz matrix edge between each 
		 * 			interface operation and all the implementor operations (and only them)
		 */

	private void handleInterfaces() {

		 for( int i=0; i<this.callerTraceService.getAllInterfaceOperations().size(); i++){
		 	MethodWrapperDeclaration interFaceOp =	(MethodWrapperDeclaration)this.callerTraceService.getAllInterfaceOperations().elementAt(i);
		 	String keyIf = interFaceOp.getDeclaringType()+ ">>" +interFaceOp.getSignature();
		 	 // System.out.println( "keyIf " + keyIf );
			Enumeration enumIF = this.callerTraceService.getAllInterfacesAndImplementors().keys();
			while ( enumIF.hasMoreElements() ){
				
				String key = (String)enumIF.nextElement();
				 // System.out.println( "*** " + key);
				
				Collection classes = 	(Collection)this.callerTraceService.getAllInterfacesAndImplementors().get(key);
				Iterator itcls = classes.iterator();
					while ( itcls.hasNext() ){
						// String str = (String)itcls.next();
						Object obj = itcls.next();
						if( obj instanceof ClassImpl){
							 // System.out.println("+++ " + ((ClassImpl) obj).getFcn() );
							String keyImpl = ((ClassImpl) obj).getFcn()+ ">>" + interFaceOp.getSignature(); 
							 // System.out.println("+++ keyImpl" + keyImpl );
							// String keyImpl = ((ClassImpl) obj).getFcn()+ ">>" +interFaceOp.getName();
							// callerTraceService.getAdjacencyMatrix().put(keyIf, keyImpl );
							 // System.out.println(this.callerTraceService.getAllMethodsImplemented());
							 MethodWrapperDeclaration mwif = (MethodWrapperDeclaration)this.callerTraceService.getAllMethodsImplemented().selectMethodsWithoutHashkey( keyIf ) ;
							 MethodWrapperDeclaration mwd = (MethodWrapperDeclaration)this.callerTraceService.getAllMethodsImplemented().selectMethodsWithoutHashkey( keyImpl ) ;
							 // System.out.println( "mwd.getMethodKey() " + mwif.getMethodKey() );
							callerTraceService.getAdjacencyMatrix().put(mwd.getMethodKey(), mwif.getMethodKey() );
						}
					}
			}
		 	
		 }
	}

	private void aggregate(){
		// System.out.println("aggregate");
		resolveInvokeesImplementors();
		handleInterfaces();
		
		callerTraceService.init();
		callerTraceService.traceCaller();
	}

	public void leave(Repository repository) {
		aggregate();

		context.annotate( new LinkedAnnotation() {
			private String path;

			public String getName() {
				return getContext().getDescriptor().getName();
			}

			public String getPath() {
				return path;
			}

			public void render(AnnotationContext context) throws HammurapiException {
				String errorCausingFile = "";

				// Output images here. See AnnotationTest for details.

				class CallerTraceRenderer extends AbstractRenderer {
					CallerTraceRenderer() {
						super(new RenderRequest(CallerTrace.this));
					}

					public Element render(Document document)   {
						CallerTrace ctInspector=(CallerTrace) request.getRenderee();

						Element ctInspectorElement=document.createElement("CallerTrace");
						 callerTraceService.toDom( document , ctInspectorElement) ;

						
						return ctInspectorElement;
					}
				} //-- end class NcssInspectorRenderer


				AnnotationContext.FileEntry fileEntry=context.getNextFile(context.getExtension());
				path=fileEntry.getPath();
			//	System.out.println( ".> " +this.getPath().toString() );

				AnnotationContext.FileEntry fileEntryXml=context.getNextFile(".xml");
				try {
					CallerTraceRenderer renderer=new CallerTraceRenderer();
					FileOutputStream out=new FileOutputStream(fileEntry.getFile());

					renderer.setEmbeddedStyle(context.isEmbeddedStyle());
					try {
						errorCausingFile = fileEntry.getFile().getAbsolutePath();
						renderer.render(context.getParameter("style")==null ? null : new FileInputStream(context.getParameter("style").toString()), out);
					} finally {
						out.close();
					}
						//-- write a XML file for other XSL usage
						FileOutputStream outXml=new FileOutputStream(fileEntryXml.getFile());

					try {
						errorCausingFile = fileEntryXml.getFile().getAbsolutePath();
						//-- write a XML file for other XSL usage
						renderer.setEmbeddedStyle(false);
						renderer.render(outXml);
//						renderer.setPrettyPrint( true );
//						InputStream inStream=getClass().getClassLoader().getResourceAsStream(xmlResourceName);
//						renderer.render(inStream, outXml);

					} finally {
						outXml.close();
					}
				} catch (Exception e) {
					throw new HammurapiException("Can't save "+ errorCausingFile +". " +e.getMessage()  );
				}
			}
			public Properties getProperties() {
				Properties ret=new Properties();
				ret.setProperty("left-panel", "yes");
				ret.setProperty("target", "Caller Trace");
				return ret;
			}
		});
	}


	/**
     * Configures the rule. Reads in the values of the parameters XXX and
     * class-max-complexity.
     *
     * @param name the name of the parameter being loaded from Hammurapi configuration
     * @param value the value of the parameter being loaded from Hammurapi configuration
     * @exception ConfigurationException in case of a not supported parameter
     */
    public boolean setParameter(String name, Object value) throws ConfigurationException {
        if (true) {
			return true;
        } else {
            throw new ConfigurationException("Parameter '"+name+"' is not supported");
        }
    }
}

