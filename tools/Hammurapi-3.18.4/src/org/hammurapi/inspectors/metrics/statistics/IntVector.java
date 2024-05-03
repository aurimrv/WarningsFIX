/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: IntVector.java,v 1.2 2004/06/19 06:41:22 pvlasov Exp $
 */
// package org.apache.xml.utils;

package org.hammurapi.inspectors.metrics.statistics;

/**
 * A very simple table that stores a list of int.
 *
 * This version is based on a "realloc" strategy -- a simle array is
 * used, and when more storage is needed, a larger array is obtained
 * and all existing data is recopied into it. As a result, read/write
 * access to existing nodes is O(1) fast but appending may be O(N**2)
 * slow. See also SuballocatedIntVector.
 * @xsl.usage internal
 */
public class IntVector implements Cloneable
{

  /** Size of blocks to allocate          */
  protected int m_blocksize;

  /** Array of ints          */
  protected int m_map[]; // IntStack is trying to see this directly

  /** Number of ints in array          */
  protected int m_firstFree = 0;

  /** Size of array          */
  protected int m_mapSize;

  /**
   * Default constructor.  Note that the default
   * block size is very small, for small lists.
   */
  public IntVector()
  {

    m_blocksize = 32;
    m_mapSize = m_blocksize;
    m_map = new int[m_blocksize];
  }

  public IntVector(int[] intarray )
  {
    m_blocksize = 32;
    m_mapSize = m_blocksize;
    m_map = new int[m_blocksize];
    for( int i=0; i< intarray.length; i++){
    	 this.addElement(intarray[i]);
    	 }
  }

  /**
   * Construct a IntVector, using the given block size.
   *
   * @param blocksize Size of block to allocate
   */
  public IntVector(int blocksize)
  {

    m_blocksize = blocksize;
    m_mapSize = blocksize;
    m_map = new int[blocksize];
  }

  /**
   * Construct a IntVector, using the given block size.
   *
   * @param blocksize Size of block to allocate
   */
  public IntVector(int blocksize, int increaseSize)
  {

    m_blocksize = increaseSize;
    m_mapSize = blocksize;
    m_map = new int[blocksize];
  }

  /**
   * Copy constructor for IntVector
   *
   * @param v Existing IntVector to copy
   */
  public IntVector(IntVector v)
  {
  	m_map = new int[v.m_mapSize];
    m_mapSize = v.m_mapSize;
    m_firstFree = v.m_firstFree;
  	m_blocksize = v.m_blocksize;
  	System.arraycopy(v.m_map, 0, m_map, 0, m_firstFree);
  }

  /**
   * Get the length of the list.
   *
   * @return length of the list
   */
  public final int size()
  {
    return m_firstFree;
  }

  public boolean isEmpty(){
  	return m_firstFree == 0;
  }

  /**
   * Get the length of the list.
   *
   * @return length of the list
   */
  public final void setSize(int sz)
  {
    m_firstFree = sz;
  }


