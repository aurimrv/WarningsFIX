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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.ResultsFactory;
import org.hammurapi.results.quick.PackageTotal;
import org.hammurapi.results.simple.QuickResultsFactory;

import com.pavelvlasov.config.Component;
import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.impl.CompilationUnitImpl;
import com.pavelvlasov.logging.AntLogger;
import com.pavelvlasov.logging.Logger;
import com.pavelvlasov.sql.DataAccessObject;
import com.pavelvlasov.util.ClassResourceLoader;
import com.pavelvlasov.util.VisitorStack;
import com.pavelvlasov.util.VisitorStackSource;

/**
 * Performs automatic code reviews. Quick mode - doesn't store anything to the database, 
 * package and repository level inspectors are not invoked. 
 * <section name="Example" suppress-description="yes">
If you copy content of Hammurapi lib directory to ant lib directory then you can
invoke Hammurapi in the following way:
 <pre>
&lt;taskdef name="quickurappi" classname="org.hammurapi.QuickHammurapiTask" /&gt;<br/>
<br/>
&lt;quickurappi&gt;<br/>
    <tab/>&lt;src dir="src"/&gt;<br/>
    <tab/>&lt;output dir="review"/&gt;<br/>
&lt;/quickurappi&gt;</pre>
or, if you didn't copy jar files to Ant lib directory, use this syntax:
<pre>
&lt;taskdef name="quickurappi" classname="org.hammurapi.QuickHammurapiTask"&gt;<br/>
    <tab/>&lt;classpath&gt;<br/>
        <tab/><tab/>&lt;fileset dir="${hammurapi.home}/lib" includes="*.jar"/&gt;<br/>
    <tab/>&lt;/classpath&gt;<br/>
&lt;/taskdef&gt;<br/>
<br/>
&lt;quickurappi&gt;<br/>
    <tab/>&lt;src dir="src"/&gt;<br/>
    <tab/>&lt;output dir="review"/&gt;<br/>
&lt;/quickurappi&gt;
</pre>

</section>
 * @ant.element name="hammurapi" display-name="Automatic code review task"
 * @author Pavel Vlasov	
 * @version $Revision: 1.10 $
 */
