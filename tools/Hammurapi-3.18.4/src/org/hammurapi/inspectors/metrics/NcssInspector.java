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
 * URL: http://www.pavelvlasov.com/pv/content/menu.show?id=products.jtaste
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Mar 23, 2004
 *
 */
package org.hammurapi.inspectors.metrics;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;
import org.hammurapi.inspectors.metrics.reporting.LocReporter;
import org.hammurapi.inspectors.metrics.statistics.DescriptiveStatistic;
import org.hammurapi.inspectors.metrics.statistics.IntVector;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.Method;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;
import com.pavelvlasov.review.SourceMarker;

public class NcssInspector extends InspectorBase implements Parameterizable {

	/**
     * A sorted distribution list of class NCSS
     *
     */
	private IntVector ncssClassList = new IntVector();

	/**
     * A sorted distribution list of function NCSS
     *
     */
	private IntVector ncssFunctionList = new IntVector();

    /**
     * Stores the setting form the configuration for the maximum allowed
     * NCSS for an implemened method.
     */
    private Integer functionMaxLoc;

    /**
     * Stores the setting form the configuration for the maximum allowed
     * NCSS for a class.
     */
    private Integer classMaxLoc;

    /**
     * Stores the setting form the configuration for the maximum allowed
     * NCSS for a class.
     */
    private Integer  classMaxFunction;
    
    /**
     * if ncssReport == 1, then generate the NCSS metric report
     */
    private Integer ncssReport;


    /**
     *  You may want to see the Swing Chart for Debug purpose
     */
    private Integer chartDebugWindow;

	double functionAvg = 0.0;
	double classAvg = 0.0;

    private Vector packageCodeMetricList = new Vector();
    private Vector functionCodeMetricList = new Vector();
	private Map types = new HashMap();
	private Map packages = new HashMap();
	private CodeMetric projectMetric = new CodeMetric();
	String projectBaseDir = "";
	String xmlResourceName=this.getClass().getName().replace('.','/')+  "XmlPretty.xsl";
	LocReporter locReport ;


	public void visit(com.pavelvlasov.jsel.Package pkg) {
		CodeMetric packageCodeMetrics = new CodeMetric();
		packageCodeMetrics.setDescriptonEntity ("Package");
		packageCodeMetrics.setName ( pkg.getName() );
		packages.put(pkg.getName(), packageCodeMetrics);

		int sum = projectMetric.getNcss() +packageCodeMetrics.getNcss();
		projectMetric.setNcss( sum );
		sum = projectMetric.getNumber() +1;
		projectMetric.setNumber( sum );

		projectMetric.getChildren().add( packageCodeMetrics );
	}

	public void visit(TypeDefinition typeDefinition) {
		int start = typeDefinition.getAst().getFirstToken().getLine();
		int end = typeDefinition.getAst().getLastToken().getLine();
		CodeMetric typeCodeMetric = new CodeMetric();
		typeCodeMetric.setDescriptonEntity("Class");
		typeCodeMetric.setName ( typeDefinition.getFcn() );

		// packageName.replace('.','/')+compilationUnit.getName()
		//    System.out.println( "++ " +typeDefinition.getCompilationUnit().getPackage().getName().toString() );



		typeCodeMetric.source_url= ((SourceMarker) typeDefinition).getSourceURL();
		typeCodeMetric.source_line= ((SourceMarker) typeDefinition).getLine();
		typeCodeMetric.source_col= ((SourceMarker) typeDefinition).getColumn();

  		typeCodeMetric.setNcss ( end - start);

		types.put((String) typeDefinition.getFcn(), typeCodeMetric);

		// lookup for package
		CodeMetric packageCodeMetric = (CodeMetric) packages.get( typeDefinition.getCompilationUnit().getPackage().getName() );
		int sum = packageCodeMetric.getNcss() + typeCodeMetric.getNcss();
		packageCodeMetric.setNcss( sum );

		packageCodeMetric.setNumber( packageCodeMetric.getNumber() + 1);
		sum = packageCodeMetric.getFunction() +typeCodeMetric.getFunction();
		packageCodeMetric.setFunction( sum );

		sum = projectMetric.getFunction() +packageCodeMetric.getFunction();
		projectMetric.setFunction( sum );

		packageCodeMetric.getChildren().add( typeCodeMetric );

    	context.addMetric(typeDefinition, "NCSS Class Inspector", typeCodeMetric.getNcss() );
    	if (this.classMaxLoc!=null && typeCodeMetric.getNcss() > classMaxLoc.intValue()) {
    		context.reportViolation(typeDefinition, new Object[] {classMaxLoc, new Integer(typeCodeMetric.getNcss())});
    	}

	}

