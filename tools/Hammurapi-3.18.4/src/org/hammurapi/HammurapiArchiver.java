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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.xml.dom.DOMUtils;

/**
 * Packages source files and classpath entries for Hammurapi review.
 * <section name="Example" suppress-description="yes">
If you copy content of Hammurapi lib directory to ant lib directory then you can
invoke Hammurapi in the following way:
 <pre>&lt;taskdef name="har" classname="org.hammurapi.HammurapiArchiver" /&gt;<br/></pre>
or, if you didn't copy jar files to Ant lib directory, use this syntax:
<pre>&lt;taskdef name="har" classname="org.hammurapi.HammurapiArchiver"&gt;<br/>
    <tab/>&lt;classpath&gt;<br/>
        <tab/><tab/>&lt;fileset dir="${hammurapi.home}/lib" includes="*.jar"/&gt;<br/>
    <tab/>&lt;/classpath&gt;<br/>
&lt;/taskdef&gt;<br/></pre>
</section>
 * @ant.element name="har" display-name="Packager for automatic code review task"
 * @author Pavel Vlasov	
 * @version $Revision: 1.8 $
 */
public class HammurapiArchiver extends Task {
	static final String DATE_FORMAT="yyyy/MM/dd HH:mm:ss";
	
	private Boolean force;
	
	/**
	 * Force review even if the file is not changed
	 * @param force
	 * @ant.non-required
	 */
	public void setForce(boolean force) {
		this.force=force ? Boolean.TRUE : Boolean.FALSE;
	}
	
	private Boolean forceOnWarnings;
	
	/**
	 * Force review of files with warnings, even if the file is not changed.
	 * @ant.non-required
	 */
	public void setForceOnWarnings(boolean forceOnWarnings) {
		this.forceOnWarnings=forceOnWarnings ? Boolean.TRUE : Boolean.FALSE;
	}	
	
	private String title;
	
	/**
	 * Review title
	 * @ant.non-required
	 * @param title
	 */
	public void setTitle(String title) {
		this.title=title;
	}
	
	private File output;
	
	/**
	 * Output archive
	 * @ant.required
	 * @param output
	 */
	public void setOutput(File output) {
		this.output=output;
	}
		
	/**
	 * Classpath for loading classes. Classes and jars from the classpath
	 * are packaged into archive to be used during review.
	 * @ant:non-required 
	 */
	private Path classPath;
	
