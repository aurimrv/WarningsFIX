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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.xpath.CachedXPathAPI;
import org.hammurapi.render.dom.DetailedResultsRenderer;
import org.hammurapi.results.persistent.jdbc.BaselineSetupViolationFilter;
import org.hammurapi.results.persistent.jdbc.BaselineViolationFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import com.pavelvlasov.ant.ObjectEntry;
import com.pavelvlasov.ant.XmlSourceEntry;
import com.pavelvlasov.jsel.RevisionMapper;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;
import com.pavelvlasov.review.Signed;
import com.pavelvlasov.util.ClassResourceLoader;
import com.pavelvlasov.xml.dom.AbstractDomObject;

/**
 * @author Pavel Vlasov
 *
 * @version $Revision: 1.9 $
 */
public class TaskBase extends Task {

	protected void deleteFile(File file) {
		if (file!=null) {
			if (file.isDirectory()) {
				File[] children=file.listFiles();
				if (children!=null) {
					for (int i=0; i<children.length; i++) {
						deleteFile(children[i]);
					}
				}
			}
			
			if (file.isFile() || file.isDirectory()) {
				file.delete();
			}
		}
	}

	/**
	 * @return  Returns the reviewAcceptorEntries.
	 */
	protected List getReviewAcceptorEntries() {
		return reviewAcceptorEntries;
	}

	/**
	 * @return  Returns the severityThreshold.
	 */
	protected Integer getSeverityThreshold() {
		return severityThreshold;
	}

	/**
	 * @return  Returns the dpmoThreshold.
	 */
	protected Double getSigmaThreshold() {
		return sigmaThreshold;
	}

	/**
	 * @return  Returns the dpmoThreshold.
	 */
	protected Integer getDpmoThreshold() {
		return dpmoThreshold;
	}

	/**
	 * @return  Returns the failOnWarnings.
	 */
	protected boolean isFailOnWarnings() {
		return failOnWarnings;
	}
	
	private File unpackDir;
	
	/**
	 * If this attribute is set then HAR archive will be unpacked 
	 * in the given directory instead of a temporary one.
	 * @ant.non-required
	 * @param unpackDir
	 */
	public void setUnpackDir(File unpackDir) {
		this.unpackDir=unpackDir;
	}

	/**
	 * @return  Returns the debugType.
	 * @ant.ignore
	 */
	public String getDebugType() {
		return debugType;
	}

	protected Collection srcFiles = new LinkedList();

	protected static void loadEmbeddedInspectors(InspectorSet inspectorSet) throws BuildException, HammurapiException {
		ClassResourceLoader crl=new ClassResourceLoader(TaskBase.class);
		InputStream inspectorStream=crl.getResourceAsStream(null, null, "xml");
		if (inspectorStream==null) {
			throw new BuildException("Cannot load embedded inspectors");
		}
		
		DomInspectorSource source=new DomInspectorSource(inspectorStream, "Hammurapi.jar");
		source.loadInspectors(inspectorSet);
	}

