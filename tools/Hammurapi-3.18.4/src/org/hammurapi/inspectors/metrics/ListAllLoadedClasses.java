/*
 * Created on Dec 9, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author 111001082
 *
  */


public class ListAllLoadedClasses {
    public static Iterator list(ClassLoader CL) throws NoSuchFieldException,
            IllegalAccessException {
        Class CL_class = CL.getClass();
        while (CL_class != java.lang.ClassLoader.class) {
            CL_class = CL_class.getSuperclass();
        }
        java.lang.reflect.Field ClassLoader_classes_field = CL_class
                .getDeclaredField("classes");
        ClassLoader_classes_field.setAccessible(true);
        Vector classes = (Vector) ClassLoader_classes_field.get(CL);
        return classes.iterator();
    }

    public static void main(String args[]) throws Exception {
        // URLClassLoader u = new URLClassLoader();
        System.out.println( System.getProperty("java.class.path", "."));
        
        ClassLoader myCL = ListAllLoadedClasses.class.getClassLoader();
        while (myCL != null) {
            System.out.println("ClassLoader: " + myCL);
            for (Iterator iter = list(myCL); iter.hasNext();) {
                System.out.println("\t" + iter.next());
            }
            myCL = myCL.getParent();
        }
    }
}