	public void visit(Method code) {
		int start = code.getAst().getFirstToken().getLine();
		int end = code.getAst().getLastToken().getLine();
		CodeMetric methodCodeMetrics = new CodeMetric();
		methodCodeMetrics.setDescriptonEntity ( "MethodImplemented" );
		methodCodeMetrics.setName(   code.getEnclosingType().getFcn() + " >> "  + code.getName());
		
		methodCodeMetrics.source_url= ((SourceMarker) code).getSourceURL();
		methodCodeMetrics.source_line= ((SourceMarker) code).getLine();
		methodCodeMetrics.source_col= ((SourceMarker) code).getColumn();

		methodCodeMetrics.setNcss (end - start);

		// lookup for class

		CodeMetric classCodeMetric = (CodeMetric) types.get((String) code.getEnclosingType().getFcn());
		if(classCodeMetric == null ){
		     new Exception( "Can't get CodeMetric Object for " + (String) code.getEnclosingType().getFcn());
		} else {
		classCodeMetric.setFunction( classCodeMetric.getFunction() +1) ;
		classCodeMetric.getChildren().add(methodCodeMetrics);
		context.addMetric(code, "NCSS Method p Class Inspector", classCodeMetric.getFunction());
		}
    	context.addMetric(code, "NCSS Method Inspector", methodCodeMetrics.getNcss());
    	if (this.functionMaxLoc!=null && methodCodeMetrics.getNcss() > functionMaxLoc.intValue()) {
    		context.reportViolation(code, new Object[] {functionMaxLoc, new Integer(methodCodeMetrics.getNcss())});
    	}
    	
    	if (this.classMaxFunction!=null && classCodeMetric!=null && classCodeMetric.getFunction() > classMaxFunction.intValue()) {
    		context.reportViolation(code, new Object[] {classMaxFunction, new Integer(classCodeMetric.getFunction())});
    	}

	}

	private void aggregate() {


		Iterator it=types.values().iterator();
		while(it.hasNext()) {
			CodeMetric classCodeMetric = (CodeMetric) it.next();
			int last  = classCodeMetric.getName().lastIndexOf('.');
			String packg = last==-1 ? "" : classCodeMetric.getName().substring(0,last);
			CodeMetric packageCodeMetric = (CodeMetric)packages.get(packg);
			int sum = packageCodeMetric.getFunction() +classCodeMetric.getFunction();
			packageCodeMetric.setFunction ( sum );

	//		this.projectMetric.function += packCm.function;
		}
		this.copyDeepCodeMetric();
		functionAvg = DescriptiveStatistic.mean(ncssFunctionList);
		classAvg =   DescriptiveStatistic.mean(ncssClassList);
		return;
	}


