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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.persistent.jdbc.ResultsFactory;
import org.hammurapi.results.persistent.jdbc.ResultsFactoryConfig;
import org.w3c.dom.Element;

import com.pavelvlasov.ant.ConnectionEntry;
import com.pavelvlasov.config.Component;
import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.RevisionMapper;
import com.pavelvlasov.jsel.impl.DbRepositoryImpl;
import com.pavelvlasov.jsel.impl.RepositoryConfig;
import com.pavelvlasov.jsel.impl.WarningSink;
import com.pavelvlasov.logging.AntLogger;
import com.pavelvlasov.logging.Logger;
import com.pavelvlasov.metrics.MeasurementCategoryFactory;
import com.pavelvlasov.metrics.TimeIntervalCategory;
import com.pavelvlasov.persistence.CompositeStorage;
import com.pavelvlasov.persistence.FileStorage;
import com.pavelvlasov.persistence.MemoryStorage;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.review.SimpleSourceMarker;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.review.SourceMarkerComparator;
import com.pavelvlasov.sql.ConnectionPerThreadDataSource;
import com.pavelvlasov.sql.DataAccessObject;
import com.pavelvlasov.sql.JdbcStorage;
import com.pavelvlasov.sql.SQLProcessor;
import com.pavelvlasov.sql.Transaction;
import com.pavelvlasov.sql.cloudscape.CloudscapeStandaloneDataSource;
import com.pavelvlasov.sql.cloudscape.CloudscapeTmpDataSource;
import com.pavelvlasov.sql.hypersonic.HypersonicServerDataSource;
import com.pavelvlasov.sql.hypersonic.HypersonicStandaloneDataSource;
import com.pavelvlasov.sql.hypersonic.HypersonicTmpDataSource;
import com.pavelvlasov.util.VisitorStack;
import com.pavelvlasov.util.VisitorStackSource;

/**
 * Performs automatic code reviews.
 * <section name="Example" suppress-description="yes">
If you copy content of Hammurapi lib directory to ant lib directory then you can
invoke Hammurapi in the following way:
 <pre>
&lt;taskdef name="hammurapi" classname="org.hammurapi.HammurapiTask" /&gt;<br/>
<br/>
&lt;hammurapi&gt;<br/>
    <tab/>&lt;src dir="src"/&gt;<br/>
    <tab/>&lt;output dir="review"/&gt;<br/>
&lt;/hammurapi&gt;</pre>
or, if you didn't copy jar files to Ant lib directory, use this syntax:
<pre>
&lt;taskdef name="hammurapi" classname="org.hammurapi.HammurapiTask"&gt;<br/>
    <tab/>&lt;classpath&gt;<br/>
        <tab/><tab/>&lt;fileset dir="${hammurapi.home}/lib" includes="*.jar"/&gt;<br/>
    <tab/>&lt;/classpath&gt;<br/>
&lt;/taskdef&gt;<br/>
<br/>
&lt;hammurapi&gt;<br/>
    <tab/>&lt;src dir="src"/&gt;<br/>
    <tab/>&lt;output dir="review"/&gt;<br/>
&lt;/hammurapi&gt;
</pre>

</section>
 * @ant.element name="hammurapi" display-name="Automatic code review task"
 * @author Pavel Vlasov	
 * @version $Revision: 1.25 $
 */
public class HammurapiTask extends TaskBase {
	
	/**
	 * Helper class to start/stop violation filters
	 * @author Pavel Vlasov
	 * @revision $Revision: 1.25 $
	 */
	public class ViolationFilterVisitor {
		public void visit(Repository repo) throws ConfigurationException {
			// Initializing violation filters
			Iterator vfit=violationFilters.iterator();
			while (vfit.hasNext()) {
				Object vf=vfit.next();
				if (vf instanceof Component) {
					((Component) vf).start();
				}
			}																
		}

		public void leave(Repository repo) throws ConfigurationException {
			// Stopping violation filters
			Iterator vfit=violationFilters.iterator();
			while (vfit.hasNext()) {
				Object vf=vfit.next();
				if (vf instanceof Component) {
					((Component) vf).stop();
				}
			}																	
		}
	}

