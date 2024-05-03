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

import org.hammurapi.results.Annotation;

import com.pavelvlasov.review.Signed;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.util.Attributable;
import com.pavelvlasov.util.VisitorStack;

/**
 * @author Pavel Vlasov
 * @version $Revision: 1.8 $
 */
public interface InspectorContext extends Attributable {
	InspectorDescriptor getDescriptor();

	/**
	 * Reports violation with a message from descriptor
	 * @param source
	 */
	void reportViolation(SourceMarker source);

	/**
	 * Reports violation with a message from descriptor
	 * @param source
	 */
	void reportViolationEx(SourceMarker source, String messageKey);

	/**
	 * 
	 * @param source
	 * @param message
	 */
	void reportViolation(SourceMarker source, String message);

	void annotate(Annotation annotation);

	void addMetric(SourceMarker source, String name, double value);

	/**
	 * Formats message taken from InspectorDescriptor with parameters.
	 * @param source
	 * @param params
	 */
	void reportViolation(SourceMarker source, Object[] params);

	/**
	 * Formats message taken from InspectorDescriptor with parameters.
	 * @param source
	 * @param params
	 */
	void reportViolationEx(SourceMarker source, Object[] params,
			String messageKey);

	/**
	 * Report warning
	 * @param source
	 * @param message
	 */
	void warn(SourceMarker source, String message);

	/**
	 * Report warning
	 * @param source
	 * @param message
	 */
	void warn(SourceMarker source, Throwable th);

	/**
	 * Outputs a message to the log
	 * @param source
	 * @param message
	 */
	void info(SourceMarker source, String message);

	/**
	 * Outputs a message to the log
	 * @param source
	 * @param message
	 */
	void debug(SourceMarker source, String message);

	/**
	 * Outputs a message to the log
	 * @param source
	 * @param message
	 */
	void verbose(SourceMarker source, String message);

	/**
	 * Creates a waiver for inspector with a given key
	 * @param inspectorKey 
	 */
	void waive(Signed signed, final String inspectorKey);

	/**
	 * Visitor stack of the master visitor
	 * @return
	 */
	VisitorStack getVisitorStack();
	
	Session getSession();	
	
	/**
	 * Helper method to avoid hard reference to Jsel objects.
	 * @param source
	 * @return
	 */
	public SourceMarker detach(final SourceMarker source);	
}
