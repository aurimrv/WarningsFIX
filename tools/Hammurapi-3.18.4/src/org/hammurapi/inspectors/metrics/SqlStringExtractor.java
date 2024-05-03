/*
 * Use Case SQL String Extractor
 * Take all SQL string components and concate them in order of occurance.
 * Postprocessor may examine the SQL expression for the following
 * 1	Table - DAO Crossreference: Which Java Class access which table in a specfic mode?
 * 2	Analyse SQL expression for quality
 * 3	Detect transaction logic in PL/SQL statements and correlate them with Java TX
 * 
 * Solution Space
 * SQL strings shall be defined as static final prepared statements inside the DAO.
 * In this scenario, the SQL string and Java class are easy to associate.
 * But schema & table names are typically constants defined in constant "interfaces" or property files.
 * Here we need a resolving mechanism and have to use a string constant repository.
 * 
 * Example:
 *   String str = "SELECT * FROM " + LtcConstant.EMPLOYEE_TABLE; 
 * 
 * Where Clause Injection
 * Many SELECT statements are dynamically concated and parts (especially WHERE clauses) are injected.
 * The parameters (e.g. Foreign Key value) are passed in the method parameter lists and StringBuffer >> append(String)
 * or the slow "+" operator concat the SQL String. Our use case could survive by catching the parameter names. 
 * 
 * Example:
 *   String str = "Select DOC_I from EC_TRANS where DOC_I = ?"; 
 * 
 * But the table name has to be resolved and any complex SQL injection
 * 
 * Variable Stack
 * The visitor has to build a variable stack for class variables, instance & local variables, and method parameters.
 * Not only variables keeping string constants but all Strings, and StringBuffer has to be stored.
 * The dynamics of operator "+" and at least the StringBuffer API append(String) has to be simulated.
 * We have here a mass problem and a memory storage has to be replaced by a persistent store.
 * 
 * Algorithm:
 * 		First Scan
 * 			Identify all StringConstants, String, and StringBuffer and save them 
 * 			with there access level, class, modifier, line, column, and value
 * 			
 * 		Variable Resolving
 * 			Check for variables in the string variables value.
 * 			If the variable contains other vars, use a recursive approach.
 * 			Each resolved variable value will be stored and bind.
 * 			In case of any errors (e.g. variable values are result of UI or 3rd party returns), 
 * 			the constant "<varNotResolvable> will be inserted 
 *   
 * 
 * 
 * Example:
 *   String str = "Select DOC_I from (" + strWithComplexSQLString +")"; 
 * 
 * A full caller trace is needed in order to reference the source of parameters. 
 * 
 * 
 * Grammar for Cross Reference
 * We could use a ANTLR SQL grammar, but for simple cross referencing we have adavantage of a SAX-minded state graph.
 * 
 * SELECT -> FROM -> <tableName>
 * INSERT -> INTO -> <tableName>
 * UPDATE -> <tableName> 
 * DELETE -> FROM -> <tableName>
 * 
 * These SQLs may be nested, but this is irrelevant for cross referencing.
 * 
 * Challenge
 * 		Hibernate Config 
 * 
 */
package org.hammurapi.inspectors.metrics;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorBase;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;

import com.pavelvlasov.jsel.Constructor;
import com.pavelvlasov.jsel.Interface;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.LanguageElement;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.Dot;
import com.pavelvlasov.jsel.expressions.Ident;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.Plus;
import com.pavelvlasov.jsel.expressions.PlusAssignment;
import com.pavelvlasov.jsel.expressions.StringConstant;
import com.pavelvlasov.jsel.statements.ForInitializer;



