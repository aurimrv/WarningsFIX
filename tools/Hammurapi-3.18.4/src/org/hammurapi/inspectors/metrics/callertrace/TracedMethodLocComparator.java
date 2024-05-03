/*
 * Created on Nov 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

import java.util.Comparator;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TracedMethodLocComparator implements Comparator {

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		MethodWrapper m1 = (MethodWrapper) o1;
		MethodWrapper m2 = (MethodWrapper) o2;
		if (m1.getLine() > m2.getLine()) {
			return 1;
		} else if (m1.getLine() < m2.getLine()) {
			return -1;
		} else if (m1.getLine() == m2.getLine()) {
			return 0;
		}

		return 0;
	}

}