	private static final TimeIntervalCategory tic=MeasurementCategoryFactory.getTimeIntervalCategory(HammurapiTask.class);
	
	private boolean cloudscape=false;
	
	/**
	 * Use Cloudscape database instead of Hypersonic
	 * @param force
	 * @ant.non-required
	 */
	public void setCloudscape(boolean cloudscape) {
		this.cloudscape=cloudscape;
	}
	
	private boolean wrap=false;
	
	private boolean cleanup=true;
	boolean skipIntactPackages=false;

	private boolean forceOnWaivers;
	
	/**
	 * Cleanup old reviews info after review.
	 * Defaults to 'true'.
	 * @ant.non-required
	 * @param cleanup
	 */	
	public void setCleanup(boolean cleanup) {
		this.cleanup = cleanup;
	}
	
	/**
	 * Do not generate summary pages for packages and summary
	 * if no files were changed in package/summary.
	 * Set it to 'true' to improve performance if you don not use 
	 * 'New' marker on modified files.
	 * @ant.non-required
	 * @param skipIntactPackages
	 */
	public void setSkipIntactPackages(boolean skipIntactPackages) {
		this.skipIntactPackages = skipIntactPackages;
	}
	
	/**
	 * Force review of compilation units for which waivers are available.
	 * Default is true.
	 * @ant.non-required.
	 */
	public void setForceOnWaivers(boolean forceOnWaivers) {
		this.forceOnWaivers=forceOnWaivers;
	}
	
	boolean isForceOnWaivers() {
		return forceOnWaivers;
	}
	
	private Date baseLine;
	
	/**
	 * Date of baseline report
	 * @ant.non-required
	 * @param baseLine
	 */
	public void setBaseLine(Date baseLine) {
		this.baseLine=baseLine;
	}
	
	private String hostId;