/**
 * @author MUCBJ0
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SqlStringExtractor extends InspectorBase {

    //-- everything with prefix current* represents the current source
    private StringVariable currentVariable =null;

    private String currentClassName = "";
    private String currentClassFcn = "";
	
	
	public SqlExtractorPersistencyService persistencyService = null;
	
	//!! memory greed prob .. pass to DB; reinitalized in visit(TypeDef)
	private Hashtable variableTable = new Hashtable();
	
	public void init() throws HammurapiException{
		super.init();
		// persistencyService = new SqlExtractorHyperSonicInMemoryDb(context);
		persistencyService = new SqlExtractorHyperSonicStanaloneServer(context);
	}
	
	public void visit(TypeDefinition p){
	    System.out.println ( variableTable.size() + " -- " + p.getName()  );
	    //-- store current state
	    storeCurrentState();
	    
	    //-- reinitalize
	    variableTable = new Hashtable();
	    currentClassName = p.getName();
	    currentClassFcn = p.getFcn();
	}
	
	public void visit(Interface p){
	    System.out.println ( variableTable.size() + " -- " + p.getName()  );
	    //-- store current state
	    storeCurrentState();
	    
	    //-- reinitalize
	    variableTable = new Hashtable();
	    currentClassName = p.getName();
	    currentClassFcn = p.getFcn();
	}

    public void visit(MethodCall methodCall) {
        // System.out.println("*> " + methodCall.getMethodName() );
        
        if( "append".equals( methodCall.getMethodName()) ){
            
                System.out.println( currentVariable.name );
            
            //StringBuffer currentVarValue = (StringBuffer) variableTable  .get(id.toString());

          }
        
    	/*	Code code = methodCall.getEnclosingCode();
			try {
			    // System.out.println("*> " + methodCall.getProvider().toString());
                if( code != null){
        				Operation op = (Operation) code;
        			// OperationInfo provider = new OperationInfo(code);
        			TypeBody tb = code.getEnclosingType();
          
                String key = tb.getFcn()+ ">>" +op.getOperationSignature();
               // System.out.println( key );
                } else {
                    // System.out.println( "code null" );
                }
            } catch (JselException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
    }
    public void visit(final Plus aPlus) {
        //System.out.println(" Plus "  + aPlus );
        
        try {
            
        if( "java.lang.String".equals( aPlus.getTypeSpecification().toString())){
        //System.out.println(" Plus "  + aPlus.getOperands() );
        Collection lst = aPlus.getOperands();
        // System.out.println(" instanceof "  + lst.get(lst.size()-1).getClass().toString() );
/*        
        String lastString = (String)lst.get(lst.size()-1);
        StringBuffer tempString = currentString;
        tempString.append(lastString);
        currentString= tempString;
        
           System.out.println(" currentString "  + currentString );
        } else {
            // handle variable of method call
            System.out.println( "+++ " +lst.get(lst.size()-1) );
        }
 */       
        } 
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }

	
    public void visit(final PlusAssignment aPlus) {
        // System.out.println(" PlusAssignment " );
    }
