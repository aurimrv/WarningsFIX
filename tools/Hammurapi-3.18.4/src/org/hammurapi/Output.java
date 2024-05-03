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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.hammurapi.render.dom.CompositeResultsRenderer;
import org.hammurapi.render.dom.HammurapiMetricRenderer;
import org.hammurapi.render.dom.InspectorDescriptorRenderer;
import org.hammurapi.render.dom.InspectorSetRenderer;
import org.hammurapi.render.dom.InspectorSummaryRenderer;
import org.hammurapi.render.dom.ReportRenderer;
import org.hammurapi.render.dom.ReviewResultsRenderer;
import org.hammurapi.results.AggregatedResults;
import org.hammurapi.results.Annotation;
import org.hammurapi.results.AnnotationConfig;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.InspectorSummary;
import org.hammurapi.results.ReportMixer;
import org.hammurapi.results.ReviewResults;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.metrics.MeasurementCategoryFactory;
import com.pavelvlasov.metrics.Metric;
import com.pavelvlasov.metrics.TimeIntervalCategory;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;

/**
 * Outputs review results to XML or HTML.
 * @ant.element parent="hammurapi" name="output" display-name="Output subelement"
 * @ant.non-required
 * @author Pavel Vlasov
 * @version $Revision: 1.12 $
 */
public class Output implements Listener /*, ReviewUnitFilter*/ {
    private String dir;
    private boolean embeddedStyle=true;
    private String extension=".html";
	private TaskBase task;
    
    public Output(TaskBase task) {
        this.task=task;
    }
    
    /**
     * Output directory
     * @ant.required
     */
    public void setDir(String dir) {
        this.dir=dir;
    }
    
    private Map styleSheets=new HashMap();
    
    {
    	styleSheets.put("compilation-unit", new StyleSheetEntry());
    	styleSheets.put("summary", new StyleSheetEntry());
    	styleSheets.put("left-panel", new StyleSheetEntry());
    	styleSheets.put("waived-violations", new StyleSheetEntry());
    	styleSheets.put("package", new StyleSheetEntry());
    	styleSheets.put("inspector-set", new StyleSheetEntry());
    	styleSheets.put("inspector-descriptor", new StyleSheetEntry());
    	styleSheets.put("inspector-summary", new StyleSheetEntry());
    	styleSheets.put("metric-details", new StyleSheetEntry());
    }
    
    public void addConfiguredStyleSheet(StyleSheetEntry styleSheet) {
    	if (styleSheet.getName()==null) {
    		throw new BuildException("Unnamed stylesheet");
    	}
    	
    	StyleSheetEntry existing=(StyleSheetEntry) styleSheets.get(styleSheet.getName());
    	if (existing==null) {
    		throw new BuildException("Invalid stylesheet name: "+styleSheet.getName());
    	}
    	
    	if (styleSheet.getFile()!=null) {
    		existing.setFile(styleSheet.getFile());
    	}
    	
    	if (styleSheet.getUrl()!=null) {
    		existing.setUrl(styleSheet.getUrl());
    	}
    	
    	existing.setParameters(styleSheet.getParameters());    		
    }
        
    /**
     * Use embedded stylesheets if no stylesheets has been set explicitly.
     * Default is true.
     * @ant.non-required
     */
    public void setEmbeddedStyle(boolean embeddedStyle) {
        this.embeddedStyle=embeddedStyle;
    }
    
    /**
     * Extension for output files. Defaults to ".html"
     * @ant.non-required
     */
    public void setExtension(String extension) {
        this.extension=extension;
    }
    
    private File javaDocDir;
    
    /**
     * JavaDoc directory to generate links.
     * @ant.non-required
     */
    public void setJavaDocDir(File javaDocDir) {
        this.javaDocDir=javaDocDir;
    }
    
