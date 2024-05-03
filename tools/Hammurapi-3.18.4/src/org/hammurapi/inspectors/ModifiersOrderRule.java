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
package org.hammurapi.inspectors;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hammurapi.InspectorBase;

import com.pavelvlasov.jsel.Field;

/**
 * 
 * @author  Pavel Vlasov
 * @version $Revision: 1.2 $
 */
public class ModifiersOrderRule extends InspectorBase {
    public static final List modifiers;
    
    static {
        List mdf=new LinkedList();
        mdf.add("public");
        mdf.add("protected");
        mdf.add("private");
        mdf.add("abstract");
        mdf.add("static");
        mdf.add("final");
        mdf.add("synchronized");
        mdf.add("native");
        mdf.add("transient");
        mdf.add("volatile");
        mdf.add("strictfp");
        modifiers=Collections.unmodifiableList(mdf);
    }    

    public void visit(Field field) {
        int prevIdx=-1;
        Iterator it=field.getModifiers().iterator();
        while (it.hasNext()) {            
            int idx=modifiers.indexOf(it.next());
            if (idx<prevIdx) {
                context.reportViolation(field);
                break;
            }
            prevIdx=idx;
        }
    }    
}
