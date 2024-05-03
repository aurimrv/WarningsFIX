/*
 * Created on Oct 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import org.hammurapi.HammurapiException;
import org.hammurapi.results.AnnotationContext;

/**
 * @author 111001082
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SqlExtractorPersistencyService {
	/**
	 * Creating a table to store results
	 */
	public abstract void init() throws HammurapiException;

	public abstract void saveLanguageElement(final StringVariable strVar);

	public abstract void render(AnnotationContext context, String path)
			throws HammurapiException;

	public abstract void destroy();
}