	public void execute() throws BuildException {
		long started=System.currentTimeMillis();
		
		if (!suppressLogo) {
			log("Hammurapi 3.18.4 Copyright (C) 2004 Hammurapi Group");
		}
		
		File archiveTmpDir=processArchive();
		
		try {			
			Logger logger=new AntLogger(this);
			
			final VisitorStack[] visitorStack={null};
			final VisitorStackSource visitorStackSource=new VisitorStackSource() {
				public VisitorStack getVisitorStack() {
					return visitorStack[0];
				}
			};
			
			final SessionImpl reviewSession=new SessionImpl();
			
			InspectorSet inspectorSet=new InspectorSet(
					new InspectorContextFactory() {
						public InspectorContext newContext(InspectorDescriptor descriptor, Logger logger) {
							return new InspectorContextImpl(
									descriptor, 
									logger, 
									visitorStackSource, 
									reviewSession,
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
			
			log("Loading source files", Project.MSG_VERBOSE);
			
			RepositoryConfig config=new RepositoryConfig();
			if (classPath!=null) {
				log("Loading class files to repository", Project.MSG_DEBUG);
				config.setClassLoader(new AntClassLoader(project, classPath, false));
				reviewSession.setClassPath(classPath.list());
			}
						
			config.setLogger(logger);
			config.setCalculateDependencies(calculateDependencies);
			config.setStoreSource(storeSource);
			
			it=srcFileSets.iterator();
			while (it.hasNext()) {
				HammurapiFileSet fs=(HammurapiFileSet) it.next();
				fs.setDefaultIncludes();
				DirectoryScanner scanner=fs.getDirectoryScanner(project);
				config.addFile(scanner.getBasedir(), scanner.getIncludedFiles());				
			}
			
			/**
			 * For command-line interface
			 */
			it=srcFiles.iterator();
			while (it.hasNext()) {
				config.addFile((File) it.next());
			}
			
			config.setName(title);		
			
			if (revisionMapper!=null) {
				config.setRevisionMapper((RevisionMapper) revisionMapper.getObject(null));
			}
			
			ConnectionPerThreadDataSource dataSource = createDataSource(reviewSession);
			
			reviewSession.setDatasource(dataSource);
			
			final LinkedList repoWarnings=new LinkedList();
			config.setWarningSink(new WarningSink() {
				public void consume(final String source, final String message) {
					repoWarnings.add(new Violation() {
						public String getMessage() {
							return message;
						}

						public InspectorDescriptor getDescriptor() {
							return null;
						}

						SourceMarker sm=new SimpleSourceMarker(0,0,source,null);
						
						public SourceMarker getSource() {
							return sm;
						}

						public int compareTo(Object obj) {
							if (obj instanceof Violation) {
								Violation v=(Violation) obj;
								int c=SourceMarkerComparator._compare(getSource(), v.getSource());
								return c==0 ? getMessage().compareTo(v.getMessage()) : c; 
							} 
							
							return hashCode()-obj.hashCode();
						}
					});
				}				
			});
			
			config.setDataSource(dataSource);
			final SQLProcessor sqlProcessor=new SQLProcessor(dataSource, null);
			sqlProcessor.setTimeIntervalCategory(tic);
			
			DbRepositoryImpl repositoryImpl = new DbRepositoryImpl(config);
			Repository repository = wrap ? (Repository) repositoryImpl.getProxy() : repositoryImpl;
			
			//new SimpleResultsFactory(waiverSet).install();
			
			ResultsFactoryConfig rfConfig=new ResultsFactoryConfig();
			rfConfig.setInspectorSet(inspectorSet);
			rfConfig.setName(title);
			rfConfig.setReportNumber(repository.getScanNumber());
			rfConfig.setRepository(repository);
			rfConfig.setSqlProcessor(sqlProcessor);
			rfConfig.setHostId(hostId);	
			rfConfig.setBaseLine(baseLine);
			rfConfig.setDescription(reviewDescription);
			
			try {			
				rfConfig.setHostName(InetAddress.getLocalHost().getHostName());
			} catch (Exception e) {
				log("Cannot resolve host name: "+e);
			}
			
			CompositeStorage storage=new CompositeStorage();
			storage.addStorage("jdbc", new JdbcStorage(sqlProcessor));
			storage.addStorage("file", new FileStorage(new File(System.getProperties().getProperty("java.io.tmpdir"))));
			storage.addStorage("memory", new MemoryStorage());			
			
			rfConfig.setStorage(storage);
			rfConfig.setWaiverSet(waiverSet);
			
			ResultsFactory resultsFactory=new ResultsFactory(rfConfig);
			resultsFactory.install();
			
			CompositeResults summary=ResultsFactory.getInstance().newCompositeResults(title);
			ResultsFactory.getInstance().setSummary(summary);
			ResultsFactory.pushThreadResults(summary);
			
			Collection inspectorsPerSe=new LinkedList(inspectorSet.getInspectors());
			reviewSession.setInspectors(inspectorSet);
			Iterator inspectorsIt=inspectorsPerSe.iterator();
			log("Inspectors mapping", Project.MSG_VERBOSE);
			while (inspectorsIt.hasNext()) {
				Inspector inspector=(Inspector) inspectorsIt.next();
				log("\t"+inspector.getContext().getDescriptor().getName()+" -> "+inspector.getClass().getName(), Project.MSG_VERBOSE);
			}	
			
			// Initializes listeners
			it=listeners.iterator();
			while (it.hasNext()) {
				((Listener) it.next()).onBegin(inspectorSet);
			}
			
			Iterator vfit=violationFilters.iterator();
			while (vfit.hasNext()) {
				Object vf=vfit.next();
				if (vf instanceof DataAccessObject) {
					((DataAccessObject) vf).setSQLProcessor(sqlProcessor);
				}
 			}
			
			ResultsCollector collector = new ResultsCollector(this, inspectorSet, waiverSet, summary, listeners);
			inspectorsPerSe.add(collector);
			
			// Storing repo warnings 
			while (!repoWarnings.isEmpty()) {
				collector.getSummary().addWarning((Violation) repoWarnings.removeFirst());
			}
			
			log("Reviewing", Project.MSG_VERBOSE);
			
			inspectorsPerSe.add(new ViolationFilterVisitor());
			
			SimpleReviewEngine rengine = new SimpleReviewEngine(inspectorsPerSe, this);
			reviewSession.setVisitor(rengine.getVisitor());
			visitorStack[0]=rengine.getVisitorStack();
						
			rengine.review(repository);
			
			writeWaiverStubs(waiverSet.getRejectedRequests());
			
			ResultsFactory.getInstance().commit(System.currentTimeMillis()-started);

			if (cleanup) {
				repositoryImpl.cleanupOldScans();
				resultsFactory.cleanupOldReports();
			}
			
			repositoryImpl.shutdown();						
			reviewSession.shutdown();
			resultsFactory.shutdown();
			dataSource.shutdown();
			
			//log("SQL metrics:\n"+resultsFactory.getSQLMetrics(),Project.MSG_VERBOSE);
			
			if (hadExceptions) {
				throw new BuildException("There have been exceptions during execution. Check log output.");
			}			
		} catch (JselException e) {
			throw new BuildException(e);
		} catch (HammurapiException e) {
			throw new BuildException(e);
		} catch (ConfigurationException e) {
			throw new BuildException(e);
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		} catch (ClassNotFoundException e) {
			throw new BuildException(e);
		} catch (IOException e) {
			throw new BuildException(e);
		} catch (SQLException e) {
			throw new BuildException(e);
		} catch (RenderingException e) {
			throw new BuildException(e);
		} finally {
			if (archiveTmpDir!=null) {
				deleteFile(archiveTmpDir);
			}
		}
	}
	
	/**
	 * @param reviewSession
	 * @param hammurapiNameMap
	 * @param dataSource
	 * @param hammurapiNameMap
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	private ConnectionPerThreadDataSource createDataSource(final SessionImpl reviewSession) throws ClassNotFoundException, IOException, SQLException {
		ConnectionPerThreadDataSource dataSource;
		if (database==null && server==null && connection==null) {
			if (cloudscape) {
				dataSource = new CloudscapeTmpDataSource(DbRepositoryImpl.CLOUDSCAPE_INIT_SCRIPT);					
			} else {
				dataSource = new HypersonicTmpDataSource(DbRepositoryImpl.HYPERSONIC_INIT_SCRIPT);
			}
			SQLProcessor sqlProcessor=new SQLProcessor(dataSource, null);
			sqlProcessor.setTimeIntervalCategory(tic);
			sqlProcessor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(cloudscape ? ResultsFactory.CLOUDSCAPE_INIT_SCRIPT : ResultsFactory.HYPERSONIC_INIT_SCRIPT)));
			
			reviewSession.scheduleInitDb();
		} else if (database!=null && server==null && connection==null) {
			if (cloudscape) {
				reviewSession.setDbProperty("type", "Cloudscape");
				
				class CSDS extends CloudscapeStandaloneDataSource {
					boolean scheduleInitDb;
					
					CSDS(SessionImpl reviewSession) throws ClassNotFoundException, IOException, SQLException {
						super(database.getAbsolutePath(), "sa", "sa", true, null);
						if (scheduleInitDb) {
							reviewSession.scheduleInitDb();
						}
					}
					
					protected void initDB() throws SQLException {
						super.initDB();
						scheduleInitDb=true;
						SQLProcessor sqlProcessor=new SQLProcessor(this, null);
						sqlProcessor.setTimeIntervalCategory(tic);
						try {
							sqlProcessor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(DbRepositoryImpl.CLOUDSCAPE_INIT_SCRIPT)));						
							sqlProcessor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(ResultsFactory.CLOUDSCAPE_INIT_SCRIPT)));
						} catch (IOException e) {
							throw new BuildException("Cannot initialize database", e);
						}
					}
				}
				dataSource = new CSDS(reviewSession);
			} else {
				reviewSession.setDbProperty("type", "Hypersonic");
								
				dataSource = new HypersonicStandaloneDataSource(
						database.getAbsolutePath(), 
						new Transaction() {

							public boolean execute(SQLProcessor processor) throws SQLException {
								processor.setTimeIntervalCategory(tic);
								try {
									processor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(DbRepositoryImpl.HYPERSONIC_INIT_SCRIPT)));						
									processor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(ResultsFactory.HYPERSONIC_INIT_SCRIPT)));
								} catch (IOException e) {
									throw new BuildException("Cannot initialize database", e);
								}							
								
								reviewSession.scheduleInitDb();										
								return true;
							}							
						});
			}
		} else if (database==null && server!=null && connection==null) {
			dataSource=new HypersonicServerDataSource(server.getHost(), server.getUser(), server.getPassword(), null);
		} else if (database==null && server==null && connection!=null) {				
			dataSource=connection.getDataSource();
		} else {
			throw new BuildException("server nested element, connection nested element and database attribute are mutually exclusive");
		}
		return dataSource;
	}

	/**
	 * Host id to differentiate reports created on different machines.
	 * @ant.non-required 
	 */
	public void setHostId(String hostId) {
		this.hostId=hostId;
	}	
	
	private ServerEntry server;

	private ConnectionEntry connection;

	private boolean calculateDependencies;
	private boolean storeSource;
	
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
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.err.flush();
            printHelpAndExit(options);
        }
        
        if (line.hasOption("h")) {
            printHelpAndExit(options);
        }

		HammurapiTask task=new HammurapiTask();
		Project project = new Project();
		task.setProject(project);
		project.setCoreLoader(task.getClass().getClassLoader());
		
        task.configure(options, line);
				
		task.suppressLogo=true;
		
		task.setTaskName("hammurapi");
		
		try {
			task.execute();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
	
	/**
	 * @param options
	 * @param line
	 * @param task
	 * @param project
	 */
	protected void configure(Options options, CommandLine line) {
		super.configure(options, line);
        
        if (line.hasOption('z')) {
        	setCalculateDependencies(true);
        }
        
        if (line.hasOption('b')) {
        	setStoreSource(true);
        }
        
        if (line.hasOption('a')) {
        	setCloudscape(true);
        }
        
        if (line.hasOption('n')) {
        	setBaseLine(new Date(line.getOptionValue('n')));
        }
        		
		//if (line.hasOption('B')) {
		//	setBaselining(line.getOptionValue('B'));
		//}
		
		if (line.hasOption('H')) {
			setHostId(line.getOptionValue('H'));
		}
		
		if (line.hasOption('L')) {
			ConnectionEntry ce=new ConnectionEntry();
			ce.setDriverClass(line.getOptionValue('L'));
			ce.setUrl(line.getOptionValue('N'));
			ce.setUser(line.getOptionValue('j'));
			ce.setPassword(line.getOptionValue('p'));
			addConnection(ce);			
		}
		
		if (line.hasOption('R')) {
			addServer(new ServerEntry(line.getOptionValue('R'), line.getOptionValue('j'), line.getOptionValue('p')));
		}
				        
        if (line.hasOption('F')) {
        	setForceOnWaivers(false);
        }
        
		//setWrap(line.hasOption('r'));
	}

	//Anu 20050701 : Method moved to TaskBase.java
	//	/**
//	 * Sets baselining mode. Possible values:
//	 * off (default) - no baselining, on - do not report
//	 * violations stored in the baseline table, set - all violations
//	 * from current scan are saved to the baseline table.
//	 * The idea is to filter out all violations in
//	 * preexisting code and report only new violations. 
//	 * Not all violations can be filtered out, only thouse
//	 * with signatures. Significant code modifications can surface some 
//	 * baselined violation.  
//	 * @ant.non-required
//	 * @param baselineMode
//	 */
//	public void setBaselining(String baselineMode) {
//		if ("off".equals(baselineMode)) {
//			// Nothing.
//		} else if ("on".equals(baselineMode)) {
//			violationFilters.add(new BaselineViolationFilter());
//		} else if ("set".equalsIgnoreCase(baselineMode)) {
//			violationFilters.add(new BaselineSetupViolationFilter());
//		} else {
//			throw new BuildException("Invalid baselining mode: "+baselineMode);
//		}
//		
//	}

	/**
	 * @param options
	 */
	protected static void populateOptions(Options options) {
		TaskBase.populateOptions(options);
        
        Option hostIdOption=OptionBuilder
        .withArgName("hostId")
        .hasArg()
        .withDescription("Host id")
        .isRequired(false)
        .create("H");
        
        options.addOption(hostIdOption);
        
//Anu 20050701 : Moved to TaskBase.java
//        Option baseliningOption=OptionBuilder
//        .withArgName("off|on|set")
//        .hasArg()
//        .withDescription("Baselining mode")
//        .isRequired(false)
//        .create("B");
        
//        options.addOption(baseliningOption);
        
        Option serverOption=OptionBuilder
        .withDescription("Database server name")
		.withArgName("database server")
		.hasArg()
        .isRequired(false)
        .create("R");
        
        options.addOption(serverOption);
        
        Option driverClassOption=OptionBuilder
        .withDescription("Database driver class")
		.withArgName("class name")
		.hasArg()
        .isRequired(false)
        .create("L");
        
        options.addOption(driverClassOption);
        
        Option connectionUrlOption=OptionBuilder
        .withDescription("Database connection URL")
		.withArgName("url")
		.hasArg()
        .isRequired(false)
        .create("N");
        
        options.addOption(connectionUrlOption);
        
        Option userOption=OptionBuilder
        .withDescription("Database user")
		.withArgName("user name")
		.hasArg()
        .isRequired(false)
        .create("j");
        
        options.addOption(userOption);
        
        Option passwordOption=OptionBuilder
        .withDescription("Database password")
		.withArgName("password")
		.hasArg()
        .isRequired(false)
        .create("p");
        
        options.addOption(passwordOption);
        
        Option baseLineOption=OptionBuilder
        .withDescription("Baseline date")
		.withArgName("date")
		.hasArg()
        .isRequired(false)
        .create("n");
        
        options.addOption(baseLineOption);
                
        Option calculateDependenciesOption=OptionBuilder
        .withDescription("Calculate dependencies")
        .isRequired(false)
        .create("z");
                                                
        options.addOption(calculateDependenciesOption);
        
        Option storeSourceOption=OptionBuilder
        .withDescription("Store source")
        .isRequired(false)
        .create("b");
                                                
        options.addOption(storeSourceOption);
        
        Option cloudscapeOption=OptionBuilder
        .withDescription("Use cloudscape database")
        .isRequired(false)
        .create("a");
                                                
        options.addOption(cloudscapeOption);
        
        
        Option forceOnWaiversOption=OptionBuilder
        .withDescription("Do not force reviews on waivers")
        .isRequired(false)
        .create("F");
        
        options.addOption(forceOnWaiversOption);
        
	}

	/**
	 * If set to 'true' Hammurapi stores dependency information to the
	 * database.
	 * @ant.non-required
	 * @param b
	 */
	public void setCalculateDependencies(boolean calculateDependencies) {
		this.calculateDependencies=calculateDependencies;		
	}

	/**
	 * If set to 'true' Hammurapi stores source code to the database.
	 * @ant.non-required
	 * @param b
	 */
	public void setStoreSource(boolean storeSource) {
		this.storeSource=storeSource;		
	}

	/**
	 * Database (Hypersonic) server to use as repository.
	 * @param entry
	 * @ant.non-required
	 */
	public void addServer(ServerEntry server) {
		this.server=server;		
	}
	
	/**
	 * Defines database server to be used as repository.
	 * Mutually exclusive with server nested element and database attribute.
	 * @ant.non-required.
	 * @param connection
	 */
	public void addConnection(ConnectionEntry connection) {
		this.connection=connection;
	}
	
	/**
	 * @param config
	 * @throws ParseException
	 */
	protected void setAttributes(Element config) {
		super.setAttributes(config);
		if (config.hasAttribute("host-id")) {
			setHostId(config.getAttribute("host-id"));
		}
		if (config.hasAttribute("baseline")) {
		    try {
		        setBaseLine(new SimpleDateFormat(HammurapiArchiver.DATE_FORMAT).parse(config.getAttribute("baseline")));
	        } catch (java.text.ParseException e) {
	            throw new BuildException("Cannot parse baseline date", e);
            }
		}
	}	
}
