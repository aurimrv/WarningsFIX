<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="inspector-details">no</xsl:param>
<xsl:param name="embedded"/>

    <xsl:template match="/">       
        <xsl:choose>
            <xsl:when test="$embedded='yes'">
                <xsl:apply-templates select="/inspector-descriptor"/>
            </xsl:when>
            
            <xsl:otherwise>
		        <HTML>
		        <HEAD>
		            <TITLE>Hammurapi 3.18.4 [Inspector]</TITLE>
		            <STYLE> 
		                <xsl:text disable-output-escaping="yes">
		                <![CDATA[<!-- ]]>
		                    TABLE.standard { background:silver }
		                    TH.standard { font:bold; background:white }
		                    TR.standard { background:white }
		                    .code-snippet { background-color:EEEEEE; color:blue }
		                    .hidden { color:EEEEEE; } 
				    span.problem { font:bold; color:red }
				    span.fix { font:bold; color:green }
		                <![CDATA[ --> ]]>
		                </xsl:text>
		            </STYLE>
		        </HEAD>                    
		        <BODY>
	                <xsl:apply-templates select="inspector-descriptor"/>
			    <HR/>
			    <i>Hammurapi 3.18.4 Copyright <xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> 2004 Hammurapi Group. All Rights Reserved.</i>
		        </BODY>    
		        </HTML>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="inspector-descriptor">
    	<DL>
            <DT>
                <a>
                    <xsl:attribute name="name">inspector_<xsl:value-of select="name"/></xsl:attribute>
                    <B style="color:blue"><xsl:value-of select="name"/></B><xsl:text> </xsl:text>
                </a>
                <xsl:value-of disable-output-escaping="yes" select="description"/>
            </DT>
            
            <P/>
            <DD>
                <TABLE cellspacing='1' cellpadding='3' class="standard">
                    <TR class="standard">
                        <TH align="left" valign="top">Severity</TH>
                        <TD><xsl:value-of select="severity"/></TD>
                    </TR>
                    
                    <TR class="standard">
                        <TH align="left" valign="top">Enabled</TH>
                        <TD><xsl:value-of select="enabled"/></TD>
                    </TR>

                    <TR class="standard">
                        <TH align="left" valign="top">Waivable</TH>
                        <TD><xsl:value-of select="waivable"/></TD>
                    </TR>

			        <xsl:if test="config-info">
	                    <TR class="standard">
	                        <TH align="left" valign="top">Configuration</TH>
	                        <TD><PRE><xsl:value-of select="config-info"/></PRE></TD>
	                    </TR>
			        </xsl:if>        
        
					<!--                    
                    <xsl:if test="@action">
                        <TR class="standard">
                            <TH align="left" valign="top">Action</TH>
                            <TD><xsl:value-of select="@action"/></TD>
                        </TR>
                    </xsl:if>
                    -->
                                        
                    <xsl:if test="$inspector-details='yes'">
                        <TR class="standard">
                            <TH align="left" valign="top">Inspector class</TH>
                            <TD><xsl:value-of select="inspector/@type"/></TD>
                        </TR>
                    </xsl:if>
                    
                    <xsl:if test="rationale">
                        <TR class="standard">
                            <TH align="left" valign="top">Rationale</TH>
                            <TD><xsl:value-of disable-output-escaping="yes" select="rationale"/></TD>
                        </TR>
                    </xsl:if>
                    
                    <xsl:if test="violation-sample and fix-sample">
                        <TR class="standard">
                            <TH align="left" valign="top">Violation</TH>
                            <TD><PRE class="code-snippet"><xsl:value-of disable-output-escaping="yes" select="violation-sample"/></PRE></TD>
                        </TR>
                        <TR class="standard">
                            <TH align="left" valign="top">Fix</TH>
                            <TD><PRE class="code-snippet"><xsl:value-of disable-output-escaping="yes" select="fix-sample"/></PRE></TD>
                        </TR>
                    </xsl:if>
                    
                    <xsl:if test="resources">
                        <TR class="standard">
                            <TH align="left" valign="top">Resources</TH>
                            <TD><xsl:value-of disable-output-escaping="yes" select="resources"/></TD>
                        </TR>
                    </xsl:if>                    
                </TABLE>
            </DD>
            
			<!--            
            <xsl:if test="parameter and $inspector-details='yes'">
                <P/>
                <DD>
                    <B>Parameters</B>
                    <TABLE cellspacing='1' cellpadding='3' class="standard">
                        <TR class="standard"><TH class="standard">Name</TH><TH class="standard">Value</TH></TR>
                        <xsl:for-each select="parameter">
                            <xsl:sort select="@name"/>
                            <TR class="standard">
                                <TD><xsl:value-of select="@name"/></TD>
                                <TD><xsl:value-of select="."/></TD>
                            </TR>
                        </xsl:for-each>
                    </TABLE>
                </DD>
            </xsl:if>
            -->
            <P/>
        </DL>
    </xsl:template>    
</xsl:stylesheet>
