<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>
  <xsl:decimal-format name="amount" digit="D" />
  
<xsl:param name="inspectorsPath"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>NCSS Analysis</title>
        <style type="text/css">
          body {
          font:normal 68% verdana,arial,helvetica;
          color:#000000;
          }
          table tr td, tr th {
            font-size: 68%;
          }
          table.details tr th{
          font-weight: bold;
          text-align:left;
          background:#a6caf0;
          }
          table.details tr td{
          background:#eeeee0;
          }
          
          p {
          line-height:1.5em;
          margin-top:0.5em; margin-bottom:1.0em;
          margin-left:2em;
          margin-right:2em;
          }
          h1 {
          margin: 0px 0px 5px; font: 165% verdana,arial,helvetica
          }
          h2 {
          margin-top: 1em; margin-bottom: 0.5em; font: bold 125% verdana,arial,helvetica
          }
          h3 {
          margin-bottom: 0.5em; font: bold 115% verdana,arial,helvetica
          }
          h4 {
          margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
          }
          h5 {
          margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
          }
          h6 {
          margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
          }
          .Error {
          font-weight:bold; color:red;
          }
          .Failure {
          font-weight:bold; color:purple;
          }
          .Properties {
          text-align:right;
          }
        </style>
      </head>  
      <body>
        <h1>
        <a name="top"><a href="http://www.hammarupi.org">Hammarupi</a> NCSS Analysis</a>
        </h1>
        <p align="right">Design based on <a href="http://www.kclee.com/clemens/java/javancss/">JavaNCSS</a> powered by <a href="http://www.jfree.org/jfreechart/index.html">jFreeChart</a>.</p>
        <hr size="2"/>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  
  
  <xsl:template match="SourceCodeMetricProject">
    
    
    
    
    <xsl:call-template name="Menu"><xsl:with-param name="header">Summary</xsl:with-param>
          <xsl:with-param name="label">Summary</xsl:with-param>
      </xsl:call-template>
      

  <p/>
    <xsl:variable name="classDefects">
    	<xsl:value-of select="count(/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass[NCSS/@number &gt;  /SourceCodeMetric/ClassMaxLoc/@number])" />
    </xsl:variable> 
    <xsl:variable name="classFunctionDefects">
    	<xsl:value-of select="count(/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass[Functions/@number &gt;  /SourceCodeMetric/ClassMaxFunction/@number])" />
    </xsl:variable> 

    <xsl:variable name="classes">
    	<xsl:value-of select="count(/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass)" />
    </xsl:variable> 
    <xsl:variable name="methodDefects">
    	<xsl:value-of select="count(/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass/SourceCodeMetricMethodImplemented[NCSS/@number &gt;  /SourceCodeMetric/FunctionMaxLoc/@number])" />
    </xsl:variable> 
    <xsl:variable name="methods">
    	<xsl:value-of select="count(/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass/SourceCodeMetricMethodImplemented)" />
    </xsl:variable> 
    
        <table class="details" border="0" >
    	      <tr>
    	      	<td>
    	      	 <img>
		 <xsl:attribute name="src">
			<xsl:value-of select="/SourceCodeMetric/JpgClassFileEntry/@chartFile"/>
		 </xsl:attribute>
		  <xsl:attribute name="alt">
		 	Tanzmaus
		  </xsl:attribute>
		</img>
    	      	</td>
    	      	<td>
    	      	 <img>
		 <xsl:attribute name="src">
			<xsl:value-of select="/SourceCodeMetric/JpgFunctionFileEntry/@chartFile"/>
		 </xsl:attribute>
		  <xsl:attribute name="alt">
		 	Tanzmaus
		  </xsl:attribute>
		</img>
    	      	</td>
	      </tr>
	</table>      
