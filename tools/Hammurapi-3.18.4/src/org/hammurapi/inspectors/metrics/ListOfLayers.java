/*
 * Hammurapi
 * Automated Java code review system.
 * Copyright (C) 2004  Johannes Bellert
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * URL: http://www.pavelvlasov.com/pv/content/menu.show?id=products.jtaste
 * e-Mail: Johannes.Bellert@ercgroup.com
 *
 *  * Created on Apr 10, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Johannes Bellert
 *
 */
public class ListOfLayers {

	private Hashtable layers = new Hashtable();

	public ListOfLayers( Hashtable allCategorizedPackages){

		Vector alreadyVisitedPackages = new Vector(); 
		Enumeration enumKeys = allCategorizedPackages.keys();
		while( enumKeys.hasMoreElements()){
			String key = (String)enumKeys.nextElement();
//!! job: not all packages are reported in Tiers
						
			ListOfPackageCategories lopc =  (ListOfPackageCategories)allCategorizedPackages.get(key);
			HashSet lopcVec = lopc.getLayers();
			Iterator it = lopcVec.iterator();
			while( it.hasNext() ){
				String layStr = (String)it.next();

				Vector packageList = (Vector)layers.get(layStr);
				if( packageList == null ){
					packageList =  new Vector();
				}
				PackageOccuranceInLayer poil = new PackageOccuranceInLayer( lopc.getAPackage());
					if ( alreadyVisitedPackages.contains(  poil ) ){ 
						int i = alreadyVisitedPackages.indexOf(poil);
						
						PackageOccuranceInLayer thisPoil = (PackageOccuranceInLayer)alreadyVisitedPackages.elementAt(i);
						thisPoil.setOccurance( ((int)thisPoil.getOccurance())+1 );
						thisPoil.getLayerList().add( layStr );
						packageList.add( thisPoil  );
					} else {
						packageList.add( poil  );
						alreadyVisitedPackages.add(poil);
						poil.getLayerList().add( layStr  );
					}
					
				layers.put( layStr,  packageList );
			}
		}
	}
	
	
	public Element toDom(Document document){

		Element ret=document.createElement("ListOfLayers");
		ret.setAttribute("size", String.valueOf(this.layers.size()));
		Enumeration enum = this.layers.keys();
		while (enum.hasMoreElements()) {
			String layerKey = (String) enum.nextElement();
			Element layer=document.createElement("Tier");
			layer.setAttribute("name", layerKey );
			ret.appendChild(layer);

			Vector packages = (Vector)layers.get( layerKey );
			for(int i=0; i<packages.size(); i++ ){
				Element pack=document.createElement("Package");
				PackageOccuranceInLayer poil = ((PackageOccuranceInLayer)packages.elementAt(i));
				pack.setAttribute("name", poil.getPackageName() );
				for(int j=0; j<poil.getLayerList().size(); j++ ){
					String myLayer = (String)poil.getLayerList().elementAt(j);
					if( !layerKey.equals(myLayer) ){
						pack.setAttribute("occurance", String.valueOf(poil.getOccurance() ) );

					Element poilList=document.createElement("PoilLayerOccurance");
					poilList.setAttribute("name", (String)poil.getLayerList().elementAt(j) );
					pack.appendChild(poilList);
					}}
				layer.appendChild(pack);
			}
		}
		return ret;

	}
}