	public void setClassPath(Path classPath) {
		if (this.classPath == null) {
			this.classPath = classPath;
		} else {
			this.classPath.append(classPath);
		}
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

	/**
	 * Host id to differentiate archives created on different machines.
	 * @ant.non-required 
	 */
	public void setHostId(String hostId) {
		this.hostId=hostId;
	}	
	
	//Anu 20050701 start : Added for baselining attribute
	private String baselining;
	
	public void setBaselining(String baselining){
		this.baselining=baselining;	
	}
		
	/**
	 * Maybe creates a nested classpath element.
	 * @ant:non-required
	 */
	public Path createClasspath() {
		if (classPath == null) {
			classPath = new Path(project);
		}
		return classPath.createPath();
	}
	
	private String uniquilize(String name, Set names) {
		int idx=name.lastIndexOf('.');
		String newName = name;
		String ext="";
		if (idx!=-1) {
			ext=name.substring(idx);
			name=name.substring(0, idx);
		}
				
        for (int i=0; names.contains(newName.toLowerCase()); i++) {
			newName=name+"_"+Integer.toString(i, Character.MAX_RADIX)+ext;
		}
		names.add(newName.toLowerCase());
		return newName;
	}
	
	private int zipFile(File in, ZipOutputStream out, String entryName) throws IOException {
		if (in.isFile()) {
		    log("Zipping file "+in.getAbsolutePath()+" as "+entryName, Project.MSG_VERBOSE);
			out.putNextEntry(new ZipEntry(entryName));
			byte[] buf=new byte[4096];
			int l;
			FileInputStream fis=new FileInputStream(in);
			while ((l=fis.read(buf))!=-1) {
				out.write(buf, 0, l);
			}
			fis.close();				
			out.closeEntry();
			return 1;
		} else if (in.isDirectory()) {
		    int ret=0;
			File[] entries=in.listFiles();
			if (entries!=null && entries.length>0) {
			    log("Zipping directory "+in.getAbsolutePath()+" as "+entryName+"/", Project.MSG_VERBOSE);
				out.putNextEntry(new ZipEntry(entryName+"/"));
				for (int i=0; i<entries.length; i++) {
					ret+=zipFile(entries[i], out, entryName+"/"+entries[i].getName());
				}
				out.closeEntry();
			}
			return ret;
		} 
		
		return 0;
	}
	
	public void execute() throws BuildException {
		try {
			ZipOutputStream zos=new ZipOutputStream(new FileOutputStream(output));			
			Set entryNames=new HashSet();
			
			Document config=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root=config.createElement("hammurapi-archive");
			config.appendChild(root);
			
			if (force!=null) {
				root.setAttribute("force", force.booleanValue() ? "yes" : "no");
			}
			
			if (forceOnWarnings!=null) {
				root.setAttribute("force-on-warnings", forceOnWarnings.booleanValue() ? "yes" : "no");
			}
			
			if (title!=null) {
				root.setAttribute("title", title);
			}
			
			if (reviewDescription!=null) {
				root.setAttribute("review-description", reviewDescription);
			}
			
			if (baseLine!=null) {
				root.setAttribute("baseline", new SimpleDateFormat(DATE_FORMAT).format(baseLine));
			}
			
			if (hostId!=null) {
				root.setAttribute("host-id", hostId);
			}
			
			//Anu 20060701 : Baselining added
			if (baselining!=null) {
				root.setAttribute("baselining", baselining);
			}
			
			Element classPathElement=config.createElement("classpath");
			root.appendChild(classPathElement);
			
			if (classPath!=null) {
				String[] path=classPath.list();
				for (int i=0; i<path.length; i++) {
					File cpEntry=new File(path[i]);
					if (cpEntry.exists() && (cpEntry.isFile() || cpEntry.isDirectory())) {
						String name=uniquilize("lib/"+cpEntry.getName(), entryNames);
						if (zipFile(cpEntry, zos, name)>0) {
							Element pathElement=config.createElement("path");
							classPathElement.appendChild(pathElement);
							pathElement.appendChild(config.createTextNode(name));
						}
					} else {
						log("Classpath entry "+cpEntry.getAbsolutePath()+" does not exist", Project.MSG_VERBOSE);
					}
				}
			}
			
			Element sourcesElement=config.createElement("sources");
			root.appendChild(sourcesElement);
			
			Iterator it=srcFileSets.iterator();
			while (it.hasNext()) {
				HammurapiFileSet fs=(HammurapiFileSet) it.next();
				fs.setDefaultIncludes();
				DirectoryScanner scanner=fs.getDirectoryScanner(project);
				String name=uniquilize("source/"+scanner.getBasedir().getName(), entryNames);
				Element sourceElement=config.createElement("source");
				sourcesElement.appendChild(sourceElement);
				sourceElement.appendChild(config.createTextNode(name));
				String[] files=scanner.getIncludedFiles();
				for (int i=0; i<files.length; i++) {
					zipFile(new File(scanner.getBasedir(), files[i]), zos, name+"/"+files[i].replace(File.separatorChar, '/'));
				}
			}
			
			/**
			 * For command-line interface
			 */
			it=srcFiles.iterator();
			while (it.hasNext()) {
				String name=uniquilize("source/source", entryNames);
				File file = (File) it.next();
				String entryName = name+"/"+file.getName();
				Element sourceElement=config.createElement("source");
				sourcesElement.appendChild(sourceElement);
				sourceElement.appendChild(config.createTextNode(entryName));
				zipFile(file, zos, entryName);
			}
			
			ZipEntry configEntry=new ZipEntry("config.xml");
			zos.putNextEntry(configEntry);
			DOMUtils.serialize(config, zos);
			zos.closeEntry();
			
			zos.close();
		} catch (IOException e) {
			throw new BuildException(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new BuildException(e.getMessage(), e);
		} catch (FactoryConfigurationError e) {
			throw new BuildException(e.getMessage(), e);
		} catch (TransformerException e) {
			throw new BuildException(e.getMessage(), e);
		}
	}
		
	/**
	 * Source files fileset.
	 * @ant.non-required
	 */ 
	public FileSet createSrc() {
	    FileSet ret=new HammurapiFileSet("**/*.java");
	    srcFileSets.add(ret);
	    return ret;
	}

	private List srcFileSets = new LinkedList();
		
	private Collection srcFiles=new LinkedList();
	
	private static void printHelpAndExit(Options options) {
        HelpFormatter formatter=new HelpFormatter();
        formatter.printHelp("Usage: har [options] <output file> <source files/dirs>", options, false);
        System.exit(1);
    }
	
	/**
	 * Use it for inspector debugging
	 * @param args
	 */
	public static void main(String[] args) {
        System.out.println("Hammurapi 3.18.4 Copyright (C) 2004 Hammurapi Group");
        
        Options options=new Options();
                
        Option classPathOption=OptionBuilder
        .withArgName("classpath")
        .hasArg()
        .withDescription("ClassPath")
        .isRequired(false)
        .create("c");
        
        options.addOption(classPathOption);
                
        Option hostIdOption=OptionBuilder
        .withArgName("hostId")
        .hasArg()
        .withDescription("Host id")
        .isRequired(false)
        .create("H");
        
        options.addOption(hostIdOption);
        
        Option titleOption=OptionBuilder
        .withArgName("title")
        .hasArg()
        .withDescription("Report title")
        .isRequired(false)
        .create("T");
        
        options.addOption(titleOption);
        
        Option baseLineOption=OptionBuilder
        .withDescription("Baseline date")
		.withArgName("date")
		.hasArg()
        .isRequired(false)
        .create("n");
        
        options.addOption(baseLineOption);
        
        Option forceOption=OptionBuilder
        .withDescription("Force reviews on unchanged files")
        .isRequired(false)
        .create("f");
        
        options.addOption(forceOption);
        
        Option forceOnWarningsOption=OptionBuilder
        .withDescription("Do not force reviews of files with warnings")
        .isRequired(false)
        .create("k");
        
        options.addOption(forceOnWarningsOption);
        
        Option descriptionOption=OptionBuilder
        .withDescription("Review description")
		.withArgName("description")
		.hasArg()
        .isRequired(false)
        .create("y");
        options.addOption(descriptionOption);
        
        //Anu :20050701 Added baselining parameter
        Option baseliningOption=OptionBuilder
        .withArgName("off|on|set")
        .hasArg()
        .withDescription("Baselining mode")
        .isRequired(false)
        .create("B");
        
        options.addOption(descriptionOption);
        
        Option helpOption=OptionBuilder.withDescription("Print this message").isRequired(false).create("h");        
        options.addOption(helpOption);
        
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

		HammurapiArchiver task=new HammurapiArchiver();
		Project project = new Project();
		task.setProject(project);
		project.setCoreLoader(task.getClass().getClassLoader());
		
		String[] values=line.getOptionValues('c');
		for (int i=0; values!=null && i<values.length; i++) {
			task.createClasspath().append(new Path(project, values[i]));
		}
		
        String[] largs=line.getArgs();
        if (largs.length==0) {
        	System.out.println("Output file has to be provided");
        	printHelpAndExit(options);
        } 
        
        if (line.hasOption('f')) {
        	task.setForce(true);
        }
        
        if (line.hasOption('k')) {
        	task.setForceOnWarnings(false);
        }
        
        if (line.hasOption('n')) {
        	task.setBaseLine(new Date(line.getOptionValue('n')));
        }
        
		if (line.hasOption('H')) {
			task.setHostId(line.getOptionValue('H'));
		}
				
        if (line.hasOption('y')) {
        	task.setReviewDescription(line.getOptionValue('y'));
        }
         
		if (line.hasOption('T')) {
			task.setTitle(line.getOptionValue('T'));
		}
		
		//Anu :20050701 Added for Baselining attribute
		if (line.hasOption('B')) {
			task.setBaselining(line.getOptionValue('B'));
		}

        task.setOutput(new File(largs[0]));
        
        for (int i=1; i<largs.length; i++) {
    		File file = new File(largs[i]);
    		if (file.isFile()) {
    			task.srcFiles.add(file);
    		} else if (file.isDirectory()) {
    			task.createSrc().setDir(file);
    		}
        }
		
		task.setTaskName("har");
		
		try {
			task.execute();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}	
	
	private String reviewDescription;

	/**
	 * Description of review, e.g. release number. Appears in history annotation.
	 * @ant.non-required
	 */
	public void setReviewDescription(String reviewDescription) {
		this.reviewDescription=reviewDescription;
	}
	
}