<P></P>        
        <table class="details" border="0" >
	      <tr>
	        <th></th>
	       	<th></th>
	       	<th>Total</th>
	        <th>Defects </th>
	        <th> %</th>
	        <th> Criteria </th>
	        <th> Average </th>
	        <th></th>
      </tr>
       <tr> 
      <td>&#160;</td>
      <td><B>Classes</B></td>
	      <td align="right"><xsl:value-of select="$classes"/></td>
	      <td align="right"><xsl:value-of select="$classDefects"/></td>
	      <td align="right"><xsl:value-of select='format-number($classDefects div $classes * 100, "DD.00", "amount")'/></td>
	      <td align="right"><xsl:value-of select="/SourceCodeMetric/ClassMaxLoc/@number"/></td>
	      <td align="right"><xsl:value-of select="/SourceCodeMetric/ClassNcssAverage/@number"/></td>
	      <td>&#160;</td>
      </tr><tr>
        <td>&#160;</td>
            <td><B>Methods</B></td>
            <td align="right"><xsl:value-of select="$methods"/></td>
            <td align="right"><xsl:value-of select="$methodDefects"/></td>
            
            <td align="right"><xsl:value-of select='format-number($methodDefects div $methods * 100, "DD.00", "amount")'/></td>
            <td align="right"><xsl:value-of select="/SourceCodeMetric/FunctionMaxLoc/@number"/></td>
            <td align="right"><xsl:value-of select="/SourceCodeMetric/FunctionNcssAverage/@number"/></td>
            <td align="right">&#160;</td>
      </tr>
      <tr>
              <td>&#160;</td>
                  <td><B>Method p Class</B></td>
                  <td align="right"><xsl:value-of select="$methods"/></td>
                  <td align="right"><xsl:value-of select="$classFunctionDefects"/></td>
                  
                  <td align="right"><xsl:value-of select='format-number($classFunctionDefects div $methods * 100, "DD.00", "amount")'/></td>
                  <td align="right"><xsl:value-of select="/SourceCodeMetric/ClassMaxFunction/@number"/></td>
                  <td align="right"></td>
                  <td align="right">&#160;</td>
      </tr>
	</table>
      <p/>
      
    <xsl:call-template name="Menu"><xsl:with-param name="header">Packages</xsl:with-param>
      <xsl:with-param name="label">Packages</xsl:with-param>
  </xsl:call-template>
  
    <table class="details" border="0" width="100%">
      <tr>
        <th>Nr.</th>
        <th>Classes</th>
        <th>Functions</th>
        <th>NCSS</th>
        <th>Javadocs</th>
        <th>Package</th>
      </tr>
      <xsl:apply-templates select="SourceCodeMetricPackage"/>
      
      <tr>
        <td>&#160;</td>
        <td>&#160;</td>
        <td>&#160;</td>
        <td>&#160;</td>
        <td>&#160;</td>
        <td>&#160;</td>
      </tr>
      
      <tr>
        <td> &#160;</td>
   
      <td><B><xsl:value-of select="sum(//SourceCodeMetricPackage/Entities/@number)"/></B></td>
      <td><B><xsl:value-of select="sum(*/Functions/@number)"/></B></td>
      <td><B><xsl:value-of select="sum(*/NCSS/@number)"/></B></td>
      <td><B><xsl:value-of select="sum(*/javadocs)"/></B></td>
      <td><B>Total  </B></td>
    </tr>
    
   
    </table>
    <p/>
    
   

    <xsl:call-template name="objects"/>
    
    <xsl:call-template name="SourceCodeMetricClassDefects"/>
    
    <xsl:call-template name="MethodpClassDefects"/>
    
    <xsl:call-template name="functions"/>
    
    <xsl:call-template name="SourceCodeMetricMethodImplementedDefects"/>
    
    
  </xsl:template>


  <xsl:template match="SourceCodeMetricPackage">
    <tr>
      <td><xsl:value-of select="position()"/></td>
      <td><xsl:value-of select="Entities/@number"/></td>
      <td><xsl:value-of select="Functions/@number"/></td>
      <td><xsl:value-of select="NCSS/@number"/></td>
      <td><xsl:value-of select="javadocs"/></td>
      <td><xsl:value-of select="@name"/></td>
    </tr>
  </xsl:template>



  <xsl:template name="objects">
  
  <xsl:call-template name="Menu"><xsl:with-param name="header">Classes</xsl:with-param>
  <xsl:with-param name="label">Classes</xsl:with-param>
  </xsl:call-template>
    
    <table class="details" border="0" width="100%">
      <tr>
        <th>Nr.</th>
        <th>NCSS</th>
        <th>Functions</th>
        <th>Javadocs</th>
        <th>Class</th>
      </tr>
      <xsl:apply-templates select="/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass"/>
    </table>
    <p/>  
  </xsl:template>







  <xsl:template match="SourceCodeMetricClass">
  
  <xsl:variable name="ncss-color">
        <xsl:choose>
          <xsl:when test="NCSS/@number &gt; /SourceCodeMetric/ClassMaxLoc/@number">#ff0000</xsl:when>
          <xsl:otherwise>#000000</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <tr>
      <td><xsl:value-of select="position()"/></td>
      <td><font color="{$ncss-color}"><xsl:value-of select="NCSS/@number"/></font></td>
      
      <td><xsl:value-of select="Functions/@number"/></td>
      
     <td><xsl:value-of select="javadocs"/></td>
      <td>
        <a>
	    	      <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="@source-url"/>.html#line_<xsl:value-of select="@line"/></xsl:attribute>
	    	      <xsl:value-of select="@name"/>
	    	  </a>
	</td>
    </tr>
  </xsl:template>
  
  
  
  