	/**
	 * @param options
	 */
	protected static void populateOptions(Options options) {
		Option waiverStubsOption=OptionBuilder
	    .withArgName("waiverStubs")
	    .hasArg()
	    .withDescription("Where to output waiver stubs")
	    .isRequired(false)
	    .create("w");
	    
	    options.addOption(waiverStubsOption);
	    
        Option databaseOption=OptionBuilder
        .withDescription("Database name")
		.withArgName("local database")
		.hasArg()
        .isRequired(false)
        .create("D");
        
        options.addOption(databaseOption);
        
        Option includeInspectorOption=OptionBuilder
        .withDescription("Enable inspector")
		.withArgName("inspector name")
		.hasArg()
        .isRequired(false)
        .create("I");
        
        options.addOption(includeInspectorOption);
        
        Option configFileOption=OptionBuilder
        .withDescription("Config file")
		.withArgName("file")
		.hasArg()
        .isRequired(false)
        .create("m");
        
        options.addOption(configFileOption);
        
        Option configUrlOption=OptionBuilder
        .withDescription("Config url")
		.withArgName("url")
		.hasArg()
        .isRequired(false)
        .create("q");
        
        options.addOption(configUrlOption);
        
        Option unpackDirOption=OptionBuilder
        .withDescription("Unpack directory")
		.withArgName("directory")
		.hasArg()
        .isRequired(false)
        .create("r");
        
        options.addOption(unpackDirOption);
        
        Option excludeInspectorOption=OptionBuilder
        .withDescription("Disable inspector")
		.withArgName("inspector name")
		.hasArg()
        .isRequired(false)
        .create("X");
        
        options.addOption(excludeInspectorOption);                
        
        Option archiveFileOption=OptionBuilder
        .withArgName("archive")
        .hasArg()
        .withDescription("Hammurapi archive")
        .isRequired(false)
        .create("A");
        
        options.addOption(archiveFileOption);
	    Option waiversFileOption=OptionBuilder
	    .withArgName("waivers file")
	    .hasArg()
	    .withDescription("Waivers File")
	    .isRequired(false)
	    .create("W");
	    
	    options.addOption(waiversFileOption);
	    
        Option forceOption=OptionBuilder
        .withDescription("Force reviews of unchanged files")
        .isRequired(false)
        .create("f");
        
        //Anu 20050701 : Baselining.Moved from HammurapiTask.java
        Option baseliningOption=OptionBuilder
        .withArgName("off|on|set")
        .hasArg()
        .withDescription("Baselining mode")
        .isRequired(false)
        .create("B");
        
        options.addOption(forceOption);
        
        Option forceOnWarningsOption=OptionBuilder
        .withDescription("Do not force reviews of files with warnings")
        .isRequired(false)
        .create("k");
        
        options.addOption(forceOnWarningsOption);
        
        Option doNotEvictOption=OptionBuilder
        .withDescription("Evict bad inspectors")
        .isRequired(false)
        .create("E");
        
        options.addOption(doNotEvictOption);
        
	    Option waiversUrlOption=OptionBuilder
	    .withArgName("waivers url")
	    .hasArg()
	    .withDescription("Waivers URL")
	    .isRequired(false)
	    .create("U");
	    
	    options.addOption(waiversUrlOption);
	    
	    Option classPathOption=OptionBuilder
	    .withArgName("classpath")
	    .hasArg()
	    .withDescription("ClassPath")
	    .isRequired(false)
	    .create("c");
	    
	    options.addOption(classPathOption);
	    
	    Option sigmaThresholdOption=OptionBuilder
	    .withArgName("sigmaThreshold")
	    .hasArg()
	    .withDescription("Sigma threshold")
	    .isRequired(false)
	    .create("s");
	    
	    options.addOption(sigmaThresholdOption);
	    
	    Option dpmoThresholdOption=OptionBuilder
	    .withArgName("dpmoThreshold")
	    .hasArg()
	    .withDescription("DPMO Threshold")
	    .isRequired(false)
	    .create("d");
	    
	    options.addOption(dpmoThresholdOption);
	    
	    Option severityThresholdOption=OptionBuilder
	    .withArgName("severityThreshold")
	    .hasArg()
	    .withDescription("Severity threshold")
	    .isRequired(false)
	    .create("S");
	    
	    options.addOption(severityThresholdOption);
	    
	    Option noEmbeddedInspectorsOption=OptionBuilder
	    .withDescription("Do not load embedded inspectors")
	    .isRequired(false)
	    .create("e");
	    
	    options.addOption(noEmbeddedInspectorsOption);
	    
	    Option inspectorsFileOption=OptionBuilder
	    .withArgName("inspectorsFile")
	    .hasArg()
	    .withDescription("Inspectors file")
	    .isRequired(false)
	    .create("i");
	    
	    options.addOption(inspectorsFileOption);
	    
	    Option inspectorsURLOption=OptionBuilder
	    .withArgName("inspectorsURL")
	    .hasArg()
	    .withDescription("Inspectors URL")
	    .isRequired(false)
	    .create("u");
	    
	    options.addOption(inspectorsURLOption);
	    
	    Option titleOption=OptionBuilder
	    .withArgName("title")
	    .hasArg()
	    .withDescription("Report title")
	    .isRequired(false)
	    .create("T");
	    
	    options.addOption(titleOption);
	    
	    Option debugTypeOption=OptionBuilder
	    .withArgName("debug type")
	    .hasArg()
	    .withDescription("Jsel type to debug")
	    .isRequired(false)
	    .create("t");
	    
	    options.addOption(debugTypeOption);
	    
	    Option listenerOption=OptionBuilder
	    .withArgName("class name")
	    .hasArg()
	    .withDescription("Review listener")
	    .isRequired(false)
	    .create("l");
	    
	    options.addOption(listenerOption);
	            
	    Option debugOption=OptionBuilder
	    .withDescription("Debug")
	    .isRequired(false)
	    .create("g");
	    
	    options.addOption(debugOption);
	    
	    Option verboseOption=OptionBuilder
	    .withDescription("Verbose")
	    .isRequired(false)
	    .create("v");
	    
	    options.addOption(verboseOption);
	    
	    Option xmlOption=OptionBuilder
	    .withDescription("Output XML")
	    .isRequired(false)
	    .create("x");
	                    
	    options.addOption(xmlOption);
	    
	    Option suppressOutputOption=OptionBuilder
	    .withDescription("Suppress output")
	    .isRequired(false)
	    .create("o");
	                                            
	    options.addOption(suppressOutputOption);
	    
        Option descriptionOption=OptionBuilder
        .withDescription("Review description")
		.withArgName("description")
		.hasArg()
        .isRequired(false)
        .create("y");
        
        options.addOption(descriptionOption);
        
	    Option helpOption=OptionBuilder.withDescription("Print this message").isRequired(false).create("h");        
	    options.addOption(helpOption);
	}

