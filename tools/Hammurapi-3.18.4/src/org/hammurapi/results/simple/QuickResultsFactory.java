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

import org.hammurapi.HammurapiException;
import org.hammurapi.WaiverSet;
import org.hammurapi.results.AggregatedResults;
import org.hammurapi.results.CompositeResults;
import org.hammurapi.results.DetailedResults;
import org.hammurapi.results.NamedResults;
import org.hammurapi.results.ResultsFactory;
import org.hammurapi.results.ReviewResults;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.logging.Logger;
import com.pavelvlasov.persistence.MemoryStorage;
import com.pavelvlasov.persistence.Storage;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.4 $
 */
public class QuickResultsFactory extends ResultsFactory {		
	private WaiverSet waiverSet;
	private Logger logger;
	private ClassLoader classLoader;
	private int tabSize;

	public QuickResultsFactory(WaiverSet waiverSet, ClassLoader classLoader, int tabSize, Logger logger) {
		this.waiverSet=waiverSet;
		this.classLoader=classLoader;
		this.logger=logger;
		this.tabSize=tabSize;
	}

	public AggregatedResults newAggregatedResults() {
		return new SimpleAggregatedResults(waiverSet);
	}

	public NamedResults newNamedResults(String name) {
		return new SimpleNamedResults(name, waiverSet);
	}

	public DetailedResults newDetailedResults(String name) {
		return new SimpleDetailedResults(name, waiverSet);
	}

	public CompositeResults newCompositeResults(String name) {
		return new SimpleCompositeResults(name, waiverSet);
	}

	public ReviewResults newReviewResults(CompilationUnit compilationUnit) {
		return new ReparsingReviewResults(compilationUnit, waiverSet, classLoader, tabSize, logger);
	}

	public void setSummary(AggregatedResults summary) {
	}

	private Storage storage=new MemoryStorage();
	
	public Storage getStorage() {
		return storage;
	}

	public ReviewResults findReviewResults(CompilationUnit cu) {
		return null;
	}

	public void commit(long l) {
		// DO NOTHING
	}
	
	public void execute(Task task) throws HammurapiException {
		if (task!=null) {
			task.execute();
		}		
	}

	public void join() {
		// DO NOTHING		
	}
	
}
