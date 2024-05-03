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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.Field;
import com.pavelvlasov.jsel.javadoc.JavaDoc;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.4 $
 */
public class JavaDocInspector extends InspectorBase {
	public void visit(Field field) {
		if (field.getModifiers().contains("public") || field.getModifiers().contains("protected")) {
			JavaDoc javaDoc=field.getJavaDoc();
			if (javaDoc==null) {
				context.reportViolation(field);
			} 

			//TODO Check if JavaDoc is empty - no description
			
			//TODO Check that all tags have descriptions
			
			//TODO For operations (methods and constructors) check that @param tags correspond to operation parameters
			
			//TODO For operations check that @throw tags correspond to throws clause. There can be @throw for not listed
			//runtime exceptions (see below)
			
			//TODO For non-void methods check that @return tag is present and is not blank
			
			//TODO For operations scan operation body for "throw new ..." and check if exception being thrown is listed in @throw tags			
		}
	}
	
	private ThreadLocal modified=new ThreadLocal();
	
	public void leave(CompilationUnit compilationUnit) throws IOException {
		if (Boolean.TRUE.equals(modified.get())) {
			// Output dir is hardcoded to fixes
			File root=new File("fixes");
			File out=new File(root, compilationUnit.getRelativeName());
			out.getParentFile().mkdirs();
			FileWriter writer=new FileWriter(out);
			try {
				compilationUnit.save(writer);
			} finally {
				writer.close();
			}
		}
	}
}