	protected static void printHelpAndExit(Options options) {
	    HelpFormatter formatter=new HelpFormatter();
	    formatter.printHelp("Usage: hammurapi [options] <output dir> <source files/dirs>", options, false);
	    System.exit(1);
	}

	/**
	 * Class name to debug
	 * @ant.non-required  
	 */
	public void setDebugType(String debugType) {
		this.debugType=debugType;
	}

	/**
	 * Load embedded inspectors. Defaults to true.
	 * @ant.non-required  
	 */
	public void setEmbeddedInspectors(boolean embeddedInspectors) {
	    this.embeddedInspectors=embeddedInspectors;
	}

	private String debugType;
	protected boolean embeddedInspectors = true;
	protected List srcFileSets = new LinkedList();

	/**
	 * Source files fileset.
	 * @ant.non-required
	 */
	public FileSet createSrc() {
	    FileSet ret=new HammurapiFileSet("**/*.java");
	    srcFileSets.add(ret);
	    return ret;
	}

	protected void setHadExceptions() {
		hadExceptions=true;
	}

	/**
	 * @param collection
	 * @throws FileNotFoundException
	 * @throws RenderingException
	 */
	protected void writeWaiverStubs(final Collection rejectedViolations) throws RenderingException, FileNotFoundException {
		if (waiverStubs!=null) {
			class WaiverStubsRenderer extends AbstractRenderer {
				WaiverStubsRenderer() {
					super(new RenderRequest(rejectedViolations));
				}
	
				public Element render(Document document) {
					Element ret=document.createElement("waivers");
					Iterator it=rejectedViolations.iterator();
					final Date now=new Date();
					while (it.hasNext()) {
						final Violation violation=(Violation) it.next();
						
						StringBuffer comment=new StringBuffer();
						comment.append("Source: ");
						comment.append(violation.getSource().getSourceURL());
						
						comment.append("\nLine: ");
						comment.append(violation.getSource().getLine());
						
						comment.append("\nCol: ");
						comment.append(violation.getSource().getColumn());
						
						comment.append("\nDescription: ");
						comment.append(violation.getDescriptor().getDescription());
						
						comment.append("\nMesssage: ");
						comment.append(violation.getMessage());						
						
						ret.appendChild(document.createComment(comment.toString()));
						
						Waiver waiver=new Waiver() {
	
							public String getInspectorName() {
								return violation.getDescriptor().getName();
							}
	
							public Date getExpirationDate() {
								return now;
							}
	
							public String getReason() {
								return "*** Put reason here ***";
							}
	
							public boolean waive(Violation violation, boolean peek) {
								// This 'waiver' will never waive anything, it is used only for rendering
								return false;
							}
	
							public boolean isActive() {
								// This 'waiver' will never waive anything, it is used only for rendering
								return false;
							}
							
							Collection signatures=new HashSet();
							
							{
								if (violation.getSource() instanceof Signed) { 
									signatures.add(((Signed) violation.getSource()).getSignature());
								}
							}
	
							public Collection getSignatures() {
								return signatures;
							}
						};
						ret.appendChild(DetailedResultsRenderer.renderWaiver(waiver, document));
					}
					return ret;				
				}				
			}
			WaiverStubsRenderer renderer=new WaiverStubsRenderer();
			renderer.setEmbeddedStyle(false);
			renderer.render(new FileOutputStream(waiverStubs));			
		}
	}

