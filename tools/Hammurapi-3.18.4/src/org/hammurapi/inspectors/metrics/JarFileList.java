/*
 * Created on Dec 19, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author 111001082
 * 
 */
public class JarFileList extends Vector {

    public void markUsedJarFiles(Vector techEntityList,
            Hashtable allTechStackEntitiesTable) {
        for (int i = 0; i < this.size(); i++) {
            int j = 0;
            boolean notFound = true;
            JarFile jarFile = (JarFile) this.elementAt(i);
            while (notFound && j < techEntityList.size()) {

                TechStackEntity tse = (TechStackEntity) techEntityList.elementAt(j);
                if (tse != null) {
                    
                    // side effect in markIfPartOfJarFileList: tse Jars isUsed flag will be set
                    if (tse.markIfPartOfJarFileList(jarFile.getJarNameWithoutPath())) {
                        jarFile.setIsUsed(true);
                        // System.out.println(  "TRUE " + jarFile);
                    }
                }
                j++;
            }
        }
    }
    
    public  Element toDom(Document document){
        
        Element jarFileListE =document.createElement("ClasspathFileLicense");
        
        for (int i = 0; i < this.size(); i++) {
            int j = 0;
            boolean notFound = true;
            JarFile jarFile = (JarFile) this.elementAt(i);
            jarFileListE.appendChild(jarFile.toDom(document));
            //System.out.println( jarFile.toString());
        }        
        return jarFileListE;
    }
}
