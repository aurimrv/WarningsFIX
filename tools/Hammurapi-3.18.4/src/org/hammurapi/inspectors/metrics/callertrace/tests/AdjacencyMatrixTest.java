/*
 * Created on Nov 8, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace.tests;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hammurapi.inspectors.metrics.callertrace.AdjacencyMatrix;
import org.hammurapi.inspectors.metrics.callertrace.BreadthSearch;
import org.hammurapi.inspectors.metrics.callertrace.DepthFirstSearch;
import org.hammurapi.inspectors.metrics.callertrace.EdgeImpl;
import org.hammurapi.inspectors.metrics.callertrace.MethodMap;
import org.hammurapi.inspectors.metrics.callertrace.MethodWrapper;
import org.hammurapi.inspectors.metrics.callertrace.MethodWrapperDeclaration;
import org.hammurapi.inspectors.metrics.callertrace.SearchMethod;
import org.hammurapi.inspectors.metrics.callertrace.Trace;
import org.hammurapi.inspectors.metrics.callertrace.TracedMethod;

public class AdjacencyMatrixTest extends TestCase {

	private static Logger logger = Logger.getLogger(AdjacencyMatrixTest.class.getName());

	public static Test suite() {
		return new TestSuite(AdjacencyMatrixTest.class);
	}

	protected void setUp() throws Exception {

		super.setUp();
		PropertyConfigurator.configure("D:/a/Jegustator/0.7.0/src/config/TestRunLogConfig.txt");
	}

	/* (non_Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testSimpleField() {
		AdjacencyMatrix a = new AdjacencyMatrix(new DepthFirstSearch());

		a.put("1", "1");
		a.put("2", "2");
		a.put("3","3");
		assertTrue(!a.isVisited("1", "1"));
		assertTrue(!a.isVisited("9999", "1"));
		a.setVisitFlag("2","2");
		assertTrue(a.isVisited("2","2"));
		a.clearAllVisitFlags();
		assertTrue(!a.isVisited("1","1"));
		assertTrue(!a.isVisited("2","2"));
		assertTrue(!a.isVisited("9999","1"));

	}

	public void testNodeEdge1() {
			AdjacencyMatrix a = new AdjacencyMatrix(new DepthFirstSearch());

			a.put("1","2");
			a.put("2","1");
			a.put("3","5");
			assertTrue(!a.isVisited("1","2"));
			assertTrue(!a.isVisited("9999","1"));
			a.setVisitFlag("1","2");
			assertTrue(a.isVisited("1","2"));
			a.clearAllVisitFlags();
			assertTrue(!a.isVisited("1","2"));
			assertTrue(!a.isVisited("2","1"));
			assertTrue(!a.isVisited("9999","1"));

		}

public void testExtractStartingNode(){
	AdjacencyMatrix a = new AdjacencyMatrix(new DepthFirstSearch());
	assertTrue( "2".equals( a.extractStartingNode("2"+AdjacencyMatrix.KEY_SEPERATOR+"3")  ));
	assertTrue( "222222222222222".equals( a.extractStartingNode("222222222222222"+AdjacencyMatrix.KEY_SEPERATOR+"3") ));
	assertTrue( "222222222222222".equals( a.extractStartingNode("222222222222222"+AdjacencyMatrix.KEY_SEPERATOR) ));
	assertTrue( "".equals( a.extractStartingNode(AdjacencyMatrix.KEY_SEPERATOR) ));
	assertTrue( "".equals( a.extractStartingNode("222222222222222") ));
}


public void testExtractEndNode(){
	AdjacencyMatrix a = new AdjacencyMatrix(new DepthFirstSearch());
	assertTrue( "2".equals( a.extractSuccessorNode("3"+AdjacencyMatrix.KEY_SEPERATOR+"2")  ));
	assertTrue( "3".equals( a.extractSuccessorNode("222222222222222"+AdjacencyMatrix.KEY_SEPERATOR+"3") ));
	assertTrue( "".equals( a.extractSuccessorNode("222222222222222"+AdjacencyMatrix.KEY_SEPERATOR) ));
	assertTrue( "".equals( a.extractSuccessorNode(AdjacencyMatrix.KEY_SEPERATOR) ));
	assertTrue( "".equals( a.extractSuccessorNode("222222222222222") ));
}



	public void testAllSuccessorsOf() {
		AdjacencyMatrix a = new AdjacencyMatrix(new DepthFirstSearch());

		a.put("1", "2");
		a.put("2", "3");
		a.put("1", "5");
		a.put("1", "6");
		a.put("6", "7");
		assertTrue(!a.isVisited("9999", "1"));

		//-- No Side effect
		Vector vc = a.getAllSuccessorsOf("1");
		assertTrue(vc.size() == 3);

		vc = a.getAllNotVisitedSuccessorsOf("1");
		assertTrue(vc.size() == 3);

		//--Side effect
		vc = a.getAllNotVisitedSuccessorsOf("1");
		assertTrue(vc.size() == 0);

		vc = a.getAllNotVisitedSuccessorsOf("2");
		assertTrue(vc.size() == 1);
	}

	public void testDepthFirstSearch() {
		AdjacencyMatrix a = new AdjacencyMatrix(new DepthFirstSearch());
		a.put("1", "2");
			a.put("2", "3");
			a.put("1", "5");
			a.put("1", "6");
			a.put("6", "7");

//
//		Vector vc = a.getTraceOf("1");
//		logger.debug( vc );
//		assertTrue(vc.size() == 3);
//		assertTrue("1".equals((String) vc.elementAt(0) ));
//		assertTrue("6".equals((String) vc.elementAt(1) ));
//		assertTrue("7".equals((String) vc.elementAt(2) ));
//

//		vc = a.getTraceOf("1");
//		logger.debug( vc );
//		assertTrue(vc.size() == 2);
//		assertTrue("1".equals((String) vc.elementAt(0) ));
//		assertTrue("5".equals((String) vc.elementAt(1) ));
//
//		vc = a.getTraceOf("1");
//		logger.debug( vc );
//		assertTrue(vc.size() == 3);
//		assertTrue("1".equals((String) vc.elementAt(0) ));
//		assertTrue("2".equals((String) vc.elementAt(1) ));
//		assertTrue("3".equals((String) vc.elementAt(2) ));
//
//		vc = a.getTraceOf("1");
//		assertTrue(vc.size() == 1);
//
	}


	public void testBreadthSearchWithEdgeObjects() {
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();

		MethodWrapperDeclaration m1 = new MethodWrapperDeclaration("1", "A");
		
		// e1.setNodeA( m1 );
		mp.put(m1.getMethodKey(), m1 );

		MethodWrapperDeclaration m2 = new MethodWrapperDeclaration("2", "A");

		EdgeImpl e1 = new  EdgeImpl(m1.getMethodKey(), m2.getMethodKey());
		mp.put(m2.getMethodKey(), m2 );

		MethodWrapperDeclaration m3 = new MethodWrapperDeclaration("3", "A");
		EdgeImpl e2 = new  EdgeImpl(m2.getMethodKey(), m3.getMethodKey());
		//e2.setNodeA( m2 );
		//e2.setNodeB( m3 );
		mp.put(m3.getMethodKey(), m3 );

		MethodWrapperDeclaration m4 = new MethodWrapperDeclaration("4", "A");
		EdgeImpl e3 = new  EdgeImpl(m3.getMethodKey(), m4.getMethodKey());
		//e3.setNodeA( m3 );
		//e3.setNodeB( m4 );
		mp.put(m4.getMethodKey(), m4 );

		MethodWrapperDeclaration m5 = new MethodWrapperDeclaration("5", "A");
		EdgeImpl e4 = new  EdgeImpl(m3.getMethodKey(), m5.getMethodKey());
		//e4.setNodeA( m3 );
		//e4.setNodeB( m5 );
		mp.put(m5.getMethodKey(), m5 );
		a.setAllMethods(mp);

		a.putEdge(e1);
		a.putEdge(e2);
		a.putEdge(e3);
		a.putEdge(e4);
		Vector vc = a.getSearchMethod().extractTraceListForKey("A>>1");
		 System.out.println("§ 202"  +vc);

		// [[1, 2, 3, 4], [1, 2, 3, 5]]
		// [[1, 2, 3, 5], [1, 2, 3, 4]]
		//  § 202[[A>>1, A>>2, A>>3, A>>4], [A>>1, A>>2, A>>3, A>>5]]
		 // § 202[[A>>1, A>>2, A>>3, A>>5], [A>>1, A>>2, A>>3, A>>4]]
		 //-- the last item may vary
		assertTrue("Vector Size should be 2 but is " +vc.size() , vc.size() == 2);
		assertTrue("1".equals(((TracedMethod) ((Trace) vc.elementAt(0)).elementAt(0)).getMethod().getSignature()));
		assertTrue("3".equals(((TracedMethod) ((Trace) vc.elementAt(0)).elementAt(2)).getMethod().getSignature()));
		assertTrue("1".equals(((TracedMethod) ((Trace) vc.elementAt(1)).elementAt(0)).getMethod().getSignature()));
		assertTrue("3".equals(((TracedMethod) ((Trace) vc.elementAt(1)).elementAt(2)).getMethod().getSignature()));
	}


	public void testBreadthSearchSimple() {
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		MethodWrapper m1 = new MethodWrapperDeclaration("1", "A");
		MethodWrapper m2 = new MethodWrapperDeclaration("2", "A");
		MethodWrapper m3 = new MethodWrapperDeclaration("3", "A");
		MethodWrapper m4 = new MethodWrapperDeclaration("4", "A");
		MethodWrapper m5 = new MethodWrapperDeclaration("5", "A");
		mp.put(m1.getMethodKey(),m1);
		mp.put(m2.getMethodKey(),m2);
		mp.put(m3.getMethodKey(),m3);
		mp.put(m4.getMethodKey(),m4);
		mp.put(m5.getMethodKey(),m5);
		
		a.setAllMethods(mp);
		a.put(m1.getMethodKey(), m2.getMethodKey());
		a.put(m2.getMethodKey(), m3.getMethodKey());
		a.put(m3.getMethodKey(), m4.getMethodKey());
		a.put(m3.getMethodKey(), m5.getMethodKey());
		Vector vc = a.getSearchMethod().extractTraceListForKey( "A>>1");
		System.out.println("§ 237 " +vc);
		// § 229 [[A>>1, A>>2, A>>3, A>>5], [A>>1, A>>2, A>>3, A>>4]]
		assertTrue(vc.size() == 2);
		assertTrue("1".equals(((TracedMethod) ((Trace) vc.elementAt(0)).elementAt(0)).getMethod().getSignature()));
		assertTrue("3".equals(((TracedMethod) ((Trace) vc.elementAt(0)).elementAt(2)).getMethod().getSignature()));
		assertTrue("1".equals(((TracedMethod) ((Trace) vc.elementAt(1)).elementAt(0)).getMethod().getSignature()));
		assertTrue("3".equals(((TracedMethod) ((Trace) vc.elementAt(1)).elementAt(2)).getMethod().getSignature()));
	}

	public void testBreadthSearchWithMultipleStartingPoints() {
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		MethodWrapper m1a = new MethodWrapperDeclaration("1", "A");
		MethodWrapper m1b = new MethodWrapperDeclaration("1", "A");
		MethodWrapper m2 = new MethodWrapperDeclaration("2", "A");
		MethodWrapper m3 = new MethodWrapperDeclaration("3", "A");
		MethodWrapper m4 = new MethodWrapperDeclaration("4", "A");
		MethodWrapper m5 = new MethodWrapperDeclaration("5", "A");
		mp.put(m1a.getMethodKey(),m1a);
		mp.put(m1b.getMethodKey(),m1b);
		mp.put(m2.getMethodKey(),m2);
		mp.put(m3.getMethodKey(),m3);
		mp.put(m4.getMethodKey(),m4);
		mp.put(m5.getMethodKey(),m5);
		
		a.setAllMethods(mp);
		a.put(m1a.getMethodKey(), m2.getMethodKey());
		a.put(m1b.getMethodKey(), m2.getMethodKey());
		a.put(m2.getMethodKey(), m3.getMethodKey());
		a.put(m3.getMethodKey(), m4.getMethodKey());
		a.put(m3.getMethodKey(), m5.getMethodKey());
		SearchMethod scm = a.getSearchMethod();
		Vector vc = scm.extractTraceListForKey( "A>>1");
		System.out.println("§ 270 " +vc);
		// § 270 [[A>>1, A>>2, A>>3, A>>5], [A>>1, A>>2], [A>>1, A>>2, A>>3, A>>4]]
		assertTrue(vc.size() == 3);
	}




	public void testBreadthSearchSimpleWithHashCode() {
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		
		MethodWrapperDeclaration m1 = new MethodWrapperDeclaration("1", "A");
		mp.put(m1.getMethodKey(), m1 );
		
		MethodWrapperDeclaration m2 = new MethodWrapperDeclaration("2", "A");
		mp.put(m2.getMethodKey(), m2 );

		MethodWrapperDeclaration m3 = new MethodWrapperDeclaration("3", "A");
		mp.put(m3.getMethodKey(), m3 );

		MethodWrapperDeclaration m4 = new MethodWrapperDeclaration("4", "A");
		mp.put(m4.getMethodKey(), m4 );

		MethodWrapperDeclaration m5 = new MethodWrapperDeclaration("5", "A");
		mp.put(m5.getMethodKey(), m5 );

	
		a.setAllMethods(mp);
		a.put(m1.getMethodKey(),  m2.getMethodKey());
		a.put( m2.getMethodKey() , m3.getMethodKey());
		a.put(m3.getMethodKey(), m4.getMethodKey());
		a.put(m3.getMethodKey(), m5.getMethodKey());

		SearchMethod sm =  a.getSearchMethod();
		Vector vc =sm.extractTraceListForKey( "A>>1" );
		System.out.println("§ 306 Hash " +vc);
		// § 229 [[A>>1, A>>2, A>>3, A>>5], [A>>1, A>>2, A>>3, A>>4]]
		assertTrue(vc.size() == 2);
		assertTrue("1".equals(((TracedMethod) ((Trace) vc.elementAt(0)).elementAt(0)).getMethod().getSignature()));
		assertTrue("3".equals(((TracedMethod) ((Trace) vc.elementAt(0)).elementAt(2)).getMethod().getSignature()));
		assertTrue("1".equals(((TracedMethod) ((Trace) vc.elementAt(1)).elementAt(0)).getMethod().getSignature()));
		assertTrue("3".equals(((TracedMethod) ((Trace) vc.elementAt(1)).elementAt(2)).getMethod().getSignature()));
		
	}

	public void testBreadthSearchCommonEndpoint() {
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		MethodWrapper m1 = new MethodWrapperDeclaration("1", "A") ;
		mp.put(m1.getMethodKey(), m1);
		
		MethodWrapper m2 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m2.getMethodKey(), m2);
		
		MethodWrapper m3 = new MethodWrapperDeclaration("3", "A") ;
		mp.put(m3.getMethodKey(), m3);
		
		MethodWrapper m4 = new MethodWrapperDeclaration("4", "A") ;
		mp.put(m4.getMethodKey(), m4);

		MethodWrapper m5 = new MethodWrapperDeclaration("5", "A") ;
		mp.put(m5.getMethodKey(), m5);
		
		MethodWrapper m6 = new MethodWrapperDeclaration("6", "A") ;
		mp.put(m6.getMethodKey(), m6);

		MethodWrapper m7 = new MethodWrapperDeclaration("7", "A") ;
		mp.put(m7.getMethodKey(), m7);

		MethodWrapper m8 = new MethodWrapperDeclaration("8", "A") ;
		mp.put(m8.getMethodKey(), m8);

		a.setAllMethods(mp);

			a.put(m1.getMethodKey(), m2.getMethodKey());
			a.put(m2.getMethodKey(), m3.getMethodKey());
			a.put(m3.getMethodKey(), m8.getMethodKey());
			
			a.put(m1.getMethodKey(), m5.getMethodKey());
			a.put(m1.getMethodKey(), m6.getMethodKey());
			a.put(m6.getMethodKey(), m7.getMethodKey());
			a.put(m7.getMethodKey(), m8.getMethodKey());
			

		Vector vc = a.getSearchMethod().extractTraceListForKey("A>>1");
		System.out.println( "§ 356 "+  vc );
		assertTrue(vc.size() == 3);

		// § 296 [[A>>1, A>>6, A>>7, A>>8], [A>>1, A>>5], [A>>1, A>>2, A>>3, A>>8]]
		// § 331 [[A>>1, A>>2, A>>3, A>>8], [A>>1, A>>6, A>>7, A>>8], [A>>1, A>>5]]
		// § 356 [[A>>1, A>>2, A>>3, A>>8], [A>>1], [A>>1], [A>>1, A>>5], [A>>1, A>>6, A>>7, A>>8]]
		
		int sum1 = ((Trace)vc.elementAt(0)).size();
		int sum2 = ((Trace)vc.elementAt(1)).size();
		int sum3 = ((Trace)vc.elementAt(2)).size();
		assertTrue( 10 == (sum1 + sum2 + sum3));
		/*
		assertTrue("1".equals(((TracedMethod) ((Trace)vc.elementAt(0)).elementAt(0)).getMethod().getSignature()));
		assertTrue("8".equals(((TracedMethod) ((Trace)vc.elementAt(0)).elementAt(3)).getMethod().getSignature()));

		assertTrue("1".equals(((TracedMethod) ((Trace)vc.elementAt(1)).elementAt(0)).getMethod().getSignature()));
		assertTrue("5".equals(((TracedMethod) ((Trace)vc.elementAt(1)).elementAt(1)).getMethod().getSignature()));

		assertTrue("1".equals(((TracedMethod) ((Trace)vc.elementAt(2)).elementAt(0)).getMethod().getSignature()));
		assertTrue("8".equals(((TracedMethod) ((Trace)vc.elementAt(2)).elementAt(3)).getMethod().getSignature()));
		*/
	}

	//!! non intuitive traversal order
	public void testBreadthSearchCommonMiddlepoint() {
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		MethodWrapper m1 = new MethodWrapperDeclaration("1", "A") ;
		mp.put(m1.getMethodKey(), m1);
		
		MethodWrapper m2 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m2.getMethodKey(), m2);
		
		MethodWrapper m3 = new MethodWrapperDeclaration("3", "A") ;
		mp.put(m3.getMethodKey(), m3);
		
		MethodWrapper m4 = new MethodWrapperDeclaration("4", "A") ;
		mp.put(m4.getMethodKey(), m4);

		MethodWrapper m5 = new MethodWrapperDeclaration("5", "A") ;
		mp.put(m5.getMethodKey(), m5);
		
		MethodWrapper m6 = new MethodWrapperDeclaration("6", "A") ;
		mp.put(m6.getMethodKey(), m6);

		MethodWrapper m7 = new MethodWrapperDeclaration("7", "A") ;
		mp.put(m7.getMethodKey(), m7);

		MethodWrapper m8 = new MethodWrapperDeclaration("8", "A") ;
		mp.put(m8.getMethodKey(), m8);

		a.setAllMethods(mp);

			// [A>>1, A>>2, A>>3, A>>8
			a.put(m1.getMethodKey(), m2.getMethodKey());
			a.put(m2.getMethodKey(), m3.getMethodKey());
			a.put(m3.getMethodKey(), m8.getMethodKey());
			
			// [A>>1, A>>5]
			a.put(m1.getMethodKey(), m5.getMethodKey());
			
			// [A>>1, A>>6,  A>>3, A>>7, A>>8]
			a.put(m1.getMethodKey(), m6.getMethodKey());
			a.put(m6.getMethodKey(), m3.getMethodKey());
			a.put(m3.getMethodKey(), m7.getMethodKey());
			a.put(m7.getMethodKey(), m8.getMethodKey());

		SearchMethod sm = a.getSearchMethod();
		Vector vc  = sm.extractTraceListForKey( "A>>1");
		System.out.println( "§ 419 " +vc );
		// § 335 [[A>>1, A>>5], [A>>1, A>>6, A>>3, A>>7, A>>8], [A>>1, A>>2, A>>3], [A>>1, A>>6, A>>3, A>>8]]
		// § 419 [[A>>1, A>>5], [A>>1], [A>>1], [A>>1, A>>2, A>>3, A>>7, A>>8], [A>>1, A>>6, A>>3], [A>>1, A>>2, A>>3, A>>8]]
		// § 419 [[A>>1, A>>5], [A>>1], [A>>1], [A>>1, A>>2, A>>3, A>>7, A>>8], [A>>1, A>>6, A>>3], [A>>1, A>>2, A>>3, A>>8]]
		// § 419 [[A>>1, A>>5], [A>>1], [A>>1], [A>>1, A>>2, A>>3, A>>7, A>>8], [A>>1, A>>6, A>>3], [A>>1, A>>2, A>>3, A>>8]]
		// § 419 [[A>>1, A>>2, A>>6, A>>5], [A>>1], [A>>1], [A>>1, A>>2, A>>6, A>>5], [A>>1, A>>2, A>>6, A>>5]]
		// § 419 [[A>>1, A>>5], [A>>1], [A>>1], [A>>1, A>>2, A>>3, A>>7, A>>8], [A>>1, A>>6, A>>3], [A>>1, A>>2, A>>3, A>>8]]
		assertTrue(vc.size() == 4);
		
		int sum1 = ((Trace)vc.elementAt(0)).size();
		int sum2 = ((Trace)vc.elementAt(1)).size();
		int sum3 = ((Trace)vc.elementAt(2)).size();
		int sum4 = ((Trace)vc.elementAt(3)).size();
		
		assertTrue( "total sum of traced methods should be 14 but is " +  (sum1 + sum2 + sum3 + sum4), 14== (sum1 + sum2 + sum3 + sum4));
		
		/*
		assertTrue( ((Trace)vc.elementAt(0)).size() == 2);
		assertTrue("1".equals(((TracedMethod) ((Trace)vc.elementAt(0)).elementAt(0)).getMethod().getSignature()));
		assertTrue("5".equals(((TracedMethod) ((Trace)vc.elementAt(0)).elementAt(1)).getMethod().getSignature()));

		assertTrue( ((Trace)vc.elementAt(1)).size() == 5);
		assertTrue("1".equals(((TracedMethod) ((Trace)vc.elementAt(1)).elementAt(0)).getMethod().getSignature()));
		assertTrue("8".equals(((TracedMethod) ((Trace)vc.elementAt(1)).elementAt(4)).getMethod().getSignature()));

		assertTrue( ((Trace)vc.elementAt(2)).size() == 3);
		assertTrue("1".equals(((TracedMethod) ((Trace)vc.elementAt(2)).elementAt(0)).getMethod().getSignature()));
		assertTrue("3".equals(((TracedMethod) ((Trace)vc.elementAt(2)).elementAt(2)).getMethod().getSignature()));

		assertTrue( ((Trace)vc.elementAt(3)).size() == 4);
		assertTrue("1".equals(((TracedMethod) ((Trace)vc.elementAt(3)).elementAt(0)).getMethod().getSignature()));
		assertTrue("8".equals(((TracedMethod) ((Trace)vc.elementAt(3)).elementAt(3)).getMethod().getSignature()));
		*/
		}

	public void testBreadthSearchSpider() {
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		MethodWrapper m1 = new MethodWrapperDeclaration("1", "A") ;
		mp.put(m1.getMethodKey(), m1);
		
		MethodWrapper m2 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m2.getMethodKey(), m2);
		MethodWrapper m21 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m21.getMethodKey(), m21);
		
		MethodWrapper m3 = new MethodWrapperDeclaration("3", "A") ;
		mp.put(m3.getMethodKey(), m3);
		
		MethodWrapper m4 = new MethodWrapperDeclaration("4", "A") ;
		mp.put(m4.getMethodKey(), m4);

		MethodWrapper m5 = new MethodWrapperDeclaration("5", "A") ;
		mp.put(m5.getMethodKey(), m5);

		a.setAllMethods(mp);


			a.put(m1.getMethodKey(), m2.getMethodKey());

			a.put(m1.getMethodKey(), m21.getMethodKey());
			
			a.put(m1.getMethodKey(), m3.getMethodKey());

			a.put(m1.getMethodKey(), m4.getMethodKey());
			a.put(m4.getMethodKey(), m5.getMethodKey());
			

		SearchMethod sm = a.getSearchMethod();
		Vector vc  = sm.extractTraceListForKey( "A>>1");
		System.out.println( "§ 502 " +vc );
		// § 502 [[A>>1, A>>2], [A>>1, A>>2], [A>>1, A>>3], [A>>1, A>>4, A>>5]]
		// § 502 [[A>>1, A>>2], [A>>1, A>>2], [A>>1, A>>3], [A>>1, A>>4, A>>5]]
		
		assertTrue(vc.size() == 4);
		}
	
	public void testBreadthSearchSimpleDoubleEdge() {
		
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		
		
		MethodWrapper m1 = new MethodWrapperDeclaration("1", "A") ;
		mp.put(m1.getMethodKey(), m1);
		
		MethodWrapper m2 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m2.getMethodKey(), m2);
		
		a.setAllMethods(mp);
			a.put(m1.getMethodKey(), m2.getMethodKey());
		//	a.put(m1.getMethodKey(), m2.getMethodKey());
		//	a.put(m1.getMethodKey(), m2.getMethodKey());

		SearchMethod sm = a.getSearchMethod();
		Vector vc  = sm.extractTraceListForKey( "A>>1");
		System.out.println( "§ 515 " +vc );

		// § 515 [[A>>1, A>>2]
		assertTrue(vc.size() == 1);
		}
	
	public void testBreadthSearchSimpleDoubleEdgeDoubleNode() {
		
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		
		
		MethodWrapper m1 = new MethodWrapperDeclaration("1", "A") ;
		mp.put(m1.getMethodKey(), m1);
		
		MethodWrapper m2 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m2.getMethodKey(), m2);

		MethodWrapper m3 = new MethodWrapperDeclaration("1", "A") ;
		mp.put(m3.getMethodKey(), m3);
		
		MethodWrapper m4 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m4.getMethodKey(), m4);

		a.setAllMethods(mp);
			a.put(m1.getMethodKey(), m2.getMethodKey());
			a.put(m3.getMethodKey(), m4.getMethodKey());

		SearchMethod sm = a.getSearchMethod();
		Vector vc  = sm.extractTraceListForKey( "A>>1");
		System.out.println( "§ 546 " +vc );

		// § 546 [[A>>1, A>>2], [A>>1, A>>2]]
		assertTrue(vc.size() == 2);
		}

	
	public void testBreadthSearchSimpleDoubleEdgeDoubleNodeAndSomething() {
		
		AdjacencyMatrix a = new AdjacencyMatrix(new BreadthSearch());
		MethodMap mp = new MethodMap();
		
		
		MethodWrapper m1 = new MethodWrapperDeclaration("1", "A") ;
		mp.put(m1.getMethodKey(), m1);
		
		MethodWrapper m2 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m2.getMethodKey(), m2);

		MethodWrapper m3 = new MethodWrapperDeclaration("1", "A") ;
		mp.put(m3.getMethodKey(), m3);
		
		MethodWrapper m4 = new MethodWrapperDeclaration("2", "A") ;
		mp.put(m4.getMethodKey(), m4);

		MethodWrapper m5 = new MethodWrapperDeclaration("3", "A") ;
		mp.put(m5.getMethodKey(), m5);

		a.setAllMethods(mp);
			a.put(m1.getMethodKey(), m2.getMethodKey());
			a.put(m3.getMethodKey(), m4.getMethodKey());
			a.put(m4.getMethodKey(), m5.getMethodKey());

		SearchMethod sm = a.getSearchMethod();
		Vector vc  = sm.extractTraceListForKey( "A>>1");
		System.out.println( "§ 581 " +vc );

		// § 581 [[A>>1, A>>2], [A>>1, A>>2, A>>3]]
		assertTrue(vc.size() == 2);
		}


	public void testBoolean() {
		Boolean ret = new Boolean(false);
		assertTrue(!ret.booleanValue());
		ret = new Boolean(true);
		assertTrue(ret.booleanValue());
	}

	public void testCloneTrace(){

		Trace t = new Trace();
		for (int i= 0; i< 3; i++){

			MethodWrapperDeclaration m = new MethodWrapperDeclaration("lala"+i,  "A");

			TracedMethod tm  =  new TracedMethod(m);
			tm.setMethod(m);
			t.add(tm);
		}
		Trace cloneTrace = (Trace)t.clone();
		assertTrue( cloneTrace.equals(t));
	}

}