	protected File processArchive() {
		if (archive==null) {
			return null; 
		}
		
		String tmpDirProperty=System.getProperty("java.io.tmpdir");
		File tmpDir=tmpDirProperty==null ? new File(".") : new File(tmpDirProperty);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		String prefix = "har_"+sdf.format(new Date());		
		File workDir = unpackDir==null ? new File(tmpDir, prefix) : unpackDir;
		
		for (int i=0; unpackDir==null && workDir.exists(); i++) {
			workDir=new File(tmpDir, prefix+"_"+Integer.toString(i, Character.MAX_RADIX));
		}
		
		if (workDir.exists() || workDir.mkdir()) {
			try {
				ZipFile zipFile=new ZipFile(archive);
				Enumeration entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry=(ZipEntry) entries.nextElement();
					if (!entry.getName().endsWith("/")) {
						File outFile=new File(workDir, entry.getName().replace('/', File.separatorChar));
						if (!outFile.getParentFile().exists() && !outFile.getParentFile().mkdirs()) {
							throw new BuildException("Directory does not exist and cannot be created: "+outFile.getParentFile().getAbsolutePath());
						}
						
						log("Archive entry "+entry.getName()+" unpacked to "+outFile.getAbsolutePath(), Project.MSG_DEBUG);
						
						byte[] buf=new byte[4096];
						int l;
						InputStream in=zipFile.getInputStream(entry);
						FileOutputStream fos=new FileOutputStream(outFile);
						while ((l=in.read(buf))!=-1) {
							fos.write(buf, 0, l);
						}
						in.close();				
						fos.close();							
					}						
				}
				zipFile.close();
				
				File configFile=new File(workDir, "config.xml");
				if (configFile.exists() && configFile.isFile()) {
					Document configDoc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile);
					processConfig(workDir, configDoc.getDocumentElement());
				} else {
					throw new BuildException("Archive configuration file does not exist or is not a file");
				}
			} catch (ZipException e) {
				throw new BuildException(e.toString(), e);
			} catch (IOException e) {
				throw new BuildException(e.toString(), e);
			} catch (SAXException e) {
				throw new BuildException(e.toString(), e);
			} catch (ParserConfigurationException e) {
				throw new BuildException(e.toString(), e);
			} catch (FactoryConfigurationError e) {
				throw new BuildException(e.toString(), e);
			}				
		} else {
			throw new BuildException("Could not create directory "+workDir.getAbsolutePath());
		}
		return unpackDir==null ? workDir : null;
	}

	/**
     * @param workDir
     * @param configDoc
     * @throws ParseException
     * @throws TransformerException
     */
    private void processConfig(File workDir, Element config) {
    	if (config!=null) {
	        try {
		        setAttributes(config);
		        
		        CachedXPathAPI cxpa=new CachedXPathAPI();
		        NodeIterator nit=cxpa.selectNodeIterator(config, "sources/source");
		        Element element;
		        while ((element=(Element) nit.nextNode())!=null) {
		        	srcFiles.add(new File(workDir, AbstractDomObject.getElementText(element)));
		        }
		        
		        nit=cxpa.selectNodeIterator(config, "classpath/path");
		        while ((element=(Element) nit.nextNode())!=null) {
		        	File cpe = new File(workDir, AbstractDomObject.getElementText(element));
		        	if (cpe.exists()) {		        		
		        		createClasspath().setLocation(cpe);
		        		log("File "+cpe.getAbsolutePath()+" added to classpath", Project.MSG_DEBUG);
		        	} else {
		        		throw new BuildException("Classpath element "+cpe.getAbsolutePath()+" does not exist");
		        	}
		        }
	        } catch (TransformerException e) {
	            throw new BuildException("Cannot load config", e);
	        }
    	}
    }

    /**
	 * @param config
	 * @throws ParseException
	 */
	protected void setAttributes(Element config) {
		if (config.hasAttribute("title")) {
			setTitle(config.getAttribute("title"));
		}
		
		if (config.hasAttribute("dpmo-threshold")) {
		    setDpmoThreshold(Integer.parseInt(config.getAttribute("dpmo-threshold")));
		}
		
		if (config.hasAttribute("sigma-threshold")) {
		    setSigmaThreshold(Double.parseDouble(config.getAttribute("sigma-threshold")));
		}
		
		if (config.hasAttribute("severity-threshold")) {
		    setSeverityThreshold(Integer.parseInt(config.getAttribute("severity-threshold")));
		}
		
		if (config.hasAttribute("force")) {
			setForce("yes".equals(config.getAttribute("force")));
		}
		
		if (config.hasAttribute("force-on-warnings")) {
			setForceOnWarnings("yes".equals(config.getAttribute("force-on-warnings")));
		}
		
		if (config.hasAttribute("review-description")) {
			setReviewDescription(config.getAttribute("review-description"));
		}
		
		//Anu : 20050701 for baselining
		if (config.hasAttribute("baselining")) {
			setBaselining(config.getAttribute("baselining"));
		}
	}

	protected boolean suppressLogo;

	/**
	 * Defines output
	 * @ant.non-required
	 */
	public Output createOutput() {
		Output output=new Output(this);
		outputs.add(output);
		return output;
	}

	/**
	 * Defines history output, which stores review summary
	 * into database.
	 * @ant.non-required
	 */
	public HistoryOutput createHistoryOutput() {
		HistoryOutput historyOutput=new HistoryOutput();
		outputs.add(historyOutput);
		return historyOutput;
	}
	
	protected List outputs = new LinkedList();
	protected boolean hadExceptions;

	/**
	 * Maybe creates a nested classpath element.
	 * @ant :non-required
	 */
	public Path createClasspath() {
		if (classPath == null) {
			classPath = new Path(project);
		}
		return classPath.createPath();
	}

	public void setClassPath(Path classPath) {
		if (this.classPath == null) {
			this.classPath = classPath;
		} else {
			this.classPath.append(classPath);
		}
	}

	/**
	 * Classpath for loading classes.
	 * @ant :non-required 
	 */
	protected Path classPath;

	/**
	 * Defines inspector
	 * @ant.non-required
	 */
	public InspectorEntry createInspector() {
		InspectorEntry inspectorEntry=new InspectorEntry();
		inspectors.add(inspectorEntry);
		return inspectorEntry;
	}

	protected List inspectors = new LinkedList();

	/**
	 * Defines waivers source
	 * @ant.non-required  
	 */
	public WaiverSourceEntry createWaivers() {
		WaiverSourceEntry ret=new WaiverSourceEntry();
		ret.setProject(getProject());
		waivers.add(ret);
		return ret;
	}
	
	private Collection configs=new ArrayList();
	
	protected void processConfigs(File baseDir) {
	    Iterator it=configs.iterator();
	    while (it.hasNext()) {
	        XmlSourceEntry xse=(XmlSourceEntry) it.next();
	        processConfig(xse.getFile()==null ? baseDir : xse.getFile(), xse.getDocumentElement());
	    }
	}

	/**
	 * Configuration source.
	 * Task can be configured from multiple sources.
	 * @ant.non-required  
	 */
	public XmlSourceEntry createConfig() {
		XmlSourceEntry ret=new XmlSourceEntry();
		configs.add(ret);
		ret.setProject(getProject());
		return ret;
	}
	
	/**
	 * Defines inspector source
	 * @ant.non-required  
	 */
	public InspectorSourceEntry createInspectors() {
		InspectorSourceEntry ret=new InspectorSourceEntry();
		inspectors.add(ret);
		ret.setProject(getProject());
		return ret;
	}

	/**
	 * Hammurapi archive to process.
	 * @ant.non-required
	 * @param archive
	 */
	public void setArchive(File archive) {
		this.archive=archive;
	}

	private File archive;

	/**
	 * Fail build if project DPMO is above the threshold.
	 * @ant.non-required
	 */
	public void setDpmoThreshold(int dpmoThreshold) throws BuildException {
		this.dpmoThreshold=new Integer(dpmoThreshold);
	}

	/**
	 * Fail build if project Sigma is below the threshold.
	 * @ant.non-required
	 */
	public void setSigmaThreshold(double sigmaThreshold) throws BuildException {
		this.sigmaThreshold=new Double(sigmaThreshold);
	}

	protected Collection waivers = new LinkedList();
	private Integer dpmoThreshold;
	private Double sigmaThreshold;

	/**
	 * Review listener
	 * @ant.non-required
	 */
	public void addConfiguredListener(ListenerEntry listener) {
		listenerEntries.add(listener);
	}

	/**
	 * Review acceptor.
	 * @ant.non-required
	 */
	public void addConfiguredReviewAcceptor(ReviewAcceptorEntry reviewAcceptor) throws BuildException {
		reviewAcceptorEntries.add(reviewAcceptor);
	}

	private List reviewAcceptorEntries = new LinkedList();

	/**
	 * Fail build as soon as there is an exception. Default is false.
	 * @ant.non-required
	 */
	public void setFailOnFirstException(boolean failOnFirstException) {
		this.failOnFirstException=failOnFirstException;
	}

	protected boolean failOnFirstException = false;

	/**
	 * Fail build if there have been warnings. Default is true.
	 * @ant.non-required
	 */
	public void setFailOnWarnings(boolean failOnWarnings) {
		this.failOnWarnings=failOnWarnings;
	}

	private boolean failOnWarnings = true;

	/**
	 * Fail build on violations with severity levels lower or equal to the threshold.
	 * @ant.non-required
	 */
	public void setSeverityThreshold(int severityThreshold) {
		this.severityThreshold=new Integer(severityThreshold);
	}

	/**
	 * @ant.non-required
	 * @param title
	 */
	public void setTitle(String title) {
		this.title=title;
	}

	protected String title = "Summary "+new Date();

	/**
	 * @param options
	 * @param line
	 * @param task
	 * @param project
	 */
	protected void configure(Options options, CommandLine line) {
		String[] largs=line.getArgs();
	    if (largs.length==0) {
	    	System.out.println("Output dir has to be provided");
	    	printHelpAndExit(options);
	    } 
	    
	    if (!line.hasOption('o')) {
	        new File(largs[0]).mkdirs();
	        Output output=createOutput();
	        output.setDir(largs[0]);
	        
	        if (line.hasOption('x')) {
	        	output.setEmbeddedStyle(false);
	        	output.setExtension(".xml");
	        }
	    }
	    
        if (largs.length==1 && !line.hasOption('A')) {
	    	System.out.println("At least one source directory or archive must be provided");
	    	printHelpAndExit(options);
	    } 
	    		
        if (line.hasOption('y')) {
        	setReviewDescription(line.getOptionValue('y'));
        }
        
	    for (int i=1; i<largs.length; i++) {
			File file = new File(largs[i]);
			if (file.isFile()) {
				srcFiles.add(file);
			} else if (file.isDirectory()) {
				createSrc().setDir(file);
			}
	    }
	
		String[] values=line.getOptionValues('c');
		for (int i=0; values!=null && i<values.length; i++) {
			createClasspath().append(new Path(project, values[i]));
		}
		
		values=line.getOptionValues('m');
		for (int i=0; values!=null && i<values.length; i++) {
		    createConfig().setFile(new File(values[i]));		    
		}
		
		values=line.getOptionValues('q');
		for (int i=0; values!=null && i<values.length; i++) {
		    createConfig().setURL(values[i]);		    
		}
		
		values=line.getOptionValues('I');
		for (int i=0; values!=null && i<values.length; i++) {
			InspectorEntry ie = createInspector();
			ie.setName(values[i]);
			ie.setEnabled(true);
		}
		
		values=line.getOptionValues('X');
		for (int i=0; values!=null && i<values.length; i++) {
			InspectorEntry ie = createInspector();
			ie.setName(values[i]);
			ie.setEnabled(false);
		}
		
		setEvictBadInspectors(line.hasOption('E'));
		
		setEmbeddedInspectors(!line.hasOption('e')); 
	
		if (line.hasOption('t')) {
			setDebugType(line.getOptionValue('t'));
		}
				
		if (line.hasOption('r')) {
			setUnpackDir(new File(line.getOptionValue('r')));
		}
				
		if (line.hasOption('T')) {
			setTitle(line.getOptionValue('T'));
		}
	
		BuildLogger logger = new DefaultLogger();
		logger.setMessageOutputLevel(Project.MSG_INFO);
		logger.setOutputPrintStream(System.out);
		logger.setErrorPrintStream(System.err);
		logger.setEmacsMode(false);
		
		if (line.hasOption('v')) {
			logger.setMessageOutputLevel(Project.MSG_VERBOSE);
		}
		
		if (line.hasOption('g')) {
			logger.setMessageOutputLevel(Project.MSG_DEBUG);
		}
		
		project.addBuildListener(logger);
		
		System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
		System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
		
		if (line.hasOption('w')) {
			setWaiverStubs(new File(line.getOptionValue('w')));
		}
		
		if (line.hasOption('s')) {
			setSigmaThreshold(Double.parseDouble(line.getOptionValue('s')));
		}
		
		if (line.hasOption('d')) {
			setDpmoThreshold(Integer.parseInt(line.getOptionValue('d')));
		}
		
		if (line.hasOption('S')) {
			setSeverityThreshold(Integer.parseInt(line.getOptionValue('S')));
		}
		        
        if (line.hasOption('f')) {
        	setForce(true);
        }
        
        if (line.hasOption('k')) {
        	setForceOnWarnings(false);
        }
        
		if (line.hasOption('D')) {
			setDatabase(new File(line.getOptionValue('D')));
		}
		
		values=line.getOptionValues('i');
		for (int i=0; values!=null && i<values.length; i++) {
			createInspectors().setFile(new File(values[i]));
		}
				
		values=line.getOptionValues('u');
		for (int i=0; values!=null && i<values.length; i++) {
			createInspectors().setURL(values[i]);
		}
		
		values=line.getOptionValues('l');
		for (int i=0; values!=null && i<values.length; i++) {
			ListenerEntry listenerEntry = new ListenerEntry();
			listenerEntry.setClassName(values[i]);
			addConfiguredListener(listenerEntry);
		}
		
		values=line.getOptionValues('W');
		for (int i=0; values!=null && i<values.length; i++) {
			createWaivers().setFile(new File(values[i]));
		}
				
		values=line.getOptionValues('U');
		for (int i=0; values!=null && i<values.length; i++) {
			createWaivers().setURL(values[i]);
		}
		
		if (line.hasOption('A')) {
			setArchive(new File(line.getOptionValue('A')));
		}	
		
		//Anu 20050701 : baselining
		if (line.hasOption('B')) {
			setBaselining(line.getOptionValue('B'));
		}
	}

	/**
	 * File to output waiver stubs for rejected waiver requests to. Selected waiver stubs can then be copied to waiver source.  Simplifies waiver creation
	 * @ant.non-required
	 * @param waiverStubs
	 */
	public void setWaiverStubs(File waiverStubs) {
		this.waiverStubs=waiverStubs;
	}

	/**
	 * Revision mapper. Must implement com.pavelvlasov.jsel.RevisionMapper  interface.
	 * @ant.non-required
	 * @return
	 */
	public ObjectEntry createRevisionMapper() {
		if (revisionMapper==null) {
			revisionMapper = new ObjectEntry() {			
				protected void validateClass(Class clazz) throws BuildException {
					super.validateClass(clazz);
					if (!RevisionMapper.class.isAssignableFrom(clazz)) {
						throw new BuildException(clazz.getName()+" doesn't implement "+RevisionMapper.class.getName());
					}
				}
			};
			return revisionMapper;
		} else {
			throw new BuildException("Revision mapper already defined");
		}
	}

	private File waiverStubs;
	private Integer severityThreshold;
	protected List listenerEntries = new LinkedList();
	ObjectEntry revisionMapper;
	protected boolean force = false;
	protected boolean evictBadInspectors = false;

	/**
	 * Remove inspector from inspector set if it throws an exception
	 * during review
	 * @param evictBadInspectors
	 * @ant.non-required
	 */
	public void setEvictBadInspectors(boolean evictBadInspectors) {
		this.evictBadInspectors=evictBadInspectors;
	}

	/**
	 * Force review even if the file is not changed
	 * @param force
	 * @ant.non-required
	 */
	public void setForce(boolean force) {
		this.force=force;
	}

	//Anu 20050701 : setBaselining method moved from HammurapiTask to TaskBase
	/**
	 * Sets baselining mode. Possible values:
	 * off (default) - no baselining, on - do not report
	 * violations stored in the baseline table, set - all violations
	 * from current scan are saved to the baseline table.
	 * The idea is to filter out all violations in
	 * preexisting code and report only new violations. 
	 * Not all violations can be filtered out, only thouse
	 * with signatures. Significant code modifications can surface some 
	 * baselined violation.  
	 * @ant.non-required
	 * @param baselineMode
	 */
	public void setBaselining(String baselineMode) {
		if ("off".equals(baselineMode)) {
			// Nothing.
		} else if ("on".equals(baselineMode)) {
			violationFilters.add(new BaselineViolationFilter());
		} else if ("set".equalsIgnoreCase(baselineMode)) {
			violationFilters.add(new BaselineSetupViolationFilter());
		} else {
			throw new BuildException("Invalid baselining mode: "+baselineMode);
		}
		
	}

	
	protected boolean forceOnWarnings = true;

	/**
	 * Force review of files with warnings, even if the file is not changed.
	 * Default is true
	 * @param 
	 * @ant.non-required
	 */
	public void setForceOnWarnings(boolean forceOnWarnings) {
		this.forceOnWarnings=forceOnWarnings;
	}

	protected File database;
	protected String reviewDescription;

	/**
	 * Description of review, e.g. release number. Appears in history annotation.
	 * @ant.non-required
	 * @param baseLine
	 */
	public void setReviewDescription(String reviewDescription) {
		this.reviewDescription=reviewDescription;
	}

	/**
	 * If this parameter is set then Hypersonic standalone database
	 * will be used instead of temporary database. You must set
	 * database name if you want to run incremental reviews.
	 * @ant.non-required
	 * @param database
	 */
	public void setDatabase(File database) {
		this.database=database;
	}

	protected boolean isForceOnWarnings() {
		return forceOnWarnings;
	}

	protected boolean isForce() {
		return force;
	}
	
	protected int tabSize=8;		

	/**
	 * Tab size in source files. Defaults to 8.
	 * @param tabSize The tabSize to set.
	 * @ant.non-required
	 */
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}

	protected Collection violationFilters = new ArrayList();
}
