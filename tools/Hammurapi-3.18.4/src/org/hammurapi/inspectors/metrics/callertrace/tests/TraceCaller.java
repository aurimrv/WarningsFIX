/*
 * Created on Nov 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace.tests;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hammurapi.inspectors.metrics.callertrace.MethodWrapperDeclaration;
import org.hammurapi.inspectors.metrics.callertrace.Trace;
import org.hammurapi.inspectors.metrics.callertrace.TraceList;
import org.hammurapi.inspectors.metrics.callertrace.TraceTable;
import org.hammurapi.inspectors.metrics.callertrace.TracedMethod;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TraceCaller extends TestCase {
	private static Logger logger = Logger.getLogger(TraceCaller.class.getName());


	protected void setUp() throws Exception {

		super.setUp();
		PropertyConfigurator.configure("D:/a/Jegustator/0.7.0/src/config/TestRunLogConfig.txt");
	}


	public static Test suite() {
		return new TestSuite(TraceCaller.class);
	}

	public void testTraceCaller1() {

		TracedMethod tm1 = new TracedMethod(new MethodWrapperDeclaration("executeEndPoint", "MyClass"));
		

		TracedMethod tm2 = new TracedMethod(new MethodWrapperDeclaration("execute2", "MyClass"));
		

		TracedMethod tm3 = new TracedMethod(new MethodWrapperDeclaration("executeStartPoint", "MyClass"));
		

		Trace t = new Trace();
		t.add(tm1);
		t.add(tm2);
		t.add(tm3);

		TraceList tl = new TraceList("java.lang.String>>equals(String)");
		tl.add(t);

		TraceTable tt = new TraceTable();
		tt.put("java.lang.String>>equals(String)", tl);

		assertTrue(tt.size() == 1);
		TraceList tlo = tt.get("java.lang.String>>equals(String)");
		assertTrue(tlo.size() == 1);
		Trace to = tlo.traceElementAt(0);
		assertTrue(to.size() == 3);


		logger.debug(  to );
		TracedMethod tmo = to.traceElementAt(0);
		logger.debug(  tmo.getMethod().toString() );
		assertTrue("should be endpoint but is " + tmo.getMethod().toString(), tmo.getMethod().toString().equals("MyClass>>executeEndPoint"));
		tmo = to.traceElementAt(1);
		logger.debug(  tmo.getMethod().toString() );
		assertTrue("should be execute2 but is " +tmo.getMethod().toString(), tmo.getMethod().toString().equals("MyClass>>execute2"));
		tmo = to.traceElementAt(2);
		logger.debug(  tmo.getMethod().toString() );
		assertTrue(tmo.getMethod().toString().equals("MyClass>>executeStartPoint"));
		assertTrue(true);
		logger.debug(  tt.toXml() );

		return;
	}

}

