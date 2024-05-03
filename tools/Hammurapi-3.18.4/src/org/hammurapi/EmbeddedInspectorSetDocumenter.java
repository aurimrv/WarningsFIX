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
import java.io.InputStream;
import java.util.Iterator;

import org.hammurapi.render.dom.InspectorDescriptorRenderer;
import org.hammurapi.render.dom.InspectorSetRenderer;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.logging.Logger;
import com.pavelvlasov.logging.ConsoleLogger;
import com.pavelvlasov.render.RenderRequest;
import com.pavelvlasov.render.RenderingException;
import com.pavelvlasov.render.dom.AbstractRenderer;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.9 $
 */
public class EmbeddedInspectorSetDocumenter {

	public static void main(String[] args) throws Exception {
		System.out.println("Usage: EmbeddedInspecgtorSetDocumenter <output dir> <yes|no>");
		
		InspectorSet inspectorSet=new InspectorSet(
			new InspectorContextFactory() {
				public InspectorContext newContext(InspectorDescriptor descriptor, Logger logger) {
					return new InspectorContextImpl(descriptor, logger, null, null, null);
				}
			},
			new ConsoleLogger(ConsoleLogger.VERBOSE));
		
		InputStream inspectorStream=HammurapiTask.class.getResourceAsStream("TaskBase.xml");
		if (inspectorStream==null) {
			throw new HammurapiException("Cannot load embedded inspectors");
		}
		
		DomInspectorSource source=new DomInspectorSource(inspectorStream, "Hammurapi.jar");
		source.loadInspectors(inspectorSet);
		
		File outDir=new File(args[0]);
		
        boolean embedded = "yes".equals(args[1]);
		render(new InspectorSetRenderer(new RenderRequest(inspectorSet)), new File(outDir, "inspectors.html"), embedded);        

		try {
	        Iterator inspectors=inspectorSet.getInspectors().iterator();
			while (inspectors.hasNext()) {
				InspectorDescriptor d =((Inspector) inspectors.next()).getContext().getDescriptor();
				render(new InspectorDescriptorRenderer(new RenderRequest(d)), new File(outDir, "inspectors/inspector_" + d.getName() + ".html"), embedded);
			}
		} catch (ConfigurationException e) {
		    throw new HammurapiException("Cannot render inspector descriptions.", e);
		}
		
	}

    private static void render(AbstractRenderer renderer, File outFile, boolean embedded) throws RenderingException, FileNotFoundException, HammurapiException {
        File outFileParent=outFile.getParentFile();
        if (!outFileParent.exists()) {
            if (!outFileParent.mkdirs()) {
                throw new HammurapiException("Can't create "+outFileParent.getAbsolutePath());
            }
        }
        
        renderer.setEmbeddedStyle(true);        
        
        if (embedded) {
        	renderer.setParameter("embedded", "yes");
        }
                
        renderer.render(new FileOutputStream(outFile));
    }
    

}
