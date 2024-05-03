<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:param name="publisher"/>

    <xsl:template match="/tech-stack">
    	<HTML>
    	<BODY>
    	<h1>Technology stack</h1>
    	<hr/>
    	<a href="summary.html">Summary</a><xsl:text> </xsl:text> <a><xsl:attribute name="href"><xsl:value-of select="@root-path"/></xsl:attribute>Products</a> <xsl:text> </xsl:text> <xsl:text> </xsl:text><a><xsl:attribute name="href"><xsl:value-of select="@license-summary"/></xsl:attribute>Licenses</a>
    	<hr/>
	    	<xsl:apply-templates select="publisher[@key=$publisher]"/>
	    </BODY>
	    </HTML>
    </xsl:template>
    
    <xsl:template match="publisher">
    	<h2>Clients of pubisher
    		<I>
		    	<xsl:choose>
		    		<xsl:when test="url">
		    			<a>
		    				<xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute>
		    				<xsl:value-of select="name"/>
		    			</a>
		    		</xsl:when>
		    		<xsl:otherwise>
		    			<xsl:value-of select="name"/>
		    		</xsl:otherwise>
		    	</xsl:choose>    		
    		</I>
    	</h2>
	    
	    	<table bgcolor="gray" cellspacing="1" cellpadding="3" >
	    	<TR bgcolor="silver">
				<TH>Client</TH>
			</TR>
		    	<xsl:for-each select="client">		
		    		<TR bgcolor="white">
		    			<TD>
		    				<a>
		    					<xsl:attribute name="href">source/<xsl:value-of select="."/>.html</xsl:attribute>
		    					<xsl:value-of select="."/>
		    				</a>
		    			</TD>
		    		</TR>
		    	</xsl:for-each>
			</table>
    </xsl:template>

</xsl:stylesheet>
