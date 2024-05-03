/*
 * Created on Mar 18, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.hammurapi.inspectors.metrics.statistics.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hammurapi.inspectors.metrics.statistics.DescriptiveStatistic;
import org.hammurapi.inspectors.metrics.statistics.IntVector;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DescriptiveStatTest extends TestCase {
	/**
	 * The suite() method.
	 */
	public static Test suite() {
		//
		return new TestSuite(DescriptiveStatTest.class);
	}// End o

	/**
	 * This method is the starting point of the test programe
	 *
	 * @param strArgsArr
	 *            The command line parameters passed.
	 * @exception Exception
	 *                A Exception object.
	 */
	public static void main(String[] strArgsArr) throws Exception {
		junit.textui.TestRunner.run(suite());
	}// End of main

	public void testFrequency1(){
		IntVector sortedData = new IntVector (   );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  3 );
		sortedData.addElement(  3 );
		sortedData.addElement(  5 );
		sortedData.addElement(  5 );
		sortedData.addElement(  7 );

		IntVector distinctValues = new IntVector (  );
		IntVector frequencies = new IntVector (  );
		new DescriptiveStatistic().frequencies( sortedData,  distinctValues,  frequencies);
		System.out.println( frequencies );
		assertTrue( "", frequencies.elementAt(0) == 3 );
		assertTrue( "", frequencies.elementAt(1) == 2 );
		assertTrue( "", frequencies.elementAt(2) == 2 );
		assertTrue( "", frequencies.elementAt(3) == 1 );
	}


	public void testFrequency2(){
		IntVector sortedData = new IntVector (   );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );

		IntVector distinctValues = new IntVector (  );
		IntVector frequencies = new IntVector (  );
		new DescriptiveStatistic().frequencies( sortedData,  distinctValues,  frequencies);
		System.out.println( frequencies );
		assertTrue( "", frequencies.size() == 1 );
		assertTrue( "", frequencies.elementAt(0) == 8 );

	}


	public void testFrequencyEmptyList(){
		IntVector sortedData = new IntVector (   );

		IntVector distinctValues = new IntVector (  );
		IntVector frequencies = new IntVector (  );
		new DescriptiveStatistic().frequencies( sortedData,  distinctValues,  frequencies);
		System.out.println( frequencies );
		assertTrue( "", frequencies.isEmpty()  );

	}

	public void testAggregateData(){
    	 // int[] numbers = { 26, 26, 27, 27, 27, 32, 42, 42, 42, 42, 43, 43, 43, 43, 43, 44, 44, 45, 45, 46, 46, 48, 49, 51, 52, 53, 54, 54, 55, 61, 65, 66, 67, 68, 69, 71, 72, 74, 74, 74, 82, 89, 103, 106, 108, 112, 113, 127, 139, 140, 145, 150, 161, 175, 179, 183, 202, 211, 256, 269, 276, 286, 302, 309, 324, 346, 387, 684, 720, 1228, 1229, 2569 };
		int[] numbers = { 26,   26, 26, 26, 26,42, 65,175,  387, 684, 720, 1228, 1229, 2569 };
    	IntVector distribution = new IntVector(  );
    	IntVector distinctValues = new IntVector(  );
    	IntVector frequencies = new IntVector(  );
    	for ( int i= 0; i< numbers.length; i++){
    		int index = numbers[i];
    		// System.out.println(  index  + " -> "+index % 100);
    		int piv =  (index / 50) *50;
    		System.out.println( index  + " -> " + piv );
    			distribution.addElement(piv);
    	}
    	System.out.println("-------------------------------------------" );

		new DescriptiveStatistic().frequencies( distribution,  distinctValues,  frequencies);
		assertTrue( "frequencies.size() is "+frequencies.size(),frequencies.size()== 8 );
		assertTrue( "",frequencies.elementAt(0)== 6 );
		assertTrue( "",frequencies.elementAt(7)== 1 );
		assertTrue( "distinctValues.size is "+ distinctValues.size(),distinctValues.size()== 8 );
		assertTrue( "",distinctValues.elementAt(0)== 0 );
		assertTrue( "",distinctValues.elementAt(7)== 2550 );

		//System.out.println( distribution );
		//System.out.println( distinctValues );
		//System.out.println( frequencies );

    }
	public void testSortIntVector(){
   	 // int[] numbers = { 26, 26, 27, 27, 27, 32, 42, 42, 42, 42, 43, 43, 43, 43, 43, 44, 44, 45, 45, 46, 46, 48, 49, 51, 52, 53, 54, 54, 55, 61, 65, 66, 67, 68, 69, 71, 72, 74, 74, 74, 82, 89, 103, 106, 108, 112, 113, 127, 139, 140, 145, 150, 161, 175, 179, 183, 202, 211, 256, 269, 276, 286, 302, 309, 324, 346, 387, 684, 720, 1228, 1229, 2569 };
		int[] numbers = {684, 26, 26, 720, 1228, 26,     65,175,  387,  1229, 2569, 26, 26,42};
		IntVector distribution = new IntVector( numbers );
		// System.out.println( distribution );
		distribution.sort();
		// System.out.println( distribution );
		assertTrue( "distribution.size is "+ distribution.size(),distribution.size()== 14 );
		assertTrue( "",distribution.elementAt(0)== 26 );
		assertTrue( "",distribution.elementAt(13)== 2569 );
	}

	public void testSumIntVector1(){
		IntVector sortedData = new IntVector (   );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  3 );
		sortedData.addElement(  3 );
		sortedData.addElement(  5 );
		sortedData.addElement(  5 );
		sortedData.addElement(  7 );
		int sum = new DescriptiveStatistic().sum(sortedData);
		assertTrue( "",sum == 26 );

	}
	public void testSumIntVector2(){
		IntVector sortedData = new IntVector (   );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  3 );
		sortedData.addElement(  3 );
		sortedData.addElement(  5 );
		sortedData.addElement(  5 );
		sortedData.addElement(  7 );
		sortedData.addElement(  1000 );
		int sum = new DescriptiveStatistic().sum(sortedData);
		assertTrue( "",sum == 1026 );

	}

	public void testMeanIntVector1(){
		IntVector sortedData = new IntVector (   );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  3 );
		sortedData.addElement(  3 );
		sortedData.addElement(  5 );
		sortedData.addElement(  5 );
		sortedData.addElement(  7 );
		double mean = new DescriptiveStatistic().mean(sortedData);
		assertTrue( "Mean is not xx but " + mean, mean == 3.0 );
	}
	
	public void testMeanEmpytyIntVector(){
		IntVector sortedData = new IntVector (   );
		double mean = new DescriptiveStatistic().mean(sortedData);
		assertTrue( "Mean is not xx but " + mean, mean == 0.0 );
	}
	public void testMeanIntVector2(){
		IntVector sortedData = new IntVector (   );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  1 );
		sortedData.addElement(  3 );
		sortedData.addElement(  3 );
		sortedData.addElement(  5 );
		sortedData.addElement(  5 );
		sortedData.addElement(  7 );
		sortedData.addElement(  1000 );
		double mean = new DescriptiveStatistic().mean(sortedData);
		assertTrue( "Mean is not xx but " + mean, mean == 114.0 );

	}
}

