/*
 * Created on Nov 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

  public static void main(String args[]) {
    junit.textui.TestRunner.run (suite());
  }

  public static Test suite()
  {

    TestSuite suite = new TestSuite("Test for XREF ");

    // Add test suites
    suite.addTest(TracedMethodLocComparatorTest.suite());
    suite.addTest(TraceCaller.suite());
	suite.addTest(AdjacencyMatrixTest.suite());



    return suite;
  }

   public static void oneTimeSetUp() {
      //one-time initialization code
   }

   public static void oneTimeTearDown() {
      //one-time claen up code
   }

}//class AllTests
