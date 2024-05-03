<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="javaDocPath"/>
<xsl:param name="newMarker"/>

    <xsl:template match="/">       
        <HTML>
            <xsl:apply-templates select="/results"/>
        </HTML>
    </xsl:template>
    
    <xsl:template match="results">
        <HEAD>
            <TITLE>Hammurapi 3.18.4 [Summary]</TITLE>
        </HEAD>                    
        <BODY>
            <B style="color:navy">Hammurapi 3.18.4</B><P/>
            
            <a href="inspectors.html" target="mainFrame">Inspectors</a><P/>            
			
            <xsl:if test="$javaDocPath">
                <a target="mainFrame">
                    <xsl:attribute name="href"><xsl:value-of select="$javaDocPath"/></xsl:attribute>
                    JavaDoc
                </a>
                <P/>
            </xsl:if>
			
            <DL>
				<DT><a href="summary.html" target="mainFrame">Summary</a></DT>
				
				<DD><a href="summary.html#severity_summary" target="mainFrame">Severity summary</a></DD>
                        
				<xsl:if test="metric">
					<DD><a href="summary.html#metrics" target="mainFrame">Metrics</a></DD>
				</xsl:if>
            
	            <xsl:if test="warnings">
		            <DD><a href="summary.html#warnings" target="mainFrame">Warnings</a> (<xsl:value-of select="count(warnings/violation)"/>)</DD>
	            </xsl:if>
                        
		        <xsl:if test="annotations/annotation[@path or content]">
	                <DD><a href="summary.html#annotations" target="mainFrame">Annotations</a></DD>
					<UL>
						<xsl:for-each select="annotations/annotation">
							<LI>
								<a>
									<xsl:attribute name="name">annotation_<xsl:value-of select="@name"/></xsl:attribute>
								</a> 
				
								<xsl:choose>
									<xsl:when test="@path">
										<a>
											<xsl:attribute name="href"><xsl:value-of select="@path"/></xsl:attribute>
											
											<xsl:if test="property[@name='target']">
												<xsl:attribute name="target"><xsl:value-of select="property[@name='target']"/></xsl:attribute>
											</xsl:if>
											
											<xsl:if test="not(property[@name='target'])">
												<xsl:attribute name="target">mainFrame</xsl:attribute>
											</xsl:if>
											
											<xsl:value-of select="@name"/>
										</a>
									</xsl:when>
						
									<xsl:otherwise>								
											<xsl:attribute name="href">summary.html#annotation_<xsl:value-of select="@name"/></xsl:attribute>
											<xsl:attribute name="target">mainFrame</xsl:attribute>
											<xsl:value-of select="@name"/>
									</xsl:otherwise>
								</xsl:choose>			
							</LI>
		                </xsl:for-each>
			        </UL>
				</xsl:if>
            
	            <xsl:if test="violation">
		           <DD><a href="summary.html#violations" target="mainFrame">Violations</a></DD>
	            </xsl:if>
            
		        <xsl:if test="waived-violation">
	                <DD><a href="summary.html#waived_violations" target="mainFrame">Waived violations</a></DD>>
	            </xsl:if>                                                
			</DL>
			
            <B>Files</B>
            <table cellspacing="0" cellpadding="3">            
	            <xsl:for-each select="results">
                        <xsl:sort select="@name"/>
						<xsl:variable name="dirName"><xsl:value-of select="translate(@name, '.', '/')"/></xsl:variable>
	                
                        <TR><TD colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></TD></TR>
	            	<TR style="background:silver">
                            <TD>
                            <B>
                                <a target="mainFrame">
                                    <xsl:attribute name="href">source/<xsl:value-of select="$dirName"/>/.summary.html</xsl:attribute>
                                    <xsl:value-of select="@name"/>
                                </a>
							<xsl:if test="$newMarker and @new='yes'">
								<xsl:value-of disable-output-escaping="yes" select="$newMarker"/>
							</xsl:if>								
                            </B>
                            </TD>
                            
                            <td align='right'>
                                <xsl:if test="@violations>0">
                                    <xsl:value-of select="@violations"/>
                                </xsl:if>
                            </td>
                        </TR>
	                <xsl:for-each select="results">
	                	<xsl:sort select="@name"/>
	                    <tr>
	                    	<xsl:if test="@violations>0">
	                            <td>
	                                <a target="mainFrame">
	                                    <xsl:attribute name="href">source/<xsl:value-of select="$dirName"/>/<xsl:value-of select="@name"/>.html</xsl:attribute>
	                                    <B><xsl:value-of select="@name"/></B>
	                                </a>
							
									<xsl:if test="$newMarker and @new='yes'">
										<xsl:value-of disable-output-escaping="yes" select="$newMarker"/>
									</xsl:if>															
	                            </td>
	                            <td align='right'><xsl:value-of select="@violations"/></td>
	                        </xsl:if>
		                        
	                    	<xsl:if test="@violations=0">
	                            <td>
	                                <a target="mainFrame">
	                                    <xsl:attribute name="href">source/<xsl:value-of select="$dirName"/>/<xsl:value-of select="@name"/>.html</xsl:attribute>
	                                    <xsl:value-of select="@name"/>
	                                </a>
							
									<xsl:if test="$newMarker and @new='yes'">
										<xsl:value-of disable-output-escaping="yes" select="$newMarker"/>
									</xsl:if>															
	                            </td>
	                            <td/>
	                        </xsl:if>
	                    </tr>                        
	                </xsl:for-each>
	            </xsl:for-each>
            </table>
            </BODY>    
    </xsl:template>        
</xsl:stylesheet>