    private File getOutDir() throws HammurapiException {
        File outDir=FileUtils.newFileUtils().resolveFile(task.getProject().getBaseDir(), dir);
        if (!outDir.exists()) {
            throw new HammurapiException("Output directory does not exist: "+outDir.getAbsolutePath());
        }
        
        if (!outDir.isDirectory()) {
            throw new HammurapiException("Not a directory: "+outDir.getAbsolutePath());
        }
        
        return outDir;        
    }
    
    private String getFileName(ReviewResults reviewResult) {
        String packageName=reviewResult.getCompilationUnit().getPackage().getName();
        String unitName=reviewResult.getName();
        return "source/"+packageName.replace('.', '/')+'/'+unitName;        
    }
    
    private File getFile(ReviewResults reviewResult) throws HammurapiException {
        return new File(getOutDir(), getFileName(reviewResult)+extension);        
    }
    
    private static final Object mkDirSynchronizationMonitor=new Object();
    
    public void onReview(ReviewResults reviewResult) throws HammurapiException {
        if (dir==null) {
            throw new HammurapiException("dir attribute is mandatory");
        }
        
        final File outFile=getFile(reviewResult);
        final File outFileParent=outFile.getParentFile();
        synchronized (mkDirSynchronizationMonitor) {
	        if (!outFileParent.exists()) {
	            if (!outFileParent.mkdirs()) {
	                throw new HammurapiException("Can't create "+outFileParent.getAbsolutePath());
	            }
	        }
        }
        
        String packageName=reviewResult.getCompilationUnit().getPackage().getName();
        int count=0;
        for (int i=0; i<packageName.length(); i++) {
            if ('.'==packageName.charAt(i)) {
                count++;
            }
        }
        
        StringBuffer inspectorsPath=new StringBuffer("../");
        while (count-->=0) {
            inspectorsPath.append("../");
        }
                       
        renderAnnotations(reviewResult, outFile);
        
        writeUnitDoc(reviewResult, outFile, inspectorsPath.toString(), getJavaDocPath(reviewResult));        
    }
    
    /**
	 * @param reviewResult
	 * @param outFile
	 * @throws HammurapiException
	 */
	private void renderAnnotations(AggregatedResults reviewResult, final File outFile) throws HammurapiException {
		Iterator ait=reviewResult.getAnnotations().iterator();
        while (ait.hasNext()) {
        	final Annotation annotation=(Annotation) ait.next();
        	annotation.render(new AnnotationContext() {
				public FileEntry getNextFile(String extension) throws HammurapiException {
					try {
						final File ret=File.createTempFile(outFile.getName()+"_"+annotation.getName(),extension,outFile.getParentFile());
						return new AnnotationContext.FileEntry() {
							public File getFile() {
								return ret;
							}
	
							public String getPath() {
								return ret.getName();
							}						
						};
					} catch (IOException e) {
						throw new HammurapiException(e);
					}
				}

				public String getExtension() {					
					return Output.this.getExtension();
				}

				public Object getParameter(String name) throws BuildException {
					AnnotationConfig ac=(AnnotationConfig) annotationConfigs.get(annotation.getName());
					if (ac==null) {
						return null;
					}
					
					return ac.getParameter(name);
				}

				public boolean isEmbeddedStyle() {
					return Output.this.isEmbeddedStyle();
				}
        	});
        }
	}

	private String getRelativePath(String packageName) throws HammurapiException {
        if (javaDocDir==null) {
            return null;
        }
        
        try {
            StringTokenizer ost=new StringTokenizer(getOutDir().getCanonicalPath(), File.separator);
            StringTokenizer jst=new StringTokenizer(javaDocDir.getCanonicalPath(), File.separator);
            StringBuffer upPath=new StringBuffer("../");
            StringBuffer downPath=new StringBuffer();
            while (ost.hasMoreTokens() && jst.hasMoreTokens()) {
                String ot=ost.nextToken();
                String jt=jst.nextToken();
                if (!ot.equals(jt)) {
                    upPath.append("../");
                    downPath.append(jt);
                    downPath.append("/");
                    break;
                }
            }

            while (ost.hasMoreTokens()) {
                upPath.append("../");
                ost.nextToken();
            }

            while (jst.hasMoreTokens()) {
                downPath.append(jst.nextToken());
                downPath.append("/");
            }
            
            StringTokenizer pst=new StringTokenizer(packageName, ".");
            while (pst.hasMoreTokens()) {
                upPath.append("../");
                downPath.append(pst.nextToken());
                downPath.append("/");
            }

            upPath.append(downPath);
            return upPath.toString();                
        } catch (IOException e) {
            return null;
        }
    }
     
