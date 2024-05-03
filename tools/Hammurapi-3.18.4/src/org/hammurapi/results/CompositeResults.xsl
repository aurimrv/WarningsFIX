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
                <xsl:if test="@review-description">
                    <td align="left">Description</td>
                    <td align='right'><xsl:value-of select="@review-description"/></td>                
                </xsl:if>
                                
                <tr>
                    <td align="left">Date</td>
                    <td align='right'><xsl:value-of select="@date"/></td>
                </tr>
                
                <xsl:if test="baseline/results">
                    <td align="left">Baseline date</td>
                    <td align='right'><xsl:value-of select="baseline/results/@date"/></td>                
                </xsl:if>
                                
                <tr>
                    <td align="left"><B>Packages</B></td>
                    <td align='right'><xsl:value-of select="count(results)"/></td>
                </tr>
                                
                <tr>
                    <td align="left"><B>Files</B></td>
                    <td align='right'><xsl:value-of select="@size - count(results)"/></td>
                </tr>
                                
                <tr>
                    <td align="left"><B>Codebase</B></td>
                    <td align='right'><xsl:value-of select="@code-base"/>
		                <xsl:if test="baseline/results and not(@code-base=baseline/results/@code-base)">
		                    (<xsl:value-of select="format-number(@code-base - baseline/results/@code-base, '+0;-0')"/>)                
		                </xsl:if>                    
                    </td>
                </tr>
                            
                <tr>
                    <td align="left"><B>Reviews</B></td>
                    <td align='right'><xsl:value-of select="@reviews"/>                    
		                <xsl:if test="baseline/results and not(@reviews=baseline/results/@reviews)">		                
		                    (<xsl:value-of select="format-number(@reviews - baseline/results/@reviews, '+0;-0')"/>)                
		                </xsl:if>                    
                    </td>
                </tr>
                                
                <tr>
                    <td align="left"><B>Violations</B></td>
                    <td align='right'><xsl:value-of select="@violations"/>
		                <xsl:if test="baseline/results and not(@violations=baseline/results/@violations)">		                
		                    (<xsl:value-of select="format-number(@violations - baseline/results/@violations, '+0;-0')"/>)                
		                </xsl:if>                    
		               </td>
                </tr>
                                
                <tr>
                    <td align="left"><B>Waived violations</B></td>
                    <td align='right'>
						<xsl:choose>
							<xsl:when test="@waived-violations='0'">
								<xsl:value-of select="@waived-violations"/>	
							</xsl:when>
							<xsl:otherwise>
								<a href="waivedViolations.html"><xsl:value-of select="@waived-violations"/></a>
							</xsl:otherwise>
						</xsl:choose>
						
		                <xsl:if test="baseline/results and not(@waived-violations=baseline/results/@waived-violations)">		                
		                    (<xsl:value-of select="format-number(@waived-violations - baseline/results/@waived-violations, '+0;-0')"/>)                
		                </xsl:if>                    
		               </td>
                </tr>

				<!--
                <tr title="Perfection Affinity Index">
                    <td align="left"><B>PAI</B></td>
                    <td align='right'><xsl:value-of select="substring(100*number(@pai), 0, 6)"/>%</td>
                </tr>
                -->
                
                <tr title="Defects per million opportunities">
                    <td align="left"><B>DPMO</B></td>
                    <td align='right'><xsl:value-of select="@dpmo"/>
		                <xsl:if test="baseline/results and (@dpmo &lt; baseline/results/@dpmo or @dpmo &gt; baseline/results/@dpmo)">		                
		                    (<xsl:value-of select="format-number(@dpmo - baseline/results/@dpmo, '+0;-0')"/>)                
		                </xsl:if>                    
		               </td>
                </tr>

                <tr title="Sigma">
                    <td align="left"><B>Sigma</B></td>
                    <td align='right'><xsl:value-of select="@sigma"/>
		                <xsl:if test="baseline/results and (@sigma &lt; baseline/results/@sigma  or @sigma &gt; baseline/results/@sigma)">		                
		                    (<xsl:value-of select="format-number(@sigma - baseline/results/@sigma, '+0.###;-0.###')"/>)                
		                </xsl:if>                    
		               </td>
                </tr>
                
            </table>                                           
            
            <P/>
            <B style="color:blue"><a name="severity_summary">Severity summary</a></B>
            <xsl:for-each select="severity-summary">
	           <P/>
                <B>Severity <xsl:value-of select="@severity"/>, total <xsl:value-of select="sum(inspector-summary/@count)"/>
		                <xsl:if test="baseline/results and not(sum(inspector-summary/@count)=sum(inspector-summary/@baseline))">		                
		                    (<xsl:value-of select="format-number(sum(inspector-summary/@count)-sum(inspector-summary/@baseline), '+0;-0')"/>)                
		                </xsl:if>                                    
                </B><BR/>
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
	                                <xsl:attribute name="href">inspectors/inspector_<xsl:value-of select="@inspector"/>.html</xsl:attribute>
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
			                        		<xsl:attribute name="href">summary_<xsl:value-of select="@inspector"/>.html</xsl:attribute>
			                        		<xsl:value-of select="@count"/>
			                        	</a>
			                        </xsl:when>
			                        
			                        <xsl:otherwise>
			                        	<xsl:value-of select="@count"/>			                        
			                        </xsl:otherwise>
			                    </xsl:choose>
		                        
				                <xsl:if test="not(@baseline=-1 or @count=@baseline)">		                
				                    (<xsl:value-of select="format-number(@count - @baseline, '+0;-0')"/>)                
				                </xsl:if>                    	                        	
	                        </td>
	                    </tr>                        
	                </xsl:for-each>
    	        </table>
            </xsl:for-each>
                        
            <xsl:if test="metric">
                <P/>
                <B style="color:blue"><a name="metrics">Metrics</a></B>
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
											<xsl:attribute name="href">metric_details_<xsl:value-of select="@name"/>.html</xsl:attribute>
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
            
            <xsl:if test="warnings">
                <P/>
                <B style="color:blue"><a name="warnings">Warnings</a> (<xsl:value-of select="count(warnings/violation)"/>)</B>
                <table border="0" cellspacing="1" cellpadding="3" class="standard">
                    <tr class="standard">
                        <th class="standard">#</th>
		                <th class="standard">File</th>
                        <th class="standard">Line</th>
                        <th class="standard">Column</th>
                        <th class="standard">Message</th>
                    </tr>
                    <xsl:for-each select="warnings/violation">
                        <tr class="standard">
                            <td align='right'><xsl:value-of select="position()"/></td>
		                    <td><xsl:value-of select="@source-url"/></td>
		                    <td align='right'>
		                    	<xsl:if test="@line>0">
			                        <a>
			                            <xsl:attribute name="href">source/<xsl:value-of select="@source-url"/>.html#line_<xsl:value-of select="@line"/></xsl:attribute>
			                            <xsl:value-of select="@line"/>
			                        </a>
			                    </xsl:if>
		                    </td>
                            <td align='right'><xsl:if test="@col>0"><xsl:value-of select="@col"/></xsl:if></td>
                            <!--
                            <td NOWRAP="yes">
                                <a>
                                    <xsl:attribute name="href">inspector.html#inspector_<xsl:value-of select="inspector-descriptor/name"/>.html</xsl:attribute>
                                    <xsl:value-of select="inspector-descriptor/name"/>
                                </a>
                             </td>
                             -->
                            <td><xsl:value-of disable-output-escaping="yes" select="message"/></td>
                        </tr>                        
                    </xsl:for-each>
                </table>
            </xsl:if>
                        
            <xsl:if test="annotations/annotation[@path or content]">
                <P/>
                <B style="color:blue"><a name="annotations">Annotations</a></B>
                <UL>
	                <xsl:for-each select="annotations/annotation">
						<LI>
							<a>
								<xsl:attribute name="name">annotation_<xsl:value-of select="@name"/></xsl:attribute>
							</a> 
							
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
            
            <xsl:if test="violation">
                <P/>
                <B style="color:blue"><a name="violations">Violations</a></B>
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
                                    <xsl:attribute name="href">inspectors/inspector_<xsl:value-of select="inspector-descriptor/name"/>.html</xsl:attribute>
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
                <B style="color:blue"><a name="waived_violations">Waived violations</a></B>
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
                                    <xsl:attribute name="href">inspectors/inspector_<xsl:value-of select="violation/inspector-descriptor/name"/>.html</xsl:attribute>
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
                                                
            <xsl:if test="$javaDocPath">
                <P/>
                <a>
                    <xsl:attribute name="href"><xsl:value-of select="$javaDocPath"/></xsl:attribute>
                    JavaDoc
                </a>
            </xsl:if>
            
            <P/>
            <B><a name="files">Files</a></B>
            <xsl:for-each select="results">       
            	<xsl:sort select="@name"/>     
                <xsl:variable name="dirName"><xsl:value-of select="translate(@name,'.','/')"/></xsl:variable>
                <P/>
                <B><xsl:value-of select="@name"/></B>
				<xsl:if test="$newMarker and @new='yes'">
					<xsl:value-of disable-output-escaping="yes" select="$newMarker"/>
				</xsl:if>
				<BR/>
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
                                    <xsl:attribute name="href">source/<xsl:value-of select="$dirName"/>/<xsl:value-of select="@name"/>.html</xsl:attribute>
                                    <xsl:if test="@violations>0">                                                                
                                        <B><xsl:value-of select="@name"/></B>
                                    </xsl:if>
                                    <xsl:if test="@violations=0">
                                        <xsl:value-of select="@name"/>
                                    </xsl:if>
                                </a>
								<xsl:if test="$newMarker and @new='yes'"><xsl:value-of disable-output-escaping="yes" select="$newMarker"/></xsl:if>
                            </td>
                            <td align='right'><xsl:value-of select="@reviews"/></td>
                            <td align='right'><xsl:value-of select="@violations"/></td>
                            <td align='right'><xsl:value-of select="@dpmo"/></td>
                            <td align='right'><xsl:value-of select="@sigma"/></td>
                        </tr>                        
                    </xsl:for-each>
                </table>
            </xsl:for-each>
		    <HR/>
		    <i>Hammurapi 3.18.4 Copyright <xsl:text disable-output-escaping="yes">&amp;copy;</xsl:text> 2004 Hammurapi Group. All Rights Reserved.</i>
            </BODY>    
    </xsl:template>        
</xsl:stylesheet>