<xsl:template name="SourceCodeMetricClassDefects">

    
 <xsl:call-template name="Menu"><xsl:with-param name="header">Class Length Defects</xsl:with-param>
  <xsl:with-param name="label">MethodPClassDefects</xsl:with-param>
  </xsl:call-template>
     
    <table class="details" border="0" width="100%">
      <tr>
        <th>Nr.</th>
        <th>NCSS</th>
        <th>Functions</th>
        <th>Javadocs</th>
        <th>Class</th>
      </tr>
  
  <xsl:for-each select="/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass">
  	<xsl:sort select="NCSS/@number" data-type="number" order="descending"/>
  	
  	<xsl:if test="NCSS/@number  &gt; /SourceCodeMetric/ClassMaxLoc/@number">
  	
    <tr>
      <td> <xsl:value-of select="position()"/></td>
      <td><font color="#ff0000"><xsl:value-of select="NCSS/@number"/></font></td>
      <td><xsl:value-of select="Functions/@number"/></td>
      <td><xsl:value-of select="javadocs"/></td>
      <td>
	  <a>
	       <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="@source-url"/>.html#line_<xsl:value-of select="@line"/></xsl:attribute>
	       <xsl:value-of select="@name"/>
	  </a>

      </td>
    </tr>
    
    
    </xsl:if>
    
    </xsl:for-each>
    
    </table>
  </xsl:template>
  
  
  <xsl:template name="MethodpClassDefects">
      
   <xsl:call-template name="Menu"><xsl:with-param name="header">Method p Class Defects</xsl:with-param>
    <xsl:with-param name="label">ClassDefects</xsl:with-param>
    </xsl:call-template>
       
      <table class="details" border="0" width="100%">
        <tr>
          <th>Nr.</th>
          <th>NCSS</th>
          <th>Functions</th>
          <th>Javadocs</th>
          <th>Class</th>
        </tr>
    
    <xsl:for-each select="/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass">
    	<xsl:sort select="Functions/@number" data-type="number" order="descending"/>
    	
    	<xsl:if test="Functions/@number  &gt; /SourceCodeMetric/ClassMaxFunction/@number">
    	
      <tr>
        <td> <xsl:value-of select="position()"/></td>
        <td><font color="#ff0000"><xsl:value-of select="NCSS/@number"/></font></td>
        <td><xsl:value-of select="Functions/@number"/></td>
        <td><xsl:value-of select="javadocs"/></td>
        <td>
  	  <a>
  	       <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="@source-url"/>.html#line_<xsl:value-of select="@line"/></xsl:attribute>
  	       <xsl:value-of select="@name"/>
  	  </a>
  
        </td>
      </tr>
      
      
      </xsl:if>
      
      </xsl:for-each>
      
      </table>
    </xsl:template>
  
  
  
  <xsl:template name="functions">
    
    <xsl:call-template name="Menu"><xsl:with-param name="header">Methods</xsl:with-param>
      <xsl:with-param name="label">Methods</xsl:with-param>
      </xsl:call-template>
 
    
    <table class="details" border="0" width="100%">
      <tr>
        <th>Nr.</th>
        <th>NCSS</th>
        
        <th>Javadoc</th>
        <th>Function</th>
      </tr>
      <xsl:apply-templates select="/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass/SourceCodeMetricMethodImplemented"/>
      
    </table>
    <p/>
  </xsl:template>

  <xsl:template match="SourceCodeMetricMethodImplemented">
    <xsl:variable name="ncss-color">
      <xsl:choose>
        <xsl:when test="NCSS/@number &gt; /SourceCodeMetric/FunctionMaxLoc/@number">#ff0000</xsl:when>
        <xsl:otherwise>#000000</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="ccn-color">
      <xsl:choose>
        <xsl:when test="ccn &gt; '9'">#ff0000</xsl:when>
        <xsl:otherwise>#000000</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="jdocs-color">
      <xsl:choose>
        <xsl:when test="javadocs &lt; '1'">#ff0000</xsl:when>
        <xsl:otherwise>#000000</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <tr>
      <td><xsl:value-of select="position()"/></td>
      <td><font color="{$ncss-color}"><xsl:value-of select="NCSS/@number"/></font></td>
      <td><font color="{$jdocs-color}"><xsl:value-of select="javadocs"/></font></td>
          <td>
	  <a>
	      <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="@source-url"/>.html#line_<xsl:value-of select="@line"/></xsl:attribute>
	      <xsl:value-of select="@name"/>
	  </a>

          
      </td>
    </tr>
  </xsl:template>
  
    <xsl:template name="SourceCodeMetricMethodImplementedDefects">
    
    
        <xsl:call-template name="Menu"><xsl:with-param name="header">Method Defects</xsl:with-param>
          <xsl:with-param name="label">MethodDefects</xsl:with-param>
          </xsl:call-template>
 
    <table class="details" border="0" width="100%">
      <tr>
        <th>Nr.</th>
        <th>NCSS</th>
        
        <th>Javadoc</th>
        <th>Function</th>
      </tr>
    
    
      <xsl:for-each select="/SourceCodeMetric/SourceCodeMetricProject/SourceCodeMetricPackage/SourceCodeMetricClass/SourceCodeMetricMethodImplemented">
      	<xsl:sort select="NCSS/@number" data-type="number" order="descending"/>
      	<xsl:if test="NCSS/@number  &gt; /SourceCodeMetric/FunctionMaxLoc/@number">
      <tr>
        <td><xsl:value-of select="position()"/></td>
        <td><font color="#ff0000"><xsl:value-of select="NCSS/@number"/></font></td>
            
        
        <td><xsl:value-of select="javadocs"/></td>
            <td>
            	  <a>
	    	      <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="@source-url"/>.html#line_<xsl:value-of select="@line"/></xsl:attribute>
	    	      <xsl:value-of select="@name"/>
	    	  </a>

            
            </td>
      </tr>
      </xsl:if>
      </xsl:for-each>
      
      </table>
      
  </xsl:template>

  <xsl:template match="text()"/>



<xsl:template name="Menu"><xsl:param name="header"/><xsl:param name="label"/>

	<table width="100%"><tr><td>
	<a><xsl:attribute name="name">
				<xsl:value-of select="concat('NV',$label)"/>
			</xsl:attribute></a>
	
			
	<h2> <xsl:value-of select="$header"/> </h2>
		</td>
	<td align="right">
	[<a href="#NVSummary">Summary</a>]
	[<a href="#NVPackages">Packages</a>]
	[<a href="#NVClasses">Classes</a>]
	[<a href="#NVClassDefects">Class Defects</a>]
	[<a href="#NVMethodPClassDefects">Mthd p C Defcts</a>]
	[<a href="#NVMethods">Methods</a>]
	[<a href="#NVMethodDefects">Method Defcts</a>]
	</td></tr></table>
</xsl:template>



</xsl:stylesheet>
