package com.puppycrawl.tools.checkstyle;

/**
 * A parser factory that produces parsers that support XML namespaces. 
 * @author Rick Giles
 * @version May 28, 2004
 */
public class NamespacesSAXParserFactoryImpl extends SAXParserFactoryImpl
{
    /**
     * Constructs a NamespacesSAXParserFactoryImpl. Initializes
     * it to produce parsers that support XML namespaces. 
     */
    public NamespacesSAXParserFactoryImpl()
    {
        super();
        setNamespaceAware(true);
    }
}
