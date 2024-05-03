/*
 * Created on Sep 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import com.pavelvlasov.jsel.LanguageElement;

/**
 * @author Johannes
 *
 */
public class StringVariable {
    
    public StringVariable( LanguageElement _langElement,
            				StringBuffer _varValue,
            				String _name,
            				String _type,
            				String _className,
            				String _classFcn ){
        super();
        langElement = _langElement;
        varValue = _varValue;
        name = _name;
        type = _type;
        className = _className;
        if( _classFcn != null){
        classFcn = _classFcn;}
    }
    
    public LanguageElement langElement = null;
    public String className = "<undefined>";
    public String classFcn = "<undefined>";
    public String name = "<undefined>";
    public StringBuffer varValue = new StringBuffer(); 
    public String type = "<undefined>";


	public String toString(){
	    return name + " <-- " + varValue;
	}
}
