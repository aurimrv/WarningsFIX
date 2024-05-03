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

package org.hammurapi.inspectors;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.InlineAnnotation;
import org.hammurapi.results.LinkedAnnotation;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.jsel.CompilationUnit;

/**
 * This class demostrates how to:
 * 	Generate annotations with multiple files
 * 	Use renderers and embedded styles
 * 	Read inspector parameters
 * 	Read annotation config parameters
 *  getConfigInfo implementation	
 * 
 * @author Pavel Vlasov	
 * @version $Revision: 1.2 $
 */
public class SimpleAnnotationSample extends InspectorBase  {
	
	public void visit(final CompilationUnit compilationUnit) {
		context.annotate(new LinkedAnnotation() {
			private String path;
			private String cuPath=compilationUnit.getRelativeName();

			public String getName() {
				return getContext().getDescriptor().getName();
			}

			public String getPath() {
				return path;
			}

			public void render(final AnnotationContext context) throws HammurapiException {
				final AnnotationContext.FileEntry linkEntry=context.getNextFile(".txt");

				try {
					Writer out=new FileWriter(linkEntry.getFile());
					try {
						out.write("Hello, world!!!");
					} finally {
						out.close();
					}
				} catch (IOException e) {
					throw new HammurapiException("Cannot save "+linkEntry.getFile().getAbsolutePath(), e);
				}
												
				AnnotationContext.FileEntry fileEntry=context.getNextFile(context.getExtension());
				path=fileEntry.getPath(); // This file is the entry point to the annotation.
				try {
					Writer out=new FileWriter(fileEntry.getFile());
					try {
						out.write("<HTML><BODY><H1>My simple annotation</H1>"+
								"PI: "+pi+
								"<P><a href=\""+linkEntry.getPath()+"\">Greeting</a>" +
								"</BODY></HTML>");
					} finally {
						out.close();
					}
				} catch (IOException e) {
					throw new HammurapiException("Cannot save "+linkEntry.getFile().getAbsolutePath(), e);
				}												
			}

			public Properties getProperties() {
				return null;
			}
		});
		
		context.annotate(new InlineAnnotation() {

			public String getContent() {
				return "<DIV style=\"border:solid; background-color:yellow\">Simple annotation sample</DIV>";
			}

			public String getName() {
				return getContext().getDescriptor().getName();
			}

			public void render(AnnotationContext context) throws HammurapiException {
				// This annotation doesn't create any additional files, so this method is empty
				
			}

			public Properties getProperties() {
				return null;
			}
			
		});
	}

	private Double pi;
		
	public void setParameter(String name, Object parameter) throws ConfigurationException {
		if ("pi".equals(name)) {
			pi=(Double) parameter;
		} else {
			throw new ConfigurationException("Parameter "+name+" is not supported");
		}
	}
	
	public String getConfigInfo() {
		return "PI="+pi;
	}

}
