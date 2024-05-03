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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.pavelvlasov.config.ConfigurationException;
import com.pavelvlasov.config.Parameterizable;

/**
 * Base class for inspectors which require extensive configuration by different helper classes.
 * @author Pavel Vlasov
 * @version $Revision: 1.1 $
 */
public class ParameterizableInspectorBase extends InspectorBase implements Parameterizable {
    private Collection configurators=new ArrayList();

    /**
     * Adds a helper class (configurator) to the list of configurators.
     * @param configurator
     */
    protected void addConfigurator(Parameterizable configurator) {
        configurators.add(configurator);
    }
    
	
	/**
     * Iterates through configurators and passes parameter to them unless it is handled.
     * @param name the name of the parameter being loaded from Hammurapi configuration
     * @param value the value of the parameter being loaded from Hammurapi configuration
     * @exception ConfigurationException in case of a not supported parameter
     */
    public boolean setParameter(String name, Object value) throws ConfigurationException {
        Iterator it=configurators.iterator();
        while (it.hasNext()) {
            if (((Parameterizable) it.next()).setParameter(name, value)) {
                return true;
            }
        }
        return false;
    }	
}
