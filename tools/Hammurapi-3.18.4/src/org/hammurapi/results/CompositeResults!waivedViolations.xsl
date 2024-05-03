<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="inspectorsPath"/>

    <xsl:template match="/">
        <HTML>
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
        <H1>Waived violations</H1>
            <xsl:if test="//waived-violation">
                <P/>
                <B style="color:blue">Waived violations</B>
                <table border="0" cellspacing="1" cellpadding="3" class="standard">
                    <tr class="standard">
                        <th class="standard">#</th>
		                <th class="standard">File</th>
                        <th class="standard">Line</th>
                        <th class="standard">Column</th>
                        <th class="standard">Name</th>
                        <th class="standard">Severity</th>
                        <th class="standard">Description</th>
                        <th class="standard">Waiver reason</th>
                        <th class="standard">Waiver expires</th>
                    </tr>
                    <xsl:for-each select="//waived-violation">
						<xsl:sort select="violation/@source-url"/>
						<xsl:sort select="violation/@line"/>
						<xsl:sort select="violation/@col"/>
						<xsl:sort select="violation/inspector-descriptor/name"/>
						
                        <tr class="standard">
                            <td align='right'><xsl:value-of select="position()"/></td>
		                    <td><xsl:value-of select="violation/@source-url"/></td>
                            <td align='right'>
								<a>
									<xsl:attribute name="href">
										<xsl:choose>
											<xsl:when test="not(violation/@source-url)">summary.html</xsl:when>
											
											<xsl:when test="substring(violation/@source-url, string-length(violation/@source-url))='/'"><xsl:value-of select="$inspectorsPath"/><xsl:value-of select="violation/@source-url"/>.summary.html</xsl:when>
											
											<xsl:otherwise>source/<xsl:value-of select="violation/@source-url"/>.html#line_<xsl:value-of select="violation/@line"/></xsl:otherwise>
										</xsl:choose>
									</xsl:attribute>
									<xsl:value-of select="violation/@line"/>
								</a>
                            </td>
                            <td align='right'>
                           		<xsl:if test="violation/@signature">
                           			<xsl:attribute name="title">
                           				<xsl:value-of select="violation/@signature"/>
                           			</xsl:attribute>
                           		</xsl:if>                           			
                            			
                            	<xsl:value-of select="violation/@col"/>
                            </td>
                            <td NOWRAP="yes">
                                <a>
                                    <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>inspectors/inspector_<xsl:value-of select="violation/inspector-descriptor/name"/>.html</xsl:attribute>
                                    <xsl:value-of select="violation/inspector-descriptor/name"/>
                                </a>
                             </td>
                            <td align='right'><xsl:value-of select="violation/inspector-descriptor/severity"/></td>
                            <td><xsl:value-of disable-output-escaping="yes" select="violation/message"/></td>

                            <td><xsl:value-of select="waiver/reason"/></td>
                            <td><xsl:value-of select="waiver/expiration-date"/></td>
                        </tr>                        
                    </xsl:for-each>
                </table>
            </xsl:if>		
	    <HR/>
	    <i>Hammurapi 3.18.4 Copyright <xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> 2004 Hammurapi Group. All Rights Reserved.</i>
        </BODY>    
        </HTML>
    </xsl:template>        
</xsl:stylesheet>
