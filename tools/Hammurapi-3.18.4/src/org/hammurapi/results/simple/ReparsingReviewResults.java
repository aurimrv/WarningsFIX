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
package org.hammurapi.results.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.WaiverSet;
import org.hammurapi.results.ReviewResults;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.jsel.JselException;
import com.pavelvlasov.jsel.impl.CompilationUnitImpl;
import com.pavelvlasov.logging.Logger;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.3 $
 */
public class ReparsingReviewResults extends SimpleDetailedResults implements ReviewResults {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -349746437092882478L;
	private String path;
	
	private static class Context {
		private Reference ref;
		private ClassLoader classLoader;
		private Logger logger;		
	}
	
	private static ThreadLocal contextMap=new ThreadLocal() {
		protected Object initialValue() {
			return new HashMap();
		}
	};
	private int tabSize;
	
	ReparsingReviewResults(CompilationUnit cu, WaiverSet waiverSet, ClassLoader classLoader, int tabSize, Logger logger) {
		super(cu.getName(), waiverSet);
		path=cu.getFile().getAbsolutePath();
		Context context=new Context();
		context.ref=new SoftReference(cu);
		context.classLoader=classLoader;
		context.logger=logger;
		((Map) contextMap.get()).put(path, context);
		this.tabSize=tabSize;
	}
	
	public CompilationUnit getCompilationUnit() {
		Context context=(Context) ((Map) contextMap.get()).get(path);
		
		CompilationUnit cu = (CompilationUnit) (context.ref==null ? null : context.ref.get());
		if (cu==null) {
			try {
				cu=new CompilationUnitImpl(new FileReader(new File(path)), null, path, tabSize, context.classLoader, context.logger);
			} catch (JselException e) {
				throw new HammurapiRuntimeException(e);
			} catch (FileNotFoundException e) {
				throw new HammurapiRuntimeException(e);
			}
			context.ref=new SoftReference(cu);
		}
		return cu;
	}
}
