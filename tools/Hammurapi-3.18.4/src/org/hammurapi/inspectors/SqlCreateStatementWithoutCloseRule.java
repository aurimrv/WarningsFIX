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
 * URL: http://www.hammurapi.org
 * e-Mail: CraftOfObjects@gmail.com

 */

package org.hammurapi.inspectors;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Code;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Operation;
import com.pavelvlasov.jsel.OperationInfo;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Expression;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.PlainAssignment;
import com.pavelvlasov.jsel.expressions.TypeCast;
import com.pavelvlasov.jsel.statements.ReturnStatement;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.Visitor;

/**
 *  Fix 5: Ignore all createStatement & preparedStatements method calls which are NOT provided by java.sql.Connection
 *  Fix 4: handle TypeCast of parameters in "releaseSafe" helper methods
 *  Fix 3: find multiple occurance of a "releaseSafe" helper methods
 *  Fix 2: Count create & close statements in a method and report violation if close less than create
 *  Fix 1: ReturnStatement added in visit(MethodCall)
 */
public class SqlCreateStatementWithoutCloseRule extends InspectorBase implements
		Parameterizable {

	private java.util.Set releaseResourceMethodList = new java.util.HashSet();

	boolean ret;

	//--  State Engine
	private String currentStatementVarDef = "";

	private Vector createStatementList = new Vector();
	private Vector closeStatementList = new Vector();
	private SourceMarker compilationUnitSourcingMarker = null;
	private SourceMarker methodSourcingMarker = null;

	public void visit(CompilationUnit cu) {
	    compilationUnitSourcingMarker = (SourceMarker)cu;
	}

	public void visit(Method methodDecl) {

	    checkNumberOfCreateCloseAPI();   // initalize State Engine

	    //--- re-initialize
		createStatementList = new Vector();
		closeStatementList = new Vector();
		methodSourcingMarker = (SourceMarker)methodDecl;
	}

	public void visit(MethodCall methodCall) {


		//System.out.println( methodCall.getMethodName() );

		checkForReleaseSafeMethod( methodCall );
		checkForCloseMethod( methodCall );
		checkForCreateMethod( methodCall );
	}

    private void checkForReleaseSafeMethod(MethodCall methodCall) {
        boolean ret = false;
        try {
            int i = 0;
            Object[] releaseResourceMethodStrings = releaseResourceMethodList
                    .toArray();
            while (!ret && i < releaseResourceMethodStrings.length) {

                String releaseResourceMethodName = (String) releaseResourceMethodStrings[i];
                i++;
                // System.out.println(releaseResourceMethodName  +" -- " +  methodCall.getMethodName() );
                if (releaseResourceMethodName
                        .equals(methodCall.getMethodName())) {
                    //-- search for parameters
                    List parameterList = methodCall.getParameters();
                    Iterator pit = parameterList.iterator();
                    while (pit.hasNext() && !ret) {
                        Object parameter = pit.next();

                        //!! refactoring: code doublet with method CloseVisitor
                        if (parameter instanceof TypeCast) {

                            if ("Statement".equals( ((TypeCast)parameter).getTypeSpecification().toString())
                                ||    "PreparedStatement".equals( ((TypeCast)parameter).getTypeSpecification().toString())   ){


                                methodSourcingMarker = (SourceMarker) methodCall;
                                closeStatementList.add(methodCall);
                                ret = true;
                            }

                        } else if (parameter instanceof Ident) {
                            Ident p = (Ident) parameter;
                            String paramName = p.toString();
                            String paramTypDef = p.getTypeSpecification().getName();
                            // System.out.println( "paramName " + paramName );
                            // System.out.println( "paramTypDef " + paramTypDef );
                            // System.out.println( "p.getTypeSpecification " + p.getTypeSpecification().getName() );
                           	if (("java.sql.Statement".equals(paramTypDef)
						//        || "java.sql.PreparedStatement".equals(p.getTypeSpecification().getName()))
                           	        || "java.sql.PreparedStatement".equals(paramTypDef))
									// && paramName.equals(currentStatementVarDef)
									) {

                                methodSourcingMarker = (SourceMarker) methodCall;
                                closeStatementList.add(methodCall);
                                ret = true;
                            }
                        }
                    }
                }
            }
        } catch (JselException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void checkForCloseMethod(MethodCall methodCall) {
    if ("close".equals(methodCall.getMethodName()) ){

        try {
            OperationInfo opi = methodCall.getProvider();

            if("java.sql.Statement".equals(opi.getDeclaringType().getName()) ){
                methodSourcingMarker = (SourceMarker)methodCall;
                this.closeStatementList.add( methodCall );
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //!! no provider type check ??
    }
    }

    private void checkForCreateMethod(MethodCall methodCall) {
        Code code = ((LanguageElement) methodCall).getEnclosingCode();
        LanguageElement parentLangElem = ((LanguageElement) methodCall)
                .getParent();

        if ("createStatement".equals(methodCall.getMethodName())
                || "prepareStatement".equals(methodCall.getMethodName())) {

            
            

            try {

                //!! Ugly Quick Hack
                if (parentLangElem instanceof VariableDefinition) {
                    VariableDefinition varDef = (VariableDefinition) parentLangElem;
                    currentStatementVarDef = varDef.getName();
                } else if (parentLangElem instanceof Ident) {
                    Ident id = (Ident) parentLangElem;
                    final Object provider = id.getProvider();
                    if (provider != null
                            && (provider instanceof VariableDefinition)) {
                        currentStatementVarDef = ((VariableDefinition) provider)
                                .getName();
                    }
                } else if (parentLangElem instanceof TypeCast) {
                    VariableDefinition grandpaLangElem = (VariableDefinition)((LanguageElement) parentLangElem)
                    .getParent();
                    
                    currentStatementVarDef = grandpaLangElem.getName(); 
                    
                } else if (parentLangElem instanceof PlainAssignment) {
                    PlainAssignment pa = (PlainAssignment) parentLangElem;


					Collection lt  = pa.getOperands();
                    if ( !lt.isEmpty() ){
                    		Iterator it = lt.iterator();
                        	String str = ((Ident)it.next()).getText();
                            currentStatementVarDef = str;
                    } else {
                        //!!
                        context.warn(parentLangElem, "!!!!!!!!! currentStatementVarDef could not be determined for " + parentLangElem);
                    }
                }

                else if (parentLangElem instanceof ReturnStatement) {
                    // Fix 1
                    ReturnStatement pa = (ReturnStatement) parentLangElem;
                    Expression ex = pa.getExpression();
                    if (ex instanceof MethodCall) {

                        //--- this is not the Statement name but we can use it
                        // instead of a "NA" or null value.
                        currentStatementVarDef = ((LanguageElement) ex)
                                .getAst().getFirstToken().getText();

                        //!! only method calls referenced to a var are handled
                        // .. recurively resolution of nested method calls
                        // needed.
                        // currentStatementVarDef =
                        // methodCallX.getTypeSpecification().getName().toString();
                    }

                } else {
                    context.warn((SourceMarker) methodCall,
                            " methodCall has no varDef ");
                }

                // System.out.println(" currentStatementVarDef " +
                // currentStatementVarDef);
                if (code != null) {
                    Operation op = (Operation) code;
                    OperationInfo opi = methodCall.getProvider();
                    try {
                        if ("java.sql.Connection".equals(opi.getDeclaringType()
                                .getName())) {
                            
                            createStatementList.add(methodCall);                
                            //--reinit
                            ret = false;
                            op.accept(new CloseVisitor());
                            if (!ret) {
                                code.accept(new CloseVisitor());
                            }

                            if (!ret) {
                                context
                                        .reportViolation((SourceMarker) methodCall);
                            }
                        }
                    } catch (JselException e) {
                        context.warn(op, e);
                    }

                }
            } catch (JselException e) {
                context.warn((SourceMarker) methodCall, e);
            }
        }
    }
	public void checkNumberOfCreateCloseAPI (){
	    if( closeStatementList.size()< createStatementList.size() ){
	        StringBuffer str = new StringBuffer();
        str.append( "Found " + createStatementList.size() );
        str.append( " calls of create Statements but only " );
        str.append( closeStatementList.size() );
        str.append( " close() calls" );

        // context.reportViolation( this.methodSourcingMarker , str.toString() );
      context.getSession().getContext("ER-209").reportViolation( this.methodSourcingMarker , str.toString() );

	    }
	}

    public void leave(Repository repo) {
              this.checkNumberOfCreateCloseAPI();
    }


	class CloseVisitor implements Visitor {

		/**
		 * Demonstrates usage of VisitorStack
		 */
		/*
		 * public void visit(MethodCall mc) throws JselException { if
		 * (VisitorStack.getThreadInstance().isIn(ForStatement.class)) {
		 * context.reportViolation((SourceMarker) mc, "Method call from for
		 * loop"); } }
		 */
		public boolean visit(Object target) {
			try {
				if (target instanceof MethodCall) {
					MethodCall subMethCall = (MethodCall) target;

					//-- check for close
					if ("close".equals(subMethCall.getMethodName())) {
						OperationInfo opi = subMethCall.getProvider();

						if ("java.sql.Statement".equals(opi.getDeclaringType().getName())
								|| "java.sql.PreparedStatement".equals(opi.getDeclaringType().getName())) {

							Collection lt = ((MethodCall) target).getName().getOperands();
							Iterator it = lt.iterator();

							String str = ((Ident)it.next()).getText();
							if ( !lt.isEmpty() && str != null
									&& str.equals ( currentStatementVarDef ) ) {
							   ret = true;
							}
						}
					} else {

					    int i =0;
					    Object[] releaseResourceMethodStrings = releaseResourceMethodList.toArray();
					    while ( !ret && i < releaseResourceMethodStrings.length ){

					        String releaseResourceMethodName = (String)releaseResourceMethodStrings[i];
					        i++;
					        // System.out.println( " check for " + releaseResourceMethodName.toString());
					    if (releaseResourceMethodName.equals(subMethCall.getMethodName())) {

						//-- search for parameters
						List parameterList = subMethCall.getParameters();
						Iterator pit = parameterList.iterator();
						while (pit.hasNext() && !ret) {
							Object parameter = pit.next();

//!! refactoring: code doublet with method checkForReleaseSafeMethod
		                      if (parameter instanceof TypeCast) {
		                            context.debug((SourceMarker) parameter,  "((TypeCast)parameter).getTypeSpecification().toString() " + ((TypeCast)parameter).getTypeSpecification().toString());
		                            if ("Statement".equals( ((TypeCast)parameter).getTypeSpecification().toString())
		                                ||    "PreparedStatement".equals( ((TypeCast)parameter).getTypeSpecification().toString())   ){


		                                methodSourcingMarker = (SourceMarker)target;
		                                ret = true;
		                            }

		                      }else if (parameter instanceof Ident) {
								Ident p = (Ident) parameter;
								String paramName = p.toString();
								String paramTypDef = p.getTypeSpecification()
										.getName();
								if (("java.sql.Statement".equals(paramTypDef)
								        || "java.sql.PreparedStatement".equals(paramTypDef ))
										&& paramName.equals(currentStatementVarDef)) {

								    methodSourcingMarker = (SourceMarker)target;
					               // closeStatementList.add( target );
								    ret = true;
								}
							}



						}
					}}

					}
				}
			} catch (JselException e) {
				context.warn((SourceMarker) target, e);
			}
			return true;
		}
	}

	/**
	 * Configures the rule. Reads in the values of the parameter copyright.
	 *
	 * @param name
	 *            the name of the parameter being loaded from Hammurapi
	 *            configuration
	 * @exception ConfigurationException
	 *                in case of a not supported parameter
	 */
	public boolean setParameter(String name, Object parameter)
			throws ConfigurationException {
		if ("release-resource-method".equals(name)) {
			String s = parameter.toString();
			releaseResourceMethodList.add(s);
			return true;
		}

		throw new ConfigurationException("Parameter '" + name
				+ "' is not supported by " + getClass().getName());
	}
	/**
	 * Gives back the preconfigured values.
	 */
	public String getConfigInfo() {
		StringBuffer ret=new StringBuffer("Configured releaseResource method names:\n");
		Iterator it=releaseResourceMethodList.iterator();
		while (it.hasNext()) {
			ret.append("    " + it.next() + "\n");
		}
		return ret.toString();
	}

}