/*
 * Problem: Parameter Strings are visited AFTER method internals
 */
    /*
     * Problem: Parameter Strings are visited AFTER method internals
     */
        public void visit(final Parameter aParam) {
            // System.out.println(" Parameter " + aParam);
            try {
                if( aParam.getTypeSpecification().isKindOf("java.lang.String") 
                        || aParam.getTypeSpecification().isKindOf("java.lang.StringBuffer") ){
                
                currentVariable = new StringVariable(
                        				aParam, 
                        				new StringBuffer("<"+ aParam.getName() +">" ) ,
                        				aParam.getName(),
                        				"Parameter",
                        				currentClassName,
                        				currentClassFcn);
     /*           currentVariable.langElement= aParam;
                currentVariable.type = "Parameter";
                currentVariable.varValue = new StringBuffer("<"+ aParam.getName() +">" );
      */          // saveLanguageElement( currentVariable.langElement, currentVariable.varValue.toString() );
                variableTable.put( aParam.getName(),  currentVariable );
                //this.currentStringValue = new StringBuffer();
                }
            } catch (JselException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        public void visit(final VariableDefinition varDef) {
            //   System.out.println("## varDef "+ varDef);
               try {
                   if( varDef.getTypeSpecification().isKindOf("java.lang.String") 
                           || varDef.getTypeSpecification().isKindOf("java.lang.StringBuffer") ){
                       if(currentVariable != null && currentVariable.langElement != null ){
        
                           // reinitalize 
                       if(   currentVariable.langElement instanceof VariableDefinition) {
                           variableTable.put( ((VariableDefinition) currentVariable.langElement).getName(),  currentVariable) ;
                       } else if (   currentVariable.langElement instanceof Parameter ) {
                           variableTable.put( ((Parameter) currentVariable.langElement).getName(),  currentVariable );
                       }
                       }
                       currentVariable = new StringVariable(
               				varDef, 
               				new StringBuffer() ,
               				varDef.getName(),
               				"local variable",
               				currentClassName,
               				currentClassFcn);
                     
                   } else {
                       // not a String Var: Reset current stack.
                       currentVariable = new StringVariable(
               				varDef, 
               				new StringBuffer() ,
               				varDef.getName(),
               				"local variable: not a String type",
               				currentClassName,
               				currentClassFcn);          }
               } catch (JselException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }
           }

    public void visit(Dot dot) {
        
    }   
    
    public void visit( Constructor le) {
        //System.out.println("Constructor " + le.toString());
    }
    public void visit( Method le) {
       // System.out.println("Method " + le.toString());
    }
    public void visit( ForInitializer le) {
        //System.out.println("ForInitializer " + le.toString());
    }
        
    //!! Problem: static class vars: search term is var name without class name only.
    /*
     * 	Ident AnotherDaoObject
     	Ident LTC_REPORT_DB
     	Ident LTC_REPORT_DB<--"LTC_REPORT"
     	
     	Johannes,

In AnotherDaoObject.LTC_REPORT_DB you need to invoke getProvider() of the 
last ident and you'll get a reference to LTC_REPORT_DB field in 
AnotherDaoObject, which would be of type VariableDefinition, Parameter, 
TypeDefiniton for source files and java.lang.reflect.Field for external 
classes, ... See JavaDoc 
http://www.pavelvlasov.com/products/Jsel/doc/api/com/pavelvlasov/jsel/expressions/Ident.html#getProvider().
---
Best regards, Pavel.

----- Original Message ----- 
From: <Johannes.Bellert@ge.com>
To: <vlasov@pavelvlasov.com>; <Pavel.Vlasov@ge.com>
Sent: Monday, September 27, 2004 4:18 PM
Subject: Ident -> Dot -> Ident


> Pavel,
> I got a baby step further with my SQL Extractor.
> I catch all StringLiterals and assigne them to variables .. if a String is
> concated (Plus, PlusAssignment, append(String)), I simulate the behavior.
> I also look up for already defined Vars which works quite OK for local and
> instance  Vars.
> Public Class Vars will be stored with Class name qualifier.
>
> Problem I have now is, how to identify something like
>     String strCompanyDaoUpdate = "INSERT INTO " +
> AnotherDaoObject.LTC_REPORT_DB;
>
> I tried to implement a state engine like:
> Ident -> Dot -> Ident
> and append the var name appropriately. Unfortunately, I have to implement
> all possible nodes in the visitor for all Dots & Idents ..
> I also could ask the Dot for children but this is very hard wired and I
> learned that I have to add those checks in the standard Ident as well.
>
> Any ideas?
>
> <<summary.html_String literals39090.ZIP>>
> Thanks,
> Johannes
     */
    public void visit(final Ident id) {
        // System.out.println("Ident " + id.toString());
        try {
            final Object provider = id.getProvider();
            if (provider != null
                    && ((provider instanceof VariableDefinition) && (((VariableDefinition) provider)
                            .getTypeSpecification()
                            .isKindOf("java.lang.String") 
                         || ((VariableDefinition) provider)
                         		.getTypeSpecification().isKindOf(
                                    "java.lang.StringBuffer")))
                    || (provider instanceof Parameter && (((Parameter) provider)
                            .getTypeSpecification()
                            .isKindOf("java.lang.String") 
                         || ((Parameter) provider)
                            .getTypeSpecification().isKindOf(
                                    "java.lang.StringBuffer")))) {
                
                StringVariable svTmp = (StringVariable)variableTable.get(id.toString());
                StringBuffer currentVarValue;

                if(svTmp != null ){
                    currentVarValue = svTmp.varValue;
                } else {
                    currentVarValue = new StringBuffer();
                }

                // handle public static Class variables
                LanguageElement le = ((LanguageElement) id).getParent();
                if (le instanceof Dot && le.getAst().getNumberOfChildren() == 2) {
                    String className = le.getAst().getFirstChild().getText();
                    StringVariable currentVariableX = (StringVariable) variableTable  .get(className);
                    
                    // not a class but a StringBuffer Variable or something
                    if( currentVariableX != null ){
             //           System.out.println("currentVarValueX " + currentVariableX.name );
                        currentVariable = currentVariableX; 
/*                        
                        currentVariable.varValue.append(currentVarValue);
                        // put here the right Hand to left hand side assignment
                        System.out.println("Ident "
                                + currentVariable.name.toString() + "<--"
                                + currentVariable.varValue);
                        saveLanguageElement(currentVariable.langElement,
                                currentVariable.varValue.toString());
*/
                        // System.out.println("Ident " + currentVariable.name.toString() + "<--" + currentVariable.varValue);
                    } else{
                    String currentLangElementName = className + "."
                            + id.toString();
 //!!                   
// variableTable.put(currentLangElementName, );
                    // System.out.println("Ident added: " + currentLangElementName + " " + currentVariable.varValue);

                    currentVariable.varValue.append(" <");
                    currentVariable.varValue.append(currentLangElementName);
                    currentVariable.varValue.append("> ");
                    }
                } else if (currentVarValue != null && currentVarValue.length() > 0) {

                    currentVariable.varValue.append(currentVarValue);
                    // put here the right Hand to left hand side assignment
                    // System.out.println("Ident " + currentVariable.name.toString() + "<--"  + currentVariable.varValue);
                    // saveLanguageElement(currentVariable.langElement,currentVariable.varValue.toString());

                } else {
                    boolean paramIsFound = false;
                    // unresolved variables -- assumption: Operation Parameters
                    LanguageElement lex = currentVariable.langElement
                            .getParent().getParent();
                    if (lex instanceof Method) {
                        Method met = (Method) lex;
                        // System.out.println("Ident currentVariable ..  " + met.getName());
                        Iterator it = met.getParameters().iterator();
                        while (it.hasNext()) {
                            Parameter p = (Parameter) it.next();
                           // System.out.println("p.getName()  " + p.getName());
                            if (id.toString().equals(p.getName())) {
                                paramIsFound = true;
                            }
                        }
                        
                        if (paramIsFound) {
                            currentVariable.varValue.append("@");
                            currentVariable.varValue.append(currentClassName);
                            currentVariable.varValue.append(">>");
                            currentVariable.varValue.append(met.getName());
                            currentVariable.varValue.append("::");
                            currentVariable.varValue.append(id);
                            currentVariable.varValue.append("-- ");
                            // saveLanguageElement(currentVariable.langElement,  currentVariable.varValue.toString());
                            variableTable.put(currentVariable.name, currentVariable );
                            // System.out.println("Ident currentVariable "+ currentVariable.name +" varValue " + currentVariable.varValue);
                        }                     
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // try to resolve string
        /*
         * try { if (currentLangElement != null) { System.out.println("
         * currentString " + currentLangElement.toString() + " <- " +
         * currentStringValue); }
         * 
         * final Object provider = id.getProvider(); if (provider != null &&
         * provider instanceof VariableDefinition && (((VariableDefinition)
         * provider).getTypeSpecification() .isKindOf("java.lang.String") ||
         * ((VariableDefinition) provider) .getTypeSpecification().isKindOf(
         * "java.lang.StringBuffer"))) { System.out.println("OO " +
         * provider.toString()); SQLProcessor processor =
         * getProcessor((SourceMarker) id); if (processor != null) { // check
         * for String Only ! processor.processUpdate("INSERT INTO " +
         * varTableName + " (VAR_NAME, VAR_VALUE, SOURCE, LINE, COL) " + "VALUES
         * (?,?,?,?,?)", new Parameterizer() { public void
         * parameterize(PreparedStatement ps) throws SQLException {
         * ps.setString(1, id.toString()); ps.setString(2, provider.toString());
         * SourceMarker sourceMarker = (SourceMarker) id; ps.setString(3,
         * sourceMarker.getSourceURL()); ps.setInt(4, sourceMarker.getLine());
         * ps.setInt(5, sourceMarker.getColumn()); } }); } } } catch (Exception
         * e) { // TODO Auto-generated catch block e.printStackTrace(); }
         */
    }
    
    public String cutLeadingTrailingQuote(String str){
        String tmpString = str;
        if( str != null ){
            if ( str.charAt(0) == '"'){
                tmpString = str.substring(1);
            }
            str = tmpString;
            if ( str.charAt(str.length()-1) == '"' && (str.length()-1)> -1 ) {
                tmpString = str.substring(0, str.length()-1);
            }
        }
        return tmpString;
    }
    
    public void visit(final StringConstant constant) {
        // System.out.println("++ "+ constant.toString() );
        
        final String constantCopy = cutLeadingTrailingQuote( constant.toString() );
        // System.out.println("++ "+ constantCopy );
        currentVariable.varValue.append(constantCopy);
        logCurrentLangElement();
        /*
        SQLProcessor processor = getProcessor((SourceMarker) constant);
        if (processor != null) {
            try {
                processor.processUpdate(
                        "INSERT INTO " + sqlTableName
                                + " (LITERAL, SOURCE, LINE, COL) "
                                + "VALUES (?,?,?,?)", new Parameterizer() {
                            public void parameterize(PreparedStatement ps)
                                    throws SQLException {
                                ps.setString(1, constantCopy );
                                SourceMarker sourceMarker = (SourceMarker) constant;
                                ps.setString(2, sourceMarker.getSourceURL());
                                ps.setInt(3, sourceMarker.getLine());
                                ps.setInt(4, sourceMarker.getColumn());
                            }
                        });
            } catch (SQLException e) {
                context.warn((SourceMarker) constant, e);
            }
        }
        */
    }

    private void storeCurrentState() {
        if (currentClassName != null && !"".equals(currentClassName)) {
            Enumeration enum = this.variableTable.elements();
            while (enum.hasMoreElements()) {
                StringVariable sVar = (StringVariable) enum.nextElement();
                this.persistencyService.saveLanguageElement(sVar);
            }
        }
    }


    private void logCurrentLangElement(){
        if ( currentVariable.langElement != null ){
            // System.out.println(" currentString " + currentVariable.langElement.toString() +" <- "  +  currentVariable.varValue );
        } else {
            // System.out.println(" currentString is null" );
        }
    }

    public void leave(Repository repo) {
        
        this.storeCurrentState();
        
    		context.annotate(new LinkedAnnotation() {
    			String path;

    			public String getPath() {
    				return path;
    			}

    			public String getName() {
    				return "String literals";
    			}

    			public void render(AnnotationContext context) throws HammurapiException {
					persistencyService.render( context, path);
    			}

    			public Properties getProperties() {
    				return null;
    			}				
    		});
    	
    	
    }

public void destroy() {
	persistencyService.destroy();
	
	super.destroy();
}

}
