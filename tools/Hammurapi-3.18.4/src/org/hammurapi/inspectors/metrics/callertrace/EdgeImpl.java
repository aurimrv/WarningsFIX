/*
 * Created on Jan 16, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.hammurapi.inspectors.metrics.callertrace;

/**
 * @author mucbj0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EdgeImpl {
	private Object nodeA;
	private Object nodeB;

	public EdgeImpl ( Object nodeA, Object nodeB ){
		super();
		this.setNodeA( nodeA );
		this.setNodeB( nodeB );
	}

	public String toString(){

		return nodeA.toString() + AdjacencyMatrix.KEY_SEPERATOR + nodeB.toString();
	}

	public Object toKey(){

		return this.toString();
	}

	//!! use Interface here
	public boolean equals ( EdgeImpl e){
		if ( e.toKey() != null &&  this.toKey().equals(e.toKey() )){
			return true;
		} else { return false; }
	}

	/**
	 * @return Returns the nodeA.
	 */
	public Object getNodeA() {
		return nodeA;
	}

	/**
	 * @param nodeA The nodeA to set.
	 */
	public void setNodeA(Object nodeA) {
		this.nodeA = nodeA;
	}

	/**
	 * @return Returns the nodeB.
	 */
	public Object getNodeB() {
		return nodeB;
	}

	/**
	 * @param nodeB The nodeB to set.
	 */
	public void setNodeB(Object nodeB) {
		this.nodeB = nodeB;
	}

}
