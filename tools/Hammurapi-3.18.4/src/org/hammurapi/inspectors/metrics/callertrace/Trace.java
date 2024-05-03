/*
 * Created on Nov 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;


import java.util.Vector;


/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Trace extends Vector {

	// private static Logger logger = Logger.getLogger(Trace.class.getName());

	public Trace(Vector adjTrace, MethodMap allMethods){
		super();

		//!! doublet code with TraceList
		for (int i= 0; i<adjTrace.size(); i++){

			String key = (String)adjTrace.elementAt(i);
		if( key != null &&  !"".equals( key ) ){
			MethodWrapper m = (MethodWrapper)allMethods.get(key);
			if (m != null ){
		TracedMethod tm = new TracedMethod(m);
		tm.setMethod((MethodWrapper)allMethods.get(key));
		this.add(tm);
			}else {
				System.out.println( "Method not resolved for key: " + key);
			}
		}else{
			System.out.println( "Trace contains key: " + key);
			}
		}
	}
	
	public boolean sameNameAndSizeButNotEqual(Trace searchTrace ){
		TracedMethod thisTm = (TracedMethod) this.elementAt(this.size() - 1);
		TracedMethod searchTraceTm = (TracedMethod) searchTrace.elementAt(searchTrace.size() - 1);
		return ( this.size() == searchTrace.size() &&
				thisTm.toKey().equals( searchTraceTm.toKey() ) );
	}

	public Trace(){
		super();
	}
	/* (non-Javadoc)
	 * @see antlr.collections.impl.Vector#elementAt(int)
	 */
	public  TracedMethod traceElementAt(int arg0) {
		// TODO Auto-generated method stub
		return (TracedMethod)super.elementAt(arg0);
	}

}