public class QuickHammurapiTask extends TaskBase {
	public void execute() throws BuildException {
		if (!suppressLogo) {
			log("Quick Hammurapi 3.18.4 Copyright (C) 2004 Hammurapi Group");
		}
		
		File archiveTmpDir=processArchive();
		
		try {		
			long start=System.currentTimeMillis();
			
			Logger logger=new AntLogger(this);
			
			final VisitorStack[] visitorStack={null};
			final VisitorStackSource visitorStackSource=new VisitorStackSource() {
				public VisitorStack getVisitorStack() {
					return visitorStack[0];
				}
			};
			
			final SessionImpl session=new SessionImpl();
			
			InspectorSet inspectorSet=new InspectorSet(
					new InspectorContextFactory() {
						public InspectorContext newContext(InspectorDescriptor descriptor, Logger logger) {
							return new InspectorContextImpl(
									descriptor, 
									logger, 
									visitorStackSource, 
									session,
									violationFilters);
						}
					},
					logger);
			
			if (embeddedInspectors) {
				log("Loading embedded inspectors", Project.MSG_VERBOSE);
				loadEmbeddedInspectors(inspectorSet);
			}
			
			log("Loading inspectors", Project.MSG_VERBOSE);
			Iterator it=inspectors.iterator();
			while (it.hasNext()) {
				Object o=it.next();
				if (o instanceof InspectorSource) {
					((InspectorSource) o).loadInspectors(inspectorSet);
				} else {
					InspectorEntry inspectorEntry = (InspectorEntry) o;
					inspectorSet.addDescriptor(inspectorEntry);
					inspectorSet.addInspectorSourceInfo(
							new InspectorSourceInfo(
									"Inline inspector "+inspectorEntry.getName(),
									"Build file: "+inspectorEntry.getLocation().toString(),
									""));
				}
			}
			
			log("Inspectors loaded: "+inspectorSet.size(), Project.MSG_VERBOSE);
			
			log("Loading waivers", Project.MSG_VERBOSE);
			Date now=new Date();
			WaiverSet waiverSet=new WaiverSet();
			it=waivers.iterator();
			while (it.hasNext()) {
				((WaiverSource) it.next()).loadWaivers(waiverSet,now);
			}
			
			log("Waivers loaded: "+waiverSet.size(), Project.MSG_VERBOSE);
			
			log("Loading listeners", Project.MSG_VERBOSE);
			List listeners=new LinkedList();
			it=listenerEntries.iterator();
			while (it.hasNext()) {
				listeners.add(((ListenerEntry) it.next()).getObject(null));
			}
			
			//Outputs
			listeners.addAll(outputs);
			listeners.add(new ReviewToLogListener(project));
			
			Collection inspectors=new LinkedList(inspectorSet.getInspectors());
			session.setInspectors(inspectorSet);
			Iterator inspectorsIt=inspectors.iterator();
			log("Inspectors mapping", Project.MSG_VERBOSE);
			while (inspectorsIt.hasNext()) {
				Inspector inspector=(Inspector) inspectorsIt.next();
				log("\t"+inspector.getContext().getDescriptor().getName()+" -> "+inspector.getClass().getName(), Project.MSG_VERBOSE);
			}						
			
			ClassLoader classLoader;
			if (classPath==null) {
				classLoader=this.getClass().getClassLoader();
			} else {
				classLoader=new AntClassLoader(project, classPath, false);
				session.setClassPath(classPath.list());
			}
			
			new QuickResultsFactory(waiverSet, classLoader, tabSize, logger).install();
						
			Iterator lit=listeners.iterator();
			while (lit.hasNext()) {
				((Listener) lit.next()).onBegin(inspectorSet);
			}
			
			try {							
				QuickResultsCollector collector = new QuickResultsCollector(this, title, listeners);
				
				// Initializing violation filters
				Iterator vfit=violationFilters.iterator();
				while (vfit.hasNext()) {
					Object vf=vfit.next();
					if (vf instanceof DataAccessObject) {
						((DataAccessObject) vf).setSQLProcessor(collector.getProcessor());
					}
					
					if (vf instanceof Component) {
						((Component) vf).start();
					}
	 			}			
							
				try {
					inspectors.add(collector);
					
					QuickReviewEngine engine=new QuickReviewEngine(inspectors, this, collector);
					visitorStack[0]=engine.getVisitorStack();
					session.setVisitor(engine.getVisitor());
											
					it=srcFileSets.iterator();
					while (it.hasNext()) {
						HammurapiFileSet fs=(HammurapiFileSet) it.next();
						fs.setDefaultIncludes();
						DirectoryScanner scanner=fs.getDirectoryScanner(project);
						String[] includedFiles=scanner.getIncludedFiles();
						for (int i=0; i<includedFiles.length; i++) {
							review(new File(scanner.getBasedir(), includedFiles[i]), engine, classLoader, logger);
						}
					}
					
					it=srcFiles.iterator();
					while (it.hasNext()) {
						review((File) it.next(), engine, classLoader, logger);
					}
					
					collector.getEngine().deleteOld();
					
					Collection packageResults=new ArrayList();
					Iterator pit=collector.getEngine().getPackageTotal().iterator();
					while (pit.hasNext()) {						
						CompositeResults packageResult = new QuickPackageResults((PackageTotal) pit.next(), collector, inspectorSet); 
						packageResult.commit();
						Iterator llit=listeners.iterator();
						while (llit.hasNext()) {
							((Listener) llit.next()).onPackage(packageResult);
						}
						
						packageResults.add(packageResult);
					}
													
					log("Building summary");
					
					CompositeResults summary = new QuickSummary(title, collector, inspectorSet, packageResults);					
					ResultsFactory.pushThreadResults(summary);
					
					// Stopping violation filters
					vfit=violationFilters.iterator();
					while (vfit.hasNext()) {
						Object vf=vfit.next();
						if (vf instanceof Component) {
							((Component) vf).stop();
						}
		 			}																					
					
					Iterator slit=listeners.iterator();
					while (slit.hasNext()) {
						((Listener) slit.next()).onSummary(summary, inspectorSet);
					}
					
					Iterator rit=getReviewAcceptorEntries().iterator();
					while (rit.hasNext()) {
						((ReviewAcceptor) ((ReviewAcceptorEntry) rit.next()).getObject(null)).accept(summary);
					}
					
					long finish=System.currentTimeMillis();			
					
					long elapsedSec = (finish-start)/1000;
					long min=elapsedSec/60;
					long sec=elapsedSec % 60;		
					
					log("Time: "+min+" min. "+sec+" sec.");
					log(
							MessageFormat.format(
									"Performance {0, number,###.000000}",
									new Object[] {
											new Double(
													(double) summary.getCodeBase() * 1000
													/ (finish - start))}));
					
					
					Integer severityThreshold = getSeverityThreshold();
					if (severityThreshold!=null) {
						final int sth=getSeverityThreshold().intValue();
						new ReviewAcceptor() {
							public void accept(CompositeResults summary) throws HammurapiException {
								Number severity=summary.getMaxSeverity();
								if (severity!=null && severity.intValue()<=sth) {
									throw new HammurapiException("Severity threshold ("+sth+") infringed");
								}
							}
						}.accept(summary);
					}
					
					Double sigmaThreshold = getSigmaThreshold();
					if (sigmaThreshold!=null) {
						final double cth=sigmaThreshold.doubleValue();				
						new ReviewAcceptor() {
							public void accept(CompositeResults summary) throws HammurapiException {
								try {
									if (Double.parseDouble(summary.getSigma())<cth) {
										throw new HammurapiException("Sigma is below threshold ("+cth+")");
									}
								} catch (NumberFormatException e) {
									throw new HammurapiException("Sigma is not valid");
								}
							}
						}.accept(summary);
					}
					
					Integer dpmoThreshold = getDpmoThreshold();
					if (dpmoThreshold!=null) {
						final int cth=dpmoThreshold.intValue();				
						new ReviewAcceptor() {
							public void accept(CompositeResults summary) throws HammurapiException {
								try {
									if (Integer.parseInt(summary.getDPMO())>cth) {
										throw new HammurapiException("DPMO is above threshold ("+cth+")");
									}
								} catch (NumberFormatException e) {
									throw new HammurapiException("DPMO is not valid");
								}
							}
						}.accept(summary);
					}
					
					if (isFailOnWarnings() && !summary.getWarnings().isEmpty()) {
						throw new HammurapiNonConsumableException("There have been warnings during execution.");				
					}
							
					summary.commit();
				} finally {
					session.shutdown();
					collector.shutdown();
				}
			} finally {
				if (archiveTmpDir!=null) {
					deleteFile(archiveTmpDir);
				}
			}
						
			if (hadExceptions) {
				throw new BuildException("There have been exceptions during execution. Check log output.");
			}			
		} catch (ClassNotFoundException e) {
			throw new BuildException(e);
		} catch (SQLException e) {
			throw new BuildException(e);
		} catch (JselException e) {
			throw new BuildException(e);
		} catch (HammurapiException e) {
			throw new BuildException(e);
		} catch (ConfigurationException e) {
			throw new BuildException(e);
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		} catch (IOException e) {
			throw new BuildException(e);
		} 
	}
	
