/*
 * Created on Nov 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CallerTraceService {
	private File projectBaseDir = null;
	// private static Logger logger = Logger.getLogger(CallerTraceService.class.getName());

    private Hashtable allClasses = new Hashtable();
	private Hashtable allInterfacesAndImplementors = new Hashtable();
	private Vector allInterfaceOperations = new Vector();
	private MethodMap allInvokedMethodTable = new MethodMap();
	private MethodMap allMethodsImplemented = new MethodMap();

	private MethodMap allMethodsImplementedButNotInvoked = new MethodMap();

	// private AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(new DepthFirstSearch(	));
	private AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(new BreadthSearch(	));
	private MethodMap allMethods = new MethodMap();
	private TraceTable traceTable = new TraceTable();


	public void doIt() {
		init();
		traceCaller();
		return;
	}

	public void init(){
		adjacencyMatrix.setAllMethods(allMethods);
	}

	public static boolean doYouPassMethodNameFilter( String methodName ){
		if( methodName != null ){
			return true;
		}
		return false;
	}

	public void traceCaller() {

		
		MethodWrapper mi = null;
		int i = 0;
		String[] key = {
		//					"java.sql.Statement>>executeQuery(String)",
		//					"java.sql.Connection>>rollback()",
		//					"java.sql.Connection>>commit()"

//-- Na Fac Induction
/*  "ConnectionObject>>exeSelectPreparedStmt",
  "ConnectionObject>>exeSelectPrepStmt",
  "ConnectionObject>>exeUpdatePrepStmt",

  "ConnectionObject>>rollbackDataBase",
  "ConnectionObject>>commitDataBase",
  "ConnectionObject>>closeAllStmt",
  "ConnectionObject>>closeAllStatement",
  "ConnectionObject>>close",
  */
//NPI
//		"DocumentDAO>>update",
//		"ApplicationUserDAO>>update",
//		"DocumentActionReportDAO>>update",
//		"DocumentTaskDAO>>update",

		        "java.sql.Connection>>createStatement()",
		        "java.sql.Connection>>close()",
		 "java.sql.Statement>>executeQuery(java.lang.String)",
		 "java.sql.PreparedStatement>>executeQuery(java.lang.String)",
		 "java.sql.Connection>>rollback()",
		 "java.sql.Connection>>commit()",
		 "javax.transaction.UserTransaction>>commit()",
		 "javax.transaction.UserTransaction>>rollback()",
		 "com.inconcert.icjava.IcTask>>acquire()",
		 "com.inconcert.icjava.IcTask>>complete()",
		 "com.inconcert.icjava.IcTask>>release()"
		 
				//ADB shot
				// "BaseDao>>doCommit","BaseDao>>doRollback","BaseDao>>doUpdate",

				/*
			"ConnectionObject>>updateDataBase","ConnectionObject>>exeUpdatePrepStmt",
			"ConnectionObject>>deleteDataBase","ConnectionObject>>insertDataBase",
			"ConnectionObject>>close", "ConnectionObject>>rollbackTransaction", "ConnectionObject>>commitTransaction"
			*/

			// "Dao>>insert",
			// "IcClient>>close"
