/*
 * Created on Nov 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DepthFirstSearch implements SearchMethod {
	private static Logger logger = Logger.getLogger(AdjacencyMatrix.class.getName());
	private AdjacencyMatrix adjacencyMatrix = null;
	private TraceList resultTraceList = null;
	//!! should be
	public TraceList extractTraceList( Object nodeA ){
		return null;
	}

	// not implemented yet
	public TraceList  extractTraceListForKey(  String nodeA ){
	return null;
	}
	public Vector traverseFor(Object nodeA ){
	Vector vc = new Vector();
	vc.add( nodeA );
	String successor = this.visitLeftSuccessorsOf( nodeA);
	logger.info ( "Found: "+ successor);
	if( !"".equals(successor) ){
		vc.addAll( traverseFor(  successor ) );
	}
	return vc;
	}

	/*
	 * Sideeffect: set visited Flag
	 */
	public String visitLeftSuccessorsOf(Object nodeA) {
		logger.debug("-> visitLeftSuccessorsOf: " + nodeA);
		String successor = "";
		Enumeration enum = adjacencyMatrix.getAllKeys();
		while (enum.hasMoreElements()) {
			Object key = (Object) enum.nextElement();
			Object foundNodeA = adjacencyMatrix.extractStartingNode(key);
			logger.debug("foundNodeA: " + foundNodeA);
			if (foundNodeA.equals(nodeA) && !adjacencyMatrix.isVisited(key)) {
				adjacencyMatrix.setVisitFlag(key);
				return adjacencyMatrix.extractSuccessorNode(key);
			}
		}
		return successor;
	}

	/**
	 * @return Returns the adjacencyMatrix.
	 */
	public AdjacencyMatrix getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	/**
	 * @param adjacencyMatrix The adjacencyMatrix to set.
	 */
	public void setAdjacencyMatrix(AdjacencyMatrix adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}
	/**
	 * @return Returns the resultTraceList.
	 */
	public TraceList getResultTraceList() {
		return resultTraceList;
	}

	/**
	 * @param resultTraceList The resultTraceList to set.
	 */
	public void setResultTraceList(TraceList resultTraceList) {
		this.resultTraceList = resultTraceList;
	}
}