    private String getRelativePath() throws HammurapiException {
        if (javaDocDir==null) {
            return null;
        }
        
        try {
            StringTokenizer ost=new StringTokenizer(getOutDir().getCanonicalPath(), File.separator);
            StringTokenizer jst=new StringTokenizer(javaDocDir.getCanonicalPath(), File.separator);
            StringBuffer upPath=new StringBuffer();
            StringBuffer downPath=new StringBuffer();
            while (ost.hasMoreTokens() && jst.hasMoreTokens()) {
                String ot=ost.nextToken();
                String jt=jst.nextToken();
                if (!ot.equals(jt)) {
                    upPath.append("../");
                    downPath.append(jt);
                    downPath.append("/");
                    break;
                }
            }

            while (ost.hasMoreTokens()) {
                upPath.append("../");
                ost.nextToken();
            }

            while (jst.hasMoreTokens()) {
                downPath.append(jst.nextToken());
                downPath.append("/");
            }
            
            upPath.append(downPath);
            return upPath.toString();                
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * @pag:todo Should calculate relative path to JavaDoc dir.
     */         
    private String getJavaDocPath(ReviewResults ru) throws HammurapiException {
        if (javaDocDir==null) {
            return null;
        }
        
		if (!javaDocDir.exists()) {
		    throw new HammurapiException("JavaDoc directory does not exist");
		}
		
		String packageName = ru.getCompilationUnit().getPackage().getName();
		String path=getRelativePath(packageName);
		if (path==null) {
		    path=javaDocDir.getAbsolutePath()+'/'+packageName.replace('.', '/')+"/";
		}
		
		return path;         
    }
    
    /**
     * @pag:todo Should calculate relative path to JavaDoc dir.
     */         
    private String getJavaDocPath(CompositeResults packageResults) throws HammurapiException {
        if (javaDocDir==null) {
            return null;
        }
        
		if (!javaDocDir.exists()) {
		    throw new HammurapiException("JavaDoc directory does not exist");
		}
		
		String path=getRelativePath(packageResults.getName());
		if (path==null) {
		    path=javaDocDir.getAbsolutePath()+'/'+packageResults.getName().replace('.', '/')+'/';
		}
		
		return path+"package-summary.html";        
    }
    
    private static TimeIntervalCategory tic=MeasurementCategoryFactory.getTimeIntervalCategory(Output.class);
    
    private void render(AbstractRenderer renderer, String styleName, String inspectorsPath, String javaDocPath, File outFile) 
    throws HammurapiException {    	
    	long start=tic.getTime();
        File outFileParent=outFile.getParentFile();
        if (!outFileParent.exists()) {
            if (!outFileParent.mkdirs()) {
                throw new HammurapiException("Can't create "+outFileParent.getAbsolutePath());
            }
        }
        
        renderer.setEmbeddedStyle(embeddedStyle);
        
        StyleSheetEntry sse=(StyleSheetEntry) styleSheets.get(styleName);
        if (sse==null) {
        	throw new HammurapiException("Stylesheet entry with name '"+styleName +"' not found");
        }
        
        try {
			sse.setParameters(task.getProject(), renderer);
		} catch (ConfigurationException ce) {
			throw new HammurapiException("setParameters() failed", ce);
		}
        
        if (inspectorsPath!=null) {
            renderer.setParameter("inspectorsPath", inspectorsPath);
        }
        
        if (javaDocPath!=null) {
            renderer.setParameter("javaDocPath", javaDocPath);
        }
        
        try {
            if (sse.getFile()==null) {
                renderer.render(new FileOutputStream(outFile));
            } else {
                renderer.render(new FileInputStream(sse.getFile()), new FileOutputStream(outFile));
            }
        } catch (FileNotFoundException e) {
            throw new HammurapiException(e.toString(), e);
        } catch (RenderingException e) {
            throw new HammurapiException(e.toString(), e);
		}
        tic.addInterval("render", start);
    }
    
    private void writeUnitDoc(ReviewResults reviewResult, File outFile, String inspectorsPath, String javaDocPath) throws HammurapiException {
        task.getProject().log("Writing "+outFile.getAbsolutePath(), Project.MSG_VERBOSE);        
        render(new ReviewResultsRenderer(new RenderRequest(reviewResult)), "compilation-unit", inspectorsPath, javaDocPath, outFile);
    }
        
    public void onSummary(CompositeResults summary, InspectorSet inspectorSet) throws HammurapiException {        
        if (dir==null) {
            throw new HammurapiException("dir attribute is mandatory");
        }
        
        File outDir=FileUtils.newFileUtils().resolveFile(task.getProject().getBaseDir(), dir);
        if (!outDir.exists()) {
            throw new HammurapiException("Output directory does not exist: "+outDir.getAbsolutePath());
        }
        
        if (!outDir.isDirectory()) {
            throw new HammurapiException("Not a directory: "+outDir.getAbsolutePath());
        }
        
        String javaDocPath=javaDocDir==null ? null : getRelativePath()+"index.html";
        File summaryFile = new File(outDir, "summary"+extension);
        renderAnnotations(summary, summaryFile);
		RenderRequest summaryRenderRequest = new RenderRequest(ReportMixer.mix(summary, task.reviewDescription));
        render(new ReportRenderer(summaryRenderRequest, null), "summary", null, javaDocPath, summaryFile);
        render(new CompositeResultsRenderer(summaryRenderRequest, "leftPanel"), "left-panel", null, javaDocPath, new File(outDir, "leftPanel"+extension));
        render(new CompositeResultsRenderer(summaryRenderRequest, "waivedViolations"), "waived-violations", null, javaDocPath, new File(outDir, "waivedViolations"+extension));
        render(new InspectorSetRenderer(new RenderRequest(inspectorSet)), "inspector-set", null, null, new File(outDir, "inspectors"+extension));        

        Iterator descriptors=inspectorSet.getDescriptors().iterator();
		while (descriptors.hasNext()) {
			InspectorDescriptor d =(InspectorDescriptor) descriptors.next();
			render(new InspectorDescriptorRenderer(new RenderRequest(d)), "inspector-descriptor", null, null, new File(outDir, "inspectors/inspector_" + d.getName() + extension));
		}

        if (".HTML".equalsIgnoreCase(extension)) {
            writeFrame(outDir);
        }
        
        Iterator it=summary.getSeveritySummary().values().iterator();
        while (it.hasNext()) {
        	Iterator iit=((Map) it.next()).values().iterator();
        	while (iit.hasNext()) {
        		InspectorSummary is=(InspectorSummary) iit.next();
        		if (is.getLocations()!=null) {
        			render(new InspectorSummaryRenderer(new RenderRequest(is)), "inspector-summary", "source/", null, new File(outDir, "summary_"+is.getName()+extension));
        		}
        	}
        }
               
        it=summary.getMetrics().values().iterator();
        while (it.hasNext()) {
    		Metric metric = (Metric) it.next();
    		if (metric.getMeasurements()!=null) {
    			render(new HammurapiMetricRenderer(new RenderRequest(metric)), "metric-details", "source/", null, new File(outDir, "metric_details_"+metric.getName()+extension));
    		}
        }        
    }
    
    private void writeFrame(File outDir) throws HammurapiException {
        try {
            byte[] buf=new byte[4096];
            String resourceName="/"+getClass().getName().replace('.', '/')+"!report.html";
            InputStream in=getClass().getResourceAsStream(resourceName);
            if (in!=null) {
                FileOutputStream out=new FileOutputStream(new File(outDir, "report.html"));
                int l;
                while ((l=in.read(buf))!=-1) {
                    out.write(buf,0,l);
                }
                out.close();
                in.close();
            }
        } catch (IOException e) {
            throw new HammurapiException("Can't write report.html: "+e);
        }
    }
        
    public void onPackage(CompositeResults packageResults) throws HammurapiException {
        if (dir==null) {
            throw new HammurapiException("dir attribute is mandatory");
        }
        
        File outDir=FileUtils.newFileUtils().resolveFile(task.getProject().getBaseDir(), dir);
        if (!outDir.exists()) {
            throw new HammurapiException("Output directory does not exist: "+outDir.getAbsolutePath());
        }
        
        if (!outDir.isDirectory()) {
            throw new HammurapiException("Not a directory: "+outDir.getAbsolutePath());
        }
        
        String summaryPath="source/"+packageResults.getName().replace('.', '/')+"/.summary";
        
        int count=0;
        for (int i=0; i<summaryPath.length(); i++) {
            if ('/'==summaryPath.charAt(i)) {
                count++;
            }
        }
        
        StringBuffer inspectorsPath=new StringBuffer();
        while (count-- > 0) {
            inspectorsPath.append("../");
        }        
        
        File packageSummaryFile = new File(outDir, summaryPath+extension);
        renderAnnotations(packageResults, packageSummaryFile);
		render(new CompositeResultsRenderer(new RenderRequest(packageResults), "packageSummary"), "package", inspectorsPath.toString(), getJavaDocPath(packageResults), packageSummaryFile);
        
        Iterator it=packageResults.getSeveritySummary().values().iterator();
        while (it.hasNext()) {
        	Iterator iit=((Map) it.next()).values().iterator();
        	while (iit.hasNext()) {
        		InspectorSummary is=(InspectorSummary) iit.next();
        		if (is.getLocations()!=null) {
        			render(new InspectorSummaryRenderer(new RenderRequest(is)), "inspector-summary", inspectorsPath.toString()+"source/", null, new File(outDir, summaryPath+"_"+is.getName()+extension));
        		}
        	}
        }
        
        it=packageResults.getMetrics().values().iterator();
        while (it.hasNext()) {
    		Metric metric = (Metric) it.next();
    		if (metric.getMeasurements()!=null) {
    			render(new HammurapiMetricRenderer(new RenderRequest(metric)), "metric-details", inspectorsPath.toString()+"source/", null, new File(outDir, summaryPath+"_metric_details_"+metric.getName()+extension));
    		}
        }                
    }    
    
    private final Map annotationConfigs=new HashMap();
    
    /**
     * Annotation configuration
     * @ant.non-required
     * @param annotationConfig
     */
    public void addConfiguredAnnotationConfig(AnnotationConfig annotationConfig) {
    	annotationConfigs.put(annotationConfig.getName(), annotationConfig);
    }
	/**
	 * @return Returns the extension.
	 */
	private String getExtension() {
		return extension;
	}
	
	/**
	 * @return Returns the embeddedStyle.
	 */
	private boolean isEmbeddedStyle() {
		return embeddedStyle;
	}
	
	public void onBegin(InspectorSet inspectorSet) throws HammurapiException {
        File outDir=FileUtils.newFileUtils().resolveFile(task.getProject().getBaseDir(), dir);
        if (!outDir.exists()) {
            if (!outDir.mkdirs()) {
                throw new HammurapiException("Output directory cannot be created: "+outDir.getAbsolutePath());
            }
        }
        
        if (!outDir.isDirectory()) {
            throw new HammurapiException("Not a directory: "+outDir.getAbsolutePath());
        }        
	}
}