	/**
	 * @param file
	 * @param engine
	 * @param classLoader
	 * @param logger
	 * @throws JselException
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	private void review(File file, QuickReviewEngine engine, ClassLoader classLoader, Logger logger) throws JselException, FileNotFoundException, SQLException {
		if (file.isFile()) {
			_review(file, engine, classLoader, logger);
		} else if (file.isDirectory()) {
			File[] files=file.listFiles();
			for (int i=0; i<files.length; i++) {
				review(files[i], engine, classLoader, logger);
			}
		}		
	}

	/**
	 * @param file
	 * @param engine
	 * @param classLoader
	 * @param logger
	 * @throws JselException
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	private void _review(File file, QuickReviewEngine engine, ClassLoader classLoader, Logger logger) throws JselException, FileNotFoundException, SQLException {
		engine.review(new CompilationUnitImpl(new FileReader(file), null, file.getAbsolutePath(), tabSize, classLoader, logger));
	}
	
	/**
	 * Use it for inspector debugging
	 * @param args
	 */
	public static void main(String[] args) {
	    System.out.println("Hammurapi 3.18.4 Copyright (C) 2004 Hammurapi Group");
	    
	    Options options=new Options();
	    
	    populateOptions(options);
	    
	    CommandLineParser parser=new PosixParser();
	    CommandLine line=null;
	    try {
	        line=parser.parse(options, args);
	    } catch (org.apache.commons.cli.ParseException e) {
	        System.err.println(e.getMessage());
	        System.err.flush();
	        printHelpAndExit(options);
		}
	    
	    if (line.hasOption("h")) {
	        printHelpAndExit(options);
	    }
	
		QuickHammurapiTask task=new QuickHammurapiTask();
		Project project = new Project();
		task.setProject(project);
		project.setCoreLoader(task.getClass().getClassLoader());
		
	    task.configure(options, line);
				
		task.suppressLogo=true;
		
		task.setTaskName("quickurapi");
		
		try {
			task.execute();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
	
	protected static void loadEmbeddedInspectors(InspectorSet inspectorSet) throws BuildException, HammurapiException {
		TaskBase.loadEmbeddedInspectors(inspectorSet);
		
		ClassResourceLoader crl=new ClassResourceLoader(QuickHammurapiTask.class);
		InputStream inspectorStream=crl.getResourceAsStream(null, null, "xml");
		if (inspectorStream==null) {
			throw new BuildException("Cannot load quick embedded inspectors");
		}
		
		DomInspectorSource source=new DomInspectorSource(inspectorStream, "Hammurapi.jar");
		source.loadInspectors(inspectorSet);
	}	
}