//			"ukcBeanDataAccess>>updateRecord",
//			"ukcBeanDataAccess>>insertRecord",
//			"ukcBeanDataAccess>>deleteRecord",
			};


		for (int j = 0; j < key.length; j++) {

			// System.out.println("Trace Callers for : " + key[j]);
			adjacencyMatrix.clearAllVisitFlags();

			boolean isLeaf;
			Trace trace;
	// 		TraceList traceList = depthFirstSearchForTraces(key[j]);
			TraceList traceList = breadthSearchForTraces(key[j]);
			traceTable.put(key[j], traceList);
		}
		checkForSiblings( key );
		return;
	}

	private void checkForSiblings(String[] key) {
		
		for (Enumeration enum = traceTable.elements(); enum.hasMoreElements();) {
			TraceList traceLst = (TraceList) enum.nextElement();

			for (Enumeration enumLst = traceLst.elements(); enumLst.hasMoreElements();) {
				Trace trace = (Trace) enumLst.nextElement();

				for (Enumeration enumTrc = trace.elements(); enumTrc.hasMoreElements();) {
					TracedMethod tm = (TracedMethod) enumTrc.nextElement();

					innerCheckForSiblings(key, tm);
				}
			}
		}
	}

	/**
	 * @param key
	 * @param tm
	 */
	private void innerCheckForSiblings(String[] key, TracedMethod tm) {
		tm.innerCheckForSiblings(key);
	}

	private TraceList breadthSearchForTraces(String key) {
		TraceList traceList = new TraceList( key );

		traceList.setEndpointMethod((MethodWrapper)allMethods.get(key), key);
		traceList.addAll(this.adjacencyMatrix.getTraceOf(key));
		// System.out.println(traceList);
		return traceList;
	}

	private TraceList depthFirstSearchForTraces(String key) {
		TraceList traceList = new TraceList(key);

		// logger.error(" ** " + (MethodWrapper)allMethods.get(key));
		traceList.setEndpointMethod((MethodWrapper)allMethods.get(key), key);

		boolean isLeaf = false;
		while (!isLeaf) {

			Trace trace = new Trace(adjacencyMatrix.getTraceOf(key), this.allMethods);
			// System.out.println(trace);

			//-- termination
			if (trace.size() < 2) {
				isLeaf = true;
			}else{
				traceList.add(trace);
			}
		}
		// System.out.println(traceList);
		return traceList;
	}


	public Element toDom(Document document, Element root){
		 root.appendChild(traceTable.toDom( document) );
		 root.appendChild( allMethodsImplemented.toDom( document) );
		return root;
	}

	/**
	 * @return Returns the allMethods.
	 */
	public MethodMap getAllMethods() {
		return allMethods;
	}

	

	/**
	 * @return Returns the allMethodsImplemented.
	 */
	public MethodMap getAllMethodsImplemented() {
		return allMethodsImplemented;
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
	 * @return Returns the allInterfacesAndImplementors.
	 */
	public Hashtable getAllInterfacesAndImplementors() {
		return allInterfacesAndImplementors;
	}
	
	/**
	 * @return Returns the allInvokedMethodTable.
	 */
	public Hashtable getAllInvokedMethodTable() {
		return allInvokedMethodTable;
	}
	/**
	 * @return Returns the allInterfaceOperations.
	 */
	public Vector getAllInterfaceOperations() {
		return allInterfaceOperations;
	}

	public void resolveInvokeesImplementors(){
		
		Enumeration enumAdj = getAdjacencyMatrix().getAllKeys();
		MethodMap mp = getAllMethodsImplemented();
		// System.out.println( mp );
		while ( enumAdj.hasMoreElements() ){
			EdgeImpl edge = (EdgeImpl)enumAdj.nextElement();
		//	System.out.println( edge );
			String leftHandKey = (String)edge.getNodeA();
			String rightHandKey = (String)edge.getNodeA();
			// System.out.println( leftHandKey );
			 MethodWrapperDeclaration mwd = (MethodWrapperDeclaration)mp.get( leftHandKey );
			if(mwd == null){
				mwd = (MethodWrapperDeclaration)mp.selectMethodsBothWithoutHashkey(leftHandKey) ;
				if(mwd != null){
				//	System.out.println( "Found: " + mwd.getMethodKey());
					//!! delete the original one ..
				    mwd.afferentMethodCoupling++;
				    mwd.setCalled();
				    // System.out.println("Ma " + mwd.afferentMethodCoupling );
				    edge.setNodeA(mwd.getMethodKey());
					// this.callerTraceService.getAdjacencyMatrix().put(mwd.getMethodKey(), rightHandKey );
				} else{
					//-- keep the exisitin edge, because no implemetor is known
				}
			}
		}
	}
}

