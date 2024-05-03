/*
 * Created on Oct 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import java.sql.SQLException;

import org.hammurapi.HammurapiException;
import org.hammurapi.InspectorContext;

import com.pavelvlasov.persistence.CompositeStorage;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.sql.JdbcStorage;
import com.pavelvlasov.sql.SQLProcessor;

/**
 * @author 111001082
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SqlExtractorHyperSonicDb {

	public InspectorContext context = null;
	


	protected SQLProcessor getProcessor(SourceMarker marker) {
		JdbcStorage jdbcStorage = (JdbcStorage) ((CompositeStorage) context.getSession().getStorage()).getStorage(JdbcStorage.class);
		if (jdbcStorage==null) {
			context.warn(marker, "Could not obtain JdbcStorage");
			return null;
		} else {
			return jdbcStorage.getProcessor();
		}		
	}

}
