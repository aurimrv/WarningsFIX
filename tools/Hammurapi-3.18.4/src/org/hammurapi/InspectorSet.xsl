<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="inspector-details">no</xsl:param>
<xsl:param name="embedded"/>

    <xsl:template match="/">       
        <xsl:choose>
            <xsl:when test="$embedded='yes'">
                <xsl:apply-templates select="/inspector-set" mode="embedded"/>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:apply-templates select="/inspector-set"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="inspector-set">
        <HTML>
        <HEAD>
            <TITLE>Hammurapi 3.18.4 [Inspector set]</TITLE>
            <STYLE> 
                <xsl:text disable-output-escaping="yes">
                <![CDATA[<!-- ]]>
                    TABLE.standard { background:silver }
                    TH.standard { font:bold; background:white }
                    TR.standard { background:white }
                    .code-snippet { background-color:EEEEEE; color:blue }
                    .hidden { color:EEEEEE; } 
                <![CDATA[ --> ]]>
                </xsl:text>
            </STYLE>
        </HEAD>                    
        <BODY>
            <DL>
	            <xsl:for-each select="inspector-descriptor">
	                <xsl:sort select="concat('.', category/text())"/>
	                <xsl:sort select="number(severity)"/>
	                <xsl:sort select="name"/>
	                
	                <xsl:variable name="c1" select="category/text()"/>
	                <xsl:variable name="p1" select="position()"/>	 
	                               
	                
	                <xsl:if test="$p1=1">	                
			    		<DT><B><xsl:value-of select="category"/></B></DT>
				    		
				        <DD>
				            <xsl:value-of select="position()"/>. <a href="inspectors/inspector_{name}.html"><B style="color:blue"><xsl:value-of select="name"/></B></a>
				            (<xsl:value-of select="severity"/>)
				            <xsl:value-of disable-output-escaping="yes" select="description"/>
				        </DD>	                
			        </xsl:if>
			        
		            <xsl:for-each select="../inspector-descriptor">
		                <xsl:sort select="concat('.', category/text())"/>
		                <xsl:sort select="number(severity)"/>
		                <xsl:sort select="name"/>
		                
		                <xsl:variable name="p2" select="position()"/>
		                
		                <xsl:if test="$p2=$p1+1">	    		                            
					    	<xsl:if test="category and $c1 and not(category=$c1)">
					    		<P/>
					    		<DT><B><xsl:value-of select="category"/></B></DT>
					    	</xsl:if>
					    		
					        <DD>
					            <xsl:value-of select="position()"/>. <a href="inspectors/inspector_{name}.html"><B style="color:blue"><xsl:value-of select="name"/></B></a>
					            (<xsl:value-of select="severity"/>)
					            <xsl:value-of disable-output-escaping="yes" select="description"/>
					        </DD>	                
				        </xsl:if>
		            </xsl:for-each>
	            </xsl:for-each>
            </DL>
			
			<xsl:if test="source-info">
				<H2>Sources</H2>
				<table class="standard">
					<tr class="standard">
						<th class="standard">Name</th>
						<th class="standard">Location</th>
						<th class="standard">Revision</th>
					</tr>
					
					<xsl:for-each select="source-info">
						<tr class="standard">
							<td class="standard"><xsl:value-of select="@name"/></td>
							<td class="standard"><xsl:value-of select="@location"/></td>
							<td class="standard"><xsl:value-of select="@revision"/></td>
						</tr>						
					</xsl:for-each>
				</table>				
			</xsl:if>
	    <HR/>
	    <i>Hammurapi 3.18.4 Copyright <xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> 2004 Hammurapi Group. All Rights Reserved.</i>
        </BODY>    
        </HTML>
    </xsl:template>    
    
    <xsl:template match="inspector-set" mode="embedded">
        <HTML>
            <HEAD>
                <TITLE>Embedded inspectors</TITLE>
            </HEAD>
        <BODY>
            <DL>
                <xsl:for-each select="inspector-descriptor">
                    <xsl:sort select="concat('.', category/text())"/>
                    <xsl:sort select="number(severity)"/>
                    <xsl:sort select="name"/>
                    
                    <xsl:variable name="c1" select="category/text()"/>
                    <xsl:variable name="p1" select="position()"/>
                    
                    <xsl:if test="$p1=1">
                        <DT>
                            <B>
                                <xsl:value-of select="category"/>
                            </B>
                        </DT>
                        
                        <DD>
                            <xsl:value-of select="position()"/>.
                            <a href="inspectors/inspector_{name}.html">
                                <B style="color:blue">
                                    <xsl:value-of select="name"/>
                                </B>
                            </a> (
                            <xsl:value-of select="severity"/>)
                            <xsl:value-of disable-output-escaping="yes" select="description"/> </DD>
                    </xsl:if>
                    
                    <xsl:for-each select="../inspector-descriptor">
                        <xsl:sort select="concat('.', category/text())"/>
                        <xsl:sort select="number(severity)"/>
                        <xsl:sort select="name"/>
                        
                        <xsl:variable name="p2" select="position()"/>
                        
                        <xsl:if test="$p2=$p1+1">
                            <xsl:if test="category and $c1 and not(category=$c1)">
                                <P/>
                                <DT>
                                    <B>
                                        <xsl:value-of select="category"/>
                                    </B>
                                </DT>
                            </xsl:if>
                            
                            <DD>
                                <xsl:value-of select="position()"/>.
                                <a href="inspectors/inspector_{name}.html">
                                    <B style="color:blue">
                                        <xsl:value-of select="name"/>
                                    </B>
                                </a> (
                                <xsl:value-of select="severity"/>)
                                <xsl:value-of disable-output-escaping="yes" select="description"/> </DD>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:for-each>
            </DL>
            
            <xsl:if test="source-info">
                <H2>Sources</H2>
                <table class="standard" cellpadding="3" cellspacing="1" border="0">
                    <tr class="standard">
                        <th class="standard">Name</th>
                        <th class="standard">Location</th>
                        <th class="standard">Revision</th>
                    </tr>
                    
                    <xsl:for-each select="source-info">
                        <tr class="standard">
                            <td class="standard">
                                <xsl:value-of select="@name"/>
                            </td>
                            <td class="standard">
                                <xsl:value-of select="@location"/>
                            </td>
                            <td class="standard">
                                <xsl:value-of select="@revision"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </xsl:if>
            
        </BODY>
        </HTML>
    </xsl:template>    
    
</xsl:stylesheet>
