/*
Copyright © 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
is hereby granted without fee, provided that the above copyright notice appear in all copies and
that both that copyright notice and this permission notice appear in supporting documentation.
CERN makes no representations about the suitability of this software for any purpose.
It is provided "as is" without expressed or implied warranty.
*/

package org.hammurapi.inspectors.metrics.statistics;


//!! job: average, median

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DescriptiveStatistic {


	/**
	 * Computes the frequency (number of occurances, count) of each distinct value in the given sorted data.
	 * After this call returns both <tt>distinctValues</tt> and <tt>frequencies</tt> have a new size (which is equal for both), which is the number of distinct values in the sorted data.
	 * <p>
	 * Distinct values are filled into <tt>distinctValues</tt>, starting at index 0.
	 * The frequency of each distinct value is filled into <tt>frequencies</tt>, starting at index 0.
	 * As a result, the smallest distinct value (and its frequency) can be found at index 0, the second smallest distinct value (and its frequency) at index 1, ..., the largest distinct value (and its frequency) at index <tt>distinctValues.size()-1</tt>.
	 *
	 * <b>Example:</b>
	 * <br>
	 * <tt>elements = (5,6,6,7,8,8) --> distinctValues = (5,6,7,8), frequencies = (1,2,1,2)</tt>
	 *
	 * @param sortedData the data; must be sorted ascending.
	 * @param distinctValues a list to be filled with the distinct values; can have any size.
	 * @param frequencies      a list to be filled with the frequencies; can have any size; set this parameter to <tt>null</tt> to ignore it.
	 */
	public void frequencies(IntVector sortedData, IntVector distinctValues, IntVector frequencies) {


		int size = sortedData.size();
		int i=0;
		sortedData.sort();

		while (i<size) {
			int element = sortedData.elementAt(i);
			int cursor = i;

			// determine run length (number of equal elements)
			while (++i < size  && sortedData.elementAt(i)==element);

			int runLength = i - cursor;
			distinctValues.addElement(element  );
			if (frequencies!=null) frequencies.addElement( runLength);
		}
	}
	/**
	 * Returns the arithmetic mean of a data sequence;
	 * That is <tt>Sum( data[i] ) / data.size()</tt>.
	 */
	public static double mean(IntVector data) {
		if (data != null && data.size()>0 ){
			return sum(data) / data.size();
		} else {
			return 0;
		}
	}

	public static int sum(IntVector data) {
		int sum = 0;
		for ( int i= 0; i<data.size(); i++){
			sum += data.elementAt(i);
		}
		return sum;

	}
	}
