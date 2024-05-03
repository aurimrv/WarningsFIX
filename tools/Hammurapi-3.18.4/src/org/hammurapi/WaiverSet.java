/*
 * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Hammurapi Group
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
 * URL: http://www.hammurapi.org
 * e-Mail: support@hammurapi.biz

 */

package org.hammurapi;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.pavelvlasov.review.Signed;
import com.pavelvlasov.review.SourceMarker;

/**
 * @author Pavel Vlasov	
 * @version $Revision: 1.8 $
 */
public class WaiverSet {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4873150699959326186L;
	private Map signatureMap=new HashMap();	
	private Collection rejectedRequests=new LinkedList();

	/**
	 * @return Collection of waivers remaining in the set
	 */
	public Collection getWaivers() {
		Collection ret=new LinkedList();
		Iterator it=signatureMap.values().iterator();
		while (it.hasNext()) {
			ret.addAll(((Map) it.next()).values());
		}
		return ret;
	}
	
	public int size() {
		int ret=0;
		Iterator it=signatureMap.values().iterator();
		while (it.hasNext()) {
			ret+=((Map) it.next()).size();
		}
		return ret;		
	}
	
	/**
	 * @return Collection of violations which requested waivers, but 
	 * weren't granted ones.
	 */
	public Collection getRejectedRequests() {
		return rejectedRequests;
	}

	/**
	 * Requests and removes a waiver from the set. Waivers with signature==null
	 * aren't removed as they apply to multiple violations.
	 * @param violation
	 * @param peek Just peek for waiver.
	 * @return Waiver if there is one for the violation, null otherwise.
	 */
	public Waiver requestWaiver(Violation violation, boolean peek) {		
		if (Boolean.TRUE.equals(violation.getDescriptor().isWaivable())) {
			SourceMarker sm=violation.getSource();
			String signature= sm instanceof Signed ? ((Signed) sm).getSignature() : null;
			if (signature!=null) {
				Map nameMap=(Map) signatureMap.get(signature);
				Waiver ret = nameMap==null ? null : (Waiver) nameMap.get(violation.getDescriptor().getName());
								
				if (ret!=null) {
					if (!peek) {
						nameMap.remove(violation.getDescriptor().getName());						
					}
					
					if (ret.waive(violation, peek)) {
						return ret;
					}
				}
			}

			
			Map nameMap=(Map) signatureMap.get(null);
			Waiver ret = nameMap==null ? null : (Waiver) nameMap.get(violation.getDescriptor().getName());
			boolean waived = ret!=null && ret.waive(violation, peek);
			
			if (!waived) {
				rejectedRequests.add(violation);
			} 
			
			if (ret!=null && !ret.isActive()) {
				nameMap.remove(ret.getInspectorName());
			}
			
			return waived ? ret : null;
		}
		
		return null;		
	}
	
	public void addWaiver(Waiver waiver, Date now) {
		if (waiver.getExpirationDate()==null || now.before(waiver.getExpirationDate())) {
			Collection signatures = waiver.getSignatures();
			if (signatures!=null && !signatures.isEmpty()) {
				Iterator it=signatures.iterator();
				while (it.hasNext()) {
					String signature=(String) it.next();
					getNameMap(signature).put(waiver.getInspectorName(), waiver);
				}
			} else {
				getNameMap(null).put(waiver.getInspectorName(), waiver);
			}
		}
	}

	/**
	 * @param signature
	 * @return name map
	 */
	private Map getNameMap(String signature) {
		Map nameMap=(Map) signatureMap.get(signature);
		if (nameMap==null) {
			nameMap=new HashMap();
			signatureMap.put(signature, nameMap);
		}
		return nameMap;
	}
}
