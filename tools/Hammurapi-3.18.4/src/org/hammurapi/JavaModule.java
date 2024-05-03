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


/**
 * Performs review of Java source repository.
 * @ant.element name="Java module" display-name="Java module"
 * @author Pavel Vlasov	
 * @version $Revision: 1.2 $
 */
//public class JavaModule implements Module {
//	
//	private File database;
//	
//	/**
//	 * If this parameter is set then Hypersonic standalone database
//	 * will be used instead of temporary database. You must set
//	 * database name if you want to run incremental reviews.
//	 * @ant.non-required
//	 * @param database
//	 */
//	public void setDatabase(File database) {
//		this.database=database;
//	}
//	
//	private File waiverStubs;
//	
//	/**
//	 * File to output waiver stubs for rejected waiver requests to.
//	 * Selected waiver stubs can then be copied to waiver source. 
//	 * Simplifies waiver creation
//	 * @ant.non-required
//	 * @param waiverStubs
//	 */
//	public void setWaiverStubs(File waiverStubs) {
//		this.waiverStubs=waiverStubs;
//	}
//	
//	ObjectEntry revisionMapper;	
//	
//	/**
//	 * Revision mapper. Must implement com.pavelvlasov.jsel.RevisionMapper 
//	 * interface.
//	 * @ant.non-required
//	 * @return
//	 */
//	public ObjectEntry createRevisionMapper() {
//		if (revisionMapper==null) {
//			revisionMapper = new ObjectEntry() {			
//				protected void validateClass(Class clazz) throws BuildException {
//					super.validateClass(clazz);
//					if (!RevisionMapper.class.isAssignableFrom(clazz)) {
//						throw new BuildException(clazz.getName()+" doesn't implement "+Listener.class.getName());
//					}
//				}
//			};
//			return revisionMapper;
//		} else {
//			throw new BuildException("Revision mapper already defined");
//		}
//	}
//	
//	private boolean force=false;
//	
//	/**
//	 * Force review even if the file is not changed
//	 * @param force
//	 * @ant.non-required
//	 */
//	public void setForce(boolean force) {
//		this.force=force;
//	}
//	
//	boolean isForce() {
//		return force;
//	}
//	
//	
//	private boolean forceOnWarnings=true;
//	
//	/**
//	 * Force review of files with warnings, even if the file is not changed.
//	 * Default is true
//	 * @param 
//	 * @ant.non-required
//	 */
//	public void setForceOnWarnings(boolean forceOnWarnings) {
//		this.forceOnWarnings=forceOnWarnings;
//	}
//	
//	boolean isForceOnWarnings() {
//		return forceOnWarnings;
//	}
//	
//	private String name;
//	
//	/**
//	 * @ant.required
//	 * @param title
//	 */
//	public void setName(String name) {
//		this.name=name;
//	}
//	
//	public String getName() {
//		return name;
//	}
//	
//	private boolean wrap=false;
//	
//	/**
//	 * Tells Hammurapi to use wrappers for Jsel elements to avoid 
//	 * memory consumption because of inspectors which hold references
//	 * to Jsel objects. Defaults to false. Set it to true if you have
//	 * huge projects and run out of memory.
//	 * @ant.non-required
//	 * @param title
//	 */
//	public void setWrap(boolean wrap) {
//		this.wrap=wrap;
//	}
//		
//	private Collection waivers=new LinkedList();
//	
//	/**
//	 * Defines inspector source
//	 * @ant.non-required 
//	 */
//	public InspectorSourceEntry createInspectors() {
//		InspectorSourceEntry ret=new InspectorSourceEntry();
//		inspectors.add(ret);
//		return ret;
//	}
//	
//	/**
//	 * Defines waivers source
//	 * @ant.non-required 
//	 */
//	public WaiverSourceEntry createWaivers() {
//		WaiverSourceEntry ret=new WaiverSourceEntry();
//		waivers.add(ret);
//		return ret;
//	}
//	
//	private List inspectors=new LinkedList();
//	
//	/**
//	 * Defines inspector
//	 * @ant.non-required
//	 */
//	public InspectorEntry createInspector() {
//		InspectorEntry inspectorEntry=new InspectorEntry();
//		inspectors.add(inspectorEntry);
//		return inspectorEntry;
//	}
//	
//	/**
//	 * Classpath for loading classes.
//	 * @ant:non-required 
//	 */
//	private Path classPath;
//	
//	public void setClassPath(Path classPath) {
//		if (this.classPath == null) {
//			this.classPath = classPath;
//		} else {
//			this.classPath.append(classPath);
//		}
//	}
//
//	/**
//	 * Maybe creates a nested classpath element.
//	 * @ant:non-required
//	 */
//	public Path createClasspath() {
//		if (classPath == null) {
//			classPath = new Path(project);
//		}
//		return classPath.createPath();
//	}
//	
////	private String numberOfThreads="1";
//	private boolean hadExceptions;
//	
////	/**
////	 * Number of review threads. Can be an integer or * followed by integer
////	 * if the latter syntax is used then number of processors available for 
////	 * JVM will be multiplied to the integer. Example: *2 on 4-processor machine
////	 * will create 8 review threads.
////	 * @ant:non-required
////	 * @param numberOfThreads
////	 */
////	public void setNumberOfThreads(String numberOfThreads) {
////		this.numberOfThreads=numberOfThreads;
////	} 
//	
//	private List outputs=new LinkedList();
//	
//	private Project project;
//	
//	JavaModule(Project project) {
//		this.project=project;
//	}
//	
//	public void execute() throws BuildException {
//		try {			
//			InspectorSet inspectorSet=new InspectorSet(logger);
//			if (embeddedInspectors) {
//				log("Loading embedded inspectors", Project.MSG_VERBOSE);
//				loadEmbeddedInspectors(inspectorSet);
//			}
//			
//			log("Loading inspectors", Project.MSG_VERBOSE);
//			Iterator it=inspectors.iterator();
//			while (it.hasNext()) {
//				Object o=it.next();
//				if (o instanceof InspectorSource) {
//					((InspectorSource) o).loadInspectors(inspectorSet);
//				} else {
//					inspectorSet.addDescriptor((InspectorEntry) o);
//				}
//			}
//			
//			log("Inspectors loaded: "+inspectorSet.size(), Project.MSG_VERBOSE);
//			
//			log("Loading waivers", Project.MSG_VERBOSE);
//			Date now=new Date();
//			WaiverSet waiverSet=new WaiverSet();
//			it=waivers.iterator();
//			while (it.hasNext()) {
//				((WaiverSource) it.next()).loadWaivers(waiverSet,now);
//			}
//			
//			log("Waivers loaded: "+waiverSet.size(), Project.MSG_VERBOSE);
//			
//			log("Loading listeners", Project.MSG_VERBOSE);
//			List listeners=new LinkedList();
//			it=listenerEntries.iterator();
//			while (it.hasNext()) {
//				listeners.add(((ListenerEntry) it.next()).getObject());
//			}
//			
//			//Outputs
//			listeners.addAll(outputs);
//			listeners.add(new ReviewToLogListener(project));
//			
//			log("Loading source files", Project.MSG_VERBOSE);
//			
//			RepositoryConfig config=new RepositoryConfig();
//			if (classPath!=null) {
//				log("Loading class files to repository", Project.MSG_DEBUG);
//				config.setClassLoader(new AntClassLoader(project, classPath));
//			}
//						
//			config.setLogger(logger);
//			
//			it=srcFileSets.iterator();
//			while (it.hasNext()) {
//				HammurapiFileSet fs=(HammurapiFileSet) it.next();
//				fs.setDefaultIncludes();
//				DirectoryScanner scanner=fs.getDirectoryScanner(project);
//				config.addFile(scanner.getBasedir(), scanner.getIncludedFiles());				
//			}
//			
//			/**
//			 * For command-line interface
//			 */
//			it=srcFiles.iterator();
//			while (it.hasNext()) {
//				config.addFile((File) it.next());
//			}
//			
//			config.setName(title);		
//			
//			if (revisionMapper!=null) {
//				config.setRevisionMapper((RevisionMapper) revisionMapper.getObject());
//			}
//			
//			final Context hammurapiNameMap=null;
//			
//			ConnectionPerThreadDataSource dataSource;
//			if (database==null && server==null && connection==null) {
//				dataSource = new HypersonicTmpDataSource(RepositoryImpl.HYPERSONIC_INIT_SCRIPT);
//				SQLProcessor sqlProcessor=new SQLProcessor(dataSource, hammurapiNameMap);
//				sqlProcessor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(ResultsFactory.HYPERSONIC_INIT_SCRIPT)));
//			} else if (database!=null && server==null && connection==null) {				
//				dataSource = new HypersonicStandaloneDataSource(database.getAbsolutePath()) {
//					protected void initDB() throws SQLException {
//						super.initDB();
//						SQLProcessor sqlProcessor=new SQLProcessor(this, hammurapiNameMap);
//						try {
//							sqlProcessor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(RepositoryImpl.HYPERSONIC_INIT_SCRIPT)));						
//							sqlProcessor.executeScript(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(ResultsFactory.HYPERSONIC_INIT_SCRIPT)));
//						} catch (IOException e) {
//							throw new BuildException("Cannot initialize database", e);
//						}
//					}
//				};
//			} else if (database==null && server!=null && connection==null) {
//				dataSource=new HypersonicServerDataSource(server.getHost(), server.getUser(), server.getPassword());
//			} else if (database==null && server==null && connection!=null) {				
//				dataSource=connection.getDataSource();
//			} else {
//				throw new BuildException("server nested element, connection nested element and database attribute are mutually exclusive");
//			}
//			
//			final LinkedList repoWarnings=new LinkedList();
//			config.setWarningSink(new WarningSink() {
//				public void consume(final String source, final String message) {
//					repoWarnings.add(new Violation() {
//						public String getMessage() {
//							return message;
//						}
//
//						public InspectorDescriptor getDescriptor() {
//							return null;
//						}
//
//						SourceMarker sm=new SimpleSourceMarker(0,0,source);
//						
//						public SourceMarker getSource() {
//							return sm;
//						}
//
//						public int compareTo(Object obj) {
//							if (obj instanceof Violation) {
//								Violation v=(Violation) obj;
//								int c=SourceMarkerComparator._compare(getSource(), v.getSource());
//								if (c==0) {
//									return getMessage().compareTo(v.getMessage()); 
//								} else {
//									return c;
//								}
//							} else {
//								return hashCode()-obj.hashCode();
//							}
//						}
//					});
//				}				
//			});
//			
//			config.setDataSource(dataSource);
//			SQLProcessor sqlProcessor=new SQLProcessor(dataSource, hammurapiNameMap);
//			
//			RepositoryImpl repositoryImpl = new RepositoryImpl(config);
//			Repository repository = wrap ? (Repository) repositoryImpl.getProxy() :repositoryImpl;
//			
//			//new SimpleResultsFactory(waiverSet).install();
//			
//			ResultsFactoryConfig rfConfig=new ResultsFactoryConfig();
//			rfConfig.setInspectorSet(inspectorSet);
//			rfConfig.setName(title);
//			rfConfig.setReportNumber(repository.getScanNumber());
//			rfConfig.setRepository(repository);
//			rfConfig.setSqlProcessor(sqlProcessor);
//			
//			CompositeStorage storage=new CompositeStorage();
//			storage.addStorage("jdbc", new JdbcStorage(sqlProcessor));
//			storage.addStorage("file", new FileStorage(new File(System.getProperties().getProperty("java.io.tmpdir"))));
//			storage.addStorage("memory", new MemoryStorage());			
//			
//			rfConfig.setStorage(storage);
//			rfConfig.setWaiverSet(waiverSet);
//			
//			ResultsFactory resultsFactory=new ResultsFactory(rfConfig);
//			resultsFactory.install();
//						
//			Collection inspectors=new LinkedList(inspectorSet.getInspectors());
//			Iterator inspectorsIt=inspectors.iterator();
//			log("Inspectors mapping", Project.MSG_VERBOSE);
//			while (inspectorsIt.hasNext()) {
//				Inspector inspector=(Inspector) inspectorsIt.next();
//				log("\t"+inspector.getContext().getDescriptor().getName()+" -> "+inspector.getClass().getName(), Project.MSG_VERBOSE);
//			}						
//			
//			ResultsCollector collector = new ResultsCollector(this, inspectorSet, title, listeners);
//			inspectors.add(collector);
//			
//			// Storing repo warnings 
//			while (!repoWarnings.isEmpty()) {
//				collector.getSummary().addWarning((Violation) repoWarnings.removeFirst());
//			}
//			
//			log("Reviewing", Project.MSG_VERBOSE);
//			new SimpleReviewEngine(inspectors, repository, this);
//
//			writeWaiverStubs(waiverSet.getRejectedRequests());
//						
//			dataSource.shutdown();
//			
//			//log("SQL metrics:\n"+resultsFactory.getSQLMetrics(),Project.MSG_VERBOSE);
//			
//			if (hadExceptions) {
//				throw new BuildException("There have been exceptions during execution. Check log output.");
//			}			
//		} catch (JselException e) {
//			throw new BuildException(e);
//		} catch (HammurapiException e) {
//			throw new BuildException(e);
//		} catch (ConfigurationException e) {
//			throw new BuildException(e);
//		} catch (FileNotFoundException e) {
//			throw new BuildException(e);
//		} catch (ClassNotFoundException e) {
//			throw new BuildException(e);
//		} catch (IOException e) {
//			throw new BuildException(e);
//		} catch (SQLException e) {
//			throw new BuildException(e);
//		} catch (RenderingException e) {
//			throw new BuildException(e);
//		}
//	}
//	
//	/**
//	 * @param collection
//	 * @throws FileNotFoundException
//	 * @throws RenderingException
//	 */
//	private void writeWaiverStubs(final Collection rejectedViolations) throws RenderingException, FileNotFoundException {
//		if (waiverStubs!=null) {
//			class WaiverStubsRenderer extends AbstractRenderer {
//				WaiverStubsRenderer() {
//					super(rejectedViolations);
//				}
//
//				public Element render(Document document) {
//					Element ret=document.createElement("waivers");
//					Iterator it=rejectedViolations.iterator();
//					final Date now=new Date();
//					while (it.hasNext()) {
//						final Violation violation=(Violation) it.next();
//						
//						StringBuffer comment=new StringBuffer();
//						comment.append("Source: ");
//						comment.append(violation.getSource().getSourceURL());
//						
//						comment.append("\nLine: ");
//						comment.append(violation.getSource().getLine());
//						
//						comment.append("\nCol: ");
//						comment.append(violation.getSource().getColumn());
//						
//						comment.append("\nDescription: ");
//						comment.append(violation.getDescriptor().getDescription());
//						
//						comment.append("\nMesssage: ");
//						comment.append(violation.getMessage());						
//						
//						ret.appendChild(document.createComment(comment.toString()));
//						
//						Waiver waiver=new Waiver() {
//
//							public String getInspectorName() {
//								return violation.getDescriptor().getName();
//							}
//
//							public Date getExpirationDate() {
//								return now;
//							}
//
//							public String getReason() {
//								return "*** Put reason here ***";
//							}
//
//							public boolean waive(Violation violation) {
//								// This 'waiver' will never waive anything, it is used only for rendering
//								return false;
//							}
//
//							public boolean isActive() {
//								// This 'waiver' will never waive anything, it is used only for rendering
//								return false;
//							}
//							
//							Collection signatures=new HashSet();
//							
//							{
//								if (violation.getSource() instanceof Signed) { 
//									signatures.add(((Signed) violation.getSource()).getSignature());
//								}
//							}
//
//							public Collection getSignatures() {
//								return signatures;
//							}							
//						};
//						ret.appendChild(DetailedResultsRenderer.renderWaiver(waiver, document));
//					}
//					return ret;				
//				}				
//			}
//			WaiverStubsRenderer renderer=new WaiverStubsRenderer();
//			renderer.setEmbeddedStyle(false);
//			renderer.render(new FileOutputStream(waiverStubs));			
//		}
//	}
//
//	void setHadExceptions() {
//		hadExceptions=true;
//	}
//	
//	/**
//	 * Source files fileset.
//	 * @ant.non-required
//	 */ 
//	public FileSet createSrc() {
//	    FileSet ret=new HammurapiFileSet("**/*.java");
//	    srcFileSets.add(ret);
//	    return ret;
//	}
//
//	private List srcFileSets = new LinkedList();
//	
//	private boolean embeddedInspectors = true;
//	private String debugType;
//
//	/**
//	 * Load embedded inspectors. Defaults to true.
//	 * @ant.non-required 
//	 */
//	public void setEmbeddedInspectors(boolean embeddedInspectors) {
//	    this.embeddedInspectors=embeddedInspectors;
//	}	
//	
//	/**
//	 * Class name to debug
//	 * @ant.non-required 
//	 */
//	public void setDebugType(String debugType) {
//		this.debugType=debugType;
//	}	
//	
//	private static void loadEmbeddedInspectors(InspectorSet inspectorSet) throws BuildException, HammurapiException {
//		InputStream inspectorStream=HammurapiTask.class.getResourceAsStream("inspectors.xml");
//		if (inspectorStream==null) {
//			throw new BuildException("Cannot load embedded inspectors");
//		}
//		
//		DomInspectorSource source=new DomInspectorSource(inspectorStream);
//		source.loadInspectors(inspectorSet);
//	}        
//	
//	private static void printHelpAndExit(Options options) {
//        HelpFormatter formatter=new HelpFormatter();
//        formatter.printHelp("Usage: hammurapi [options] <output dir> <source files/dirs>", options, false);
//        System.exit(1);
//    }	
//	
//	private Collection srcFiles=new LinkedList();
//
//	private ServerEntry server;
//
//	private ConnectionEntry connection;
//		
//	/**
//	 * Database (Hypersonic) server to use as repository.
//	 * @param entry
//	 * @ant.non-required
//	 */
//	public void addServer(ServerEntry server) {
//		this.server=server;		
//	}
//	
//	/**
//	 * Defines database server to be used as repository.
//	 * Mutually exclusive with server nested element and database attribute.
//	 * @ant.non-required.
//	 * @param connection
//	 */
//	public void addConnection(ConnectionEntry connection) {
//		this.connection=connection;
//	}
//}
