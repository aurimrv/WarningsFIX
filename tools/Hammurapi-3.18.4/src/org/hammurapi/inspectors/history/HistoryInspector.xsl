<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

    <xsl:template match="/">
    	<HTML>
    	<BODY>
	    	<xsl:apply-templates select="history"/>
	    </BODY>
	    </HTML>
    </xsl:template>

    <xsl:template match="history">
		<H1><xsl:value-of select="@title"/></H1>
		<H2>history of reviews</H2>
	   	<TABLE border="0" cellspacing="1" cellpadding="2" bgcolor="gray">
    		<TR bgcolor="silver">
    			<TH rowspan="2">Date</TH>
    			<TH rowspan="2">Description</TH>
    			<TH rowspan="2">Reports</TH>
    			<TH colspan="2">Codebase</TH>
    			<TH rowspan="2">Reviews</TH>
    			<TH rowspan="2">Activity (%)</TH>
    			<TH rowspan="2">Violations</TH>
     			<TH rowspan="2">Max severity</TH>
     			<TH rowspan="2">Sigma</TH>
     			<TH rowspan="2">DPMO</TH>
    		</TR>
    		<TR bgcolor="silver">
    			<TH>Files</TH>
    			<TH>Nodes</TH>
    		</TR>
    		
	    	<xsl:apply-templates select="element"/>
	    </TABLE>
	    
		<xsl:if test="@nodes-chart">
			<p/>
			<img>
				<xsl:attribute name="src">
					<xsl:value-of  select="@nodes-chart"/>
				</xsl:attribute>
			</img>
		</xsl:if>			
		
		<xsl:if test="@files-chart">		
			<p/>
			<img>
				<xsl:attribute name="src">
					<xsl:value-of  select="@files-chart"/>
				</xsl:attribute>
			</img>
		</xsl:if>			
		
		<xsl:if test="@activity-chart">		
			<p/>
			<img>
				<xsl:attribute name="src">
					<xsl:value-of  select="@activity-chart"/>
				</xsl:attribute>
			</img>
		</xsl:if>			
		
		<xsl:if test="@sigma-chart">		
			<p/>
			<img>
				<xsl:attribute name="src">
					<xsl:value-of  select="@sigma-chart"/>
				</xsl:attribute>
			</img>
		</xsl:if>			
		
		<xsl:if test="@dpmo-chart">		
			<p/>
			<img>
				<xsl:attribute name="src">
					<xsl:value-of  select="@dpmo-chart"/>
				</xsl:attribute>
			</img>
		</xsl:if>			
		
		<xsl:if test="@max-severity-chart">		
			<p/>
			<img>
				<xsl:attribute name="src">
					<xsl:value-of  select="@max-severity-chart"/>
				</xsl:attribute>
			</img>
		</xsl:if>			
		
		<xsl:if test="@violations-chart">		
			<p/>
			<img>
				<xsl:attribute name="src">
					<xsl:value-of  select="@violations-chart"/>
				</xsl:attribute>
			</img>
		</xsl:if>			
		
		<xsl:if test="@reviews-chart">		
			<p/>
			<img>
				<xsl:attribute name="src">
					<xsl:value-of  select="@reviews-chart"/>
				</xsl:attribute>
			</img>
		</xsl:if>			
    </xsl:template>
    
    <xsl:template match="element">
   	<TR bgcolor="white">
			<TD><xsl:value-of select="substring(REPORT_DATE,1,10)"/></TD>
			<TD><xsl:value-of select="Description"/></TD>
			<TD align="right"><xsl:value-of select="REPORTS"/></TD>
			<TD align="right"><xsl:value-of select="COMPILATION_UNITS"/></TD>
			<TD align="right"><xsl:value-of select="CODEBASE"/></TD>
			<TD align="right"><xsl:value-of select="REVIEWS"/></TD>
			<TD align="right"><xsl:value-of select="format-number(CHANGE_RATIO*100, '0')"/></TD>
			<TD align="right"><xsl:value-of select="VIOLATIONS"/></TD>
			<TD align="right"><xsl:value-of select="MAX_SEVERITY"/></TD>
			<TD align="right"><xsl:value-of select="Sigma"/></TD>
			<TD align="right"><xsl:value-of select="DPMO"/></TD>
		</TR>
    </xsl:template>
</xsl:stylesheet>
