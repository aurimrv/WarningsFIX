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
    	<a href="summary.html">Summary</a><xsl:text> </xsl:text> Products <xsl:text> </xsl:text><a><xsl:attribute name="href"><xsl:value-of select="@license-summary"/></xsl:attribute>Licenses</a>
    	<hr/>
    	<h2>Publishers and Products</h2>
	    	<xsl:apply-templates select="publisher[client]"/>
	    </BODY>
	    </HTML>
    </xsl:template>

    <xsl:template match="publisher">    
    <a>
    	<xsl:attribute name="name">publisher:<xsl:value-of select="@key"/></xsl:attribute>
    </a>
    <h3>
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
    </h3>
    <xsl:if test="description">
    	<xsl:value-of select="description"/><P/>
    </xsl:if>
    
    <xsl:if test="category">
    	<B>Category: </B> <xsl:value-of select="category"/><P/>
    </xsl:if>
    
    <B>Clients: </B>
    	<a>
			<xsl:attribute name="href"><xsl:value-of select="@path"/></xsl:attribute>
			<xsl:value-of select="count(client)"/>
		</a>
    
    	<table bgcolor="gray" cellspacing="1" cellpadding="3" >
    	<TR bgcolor="silver">
			<TH>Name</TH>
			<TH>Description</TH>
			<TH>Category</TH>
			<TH>License</TH>
			<TH>Clients</TH>
		</TR>
	    	<xsl:apply-templates select="product[client]"/>		
		</table>
    </xsl:template>
    
    <xsl:template match="product">
    	<TR bgcolor="white">
			<TD>
	    		<a>
    				<xsl:attribute name="name">product:<xsl:value-of select="@key"/></xsl:attribute>
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
			<TD>
		    	<xsl:choose>
		    		<xsl:when test="license">
		    			<xsl:apply-templates select="key('license', license/@key)"/>
		    		</xsl:when>
		    		<xsl:when test="license-ref">
		    			<xsl:apply-templates select="key('license', license-ref)"/>
		    		</xsl:when>
		    		<xsl:otherwise>
		    			<xsl:apply-templates select="key('license', ../license-ref)"/>
		    		</xsl:otherwise>
		    	</xsl:choose>
				<xsl:value-of select="license"/>
			</TD>
			<TD>
			    	<a>
						<xsl:attribute name="href"><xsl:value-of select="@path"/></xsl:attribute>
						<xsl:value-of select="count(client)"/>
					</a>
			</TD>
		</TR>
    </xsl:template>
    
    <xsl:template match="license">
  			<a>
  				<xsl:attribute name="href"><xsl:value-of select="/tech-stack/@license-summary"/>#<xsl:value-of select="@key"/></xsl:attribute>
  				<xsl:value-of select="name"/>
  			</a>
    </xsl:template>
</xsl:stylesheet>
