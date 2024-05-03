<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>
  <xsl:decimal-format name="amount" digit="D" />
  
<xsl:param name="inspectorsPath"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Hammurapi Architectural Analysis</title>
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
	  		   .TD-VALUERIGHT {
			                      	     font-size: 68%;
			  	  		     background: rgb(238,238, 238);
			  	  		     margin-left: 0;
			  	  		     margin-right: 0;
			  	  		     padding-left: 0.5em;
			  	  		     padding-right: 0.5em;
			  	  		     color: black;
			  	  		     text-align: right;
			  	  		     font-weight:BOLD
			  	  		     FONT-FAMILY: Arial, Helvetica, sans-serif;
			  	  		     FONT-SIZE: 12px
			  	  		  }
	  		  
		.TD-ORANGE {
                    	     font-size: 68%;
	  		     background: rgb(238, 100, 238);
	  		     margin-left: 0;
	  		     margin-right: 0;
	  		     padding-left: 0.5em;
	  		     padding-right: 0.5em;
	  		     color: black;
	  		     text-align: right;
	  		     font-weight:BOLD
	  		     FONT-FAMILY: Arial, Helvetica, sans-serif;
	  		     FONT-SIZE: 12px
	  		  }
	  		  
		  .TD-GREEN {
					     font-size: 68%;
					     background: rgb(0, 200, 0);
					     margin-left: 0;
					     margin-right: 0;
					     padding-left: 0.5em;
					     padding-right: 0.5em;
					     color: black;
					     text-align: right;
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
        <a name="top"><a href="http://www.hammurapi.org">Hammurapi</a> Architectural Analysis</a>
        </h1>
        <p align="right">.</p>
        <hr size="2"/>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  
  
  
  <xsl:template match="ArchitecturalLayers">
    
    <xsl:call-template name="Menu"><xsl:with-param name="header">Summary</xsl:with-param>
          <xsl:with-param name="label">Summary</xsl:with-param>
      </xsl:call-template>
      

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
  
   <xsl:call-template name="ListOfLayers"/>
   
   <xsl:call-template name="TechStackEntityList"/>
    <xsl:call-template name="TechStackSummary"/>
  
   <xsl:call-template name="TemplateListOfPackageCategories"/>
   <xsl:call-template name="TemplateListOfCategories"/>
   <xsl:call-template name="tier-mappings"/> 
   <xsl:call-template name="extension-mappings"/> 
  
   <xsl:call-template name="TechStackEntityRating"/> 
   <xsl:call-template name="ClasspathFileLicense"/>
   
   <xsl:apply-templates select="UnResolvedAndNonTrivialVariableList"/>
   <xsl:call-template name="complexity-mappings"/> 
 
    
  </xsl:template>





  <xsl:template name="ListOfLayers">
    <xsl:call-template name="Menu"><xsl:with-param name="header">Layers</xsl:with-param>
    <xsl:with-param name="label">Layers</xsl:with-param>
    </xsl:call-template>
  
    <table class="details" border="0" width="100%">
    <table>
    
    	<tr><th><xsl:value-of select="@name"/></th></tr>
          	<xsl:apply-templates select="Package"/>
      
    	<xsl:apply-templates select="ListOfLayers/Tier"/>
    </table>
      </table>
      <p/>  
  
  </xsl:template>
  
  
    
    <xsl:template match="Tier">
      
      	<tr><th><xsl:value-of select="@name"/></th></tr>
      	<xsl:apply-templates select="Package"/>
      
    </xsl:template>


  <xsl:template name="TechStackEntityList">
    <xsl:call-template name="Menu"><xsl:with-param name="header">TechStack</xsl:with-param>
    <xsl:with-param name="label">Tech Stack</xsl:with-param>
    </xsl:call-template>
  
    <P/>
  	<table>
  	  	

  	<tr>
  	<td valign="top">
  	<table>	<th>Appreciated</th>
  		<xsl:for-each select="/ArchitecturalLayers/UsedTechStackEntityList/TechStackEntity">	
			<xsl:sort select="@name"/> 
	
	<xsl:if test="@rating  = 'OK'">
		<tr>
			<td class="TD-VALUE"><xsl:value-of select="@name"/></td>
		</tr>
	</xsl:if>
	
	</xsl:for-each>
	</table>	
	</td>
	
	<td valign="top">
  	<table>	<th>Evaluation</th>
  		<xsl:for-each select="TechStackEntity">	
			<xsl:sort select="@name"/> 
	
	<xsl:if test="@rating  = 'Evaluation'">
		<tr>
			<td class="TD-VALUE"><xsl:value-of select="@name"/></td>
		</tr>
	</xsl:if>
	
	</xsl:for-each>
	</table>	
	</td>
	
	<td valign="top">
  	<table>	<th>Depreciated </th>
  	
  		<xsl:for-each select="/ArchitecturalLayers/UsedTechStackEntityList/TechStackEntity">	
			<xsl:sort select="@name"/> 
	
	<xsl:if test="@rating  = 'DEPRECIATED'">
		<tr>
			<td class="TD-VALUE"><xsl:value-of select="@name"/></td>
		</tr>
	</xsl:if>
	
	
	</xsl:for-each>
	</table>	
	</td>
	
	</tr>
	
	</table>

  
  </xsl:template>




  <xsl:template name="TechStackSummary">
  <p/>
  
  <table>
	<tr>	<th>Tech Stack </th>
		<th>Complexity </th>
		<th>License </th>
		<th>Occurance </th></tr>
    	     <xsl:for-each select="/ArchitecturalLayers/TechStackEntityRating/TechStackEntity">	
		      			<xsl:sort select="@name"/> 
		
		    <xsl:variable name="category">
		    	<xsl:value-of select="@name"/> 
		    </xsl:variable> 
		    
		    <xsl:variable name="countTechE">
 				 <xsl:value-of select="count(/ArchitecturalLayers/ListOfClassCategories/ArchitecturalCategory[@category = $category])" />
		    </xsl:variable> 
 
		<tr><td class="TD-VALUE">	      			 
		      			<xsl:value-of select="@name"/> 
		</td>
		<td class="TD-VALUE">	      			 
		      			<xsl:value-of select="@complexity"/> 
		</td>
		
		<td class="TD-VALUE">	      			 
	     			<xsl:value-of select="License/@name"/> 
		</td>

		<xsl:if test="$countTechE &gt; 0">
	       		<td class="TD-ORANGE">
	       			<xsl:value-of select="$countTechE"/> 
	       		</td>
		</xsl:if>      			
		<xsl:if test="$countTechE = 0">
			       		<td class="TD-VALUERIGHT">
			       			<xsl:value-of select="$countTechE"/> 
			       		</td>
				</xsl:if>      			
		</tr>
		</xsl:for-each>
	       </table>
</xsl:template>


  <xsl:template match="Package">
	<tr>
    	    	<td></td><td class="TD-VALUE">
    	    		<a>
	  					<xsl:attribute name="href">#<xsl:value-of select="@name"/> </xsl:attribute>
						<xsl:value-of select="@name"/> 
						
					</a>
			    </td>
			   <td> <xsl:value-of select="@occurance"/>  </td>
			      <td class="TD-MultiTierList">
    	    	 <xsl:for-each select="PoilLayerOccurance">	
		      			<xsl:sort select="@name"/> 
		      			 
		      			<xsl:value-of select="@occurance"/> 
		      			<xsl:text><![CDATA[ : ]]></xsl:text>
		      			<xsl:value-of select="@name"/> 
	</xsl:for-each>
	</td>
       </tr>
</xsl:template>




  <xsl:template name="TemplateListOfPackageCategories">
    <xsl:call-template name="Menu"><xsl:with-param name="header">Packages</xsl:with-param>
    <xsl:with-param name="label">Packages</xsl:with-param>
    </xsl:call-template>
      

    <table class="details" border="0" width="100%">
        <xsl:apply-templates select="ListOfPackageCategories"/>
      </table>
      <p/>  
  </xsl:template>
  
  
  <xsl:template match="ListOfPackageCategories">

    <table><tr>
      <th class="TH-TYPE">
      	<a><xsl:attribute name="name">
				<xsl:value-of select="@package"/>
			</xsl:attribute></a>
	
      <xsl:value-of select="@package"/>
      </th>
    </tr>

        <tr><td>
        	<xsl:apply-templates select="LayerList"/>
        </td></tr>
    
    <tr><td><table>
    <tr>
            <th>category </th>
            <th>occurance</th>
            <th>identicator</th>
    </tr>
       					     
        	<xsl:apply-templates select="ArchitecturalCategoryPackage"/>
    </table></td></tr>

    </table>
    <P/>
    
  </xsl:template>


  <xsl:template match="LayerList">
    <table>
    	<tr><th>Layer</th></tr>
    	<xsl:apply-templates select="Layer"/>
    </table>
  </xsl:template>

  <xsl:template match="Layer">
	<tr>
    	    	<td class="TD-VALUE"><xsl:value-of select="@name"/> </td>
       </tr>
</xsl:template>



  <xsl:template name="TemplateListOfCategories">
    <xsl:call-template name="Menu"><xsl:with-param name="header">Classes</xsl:with-param>
    <xsl:with-param name="label">Classes</xsl:with-param>
    </xsl:call-template>
      

    <table class="details" border="0" width="100%">
        <xsl:apply-templates select="ListOfClassCategories"/>
      </table>
      <p/>  
  </xsl:template>
  
  
  <xsl:template match="ListOfClassCategories">

    <table><tr>
      <th class="TH-TYPE">
      <a> 
      <xsl:attribute name="name">#<xsl:value-of select="@class"/></xsl:attribute>
      <xsl:value-of select="@class"/>
       </a>
      
      
       <a class="TH_SOURCE">
       	<xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="SourceMarker/@source-url"/>.html#line_<xsl:value-of select="SourceMarker/@line"/></xsl:attribute>
        <xsl:text><![CDATA[  ]]></xsl:text>          source
      </a></th>
    </tr>
    
    
    <tr><td><table>
    <tr>
            <th>category </th>
            <th>identicator</th>
            
            <th>link</th></tr>

       
        	<xsl:apply-templates select="ArchitecturalCategory"/>
    </table></td></tr>
    </table>
    <P/>
  </xsl:template>



  <xsl:template match="ArchitecturalCategoryPackage">
   
  <tr>
      <td class="TD-VALUE"><xsl:value-of select="@category"/></td>
      <td class="TD-VALUE"><xsl:value-of select="@occurance"/></td>
      
      <xsl:for-each select="PoilLayerOccurance">	
      			<xsl:sort select="@name"/> 
      			<xsl:text><![CDATA[ : ]]></xsl:text> 
	</xsl:for-each>
    
      <td class="TD-VALUE">      
 	    	    		<a>
	  					<xsl:attribute name="href">#<xsl:value-of select="@identicator"/> </xsl:attribute>
						 <xsl:value-of select="@identicator"/> 
					</a>

	</td>
      </tr>
      
  </xsl:template>
  


  <xsl:template name="tier-mappings">
    <xsl:call-template name="Menu"><xsl:with-param name="header">Mappings</xsl:with-param>
    <xsl:with-param name="label">Mappings</xsl:with-param>
    </xsl:call-template>
    
  	<table>
  	<xsl:for-each select="TechStackEntityList/LayerMappings">
  	<xsl:sort select="@tier"/> 
  	<th>Tier</th><th>Identificator</th>
  	
  			<tr><td class="TD-VALUE"><xsl:value-of select="@tier"/></td>
				<td></td></tr>


  		<xsl:for-each select="Mapping">	
			<xsl:sort select="@identificator"/> 
	<tr><td></td>
		<td class="TD-VALUE"><xsl:value-of select="@identificator"/></td></tr>
	</xsl:for-each>
	<tr><td> </td><td></td></tr>
	</xsl:for-each>
	</table>
	<p/>

  </xsl:template>



  <xsl:template name="extension-mappings">
    
  	<table>
  	<th>Category</th><th>Extension Identificator</th>
  		<xsl:for-each select="ExtensionMappings/Mapping">	
			<xsl:sort select="@category"/> 
	<tr>
		<td class="TD-VALUE"><xsl:value-of select="@category"/></td>
		<td class="TD-VALUE"><xsl:value-of select="@identificator"/></td>
		</tr>
	</xsl:for-each>

	</table>

  </xsl:template>

  <xsl:template name="complexity-mappings">
    <p/>
  	<table>
  	<th>Category</th><th>Extension Identificator</th>
  		<xsl:for-each select="ComplexityMappingTable/ComplexityMapping">	
			<xsl:sort select="@name"/> 
	<tr>
		<td class="TD-VALUE"><xsl:value-of select="@name"/></td>
		<td class="TD-VALUE"><xsl:value-of select="@rate"/></td>
		</tr>
	</xsl:for-each>

	</table>

  </xsl:template>



  <xsl:template name="TechStackEntityRating">
    <P/>
  	<table><tr>
  	<th>Tech Stack Entity</th><th>Rating</th><th>License</th>
  	
  	<td><table width="100%">
  	<tr><th width="20%">Library</th><th width="10%">is Used</th><th width="10%">Version</th><th width="10%">Size</th><th width="50%">Home</th></tr> 
  	</table></td>	
  	</tr>
  		<xsl:for-each select="TechStackEntityRating/TechStackEntity">	
			<xsl:sort select="@name"/> 
	<tr>
		<td class="TD-VALUE"><xsl:value-of select="@name"/></td>
		<td class="TD-VALUE"><xsl:value-of select="@rating"/></td>
		<td>
		    <table width="100%">
			<xsl:for-each select="License">	
			<xsl:sort select="@name"/> 
			  <tr>
				<td class="TD-VALUE"><xsl:value-of select="@name"/></td>
			  </tr>
			</xsl:for-each>
		     </table>
		</td>
		<td><table width="100%">
	<!--	<tr><th>Library</th><th>is Used</th><th>Version</th><th>Size</th><th>Home</th></tr> -->
					<xsl:for-each select="JarFile">	
					<xsl:sort select="@name"/> 
					<tr>	
						<td width="20%" class="TD-VALUE"><xsl:value-of select="@name"/></td>
						<td width="10%"  class="TD-VALUE"><xsl:value-of select="@isUsed"/></td>
						<td  width="10%" class="TD-VALUE"><xsl:value-of select="@version"/></td>
						<td  width="10%" class="TD-VALUE"><xsl:value-of select="@size"/></td>
						<td width="50%" class="TD-VALUE"><xsl:value-of select="../Home/@url"/></td>
						

						</tr>
					</xsl:for-each>
				
		   </table>
		</td>
		</tr>
	</xsl:for-each>

	</table>

  </xsl:template>

<xsl:template name="ClasspathFileLicense">
    <P/>
    <xsl:call-template name="Menu"><xsl:with-param name="header">Classpath</xsl:with-param>
              <xsl:with-param name="label">Classpath</xsl:with-param>
          </xsl:call-template>
    
  	<table>
  	<tr>
  		<th>Classpath Jar Files</th><th>is Used</th><th>Version</th><th>Size</th><th>Path</th>
	</tr>

				<xsl:for-each select="ClasspathFileLicense/JarFile">	
						<xsl:sort select="@isUsed" data-type="text" order="descending"/> 
						<xsl:sort select="@name" data-type="text" order="ascending"/> 
							<tr>
							<td class="TD-VALUE"><xsl:value-of select="@name"/></td>
						
						<xsl:if test="@isUsed  = 'true'">
								
							<td class="TD-GREEN"><xsl:value-of select="@isUsed"/></td>
						</xsl:if>
						<xsl:if test="@isUsed  = 'false'">
								
							<td class="TD-VALUE"><xsl:value-of select="@isUsed"/></td>
						</xsl:if>
								
							
							<td class="TD-VALUE"><xsl:value-of select="@version"/></td>
							<td class="TD-VALUE"><xsl:value-of select="@size"/></td>
							<td class="TD-VALUE"><xsl:value-of select="@path"/></td>
							</tr>
						</xsl:for-each>
		

	</table>

  </xsl:template>


 <xsl:template match="UnResolvedAndNonTrivialVariableList">
    <P/>
  	<table>
  	<th>Unresolved And Non Trivial Variables</th>
  		<xsl:for-each select="UnResolvedAndNonTrivialVariable">	
			<xsl:sort select="@name"/> 
	<tr>
		<td class="TD-VALUE"><xsl:value-of select="@name"/></td>
		</tr>
	</xsl:for-each>

	</table>

  </xsl:template>

  <xsl:template match="ArchitecturalCategory">
   
    <tr>
      <td class="TD-VALUE"><xsl:value-of select="@category"/></td>
      <td class="TD-VALUE"><xsl:value-of select="@identicator"/> </td>
      <!--td class="TD-VALUE"><xsl:value-of select="@occurance"/></td-->
      <td class="TD-VALUE">      
      	<a>
	  <xsl:attribute name="href"><xsl:value-of select="$inspectorsPath"/>source/<xsl:value-of select="SourceMarker/@source-url"/>.html#line_<xsl:value-of select="SourceMarker/@line"/></xsl:attribute>
	source  
	</a>
	</td>
      </tr>
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
	[<a href="#NVViolations">Violations</a>]
	[<a href="#NVLayers">Layers</a>]
	[<a href="#NVTechStack">TeckStack</a>]
	[<a href="#NVPackages">Packages</a>]
	[<a href="#NVClasses">Classes</a>]
	[<a href="#NVMappings">Mappings</a>]
	[<a href="#NVClasspath">Classpath</a>]
	</td></tr></table>
</xsl:template>


</xsl:stylesheet>
