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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;
import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;

/**
 * This class demostrates how to:
 * 	Generate annotations with multiple files
 * 	Use renderers and embedded styles
 * 	Read inspector parameters
 * 	Read annotation config parameters
 *  getConfigInfo implementation	
 * 
 * @author Pavel Vlasov	
 * @version $Revision: 1.5 $
 */
public class AnnotationTest extends InspectorBase implements Parameterizable {
	private static final String BEAN_IMAGE = "AnnotationTest.jpg";
	
	public void visit(final CompilationUnit compilationUnit) {
		context.annotate(new LinkedAnnotation() {
			private String path;
			public String getName() {
				return getContext().getDescriptor().getName();
			}

			public String getPath() {
				return path;
			}

			public void render(final AnnotationContext context) throws HammurapiException {
				final AnnotationContext.FileEntry imageEntry=context.getNextFile(".jpg");
				
				try {
					byte[] buf=new byte[4096];
					InputStream in=getClass().getResourceAsStream(BEAN_IMAGE);
					if (in==null) {
						throw new HammurapiException("Resource not found "+BEAN_IMAGE);
					}
					
					try {
						OutputStream out=new FileOutputStream(imageEntry.getFile());
						try {
							// Very stupid, but there is a problem with Ant classloader
							// It puts additonal bytes at the end.
							in.read(buf);
							out.write(buf, 0, 2342);
						} finally {
							out.close();
						}
					} finally {
						in.close();
					}
				} catch (IOException e) {
					throw new HammurapiException("Cannot save "+imageEntry.getFile().getAbsolutePath(), e);
				}
				
				class AnnotationTestRenderer extends AbstractRenderer {
					AnnotationTestRenderer() {
						super(new RenderRequest(AnnotationTest.this));
					}
					
					public Element render(Document document) {
						Element annotationElement=document.createElement("annotation-test");
						String title=(String) context.getParameter("title");
						if (title!=null) {
							Element titleElement = document.createElement("title");
							annotationElement.appendChild(titleElement);
							titleElement.appendChild(document.createTextNode(title));
						}
						annotationElement.setAttribute("image", imageEntry.getPath());
						
						if (pi!=null) {
							annotationElement.setAttribute("pi", pi.toString());
						}
						
						return annotationElement;
					}
					
				}
								
				AnnotationContext.FileEntry fileEntry=context.getNextFile(context.getExtension());
				path=fileEntry.getPath(); // This file is the entry point to the annotation.
				try {
					AnnotationTestRenderer renderer=new AnnotationTestRenderer();
					FileOutputStream out=new FileOutputStream(fileEntry.getFile());
					renderer.setEmbeddedStyle(context.isEmbeddedStyle());
					try {
						renderer.render(context.getParameter("style")==null ? null : new FileInputStream(context.getParameter("style").toString()), out);
					} finally {
						out.close();
					}					
				} catch (IOException e) {
					throw new HammurapiException("Can't save "+fileEntry.getFile().getAbsolutePath(), e);
				} catch (RenderingException e) {
					throw new HammurapiException("Can't render "+fileEntry.getFile().getAbsolutePath(), e);
				}
			}

			public Properties getProperties() {
				return null;
			}
		});
	}

	private Double pi;
		
	public boolean setParameter(String name, Object parameter) throws ConfigurationException {
		if ("pi".equals(name)) {
			pi=(Double) parameter;
			return true;
		}
		throw new ConfigurationException("Parameter "+name+" is not supported");
	}
	
	public String getConfigInfo() {
		return "PI="+pi;
	}

}
