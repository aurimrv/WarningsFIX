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
package org.hammurapi.results;

import java.util.Stack;

import org.hammurapi.HammurapiException;

import com.pavelvlasov.jsel.CompilationUnit;
import com.pavelvlasov.persistence.Storage;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.6 $
 */
public abstract class ResultsFactory {
	/**
	 * Task to be executed possibly in a separate thread 
	 */
	public interface Task {
		void execute() throws HammurapiException;
	}
	
	private static ResultsFactory instance;
	
	/**
	 * Makes this instance a singleton
	 */
	public void install() {
		instance=this;
	}
	
	public static ResultsFactory getInstance() {
		return instance;
	}
	
	public abstract AggregatedResults newAggregatedResults();

	public abstract NamedResults newNamedResults(String name);

	public abstract DetailedResults newDetailedResults(String name);

	public abstract CompositeResults newCompositeResults(String name);

	public abstract ReviewResults newReviewResults(CompilationUnit compilationUnit);

	private static ThreadLocal threadInstance = new ThreadLocal() {
		protected Object initialValue() {
			return new Stack();
		}
	};

	/**
	 * @return Detailed results instance for current thread
	 */
	public static DetailedResults getThreadResults() {
		Stack stack = (Stack) threadInstance.get();
		return stack.isEmpty() ? null : (DetailedResults) stack.peek();
	}
	
	public static DetailedResults popThreadResults() {
		Stack stack = (Stack) threadInstance.get();
		return stack.isEmpty() ? null : (DetailedResults) stack.pop();	
	}

	public static void pushThreadResults(DetailedResults result) {
		Stack stack = (Stack) threadInstance.get();
		stack.push(result);	
	}

	public abstract void setSummary(AggregatedResults summary);
//	public abstract AggregatedResults getSummary();
	
	public abstract Storage getStorage();
	
	/**
	 * @param cu Compilation unit
	 * @return Previously collected review results.
	 */
	public abstract ReviewResults findReviewResults(CompilationUnit cu);

	public abstract void commit(long l);
	
	public abstract void execute(Task task) throws HammurapiException;

	/**
	 * Waits until all commands in the background thread are executed. 
	 */
	public abstract void join() throws HammurapiException;
}
