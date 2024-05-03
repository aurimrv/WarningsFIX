<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="inspectorsPath"/>

    <xsl:template match="/">       
        <HTML>
            <xsl:apply-templates select="/inspector-summary"/>
        </HTML>
    </xsl:template>
    
    <xsl:template match="inspector-summary">
        <HEAD>
            <TITLE>Hammurapi 3.18.4 [Inspector summary]</TITLE>
            <STYLE> 
            <xsl:text disable-output-escaping="yes">
                <![CDATA[<!-- ]]>
                    .severity1 { background:#FFAAAA }
                    .severity2 { background:#FFFFAA }
                    .severity3 { background:#AAFFAA }
                    TH { font:bold; background:gray }
                    TABLE { background:silver }
                    TR { background:white }
            <![CDATA[ --> ]]>
            </xsl:text>
            </STYLE>
        </HEAD>                    
        <BODY>
        <H1>Inspector <xsl:value-of select="@inspector"/> summary</H1>
        <B>Severity: </B><xsl:value-of select="@severity"/><BR/>
        <xsl:if test="@version">
	        <B>Version: </B><xsl:value-of select="@version"/><BR/>        	
        </xsl:if>
        <B>Description: </B><xsl:value-of  disable-output-escaping="yes" select="@description"/><P/>
        
        <xsl:if test="config-info">
        	<B>Configuration: </B><PRE><xsl:value-of select="config-info"/></PRE><P/>
        </xsl:if>        
        
        <B style="color:blue">Violations</B>
        <table border="0" cellspacing="1" cellpadding="3" class="standard">
            <tr class="standard">
                <th class="standard">#</th>
                <th class="standard">File</th>
                <th class="standard">Line</th>
                <th class="standard">Column</th>
            </tr>
            <xsl:for-each select="source-marker">
                <tr class="standard">
                    <td align='right'><xsl:value-of select="position()"/></td>
                    <td><xsl:value-of select="@source-url"/></td>
                    <td align='right'>
                        <a>
                            <xsl:attribute name="href">
                            	<xsl:choose>
                            		<xsl:when test="not(@source-url)">summary.html</xsl:when>
                            		
                            		<xsl:when test="substring(@source-url, string-length(@source-url))='/'"><xsl:value-of select="$inspectorsPath"/><xsl:value-of select="@source-url"/>.summary.html</xsl:when>
		                            
		                            <xsl:otherwise>		                            		                            
		                            	<xsl:value-of select="$inspectorsPath"/><xsl:value-of select="@source-url"/>.html#line_<xsl:value-of select="@line"/>
		                            </xsl:otherwise>
		                        </xsl:choose>
                            </xsl:attribute>
                            <xsl:value-of select="@line"/>
                        </a>
                    </td>
                    <td align='right'><xsl:value-of select="@col"/></td>
                </tr>                        
            </xsl:for-each>
        </table>
	    <HR/>
	    <i>Hammurapi 3.18.4 Copyright <xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> 2004 Hammurapi Group. All Rights Reserved.</i>
        </BODY>    
    </xsl:template>        
</xsl:stylesheet>
