/*
 * Created on Dec 10, 2004
 *
 */
package org.hammurapi.inspectors.metrics;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author 111001082
 */
public class JarFile {
    private String name = "undefined";

    private String version = "undefined";

    private long size = 0;
    private Date lastChanged = new Date();
    public boolean isUsed = false; 

    public JarFile(String _name, long _size, long _lastChanged) {
        super();
        name = _name;
        version = "NA";
        size = _size;
        lastChanged =        new Date( _lastChanged );
    }
    public JarFile(Element jarNode) {
        super();
        name = (String)jarNode.getAttribute("name");
        version = (String)jarNode.getAttribute("version");
        
        String sSize = jarNode.getAttribute("size");
        if( sSize == null ||  "".equals( sSize) ){
            size = 0;
        } else {
            size = new Long(sSize).longValue();
        };
    }
    public void setIsUsed(boolean _b){
        isUsed = _b;
    }

	public Element toDom(Document document){

		Element ret=document.createElement("JarFile");
		ret.setAttribute("name", this.getJarNameWithoutPath() );
		ret.setAttribute("path", this.name );
		
		ret.setAttribute("version", this.version );
		ret.setAttribute("size", Long.toString(this.size) );
		ret.setAttribute("lastChanged", this.lastChanged.toString() );
		ret.setAttribute("isUsed", Boolean.toString(isUsed) );
		ret.setAttribute("size",  Double.toString(this.size ));
		return ret;
	}
	

	public String toString(){

		StringBuffer sb = new StringBuffer();
		sb.append( this.name );
		sb.append( "\t");
		sb.append( this.size );
		sb.append( "\t");
		sb.append( this.version );
		sb.append( "\t");
		sb.append( this.lastChanged.toString() );
	
		return sb.toString();
	}
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    public String getJarNameWithoutPath(){
        int lastSlash = getName().lastIndexOf('/');
        int lastBackSlash = getName().lastIndexOf('\\');
        int index =0;
        if( lastSlash > lastBackSlash){
            index = lastSlash+1;
        }else{
            index = lastBackSlash+1;
            }
        
        return getName().substring(index);
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
