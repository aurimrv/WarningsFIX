/*
 * Created on Nov 21, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Enumeration;
import java.util.Vector;


/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BreadthSearch implements SearchMethod {
	// private static Logger logger = Logger.getLogger(BreadthSearch.class.getName());
	private TraceList resultTraceList = new TraceList( "init" );
	private AdjacencyMatrix adjacencyMatrix = null;

	//!! obsolete
	public Vector traverseFor( Object nodeA) {
		return null;
	}
	
	
	/*
	 * 	Extract all starting point methods which fitting to key
	 */
	public TraceList extractTraceListForKey( String key ){
		
		this.setResultTraceList( new TraceList(key) );
			
			Vector initialLayerStack = adjacencyMatrix.visitAllSuccessorsOfStartingPoint(key);
			for ( int i=0; i<initialLayerStack.size(); i++){
				Trace t = new Trace();
				MethodWrapper mw = (MethodWrapper)initialLayerStack.elementAt(i);
				TracedMethod tmm = new TracedMethod(mw);
				tmm.setIsKeyTrue(); 
				t.add(tmm);
				resultTraceList.add(t);
			}	
			Vector tempTesultTraceList = resultTraceList;
/*			for ( int j=0; j<tempTesultTraceList.size(); j++){
				Trace t = (Trace)tempTesultTraceList.elementAt(j);
				TracedMethod tmm = (TracedMethod)t.elementAt(0);
			}
*/				
			extractTraceList();
				
		return this.getResultTraceList();
	}
	
		// public TraceList extractTraceList( Object nodeA) {
		public TraceList extractTraceList( ) {
	
	//		System.out.println("++ " + nodeA);
	//		System.out.println("++ " + resultTraceList);
			Vector layerStack = new Vector();
	
			/*
			//-- root node load
			if (resultTraceList.size() == 0) {
				resultTraceList = new TraceList( nodeA );
				
				Vector initialLayerStack = adjacencyMatrix.visitAllSuccessorsOfStartingPoint(nodeA);
				for ( int i=0; i<initialLayerStack.size(); i++){
					Trace t = new Trace();
					MethodWrapper mw = (MethodWrapper)initialLayerStack.elementAt(i);
					TracedMethod tmm = new TracedMethod(mw);
					tmm.setIsKeyTrue(); 
					t.add(tmm);
					resultTraceList.add(t);
				}	
				extractTraceList( nodeA);
			}
	*/
			for (Enumeration enum = resultTraceList.elements(); enum.hasMoreElements();) {
				Trace t = (Trace) enum.nextElement();
				TracedMethod tm = (TracedMethod) t.elementAt(t.size() - 1);
				// System.out.println("tm.isEndpoint() " + tm.isEndpoint());
	
				// if (tm.getMethod()!= null && !tm.isEndpoint()) {
				if ( !tm.isEndpoint()) {
					//				String key = tm.getMethod().getName();
					Object key = tm.toKey();
				 // System.out.println("key: " + key.toString());
					
					layerStack = adjacencyMatrix.visitAllSuccessorsOf(key);
					// System.out.println("has " + layerStack.size() + " Children ");
					if (layerStack.size() == 0) {
						tm.setEndpointTrue();
					} else {
						// System.out.println("key not an endpoint: " + layerStack.size());
						if (layerStack.size() == 1) {
							Object key1 = (Object) layerStack.firstElement();
							t.add(traceMethodFactory(key1));
						} else if (layerStack.size() > 1) {
	
							retrieveTracesForAllLayerStack(layerStack, t);
	
							//  first element goes to trace 1
							Object key2 = (Object) layerStack.firstElement();
							t.add( traceMethodFactory(key2));
						}
					}
					extractTraceList( );
				//	logger.debug("kk " + resultTraceList);
				}
			}
			return resultTraceList;
		}

	/**
		 * @param layerStack
		 * @param t
		 */
		private void retrieveTracesForAllLayerStack(Vector layerStack, Trace t) {
			//				ignore first element
			for (int j = 1; j < layerStack.size(); j++) {
				Object key2 = (Object) layerStack.elementAt(j);
//		System.out.println("build model for children: " + key2.toString());
//		System.out.println("clone " + t);
				
				
				 // Trace tadd = (Trace) t.clone();
				Trace tadd = resultTraceList.retrieveTraceFor( t );
				if( tadd == null ){
					
					tadd = resultTraceList.cloneTraceFor( t );
					tadd.add(traceMethodFactory(key2));
//								//!!* t.add(traceMethodFactory(key2));
					resultTraceList.add(tadd);
				} else {
					tadd.add(traceMethodFactory(key2));
				}
//								//!!* resultTraceList.add(t);
			}
		}


	private TracedMethod traceMethodFactory(Object methodName) {
	
		// MethodWrapper m = (MethodWrapper) adjacencyMatrix.getAllMethods().get(methodName.toString());
		MethodWrapper m = (MethodWrapper) adjacencyMatrix.getMethodFor(methodName);
		//				if (m != null ){
		TracedMethod tm = new TracedMethod(m);
		if ( m == null ){
			// System.out.println("Scheissen in deer Hoose " + methodName.toString()  );
			tm.setSearchKey( methodName.toString() );
		} else {
			tm.setMethod(m);
		}
		// tm.setIsKeyTrue();
	
		return tm;
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


