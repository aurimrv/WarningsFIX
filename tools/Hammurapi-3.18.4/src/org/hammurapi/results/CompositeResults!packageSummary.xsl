<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="inspectorsPath"/>
<xsl:param name="javaDocPath"/>
<xsl:param name="newMarker"/>

    <xsl:template match="/">       
        <HTML>
            <xsl:apply-templates select="/results"/>
        </HTML>
    </xsl:template>
    
    <xsl:template match="results">
        <HEAD>
            <TITLE><xsl:value-of select="@name"/> (Hammurapi 3.18.4)</TITLE>
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
        <H1><xsl:value-of select="@name"/></H1>
            <B style="color:blue">Results</B>
            <table cellspacing="0" cellpadding="5">
                <tr>
                    <td align="left">Date</td>
                    <td align='right'><xsl:value-of select="@date"/></td>
                </tr>
                                
                <tr>
                    <td align="left"><B>Files</B></td>
                    <td align='right'><xsl:value-of select="count(results)"/></td>
                </tr>
                    
                <tr>
                    <td align="left"><B>Codebase</B></td>
                    <td align='right'><xsl:value-of select="@code-base"/></td>
                </tr>
                                
                <tr>
                    <td align="left"><B>Reviews</B></td>
                    <td align='right'><xsl:value-of select="@reviews"/></td>
                </tr>
                                
                <tr>
                    <td align="left"><B>Violations</B></td>
                    <td align='right'><xsl:value-of select="@violations"/></td>
                </tr>

                <tr>
                    <td align="left"><B>Waived violations</B></td>
                    <td align='right'><xsl:value-of select="@waived-violations"/></td>
                </tr>

				<!--
                <tr title="Perfection Affinity Index">
                    <td align="left"><B>PAI</B></td>
                    <td align='right'><xsl:value-of select="substring(100*number(@pai), 0, 6)"/>%</td>
                </tr>
                -->
                
                <tr title="Defects per million opportunities">
                    <td align="left"><B>DPMO</B></td>
                    <td align='right'><xsl:value-of select="@dpmo"/></td>
                </tr>

                <tr title="Sigma">
                    <td align="left"><B>Sigma</B></td>
                    <td align='right'><xsl:value-of select="@sigma"/></td>
                </tr>
                
            </table>                                           
            
            <P/>
            <B style="color:blue">Severity summary</B>
            <xsl:for-each select="severity-summary">
	           <P/>
                <B> Severity <xsl:value-of select="@severity"/>, total <xsl:value-of select="sum(inspector-summary/@count)"/></B><BR/>
	            <table cellspacing="1" cellpadding="3">
	                <tr>
	                    <th>Inspector</th>
    	                <th>Description</th>
	                    <th>Number</th>
	                </tr>
	                <xsl:for-each select="inspector-summary">
	                    <tr>
	                        <td NOWRAP="yes">
	                            <a>
	                                <!-- <xsl:attribute name="href">rules/inspector_<xsl:value-of select="@inspector"/>.html</xsl:attribute> -->
	                                <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>inspectors/inspector_<xsl:value-of select="@inspector"/>.html</xsl:attribute>
	                                <xsl:if test="@version">
	                                	<xsl:attribute name="title">Version: <xsl:value-of select="@version"/></xsl:attribute>
	                                </xsl:if>
	                                <xsl:value-of select="@inspector"/>
	                            </a>
	                        </td>
	                        
	                        <td><xsl:value-of  disable-output-escaping="yes" select="@description"/></td>
	                        <td align="right">
	                        	<xsl:choose>
		                            <xsl:when test="@has-locations='yes'">
			                        	<a>
			                        		<xsl:attribute name="href">.summary_<xsl:value-of select="@inspector"/>.html</xsl:attribute>
			                        		<xsl:value-of select="@count"/>
			                        	</a>
			                        </xsl:when>
			                        
			                        <xsl:otherwise>
			                        	<xsl:value-of select="@count"/>			                        
			                        </xsl:otherwise>
			                    </xsl:choose>
	                        </td>
	                    </tr>                        
	                </xsl:for-each>
    	        </table>
            </xsl:for-each>
            
            <xsl:if test="metric">
                <P/>
                <B style="color:blue">Metrics</B>
                <table cellspacing="1" cellpadding="3">
                    <tr>
                        <th>Name</th>
                        <th>Number</th>
                        <th>Min</th>
                        <th>Avg</th>
                        <th>Max</th>
                        <th>Total</th>
                    </tr>
                    <xsl:for-each select="metric">
                        <xsl:sort select="@name"/>
                        <tr>
                            <td>
	                        	<xsl:choose>
		                            <xsl:when test="@has-measurements='yes'">
										<a>
											<xsl:attribute name="href">.summary_metric_details_<xsl:value-of select="@name"/>.html</xsl:attribute>
											<xsl:value-of select="@name"/>
										</a>								
			                        </xsl:when>
			                        
			                        <xsl:otherwise>
										<xsl:value-of select="@name"/>
			                        </xsl:otherwise>
			                    </xsl:choose>
							</td>
                            <td align="right"><xsl:value-of select="@number"/></td>
                            <td align="right">
								<xsl:value-of select="@min"/>
							</td>
                            <td align="right"><xsl:value-of select="@avg"/></td>
                            <td align="right">
								<xsl:value-of select="@max"/>
							</td>
                            <td align="right"><xsl:value-of select="@total"/></td>
                        </tr>                        
                    </xsl:for-each>
                </table>
            </xsl:if>
            
            <xsl:if test="violation">
                <P/>
                <B style="color:blue">Violations</B>
                <table border="1" cellspacing="0" cellpadding="3">
                    <tr>
                        <th>#</th>
                        <th>Name</th>
                        <th>Severity</th>
                        <th>Description</th>
                    </tr>
                    <xsl:for-each select="violation">
                        <tr>
                            <td align='right'><xsl:value-of select="position()"/></td>
                            <td NOWRAP="yes">
                                <a>
                                    <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>inspector/inspector_<xsl:value-of select="inspector-descriptor/name"/>.html</xsl:attribute>
                                    <xsl:value-of select="inspector-descriptor/name"/>
                                </a>
                             </td>
                            <td align='right'><xsl:value-of select="inspector-descriptor/severity"/></td>
                            <td><xsl:value-of disable-output-escaping="yes" select="message"/></td>
                        </tr>                        
                    </xsl:for-each>
                </table>
            </xsl:if>
            
            <xsl:if test="waived-violation">
                <P/>
                <B style="color:blue">Waived violations</B>
                <table border="0" cellspacing="1" cellpadding="3" class="standard">
                    <tr class="standard">
                        <th class="standard">#</th>
                        <th class="standard">Name</th>
                        <th class="standard">Severity</th>
                        <th class="standard">Description</th>
                        <th class="standard">Waiver reason</th>
                        <th class="standard">Waiver expires</th>
                    </tr>
                    <xsl:for-each select="waived-violation">
                        <tr class="standard">
                            <td align='right'><xsl:value-of select="position()"/></td>
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
                                    
            <xsl:if test="warnings">
                <P/>
                <B style="color:blue">Warnings</B>
                <table border="0" cellspacing="1" cellpadding="3" class="standard">
                    <tr class="standard">
                        <th class="standard">#</th>
                        <th class="standard">Line</th>
                        <th class="standard">Column</th>
                        <th class="standard">Message</th>
                    </tr>
                    <xsl:for-each select="warnings/violation">
                        <tr class="standard">
                            <td align='right'><xsl:value-of select="position()"/></td>
                            <td align='right'>
                                <a>
                                    <xsl:attribute name="href">#line_<xsl:value-of select="@line"/></xsl:attribute>
                                    <xsl:value-of select="@line"/>
                                </a>
                            </td>
                            <td align='right'><xsl:value-of select="@col"/></td>
                            <!--
                            <td NOWRAP="yes">
                                <a>
                                    <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>inspectors/inspector_<xsl:value-of select="inspector-descriptor/@name"/>.html</xsl:attribute>
                                    <xsl:value-of select="inspector-descriptor/@name"/>
                                </a>
                             </td>
                             -->
                            <td><xsl:value-of disable-output-escaping="yes" select="message"/></td>
                        </tr>                        
                    </xsl:for-each>
                </table>
            </xsl:if>
            
            <xsl:if test="annotations">
                <P/>
                <B style="color:blue">Annotations</B>
                <UL>
	                <xsl:for-each select="annotations/annotation">
						<LI>
							<xsl:if test="@path">
								<a>
									<xsl:attribute name="href"><xsl:value-of select="@path"/></xsl:attribute>
									
									<xsl:if test="property[@name='target']">
										<xsl:attribute name="target"><xsl:value-of select="property[@name='target']"/></xsl:attribute>
									</xsl:if>
									
									<xsl:value-of select="@name"/>
								</a>
							</xsl:if>
						
							<xsl:if test="content">								
								<xsl:value-of select="content"/>
							</xsl:if>
						</LI>
                    </xsl:for-each>
                </UL>
            </xsl:if>
            
            <xsl:if test="$javaDocPath">
                <P/>
                <a>
                    <xsl:attribute name="href"><xsl:value-of select="$javaDocPath"/></xsl:attribute>
                    JavaDoc
                </a>
            </xsl:if>
            
            <P/>
            <B>Files</B>
            <table cellspacing="1" cellpadding="3">
                <tr>
                    <th>Name</th>
                    <th>Reviews</th>
                    <th>Violations</th>
                    <th title="Defects per million opportunities">DPMO</th>
                    <th>Sigma</th>
                </tr>
                
                <xsl:for-each select="results">
                	<xsl:sort select="@name"/>
                    <tr>
                        <xsl:if test="@max-severity">
                            <xsl:attribute name="class">severity<xsl:value-of select="@max-severity"/></xsl:attribute>
                        </xsl:if>    
                        <td width='300'>

                            <a>
                                <xsl:attribute name="href"><xsl:value-of select="@name"/>.html</xsl:attribute>
                                <xsl:if test="@violations>0">                                                                
                                    <B><xsl:value-of select="@name"/></B>
                                </xsl:if>
                                <xsl:if test="@violations=0">
                                    <xsl:value-of select="@name"/>
                                </xsl:if>
                            </a>
							<xsl:if test="$newMarker and @new='yes'">
								<xsl:value-of disable-output-escaping="yes" select="$newMarker"/>
							</xsl:if>							
                        </td>
                        <td align='right'><xsl:value-of select="@reviews"/></td>
                        <td align='right'><xsl:value-of select="@violations"/></td>
                        <td align='right'><xsl:value-of select="@dpmo"/></td>
                        <td align='right'><xsl:value-of select="@sigma"/></td>
                    </tr>                        
                </xsl:for-each>
            </table>
            <HR/>
            <i>Hammurapi 3.18.4 Copyright <xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> 2004 Hammurapi Group. All Rights Reserved.</i>
            </BODY>    
    </xsl:template>
    
    
</xsl:stylesheet>
