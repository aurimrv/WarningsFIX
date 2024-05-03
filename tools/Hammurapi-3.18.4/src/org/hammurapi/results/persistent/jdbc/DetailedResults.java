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

package org.hammurapi.results.persistent.jdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.hammurapi.HammurapiException;
import org.hammurapi.Violation;
import org.hammurapi.Waiver;
import org.hammurapi.WaiverEntry;
import org.hammurapi.WaiverSet;
import org.hammurapi.results.persistent.jdbc.sql.WaivedViolationJoined;

import com.pavelvlasov.convert.Converter;
import com.pavelvlasov.review.Signed;

/**
 *
 * @author  Pavel Vlasov
 * @version $Revision: 1.9 $
 */
public class DetailedResults extends NamedResults implements org.hammurapi.results.DetailedResults {
        
	/**
	 * @param id
	 * @param factory
	 * @throws SQLException
	 */
	public DetailedResults(int id, ResultsFactory factory) throws SQLException {
		super(id, factory);
	}
	
	DetailedResults(String name, WaiverSet waiverSet, ResultsFactory factory) throws SQLException {    	
        super(name, waiverSet, factory);
    }
    
    public Waiver addViolation(final Violation violation) throws HammurapiException {
    	final Waiver waiver=super.addViolation(violation);
    	if (waiver!=null) {
    		insertViolation(
    				violation, 
					WAIVED_VIOLATION, 
					new ViolationConfigurator() {
						public void setViolationInfo(org.hammurapi.results.persistent.jdbc.sql.Violation sqlViolation) {							
							String reason = waiver.getReason();
							if (reason!=null) {
								sqlViolation.setWaiverReason(new Integer(factory.addMessage(reason)));
							}
							
							Date expirationDate = waiver.getExpirationDate();
							if (expirationDate!=null) {
								sqlViolation.setWaiverExpires(new java.sql.Date(expirationDate.getTime()));
							}
						}
    		});
    	}
    	return waiver;
    }
        
    public Collection getViolations() {
    	return factory.getResultsEngine().getViolationJoined(getId(), violationConverter);
    }                
	    
 	private Converter waivedViolationConverter=new Converter() {
		public Object convert(Object o) {
			final WaivedViolationJoined wvj=(WaivedViolationJoined) o;
			final Violation violation=(Violation) violationConverter.convert(o);
			
			final Set signatures=new HashSet();			
			final String signature=wvj.getSignature();
			if (signature!=null) {
				StringTokenizer st=new StringTokenizer(signature,"|");
				while (st.hasMoreTokens()) {
					signatures.add(st.nextToken());
				}
			}
			Date waiverExpires = wvj.getWaiverExpires();
			final java.util.Date expirationDate = waiverExpires==null ? null : new Date(waiverExpires.getTime());
			
			return new WaiverEntry() {

				/**
				 * Comment for <code>serialVersionUID</code>
				 */
				private static final long serialVersionUID = -5416258427336694577L;

				public Waiver getWaiver() {
					return new Waiver() {
						public String getInspectorName() {
							return violation.getDescriptor().getName();
						}

						public java.util.Date getExpirationDate() {
							return expirationDate;
						}

						public String getReason() {
							return wvj.getWaiverReason();
						}

						public boolean waive(Violation v, boolean peek) {
							if (violation.getDescriptor().getName().equals(v.getDescriptor().getName())) {
								if (signature==null) {
									return true;
								}
								
								String vSignature = ((Signed) violation.getSource()).getSignature();
								if (violation.getSource() instanceof Signed && signatures.contains(vSignature)) {
									if (!peek) {
										signatures.remove(vSignature);
									}
									return true;
								}
							}
							return false;
						}

						public boolean isActive() {
							return !signatures.isEmpty();
						}

						public Collection getSignatures() {
							return signatures;
						}
					};
				}

				public Violation getViolation() {
					return violation;
				}
			};
		}
	};
    
	public Collection getWaivedViolations() {
		return factory.getResultsEngine().getWaivedViolationJoined(getId(), waivedViolationConverter);
	}    
}
