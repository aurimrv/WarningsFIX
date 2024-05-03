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

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.CompilationUnit;

/**
 * Files should reside in the directory structure according to the package structure.
 * @author  Pavel Vlasov
 * @version $Revision: 1.3 $
 */
public class DirectoryStructureRule extends InspectorBase {
    
    public void visit(CompilationUnit compilationUnit) {
    	String cuName=compilationUnit.getRelativeName();
    	int idx=cuName.lastIndexOf('/');
    	String dirName = idx==-1 ? "" : cuName.substring(0, idx);    	    	
    	if (!dirName.replace('/', '.').equals(compilationUnit.getPackage().getName())) {
    		context.reportViolation(compilationUnit);
    	}
    }
}
