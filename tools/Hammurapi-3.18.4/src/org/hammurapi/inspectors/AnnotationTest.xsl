<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="inspectorsPath"/>

    <xsl:template match="/">       
        <HTML>
            <xsl:apply-templates select="/annotation-test"/>
        </HTML>
    </xsl:template>
    
    <xsl:template match="annotation-test">
        <HEAD>        
            <TITLE><xsl:value-of select="title"/></TITLE>
        </HEAD>                    
        <BODY>
	        <H1>Annotation "<xsl:value-of select="title"/>"</H1>
	        <B>Pi: </B><xsl:value-of select="@pi"/><BR/>
			<img>
				<xsl:attribute name="src"><xsl:value-of select="@image"/></xsl:attribute>
			</img>        
        </BODY>    
    </xsl:template>        
</xsl:stylesheet>
