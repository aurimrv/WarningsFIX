/*
 * Created on Nov 28, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace.tests;



import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hammurapi.inspectors.metrics.callertrace.MethodWrapper;
import org.hammurapi.inspectors.metrics.callertrace.MethodWrapperDeclaration;
import org.hammurapi.inspectors.metrics.callertrace.TracedMethodLocComparator;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TracedMethodLocComparatorTest extends TestCase {


	public static Test suite() {
		return new TestSuite(TracedMethodLocComparatorTest.class);
	}


	public void testSorted1(){

		SortedMap keyMethodList = new TreeMap(new TracedMethodLocComparator());
		MethodWrapper m = new MethodWrapperDeclaration("", "MyClass");
		m.setLine(1);
		keyMethodList.put(m,m);
		m = new MethodWrapperDeclaration("", "MyClass");
				m.setLine(20);
				keyMethodList.put(m,m);
		m = new MethodWrapperDeclaration("", "MyClass");
				m.setLine(30);
				keyMethodList.put(m,m);
		m = (MethodWrapperDeclaration)keyMethodList.firstKey();

		assertTrue("keyMethodList.size() should be 3 but is " + keyMethodList.size(), keyMethodList.size() == 3);

		m = (MethodWrapperDeclaration)keyMethodList.lastKey();
		assertTrue("m.getLine() should be 30 but is " + m.getLine(),  m.getLine() == 30);

	}
}

