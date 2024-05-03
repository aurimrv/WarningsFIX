/*
 * Created on May 29, 2004
 * 
 * 
 * JSP invoke technique r Servlet response.redirect a HTML action L HTML HREF I
 * JSP include f JSP forward X JavaScript s HTML Frame Source
 *  
 */
package org.hammurapi.inspectors.metrics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.TypeDefinition;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.jsel.expressions.MethodCall;
import com.pavelvlasov.jsel.expressions.StringConstant;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;
import com.pavelvlasov.review.SourceMarker;
/**
 * @author MUCBJ0
 * 
 * TODO parameter for JspBase Type, Jsp File Extractor,
 */
public class JspInspector extends InspectorBase
		implements
			com.pavelvlasov.config.Parameterizable {
	
	private Map jspList = new HashMap();
	private boolean isJavaScriptStream = false;
	private JspDescriptor currentJspDescriptor = null;
	private String jspBaseClass = "";
	
	public void visit(MethodCall methodCall) {
		// Pavel Vlasov - to avoid NPE
		if (currentJspDescriptor == null) {
			return;
		}
		// System.out.println("*> " + methodCall.getName());
		if ("response.sendRedirect".equals(methodCall.getName().toString())) {
			String referenceStr = methodCall.getParameters().get(0).toString();
			JspXref jXref = new JspXref(currentJspDescriptor,
					"response.sendRedirect", referenceStr);
			currentJspDescriptor.listOfInvokedJsp.add(jXref);
		}
		if ("pageContext.forward".equals(methodCall.getName().toString())) {
			String referenceStr = methodCall.getParameters().get(0).toString();
			JspXref jXref = new JspXref(currentJspDescriptor, "JSP Forward",
					referenceStr);
			currentJspDescriptor.listOfInvokedJsp.add(jXref);
		}
		if ("out.print".equals(methodCall.getName().toString())
				|| "out.println".equals(methodCall.getName().toString())) {
			this.currentJspDescriptor.numberOfOutPrintOps++;
		} else if ("out.write".equals(methodCall.getName().toString())) {
			this.currentJspDescriptor.numberOfOutWriteOps++;
			List parameterList = methodCall.getParameters();
			for (int i = 0; i < parameterList.size(); i++) {
				Object param = parameterList.get(i);
				if (param instanceof StringConstant) {
					StringConstant strCnst = (StringConstant) param;
					
					if (!isJavaScriptStream
							&& strCnst.toString().toLowerCase().indexOf(
									"<script".toLowerCase()) > -1) {
						isJavaScriptStream = true;
						this.currentJspDescriptor.numberOfJavaScriptSnippets++;
						
						currentJspDescriptor.analyseJavaScriptPortion(strCnst
								.toString());									
					} else if (isJavaScriptStream
							&& strCnst.toString().toLowerCase().indexOf(
									"</script>".toLowerCase()) > -1) {
						
						currentJspDescriptor.analyseJavaScriptPortion(strCnst
								.toString());
										
						isJavaScriptStream = false;
					} else if (isJavaScriptStream) {
						
						currentJspDescriptor.analyseJavaScriptPortion(strCnst
								.toString());
					}
					if (!isJavaScriptStream
							&& strCnst.toString().toLowerCase().indexOf(
									"<form".toLowerCase()) > -1) {
						currentJspDescriptor.analyseHtmlForm(strCnst.toString());
					}
				}
			}
		}
	}
	public void extractJavaScriptPortion(String jsStream) {
		// Pavel Vlasov: to avoid NPE
		if (currentJspDescriptor != null) {
			currentJspDescriptor.analyseJavaScriptPortion(jsStream);
		}
	}
	public void visit(VariableDefinition element) {
		//!! SQL Extractor
	}
	public void visit(TypeDefinition typeDefinition) {
		try {
			if (typeDefinition.isKindOf(jspBaseClass)) {
				int start = typeDefinition.getAst().getFirstToken().getLine();
				int end = typeDefinition.getAst().getLastToken().getLine();
				JspDescriptor jspDescr = new JspDescriptor();
				CodeMetric typeCodeMetric = new CodeMetric();
				typeCodeMetric.setDescriptonEntity("Class");
				typeCodeMetric.setName(typeDefinition.getFcn());
				// packageName.replace('.','/')+compilationUnit.getName()
				System.out.println("++ "
						+ typeDefinition.getCompilationUnit().getPackage()
								.getName().toString());
				typeCodeMetric.source_url = ((SourceMarker) typeDefinition)
						.getSourceURL();
				typeCodeMetric.source_line = ((SourceMarker) typeDefinition)
						.getLine();
				typeCodeMetric.source_col = ((SourceMarker) typeDefinition)
						.getColumn();
				typeCodeMetric.setNcss(end - start);
				jspDescr.codeMetric = typeCodeMetric;
				//!! potential thread issue !!
				this.currentJspDescriptor = jspDescr;
				jspList.put((String) typeDefinition.getFcn(), jspDescr);
			}
		} catch (JselException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void leave(Repository repository) {
		//aggregate();
		context.annotate(new LinkedAnnotation() {
			private String path;
			public String getName() {
				return getContext().getDescriptor().getName();
			}
			public String getPath() {
				return path;
			}
			public void render(AnnotationContext context)
					throws HammurapiException {
				String errorCausingFile = "";
				// Output images here. See AnnotationTest for details.
				class JspInspectorRenderer extends AbstractRenderer {
					JspInspectorRenderer() {
						super(new RenderRequest(JspInspector.this));
					}
					public Element render(Document document)
							throws RenderingException {
						Element entities = document.createElement("Entities");
						Element jspMetricElement = document
								.createElement("JspMetric");
						Collection values = jspList.values();
						Iterator it = values.iterator();
						System.out.println("Number of JspDescriptors - "+values.size());
						while (it.hasNext()) {
							JspDescriptor jspD = (JspDescriptor) it.next();
							jspMetricElement.appendChild(jspD.toDom(document));
						}
						return jspMetricElement;
					}
				} //-- end class JspInspectorRenderer
				/*
				 * locReport = new LocReporter( context, classMaxLoc,
				 * functionMaxLoc, ncssReport, chartDebugWindow );
				 * locReport.setNcssClassList( ncssClassList );
				 * locReport.setNcssFunctionList( ncssFunctionList );
				 * 
				 * locReport.doIt( projectMetric) ;
				 */
				AnnotationContext.FileEntry fileEntry = context
						.getNextFile(context.getExtension());
				path = fileEntry.getPath();
				System.out.println(".> " + this.getPath().toString());
				AnnotationContext.FileEntry fileEntryXml = context
						.getNextFile(".xml");
				try {
					JspInspectorRenderer renderer = new JspInspectorRenderer();
					FileOutputStream out = new FileOutputStream(fileEntry
							.getFile());
					renderer.setEmbeddedStyle(context.isEmbeddedStyle());
					try {
						errorCausingFile = fileEntry.getFile()
								.getAbsolutePath();
						renderer.render(context.getParameter("style") == null
								? null
								: new FileInputStream(context.getParameter(
										"style").toString()), out);
					} finally {
						out.close();
					}
					//-- write a XML file for other XSL usage
					FileOutputStream outXml = new FileOutputStream(fileEntryXml
							.getFile());
					try {
						errorCausingFile = fileEntryXml.getFile()
								.getAbsolutePath();
						//-- write a XML file for other XSL usage
						renderer.setEmbeddedStyle(false);
						renderer.render(outXml);
						//						renderer.setPrettyPrint( true );
						//						InputStream
						// inStream=getClass().getClassLoader().getResourceAsStream(xmlResourceName);
						//						renderer.render(inStream, outXml);
					} finally {
						outXml.close();
					}
				} catch (Exception e) {
					throw new HammurapiException("Can't save "
							+ errorCausingFile + ". " + e.getMessage());
				}
			}
			public Properties getProperties() {
				Properties ret = new Properties();
				ret.setProperty("left-panel", "yes");
				ret.setProperty("target", "JSP Metrics");
				return ret;
			}
		});
	}
	public boolean setParameter(String name, Object value)
			throws ConfigurationException {
		if ("jsp-base-class".equals(name)) {
			jspBaseClass = value.toString();
			return true;
		} else {
			throw new ConfigurationException("Parameter '" + name
					+ "' is not supported");
		}
	}
}