	private void copyDeepCodeMetric() {

		Enumeration enumP = projectMetric.getChildren().elements();
		while (enumP.hasMoreElements()) {
			CodeMetric cmP = (CodeMetric) enumP.nextElement();
			// Classes
			Enumeration enumC = cmP.getChildren().elements();
			while (enumC.hasMoreElements()) {
				CodeMetric cmC = (CodeMetric) enumC.nextElement();
				this.packageCodeMetricList.add(cmC);
				this.ncssClassList.addElement( cmC.getNcss() ) ;
				// Functions
				Enumeration enumF = cmC.getChildren().elements();
				while (enumF.hasMoreElements()) {
					CodeMetric cmF = (CodeMetric) enumF.nextElement();
					this.functionCodeMetricList.add(cmF);
					this.ncssFunctionList.addElement( cmF.getNcss() );
				}
			}
		}
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
				projectMetric.setName ( "Project" );
				projectMetric.setDescriptonEntity ( projectMetric.getName() );

				// Output images here. See AnnotationTest for details.

				class NcssInspectorRenderer extends AbstractRenderer {
					NcssInspectorRenderer() {
						super(new RenderRequest(NcssInspector.this));
					}

					public Element render(Document document) throws RenderingException {
						NcssInspector ncssInspector=(NcssInspector) request.getRenderee();
						Element entities=document.createElement("Entities");
						Element ncssInspectorElement=document.createElement("SourceCodeMetric");

						Element classMaxLocE=document.createElement("ClassMaxLoc");
						classMaxLocE.setAttribute("number", String.valueOf( classMaxLoc ) );
						ncssInspectorElement.appendChild(classMaxLocE);

						Element classMaxFuncE=document.createElement("ClassMaxFunction");
						classMaxFuncE.setAttribute("number", String.valueOf( classMaxFunction ) );
						ncssInspectorElement.appendChild(classMaxFuncE);


						Element funcMaxLocE=document.createElement("FunctionMaxLoc");
						funcMaxLocE.setAttribute("number", String.valueOf( functionMaxLoc ) );
						ncssInspectorElement.appendChild(funcMaxLocE);


							Element functionAverage=document.createElement("FunctionNcssAverage");
							ncssInspectorElement.appendChild(functionAverage);
							functionAverage.setAttribute("number",  String.valueOf(functionAvg));


							Element classAverage=document.createElement("ClassNcssAverage");
							ncssInspectorElement.appendChild(classAverage);
							classAverage.setAttribute("number",  String.valueOf(classAvg));


						Element jpgClassFileEntry=document.createElement("JpgClassFileEntry");
						jpgClassFileEntry.setAttribute("chartFile", String.valueOf( locReport.getJpgClassFileEntry().getPath().toString()));
						ncssInspectorElement.appendChild(jpgClassFileEntry);

						Element jpgFunctionFileEntry=document.createElement("JpgFunctionFileEntry");

						jpgFunctionFileEntry.setAttribute("chartFile", String.valueOf( locReport.getJpgFunctionFileEntry().getPath().toString()));
						ncssInspectorElement.appendChild(jpgFunctionFileEntry);

						Element pmd = ncssInspector.projectMetric.toDom(document);
						ncssInspectorElement.appendChild(pmd);
						return ncssInspectorElement;
					}
				} //-- end class NcssInspectorRenderer

				locReport = new LocReporter( context, classMaxLoc, functionMaxLoc, ncssReport, chartDebugWindow  );
				locReport.setNcssClassList( ncssClassList );
				locReport.setNcssFunctionList( ncssFunctionList );

				locReport.doIt( projectMetric) ;

				AnnotationContext.FileEntry fileEntry=context.getNextFile(context.getExtension());
				path=fileEntry.getPath();
			// 	System.out.println( ".> " +this.getPath().toString() );

				AnnotationContext.FileEntry fileEntryXml=context.getNextFile(".xml");
				try {
					NcssInspectorRenderer renderer=new NcssInspectorRenderer();
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
				ret.setProperty("target", "NCSS");
				return ret;
			}
		});
	}
	   /**
     * Configures the rule. Reads in the values of the parameters operation-max-complexity and
     * class-max-complexity.
     *
     * @param name the name of the parameter being loaded from Hammurapi configuration
     * @param value the value of the parameter being loaded from Hammurapi configuration
     * @exception ConfigurationException in case of a not supported parameter
     */
    public boolean setParameter(String name, Object value) throws ConfigurationException {
        if ("function-max-loc".equals(name)) {
            functionMaxLoc=new Integer(Integer.parseInt(value.toString()));
        } else if ("class-max-loc".equals(name)) {
        	classMaxLoc=new Integer(Integer.parseInt(value.toString()));
        } else if ("class-max-function".equals(name)) {
        	classMaxFunction=new Integer(Integer.parseInt(value.toString()));
        } else if ("chart-debug-window".equals(name)) {
        	chartDebugWindow=new Integer(Integer.parseInt(value.toString()));
        } else if ("ncss-report".equals(name)) {
        	ncssReport=new Integer(Integer.parseInt(value.toString()));

        } else {
            throw new ConfigurationException("Parameter '"+name+"' is not supported");
        }
		return true;
    }

}