  /**
   * Append a int onto the vector.
   *
   * @param value Int to add to the list
   */
  public final void addElement(int value)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      int newMap[] = new int[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Append several int values onto the vector.
   *
   * @param value Int to add to the list
   */
  public final void addElements(int value, int numberOfElements)
  {

    if ((m_firstFree + numberOfElements) >= m_mapSize)
    {
      m_mapSize += (m_blocksize+numberOfElements);

      int newMap[] = new int[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    for (int i = 0; i < numberOfElements; i++)
    {
      m_map[m_firstFree] = value;
      m_firstFree++;
    }
  }

  /*
  Copyright © 1999 CERN - European Organization for Nuclear Research.
  Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
  is hereby granted without fee, provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear in supporting documentation.
  CERN makes no representations about the suitability of this software for any purpose.
  It is provided "as is" without expressed or implied warranty.
  */

  public void sort(){

  	 quickSort1( this.m_map, 0, this.m_firstFree);
  }

  /**
   * Sorts the specified sub-array of chars into ascending order.
   */
  private static void quickSort1( final int x[], int off, int len) {
	int SMALL = 7;
	int MEDIUM = 40;

	IntComparator comp = new IntComparator() {
		public int compare(int a, int b) {
			// return x[a]==x[b] ? 0 : (x[a]<x[b] ? -1 : 1);
			return a==b ? 0 : (a<b ? -1 : 1);
		}
	};

  	// Insertion sort on smallest arrays
  	if (len < SMALL) {
  		for (int i=off; i<len+off; i++)
  		for (int j=i; j>off && comp.compare(x[j-1],x[j])>0; j--)
  		    swap(x, j, j-1);
  		return;
  	}

  	// Choose a partition element, v
  	int m = off + len/2;       // Small arrays, middle element
  	if (len > SMALL) {
  		int l = off;
  		int n = off + len - 1;
  		if (len > MEDIUM) {        // Big arrays, pseudomedian of 9
  		int s = len/8;
  		l = med3(x, l,     l+s, l+2*s, comp);
  		m = med3(x, m-s,   m,   m+s, comp);
  		n = med3(x, n-2*s, n-s, n, comp);
  		}
  		m = med3(x, l, m, n, comp); // Mid-size, med of 3
  	}
  	int v = x[m];

  	// Establish Invariant: v* (<v)* (>v)* v*
  	int a = off, b = a, c = off + len - 1, d = c;
  	while(true) {
  		int comparison;
  		while (b <= c && (comparison=comp.compare(x[b],v))<=0) {
  		if (comparison == 0)
  		    swap(x, a++, b);
  		b++;
  		}
  		while (c >= b && (comparison=comp.compare(x[c],v))>=0) {
  		if (comparison == 0)
  		    swap(x, c, d--);
  		c--;
  		}
  		if (b > c)
  		break;
  		swap(x, b++, c--);
  	}

  	// Swap partition elements back to middle
  	int s, n = off + len;
  	s = Math.min(a-off, b-a  );  vecswap(x, off, b-s, s);
  	s = Math.min(d-c,   n-d-1);  vecswap(x, b,   n-s, s);

  	// Recursively sort non-partition-elements
  	if ((s = b-a) > 1)
  		quickSort1(x, off, s);
  	if ((s = d-c) > 1)
  		quickSort1(x, n-s, s);
  }

  /**
   * Swaps x[a] with x[b].
   */
  private static void swap(int x[], int a, int b) {
  	int t = x[a];
  	x[a] = x[b];
  	x[b] = t;
  }

  /**
	 * Returns the index of the median of the three indexed chars.
	 */
	private static int med3(int x[], int a, int b, int c, IntComparator comp) {
		int ab = comp.compare(x[a],x[b]);
		int ac = comp.compare(x[a],x[c]);
		int bc = comp.compare(x[b],x[c]);
		return (ab<0 ?
		(bc<0 ? b : ac<0 ? c : a) :
		(bc>0 ? b : ac>0 ? c : a));
	}
	/**
	 * Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)].
	 */
	private static void vecswap(int x[], int a, int b, int n) {
	for (int i=0; i<n; i++, a++, b++)
	    swap(x, a, b);
	}
  /**
   * Append several slots onto the vector, but do not set the values.
   *
   * @param value Int to add to the list
   */
  public final void addElements(int numberOfElements)
  {

    if ((m_firstFree + numberOfElements) >= m_mapSize)
    {
      m_mapSize += (m_blocksize+numberOfElements);

      int newMap[] = new int[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_firstFree += numberOfElements;
  }


  /**
   * Inserts the specified node in this vector at the specified index.
   * Each component in this vector with an index greater or equal to
   * the specified index is shifted upward to have an index one greater
   * than the value it had previously.
   *
   * @param value Int to insert
   * @param at Index of where to insert
   */
  public final void insertElementAt(int value, int at)
  {

    if ((m_firstFree + 1) >= m_mapSize)
    {
      m_mapSize += m_blocksize;

      int newMap[] = new int[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    if (at <= (m_firstFree - 1))
    {
      System.arraycopy(m_map, at, m_map, at + 1, m_firstFree - at);
    }

    m_map[at] = value;

    m_firstFree++;
  }

  /**
   * Inserts the specified node in this vector at the specified index.
   * Each component in this vector with an index greater or equal to
   * the specified index is shifted upward to have an index one greater
   * than the value it had previously.
   */
  public final void removeAllElements()
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      m_map[i] = java.lang.Integer.MIN_VALUE;
    }

    m_firstFree = 0;
  }

  /**
   * Removes the first occurrence of the argument from this vector.
   * If the object is found in this vector, each component in the vector
   * with an index greater or equal to the object's index is shifted
   * downward to have an index one smaller than the value it had
   * previously.
   *
   * @param s Int to remove from array
   *
   * @return True if the int was removed, false if it was not found
   */
  public final boolean removeElement(int s)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i] == s)
      {
        if ((i + 1) < m_firstFree)
          System.arraycopy(m_map, i + 1, m_map, i - 1, m_firstFree - i);
        else
          m_map[i] = java.lang.Integer.MIN_VALUE;

        m_firstFree--;

        return true;
      }
    }

    return false;
  }

  /**
   * Deletes the component at the specified index. Each component in
   * this vector with an index greater or equal to the specified
   * index is shifted downward to have an index one smaller than
   * the value it had previously.
   *
   * @param i index of where to remove and int
   */
  public final void removeElementAt(int i)
  {

    if (i > m_firstFree)
      System.arraycopy(m_map, i + 1, m_map, i, m_firstFree);
    else
      m_map[i] = java.lang.Integer.MIN_VALUE;

    m_firstFree--;
  }

  /**
   * Sets the component at the specified index of this vector to be the
   * specified object. The previous component at that position is discarded.
   *
   * The index must be a value greater than or equal to 0 and less
   * than the current size of the vector.
   *
   * @param node object to set
   * @param index Index of where to set the object
   */
  public final void setElementAt(int value, int index)
  {
    m_map[index] = value;
  }

  /**
   * Get the nth element.
   *
   * @param i index of object to get
   *
   * @return object at given index
   */
  public final int elementAt(int i)
  {
    return m_map[i];
  }

  /**
   * Tell if the table contains the given node.
   *
   * @param s object to look for
   *
   * @return true if the object is in the list
   */
  public final boolean contains(int s)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i] == s)
        return true;
    }

    return false;
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * @param elem object to look for
   * @param index Index of where to begin search
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public final int indexOf(int elem, int index)
  {

    for (int i = index; i < m_firstFree; i++)
    {
      if (m_map[i] == elem)
        return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * @param elem object to look for
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public final int indexOf(int elem)
  {

    for (int i = 0; i < m_firstFree; i++)
    {
      if (m_map[i] == elem)
        return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }

  /**
   * Searches for the first occurence of the given argument,
   * beginning the search at index, and testing for equality
   * using the equals method.
   *
   * @param elem Object to look for
   * @return the index of the first occurrence of the object
   * argument in this vector at position index or later in the
   * vector; returns -1 if the object is not found.
   */
  public final int lastIndexOf(int elem)
  {

    for (int i = (m_firstFree - 1); i >= 0; i--)
    {
      if (m_map[i] == elem)
        return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }

  /**
   * Returns clone of current IntVector
   *
   * @return clone of current IntVector
   */
  public Object clone()
    throws CloneNotSupportedException
  {
  	return new IntVector(this);
  }


  public String toString(){
  	StringBuffer strb = new StringBuffer();
  	strb.append ( "[");
  	for ( int i= 0; i< m_firstFree; i++){
  		strb.append ( this.elementAt(i) );
  		if( i+1 == m_firstFree){
  			strb.append ( "]");
  		}else{
  			strb.append ( "," );
  		}
  }

  	return strb.toString();
  	}
}
