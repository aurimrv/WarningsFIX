
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
 * e-Mail: Johannes.Bellert@gmail.com
 */

package org.hammurapi.inspectors.metrics;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author 111001082
 *

 */
public class JarFileLookup {


    public JarFileList parseClasspath() {
        JarFileList jarFileList = new JarFileList();
        StringTokenizer tokenizer = new StringTokenizer(System
                .getProperty("java.class.path"), System
                .getProperty("path.separator"));

        while (tokenizer.hasMoreTokens()) {
            try {
                String jarFileAbsolutePath = tokenizer.nextToken();
                 File jarFile = new File(jarFileAbsolutePath);
                 JarFile jf = new JarFile( jarFileAbsolutePath, jarFile.length(), jarFile.lastModified() );
                 jarFileList.add( jf );
               //  System.out.println ( jf.toString() );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jarFileList;
    }

public static void main(String[] args) {
        new JarFileLookup().parseClasspath();
    }}
