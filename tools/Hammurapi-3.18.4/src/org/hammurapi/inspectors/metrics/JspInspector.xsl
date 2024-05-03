<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>
  <xsl:decimal-format name="amount" digit="D" />
  
<xsl:param name="inspectorsPath"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Hammurapi JSP Metric</title>
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
          

          TH {
	  	background: rgb(204,204, 204) 
	  	text-align: center;
	  	color: black;
	  	FONT-FAMILY: Arial, Helvetica, sans-serif; 
	  	FONT-SIZE: 14px
	  }
	  
	  .TH_SOURCE  {
	  	background: rgb(166,202, 240)
	  	 margin-left: 0.5;
	  			     margin-right: 0;
	  			     padding-left: 0.8em;
	  			     padding-right: 0.8em;
	  
	  	text-align: left;
	  	color: white;
	  	font-weight:BOLD
	  	FONT-FAMILY: Arial, Helvetica, sans-serif;
		    
          }


          .TH-TYPE {
	  	background: rgb(166,202, 240)
	  	 margin-left: 0;
	  			     margin-right: 0;
	  			     padding-left: 0.8em;
	  			     padding-right: 0.8em;
	  
	  	text-align: left;
	  	color: white;
	  	font-weight:BOLD
	  	FONT-FAMILY: Arial, Helvetica, sans-serif;
		    font-size: 68%;
          }

                    .TD-VALUE {
                    	     font-size: 68%;
	  		     background: rgb(238,238, 238);
	  		     margin-left: 0;
	  		     margin-right: 0;
	  		     padding-left: 0.5em;
	  		     padding-right: 0.5em;
	  		     color: black;
	  		     text-align: left;
	  		     font-weight:BOLD
	  		     FONT-FAMILY: Arial, Helvetica, sans-serif;
	  		     FONT-SIZE: 12px
	  		  }

 		.TD-MultiTierList   {
                    	     font-size: 68%;
	  		     background: white;
	  		     margin-left: 0;
	  		     margin-right: 0;
	  		     padding-left: 0.5em;
	  		     padding-right: 0.5em;
	  		     color: black;
	  		     text-align: left;
	  		     font-weight:BOLD
	  		     FONT-FAMILY: Arial, Helvetica, sans-serif;
	  		     FONT-SIZE: 12px
	  		     border-color: red;
	  		     border-style:solid ;
	  		     
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
        <a name="top"><a href="http://www.hammurapi.org">Hammurapi</a> JSP Metric</a>
        </h1>
        <p align="right">.</p>
        <hr size="2"/>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  
  
  <xsl:template match="JspMetric">
    
    <xsl:call-template name="Menu"><xsl:with-param name="header">Summary</xsl:with-param>
          <xsl:with-param name="label">Summary</xsl:with-param>
      </xsl:call-template>
      
<p/>
Currently not working:  SQL String Extraction, LOC Comparision
  <p/>
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
	</table>
      <p/>
      

    <table class="details" border="0">
     
     	<tr><th>Metric</th> <th>Sum</th> </tr>
     	<tr><td>JSPs</td>  <td align="right">
	     		<xsl:value-of select="count(/JspMetric/JspDescriptor)" />
	     	
     	</td></tr> 
     	<tr><td>JSP-Java NCSS  </td> <td align="right">
     		<xsl:value-of select="sum(/JspMetric/JspDescriptor/SourceCodeMetricClass/NCSS/@number)" />
     	</td></tr> 
     	
 	<tr><td>JS Snippets  </td> <td align="right">
     		<xsl:value-of select="sum(JspDescriptor/@numberOfJavaScriptSnippets)" />
     	</td></tr>  
     	
 	<tr><td>JS Functions  </td>  <td align="right">
     		<xsl:value-of select="sum(/JspMetric/JspDescriptor/@numberOfJavaScriptFunctions)" />
     	</td> </tr>
 	<tr><td>JS Submits  </td>  <td align="right">
     		<xsl:value-of select="sum(/JspMetric/JspDescriptor/@numberOfJavaScriptSubmits)" />
     	
     	</td></tr> 
 	<tr><td>Out Print Ops</td>  <td align="right">
     		<xsl:value-of select="sum(/JspMetric/JspDescriptor/@numberOfOutPrintOps)" />
     	
     	</td> </tr>
 	<tr><td>Out Write Ops</td>  <td align="right">
     		<xsl:value-of select="sum(/JspMetric/JspDescriptor/@numberOfOutWriteOps)" />
     	
     	</td></tr> 
 	<tr><td>Pop Up Calls</td>  <td align="right">
     		<xsl:value-of select="sum(/JspMetric/JspDescriptor/@numberOfPopUpCalls)" />
     	
     	</td></tr> 
 	<tr><td>Jsp XREF Total</td>  <td align="right">
     		<xsl:value-of select="count(/JspMetric/JspDescriptor/JspXref)" />
     	</td></tr> 
     	 	<tr><td>XREF: Jsp Forward</td>  <td align="right">
	     		<xsl:value-of select="count(/JspMetric/JspDescriptor/JspXref[@invocation = 'JSP Forward'])" />
	     	</td></tr> 
     	 	<tr><td>XREF: Jsp Redirect</td>  <td align="right">
	     		<xsl:value-of select="count(/JspMetric/JspDescriptor/JspXref[@invocation = 'response.sendRedirect'])" />
	     	</td></tr> 
     	 	<tr><td>XREF: HTML Form Action</td>  <td align="right">
	     		<xsl:value-of select="count(/JspMetric/JspDescriptor/JspXref[@invocation = 'HTML form action'])" />
	     	</td></tr> 
	     	<tr><td>XREF: Java Script Action</td>  <td align="right">
			     		<xsl:value-of select="count(/JspMetric/JspDescriptor/JspXref[@invocation = 'Java Script Action'])" />
		</td></tr> 
			     	
	     	
	     	 
	
 <p/>
      
     </table>
      
      	
      
  
  
  
   <xsl:call-template name="ListOfJspDescriptors"/>
   <xsl:call-template name="ListOfJspXrefs"/>
    
  </xsl:template>





  <xsl:template name="ListOfJspDescriptors">
    <xsl:call-template name="Menu"><xsl:with-param name="header">Jsp Descriptors</xsl:with-param>
    <xsl:with-param name="label">JspDescriptors</xsl:with-param>
    </xsl:call-template>
  
  
    <table class="details" border="0" width="100%">
    
    	<tr>
    	
    	<th>NCSS</th> 
	<th>JS Snippets  </th> 
	<th>JS Functions  </th> 
	<th>JS Submits  </th> 
	<th>Out Print Ops</th> 
	<th>Out Write Ops</th> 
	<th>Pop Up Calls</th> 
	<th>Jsp XREF</th> 
	<th>Source</th>
	</tr>

        <xsl:apply-templates select="JspDescriptor"/>
     </table>
      <p/>  
  
  </xsl:template>
  
  
    
 <xsl:template match="JspDescriptor">

<tr>
<td><xsl:value-of select="SourceCodeMetricClass/NCSS/@number"/>  </td> 
<td><xsl:value-of select="@numberOfJavaScriptSnippets"/>  </td> 
<td><xsl:value-of select="@numberOfJavaScriptFunctions"/>  </td> 
<td><xsl:value-of select="@numberOfJavaScriptSubmits"/>  </td> 
<td><xsl:value-of select="@numberOfOutPrintOps"/>  </td> 
<td><xsl:value-of select="@numberOfOutWriteOps"/>  </td> 
<td><xsl:value-of select="@numberOfPopUpCalls"/>  </td> 
<td>
 <xsl:value-of select="count(JspXref)"/>
	
	</td> 
<td>
	  <a>
	       <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="SourceCodeMetricClass/@source-url"/>.html#line_<xsl:value-of select="SourceCodeMetricClass/@line"/></xsl:attribute>
	       <xsl:value-of select="SourceCodeMetricClass/@name"/>
	  </a>

      </td>
</tr>
  
  </xsl:template>


<xsl:template name="ListOfJspXrefs">


	<xsl:for-each select="/JspMetric/JspDescriptor">
<table class="details" border="0" width="100%">
    <tr><th>
		  <a>
		       <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="SourceCodeMetricClass/@source-url"/>.html#line_<xsl:value-of select="SourceCodeMetricClass/@line"/></xsl:attribute>
		       <xsl:value-of select="SourceCodeMetricClass/@name"/>
		  </a>
	</th></tr>
 	
	<xsl:for-each select="JspXref">
	<xsl:sort select="@invocation" data-type="text" order="ascending"/>
	<tr>    <td><table>	<tr><td>  
		<xsl:value-of select="@invocation "/>  
		</td><td>
		<xsl:text><![CDATA[ ->         ]]></xsl:text>	
		</td><td>
			<xsl:value-of select="@ref "/>  
		</td></tr></table>	
		</td></tr>
		</xsl:for-each>
	</table>	<P/>
</xsl:for-each>
	
</xsl:template>


<xsl:template name="Menu"><xsl:param name="header"/><xsl:param name="label"/>

	<table width="100%"><tr><td>
	<a><xsl:attribute name="name">
				<xsl:value-of select="concat('NV',$label)"/>
			</xsl:attribute></a>
	
			
	<h2> <xsl:value-of select="$header"/> </h2>
		</td>
	<td align="right">
	[<a href="#NVSummary">Summary</a>]
	[<a href="#NVViolations">Violations</a>]
	[<a href="#NVJspDescriptors">Jsp Descriptors</a>]
	</td></tr></table>
</xsl:template>


</xsl:stylesheet>
