/*
 * Created on Nov 8, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author muc
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AdjacencyMatrix {
	public static final String KEY_SEPERATOR= " -> ";
	// private static Logger logger = Logger.getLogger(AdjacencyMatrix.class.getName());
	private SearchMethod state = null;
	private Hashtable adjacencMatrix = new Hashtable();
	private MethodMap allMethods = new MethodMap();

	public AdjacencyMatrix(SearchMethod sm ){
		super();
		state = sm;
		state.setAdjacencyMatrix( this );
	}

	public int size(){
		return adjacencMatrix.size();
	}
	
	//!! add Null pointer handling for parameters
	public String toKey ( Object nodeA,  Object nodeB ){
		StringBuffer sb = new StringBuffer( nodeA.toString() );
		sb.append( AdjacencyMatrix.KEY_SEPERATOR  );
		sb.append( nodeB.toString() );
		// return (nodeA.toString() + AdjacencyMatrix.KEY_SEPERATOR + nodeB.toString());
		return sb.toString();
	}

	//!! Should return the first node in the edge representation
	public String extractStartingNode ( Object key ){
		
		if( AdjacencyMatrix.isKeyHashCode(key)){
			
			int closingBrace = ((String)key).indexOf(")");
			return ((String)key).substring(1,closingBrace);
		} else {
		int pos = key.toString().indexOf(AdjacencyMatrix.KEY_SEPERATOR);
		if ( pos > -1)
			return key.toString().substring(0,key.toString().indexOf(AdjacencyMatrix.KEY_SEPERATOR));
			
		}
		return"";
	}
	
		public static boolean isKeyHashCode(Object key){
			if( key instanceof String){
				if( ((String)key).length() >0){
				if ( ((String)key).charAt(0) == '(' &&
						((String)key).indexOf(")")>1 ){
					return true; 
				}}
			}
			return false;
		}
		public static String nodeNameWithoutHashcode ( String  key ){
			
			if( AdjacencyMatrix.isKeyHashCode(key)){
				
				int closingBrace = key.indexOf(")");
				return key.substring(closingBrace+1,key.length());
			} else {
			return key;
			}
		}
		
		public boolean compareNodes( String  adjFoundNodeA, String searchKey ){
			if ( AdjacencyMatrix.isKeyHashCode(searchKey) ){
				return adjFoundNodeA.toString().equals(searchKey.toString());
			} else {
				int closingBrace = adjFoundNodeA.indexOf(")") + 1;
				// System.out.println(" adjFoundNodeA.substring(1,closingBrace): " +  adjFoundNodeA.substring(closingBrace,adjFoundNodeA.length() ));
				return searchKey.toString().equals(  adjFoundNodeA.substring(closingBrace,adjFoundNodeA.length() ) );
			}
		}
	
	//	!! Should return the first node in the edge representation
	public String extractSuccessorNode ( Object key ){
		int pos = key.toString().indexOf(AdjacencyMatrix.KEY_SEPERATOR);
		if ( pos > -1)
			return key.toString().substring(pos+AdjacencyMatrix.KEY_SEPERATOR.length());
			// return "99";
			else{
				return "" ;
			}
	}

	//!! use Interface
	public void putEdge( EdgeImpl e){
			adjacencMatrix.put( e  , new Boolean(false));
			return;
			}

	public void put( Object nodeA,  Object nodeB ){
		EdgeImpl edg = new EdgeImpl (nodeA , nodeB) ;
// System.out.println( "Adj  " + edg.toString() );
		adjacencMatrix.put( edg , new Boolean(false));
		// adjacencMatrix.put( this.toKey(nodeA , nodeB)  , new Boolean(false));
		return;
		}
	//!! shouldnt be public
	private void put( Object key ){
		adjacencMatrix.put(key, new Boolean(false));
		return;
		}

	 public void setVisitFlag( Object nodeA,  Object nodeB ){
		this.setVisitFlag(   this.toKey(nodeA , nodeB ));
	 }

	public boolean isVisited( Object nodeA,  Object nodeB  ){
		return this.isVisited(  this.toKey(nodeA , nodeB )) ;
	}

	public void setVisitFlag( Object key ){
		adjacencMatrix.put(key, new Boolean(true));
		return;
		}

	/*
	 * Sideeffect: set visited Flag
	 */
	// should be moved to Adj Matrix
	public Vector visitAllSuccessorsOf( Object nodeA) {
		// System.out.println("-> visitAllSuccessorsOf: " + nodeA.toString());
		
		Vector result = new Vector();

		Enumeration enum = this.getAllKeys();
		while (enum.hasMoreElements()) {
			Object key = (Object) enum.nextElement();
			Object adjFoundNodeA = extractStartingNode(key);
			
			// System.out.println("check for node: " + foundNodeA.toString() + " Already Visited: " + adjacencyMatrix.isVisited(key));
			//!! toString should not be necessary
		 	// if (foundNodeA.toString().equals(nodeA.toString()) && !adjacencyMatrix.isVisited(key)) {
			if ( this.compareNodes( adjFoundNodeA.toString(), nodeA.toString()) && !this.isVisited(key)) {
				this.setVisitFlag(key);
				 // System.out.println("foundNodeA: " + key);
				result.add( this.extractSuccessorNode(key) );
			}
		}
		return result;
	}

	/*
	 * not Sideeffect, no set visited Flag
	 */
	
	public Vector visitAllSuccessorsOfStartingPoint( Object nodeA) {
		// System.out.println("-> visitAllSuccessorsOf: " + nodeA.toString());
		
		Vector result = new Vector();

		Enumeration enum = this.getAllKeys();
		while (enum.hasMoreElements()) {
			Object key = (Object) enum.nextElement();
			Object adjFoundNodeA = extractStartingNode(key);
			
			// System.out.println("check for node: " + foundNodeA.toString() + " Already Visited: " + adjacencyMatrix.isVisited(key));
			//!! toString should not be necessary
		 	// if (foundNodeA.toString().equals(nodeA.toString()) && !adjacencyMatrix.isVisited(key)) {
			if ( this.compareNodes( adjFoundNodeA.toString(), nodeA.toString()) && !this.isVisited(key)) {
				// this.setVisitFlag(key);
				 // System.out.println("foundNodeA as Successor of Starting Point: " + key);
				 // String successorKey = this.extractSuccessorNode(key);
				 MethodWrapper mw = this.getMethodFor(extractStartingNode(key));
				result.add( mw );
			}
		}
		return result;
	}
	
	public Vector getAllSuccessorsOf( Object nodeA){
		Vector vc = new Vector();
		Enumeration enum = this.adjacencMatrix.keys();
		while (enum.hasMoreElements()){
				Object key = (Object)enum.nextElement();
				Object foundNodeA = extractStartingNode(key);
				if( foundNodeA.equals( nodeA) ){
					vc.add( this.extractSuccessorNode( key ));
				}
			}

		return vc;
	}

	/*
	 * Sideeffect: set visited Flag
	 */
	public Vector getAllNotVisitedSuccessorsOf( Object nodeA){
		Vector vc = new Vector();
		Enumeration enum = this.adjacencMatrix.keys();
		while (enum.hasMoreElements()){
			Object key = (Object)enum.nextElement();

				Object foundNodeA = extractStartingNode(key);
				//!! implement equals
				if( foundNodeA.equals( nodeA  ) && !this.isVisited(key) ){
					vc.add( this.extractSuccessorNode( key ));
					this.setVisitFlag( key );
				}
			}
		return vc;
	}


	public Vector getTraceOf( Object nodeA){
		// System.out.println ( "-> getTraceOf: "+ nodeA.toString());
		// return this.state.traverseFor(  nodeA);
		
		// this.state.setResultTraceList( new TraceList(nodeA) );
		// return this.state.extractTraceList(  nodeA);
		return this.state.extractTraceListForKey( (String)nodeA );
		// return null;
	}


	private boolean isAvaiable( Object key ){

		//!! no toString necessary
		try{ adjacencMatrix.get(key.toString());
			return true;
		}catch(Exception e){
			return false;
			}

		}

	public boolean isVisited( Object key ){
		Boolean ret =null;
		//!! no toString necessary
		// try{ ret = (Boolean)adjacencMatrix.get(key.toString());
		try{ ret = (Boolean)adjacencMatrix.get( key );
			if ( ret == null){
				ret =new Boolean(false);
			}
		}catch(Exception e){
			ret =new Boolean(false);
			}
		return ret.booleanValue();
		}

	public void clearAllVisitFlags(){
		Enumeration enum = adjacencMatrix.keys();
		while (enum.hasMoreElements()){
			Object key = (Object)enum.nextElement();
			this.put(key);
		}
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();
		Enumeration enum = adjacencMatrix.keys();
				while (enum.hasMoreElements()){
					sb.append( ((Object)enum.nextElement()).toString() );
					sb.append( "\n" );
				}
		return sb.toString();
	}
	public Enumeration getAllKeys(){
		return adjacencMatrix.keys();

	}
	/**
	 * @return Returns the state.
	 */
	public SearchMethod getSearchMethod() {
		return state;
	}


	//!! job: nitializeMatrix(CallerTraceService callerTraceService)
	/*
	public void initializeMatrix(CallerTraceService callerTraceService) {

		Vector allMethodsInvoked = new Vector();
		Enumeration enum = callerTraceService.getAllClasses().elements();
		while (enum.hasMoreElements()) {
			Type c = (Type) enum.nextElement();
			callerTraceService.getAllMethodsImplemented().addAll(c.getMethodMap());
			Enumeration enumMet = c.getMethodMap().elements();
			while (enumMet.hasMoreElements()) {
				MethodImplemented mi = (MethodImplemented) enumMet.nextElement();
				// logger.debug( "\t" +mi.toString() );
				allMethodsInvoked.addAll(mi.getInvokedMethods().values());
				callerTraceService.getAllMethods().put (callerTraceService.getMethodKey(mi), mi);
			}
			// logger.debug( c.toString() );
		}

		for (int i = 0;  i< allMethodsInvoked.size(); i++){
			MethodInvoked called = (MethodInvoked)	allMethodsInvoked.elementAt(i);
			if ( CallerTraceService.doYouPassMethodNameFilter(called.getName() )){
			callerTraceService.getAllMethods().put (callerTraceService.getMethodKey(called), called);

			//!! strategy pattern for Key generation is needed
			logger.debug("experimentell " + callerTraceService.getMethodKey( called ) +" -> " + callerTraceService.getMethodKey( called.getDeclarationMethod() )) ;
			put( callerTraceService.getMethodKey( called ) , callerTraceService.getMethodKey( called.getDeclarationMethod() ) );
			}
		}

		//	logger.info ( allMethods );
		callerTraceService.traceCaller();
		callerTraceService.reporting();
		return;
	}
*/

	
	

//!! schwachsinn
	public MethodWrapper getMethodFor( Object key ) {
		
			if( key instanceof String  ){
				if( AdjacencyMatrix.isKeyHashCode(key)){
					return (MethodWrapper)allMethods.get(key);
				} else{
					String str = "here we should search for key suffix";
					str.equals(null);					 
					return null;
				}
			}else{
				return (MethodWrapper)allMethods.get(key);
			}
	}
	
	
	/**
	 * @return Returns the allMethods.
	 */
	public MethodMap getAllMethods() {
		return allMethods;
	}

	/**
	 * @param allMethods The allMethods to set.
	 */
	public void setAllMethods(MethodMap allMethods) {
		this.allMethods = allMethods;
	}
	
	

}

