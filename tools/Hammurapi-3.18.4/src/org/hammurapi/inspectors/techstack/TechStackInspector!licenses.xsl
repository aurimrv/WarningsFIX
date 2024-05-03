<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
    <xsl:key name = "license" match = "//license"  use = "@key" /> 

    <xsl:template match="/">
	    	<xsl:apply-templates select="tech-stack"/>		    
    </xsl:template>
    <xsl:template match="tech-stack">
    	<HTML>
    	<BODY>
    	<h1>Technology stack</h1>
    	<hr/>
    	<a href="summary.html">Summary</a><xsl:text> </xsl:text> <a><xsl:attribute name="href"><xsl:value-of select="@root-path"/></xsl:attribute>Products</a> <xsl:text> </xsl:text>Licenses
    	<hr/>
    	<h2>Licenses</h2>
    	<table bgcolor="gray" cellspacing="1" cellpadding="3" >
    	<TR bgcolor="silver">
			<TH>Name</TH>
			<TH>Description</TH>
			<TH>Category</TH>
			<TH>Products</TH>
			<TH>Clients</TH>
		</TR>
	    	<xsl:apply-templates select="//license[client]"/>		
		</table>
	    </BODY>
	    </HTML>
    </xsl:template>

    <xsl:template match="license">
    	<TR bgcolor="white">
			<TD>
	    		<a>
    				<xsl:attribute name="name">license:<xsl:value-of select="@key"/></xsl:attribute>
    			</a>
		    	<xsl:choose>
		    		<xsl:when test="url">
		    			<a target="_blank">
		    				<xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute>
		    				<xsl:value-of select="name"/>
		    			</a>
		    		</xsl:when>
		    		<xsl:otherwise>
		    			<xsl:value-of select="name"/>
		    		</xsl:otherwise>
		    	</xsl:choose>
			</TD>
			<TD><xsl:value-of select="description"/></TD>
			<TD><xsl:value-of select="category"/></TD>
			<TD NOWRAP="yes">
					<xsl:variable name="licenseKey" select="@key"/>
					<xsl:for-each select="/tech-stack/publisher/product[../license/@key=$licenseKey or license/@key=$licenseKey or license-ref=$licenseKey or ../license-ref=$licenseKey]">
						-<xsl:text disable-output-escaping="yes"><![CDATA[&nbsp;]]></xsl:text> <a>
							<xsl:attribute name="href">
								<xsl:value-of select="/tech-stack/@root-path"/>#product:<xsl:value-of select="@key"/>
							</xsl:attribute>
							<xsl:value-of select="name"/>
						</a>
				<BR/>
					</xsl:for-each>
			</TD>
			<TD>
				<a>
					<xsl:attribute name="href"><xsl:value-of select="@path"/></xsl:attribute>
					<xsl:value-of select="count(client)"/>
				</a>
				</TD>
		</TR>
    </xsl:template>
    
</xsl:stylesheet>
