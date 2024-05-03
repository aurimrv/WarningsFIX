<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="inspectorsPath"/>
<xsl:param name="javaDocPath"/>
<xsl:param name="parseInfo">no</xsl:param>

    <xsl:template match="/">       
        <HTML>
            <xsl:apply-templates select="/review"/>
        </HTML>
    </xsl:template>
    
    <xsl:template match="review">
        <HEAD>
            <STYLE> 
            <xsl:text disable-output-escaping="yes">
                <![CDATA[<!-- ]]>
                    .lineNum { width:40px; background:silver; text-align:right; padding-right:3px; color:maroon }
                    .reserved_word { color:blue; font:bold }
                    .comment { color:gray }
                    .literal { color:maroon }
                    TH.standard { font:bold; background:gray }
                    TABLE.standard { background:silver }
                    TR.standard { background:white }
            <![CDATA[ --> ]]>
            </xsl:text>
            </STYLE>
            <TITLE>Hammurapi 3.18.4 [<xsl:value-of select="review-unit/@name"/>]</TITLE>
        </HEAD>                    
        <BODY>            
            <H1><xsl:value-of select="source/@name"/></H1>       
            <xsl:if test="source/@revision">Revision: <xsl:value-of select="source/@revision"/></xsl:if><BR/>     
            Package: <xsl:value-of select="source/@package"/><P/>
            <P/>
            <xsl:apply-templates select="results"/>
            
            
            <xsl:if test="$javaDocPath and source/types">
                <P/>
                    <DL>
                    	<DT><B style="color:blue">JavaDoc</B></DT>
	                    <xsl:for-each select="source/types/type">
	                    	<DD>
				                <a>
				                    <xsl:attribute name="href"><xsl:value-of select="$javaDocPath"/><xsl:value-of select="text()"/>.html</xsl:attribute>
				                    <xsl:value-of select="text()"/>
				                </a>
			                </DD>
	                    </xsl:for-each>
                    </DL>
            </xsl:if>
            
            
            <xsl:if test="source">
                <HR/>
                <PRE>
                    <a name="line_1" class="lineNum">1</a>
                    <xsl:apply-templates select="source/tokens/token"/>
                </PRE>
                <HR/>
            </xsl:if>
	    <i>Hammurapi 3.18.4 Copyright <xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> 2004 Hammurapi Group. All Rights Reserved.</i>
        </BODY>    
    </xsl:template>
    
    <xsl:template match="results">
            <B style="color:blue">Results</B>
            <table cellspacing="0" cellpadding="5">
                <tr>
                    <th align="left">Date</th>
                    <td align='right'><xsl:value-of select="@date"/></td>
                </tr>
                                
                <tr>
                    <th align="left">Codebase</th>
                    <td align='right'><xsl:value-of select="@code-base"/></td>
                </tr>
                                
                <tr>
                    <th align="left">Reviews</th>
                    <td align='right'><xsl:value-of select="@reviews"/></td>
                </tr>
                
                <!--                
                <tr title="Perfection Affinity Index">
                    <th align="left">PAI</th>
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
                
            <xsl:if test="metric">
                <P/>
                <B style="color:blue">Metrics</B>
                <table border="0" cellspacing="1" cellpadding="3" class="standard">
                    <tr class="standard">
                        <th class="standard">Name</th>
                        <th class="standard">Number</th>
                        <th class="standard">Min</th>
                        <th class="standard">Avg</th>
                        <th class="standard">Max</th>
                        <th class="standard">Total</th>
                    </tr>
                    <xsl:for-each select="metric">
                        <xsl:sort select="@name"/>
                        <tr class="standard">
                            <td><xsl:value-of select="@name"/></td>
                            <td align="right"><xsl:value-of select="@number"/></td>
                            <td align="right"><xsl:value-of select="@min"/></td>
                            <td align="right"><xsl:value-of select="@avg"/></td>
                            <td align="right"><xsl:value-of select="@max"/></td>
                            <td align="right"><xsl:value-of select="@total"/></td>
                        </tr>                        
                    </xsl:for-each>
                </table>
            </xsl:if>
            
            <xsl:if test="violation">
                <P/>
                <B style="color:blue">Violations</B>
                <table border="0" cellspacing="1" cellpadding="3" class="standard">
                    <tr class="standard">
                        <th class="standard">#</th>
                        <th class="standard">Line</th>
                        <th class="standard">Column</th>
                        <th class="standard">Name</th>
                        <th class="standard">Severity</th>
                        <th class="standard">Description</th>
                    </tr>
                    <xsl:for-each select="violation">
                        <tr class="standard">
                            <td align='right'><xsl:value-of select="position()"/></td>
                            <td align='right'>
                                <a>
                                    <xsl:attribute name="href">#line_<xsl:value-of select="@line"/></xsl:attribute>
                                    <xsl:value-of select="@line"/>
                                </a>
                            </td>
                            <td align='right'>
                           		<xsl:if test="@signature">
                           			<xsl:attribute name="title">
                           				<xsl:value-of select="@signature"/>
                           			</xsl:attribute>
                           		</xsl:if>                           			
                            			
                            	<xsl:value-of select="@col"/>
                            </td>
                            <td NOWRAP="yes">
                                <a>
                                    <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>inspectors/inspector_<xsl:value-of select="inspector-descriptor/name"/>.html</xsl:attribute>
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
                        <th class="standard">Line</th>
                        <th class="standard">Column</th>
                        <th class="standard">Name</th>
                        <th class="standard">Severity</th>
                        <th class="standard">Description</th>
                        <th class="standard">Waiver reason</th>
                        <th class="standard">Waiver expires</th>
                    </tr>
                    <xsl:for-each select="waived-violation">
                        <tr class="standard">
                            <td align='right'><xsl:value-of select="position()"/></td>
                            <td align='right'>
                                <a>
                                    <xsl:attribute name="href">#line_<xsl:value-of select="violation/@line"/></xsl:attribute>
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
            
    </xsl:template>

	<!--
    <xsl:template match="token" priority="1">
    	<xsl:value-of select="text()"/>    
    </xsl:template>
	-->
    
    <xsl:template match="token[@type='NEW_LINE']">
    	<xsl:text disable-output-escaping="yes">&lt;BR/&gt;</xsl:text><A class="lineNum"><xsl:attribute name="name">line_<xsl:value-of select="@line+1"/></xsl:attribute><xsl:value-of select="@line+1"/></A>
    </xsl:template>
    
    <xsl:template match="token[starts-with(@type,'LITERAL_') or @type='ABSTRACT' or @type='FINAL']">
    	<span class="reserved_word"><xsl:value-of select="text()"/></span>
    </xsl:template>
        
    <xsl:template match="token[starts-with(@type,'NUM_') or @type='STRING_LITERAL' or @type='CHAR_LITERAL']">
    	<span class="literal"><xsl:value-of select="text()"/></span>
    </xsl:template>
        
    <xsl:template match="token[@type='ML_COMMENT' or @type='SL_COMMENT']"><!--
    	--><span class="comment"><xsl:apply-templates/></span><!--
    --></xsl:template>
    
    <xsl:template match="line">
    	<xsl:value-of select="text()"/>    
    </xsl:template>        
</xsl:stylesheet>
