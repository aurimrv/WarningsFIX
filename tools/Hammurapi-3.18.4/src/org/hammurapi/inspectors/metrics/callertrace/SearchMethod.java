/*
 * Created on Nov 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Vector;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface SearchMethod {

	// public TraceList extractTraceList(  Object nodeA );
	public TraceList  extractTraceListForKey(  String nodeA );
	
	public Vector traverseFor(  Object nodeA );
	public void setAdjacencyMatrix(AdjacencyMatrix a );

	public TraceList getResultTraceList() ;
	public void setResultTraceList(TraceList resultTraceList) ;
}

