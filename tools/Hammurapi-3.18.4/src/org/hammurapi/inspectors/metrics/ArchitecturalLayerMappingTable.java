/*
 * Created on Jul 12, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import java.util.Hashtable;

/**
 * @author MUCBJ0
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ArchitecturalLayerMappingTable extends Hashtable {

	public void put(String key, ArchitecturalLayerMapping value){
		super.put( key, value);
	}
	public ArchitecturalLayerMapping get (String key){
		return (ArchitecturalLayerMapping)super.get( key);
	}
}
