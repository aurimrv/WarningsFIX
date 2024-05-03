<?xml version='1.0' encoding='UTF-8'?>
<xsl:stylesheet version="1.0"  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">



<xsl:output  method="html" indent="no" omit-xml-declaration="yes"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>Hammurapi Caller Trace Report</title>
<style type="text/css">
          body {
          font:normal 68% verdana,arial,helvetica;
          color:#000000;
          }
          th {
            font-size: 100%;
            padding-top:0;
			padding-bottom:0;

          }
           table tr td{
           padding-top:0;
		   padding-bottom:0;

            font-size: 68%;
          }
          table.details tr th{
          padding-top:0;
		  padding-bottom:0;

          font-weight: bold;
          text-align:left;
          background:#a6caf0;
          }
          table.details tr td{
          padding-top:0;
		  padding-bottom:0;

          background:#eeeee0;
          }



      TABLE {
      	margin-left: 5;
          	margin-right: 5;
          	          	border-collapse: separate
      }


      .MODELTABLE {
      	margin-left: 5;
          	margin-right: 5;
          	background: white;
          	border-collapse: separate
      }

      .INNERTABLE {
      	margin-left:  10;
          	margin-right: 10;
          	background: white;
          	/* background: rgb(238,238, 238); */
          	border-collapse: collapse
      }

      CAPTION {
          caption-side: left;
          margin-left: -8em;
          width: 8em;
          text-align: right;
          vertical-align: bottom
      }

      TH {
      	background: rgb(204,204, 204)
      	text-align: center;
      	color: black;
      	FONT-FAMILY: Arial, Helvetica, sans-serif;
      	FONT-SIZE: 14px
      }

      TR {
      	background: red
padding-top:0;
padding-bottom:0;
      }

      TD {
         background: rgb(245,245, 245)
         margin-left: 0;
         margin-right: 0;
         padding-left: 0.5em;
         padding-right: 0.5em;
         color: black;
         text-align: left;
         FONT-FAMILY: Arial, Helvetica, sans-serif;
         FONT-SIZE: 12px
      }

      .TD-NO {
         background: rgb(238,238, 150)
         margin-left: 0;
         margin-right: 0;
         padding-left: 0.5em;
         padding-right: 0.5em;
         color: black;
         text-align: right;
         FONT-FAMILY: Arial, Helvetica, sans-serif;
         FONT-SIZE: 12px
      }

      .TD-NO-RESULT {
         background: rgb(238,238, 0)
         margin-left: 0;
         margin-right: 0;
         padding-left: 0.5em;
         padding-right: 0.5em;
         color: black;
         text-align: right;
         FONT-FAMILY: Arial, Helvetica, sans-serif;
         FONT-SIZE: 12px
         font-weight:BOLD
      }

      .TD-LABEL
      {
      	background: rgb(204,204, 204)
      	text-align: left;
      	color: black;
      	FONT-FAMILY: Arial, Helvetica, sans-serif;
      	FONT-SIZE: 14px
      }

      .TD-NULL {
         background: rgb(238,238, 238)
         margin-left: 0;
         margin-right: 0;
         padding-left: 0.5em;
         padding-right: 0.5em;
         color: rgb(200,200, 200);
         text-align: center;
         FONT-FAMILY: Arial, Helvetica, sans-serif;
         FONT-SIZE: 12px
      }
      .TD-VALUE {
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
      
      
      .methodImplementedButNotCalled {
         background: rgb(150,200, 200);
         margin-left: 0;
         margin-right: 0;
         padding-left: 0.5em;
         padding-right: 0.5em;
         color: black;
         text-align: center;
         font-weight:BOLD
         FONT-FAMILY: Arial, Helvetica, sans-serif;
         FONT-SIZE: 12px
      }
      .TD-TOTALVALUE {
         background: rgb(255,204, 51);
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
      .TD-DELETE {
         background: rgb(255,102, 00);
         margin-left: 0;
         margin-right: 0;
         padding-left: 0.5em;
         padding-right: 0.5em;
         color: black;
         text-align: center;
         FONT-FAMILY: Arial, Helvetica, sans-serif;
         FONT-SIZE: 12px
      }
      .TD-SUCCESS {
         background: rgb(00,204, 00);
         margin-left: 0;
         margin-right: 0;
         padding-left: 0.5em;
         padding-right: 0.5em;
         color: black;
         text-align: center;
         FONT-FAMILY: Arial, Helvetica, sans-serif;
         FONT-SIZE: 14px
      }

      .TD-EXCEPTION {
         background: rgb(204,51, 00);
         margin-left: 0;
         margin-right: 0;
         padding-left: 0.5em;
         padding-right: 0.5em;
         color: black;
         text-align: center;
         FONT-FAMILY: Arial, Helvetica, sans-serif;
         FONT-SIZE: 12px;
         font-weight:bold
      }


      .TD-ENDPOINT {
      font-weight: bold;
      	color: red;
      		 background: rgb(255,200, 00);
						}
.TD-SIBLING {

      	color: red;
      		 background: rgb(255,255, 160);
						}

.TD-TRACENODE {

      	color: red;
      		 background: rgb(200,200, 200);
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
        <a name="top"><a href="http://www.hammurapi.org">Hammurapi</a> Caller Trace Report</a>
        </h1>
      
        <hr size="2"/>
        
        <p/>
        <table><tr>
        <th>Legende</th></tr>
        <tr>
        	<td>
        		<table><tr>
        		<td class="TD-TRACENODE">trace node</td> <td class="TD-SIBLING">sibling</td> <td class="TD-ENDPOINT">end point node</td>
        		</tr></table>
        	</td>
        </tr>
        <tr>
        	<td>
        		<table><tr>
        		<td> Afferent Method Coupling</td> <td >Method Signature</td> <td> Efferent Method Coupling</td>
        		</tr></table>
        	</td>
        </tr>
        </table>
        <p/>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>





<xsl:template match="CallerTrace"  >

	<table>

	<tr><th>	<a><xsl:attribute name="name">
					<xsl:text><![CDATA[#index]]></xsl:text>	
				</xsl:attribute>
 		API</a> </th>
 	<th> Traces</th></tr>
 	
 	<tr><td class="methodImplementedButNotCalled"> 
 	 		 <a>
				<xsl:attribute name="href">
					<xsl:text><![CDATA[#methodImplementedButNotCalled]]></xsl:text>
		   		</xsl:attribute>
					Methods Implemented but Never Called
				</a>
	</td>	<td> </td></tr>
 	
	<xsl:for-each select="TraceTable/TraceList">
			<xsl:sort select="@methodName"/>
			<xsl:call-template name="index"/>	
		</xsl:for-each>
	</table>
	<P></P>

	<xsl:for-each select="TraceTable/TraceList">	
			<xsl:call-template name="traceList"/>	
		</xsl:for-each>
		        <xsl:apply-templates/>
</xsl:template>


<xsl:template match="ImplementedMethodList"  >
	<P></P>
	<P></P>

	<h2><a><xsl:attribute name="name">
					<xsl:text><![CDATA[#methodImplementedButNotCalled]]></xsl:text>	
				</xsl:attribute>
 		 Methods Implemented but not called</a>
 		 <xsl:text><![CDATA[   ]]></xsl:text>
 		 <a>
				<xsl:attribute name="href">
					<xsl:text><![CDATA[#index]]></xsl:text>
		   		</xsl:attribute>
					<xsl:text><![CDATA[Index]]></xsl:text>
				</a>
				</h2>
	<table>

	<tr>
	

	 	<th> Is Called</th>

	 	<th> Afferent C </th>
	 	<th> Efferent C </th>

	<th>	<a><xsl:attribute name="name">
					<xsl:text><![CDATA[#index]]></xsl:text>	
				</xsl:attribute>
 		API</a> </th>
 	</tr>
	<xsl:for-each select="MethodWrapperDeclaration">
		<xsl:sort select="@called" order="descending"/> 
		<xsl:sort select="@Me" order="descending"/> 
			<tr>
			
			<td><xsl:value-of select="@called"/></td>
			<td><xsl:value-of select="@Ma"/></td>
			<td><xsl:value-of select="@Me"/></td>
			
			
			<td>
			   		<a>

				<xsl:attribute name="alt">
					<xsl:text><![CDATA[lala]]></xsl:text>
				</xsl:attribute>

				<xsl:attribute name="href">
					<xsl:text><![CDATA[source/]]></xsl:text>
					<xsl:value-of select="@source-url"/>
					<xsl:text><![CDATA[.html#line_]]></xsl:text>
					<xsl:value-of select="@line"/>

			</xsl:attribute>
			
			<xsl:value-of select="@key" /> 
			</a>
			</td>
			
			
			</tr>
		</xsl:for-each>
	</table>
</xsl:template>



<xsl:template name="index">
	
	<tr> 
	<td>
			<a>
				<xsl:attribute name="href">
				<xsl:text><![CDATA[#]]></xsl:text><xsl:value-of select="@methodName"/>
			</xsl:attribute>
			<xsl:value-of select="@methodName"/>
			</a>
				
				</td>
	<td>
		<xsl:value-of select="count(Trace)"/>
	</td>		
	</tr>		

</xsl:template>


<xsl:template name="traceList">
	<table>
	<tr> 
	<th>
	<table><tr><th>
	  <xsl:text><![CDATA[Endpoint: ]]></xsl:text>
				
				<a><xsl:attribute name="name">
					<xsl:value-of select="@methodName"/>
				</xsl:attribute></a>
				<xsl:value-of select="@methodName"/>
			</th><td>	
				<a>
				<xsl:attribute name="href">
					<xsl:text><![CDATA[#index]]></xsl:text>
		   		</xsl:attribute>
					<xsl:text><![CDATA[Index]]></xsl:text>
				</a>

				
				</td>
	</tr></table>
	</th></tr>
	<tr><td>
			<xsl:for-each select="Trace">	
			<table><tr><td>
			<xsl:text><![CDATA[Trace ]]></xsl:text>
				<xsl:value-of select="@id"/>
			<xsl:call-template name="trace"/>	
			</td></tr></table>
			</xsl:for-each>
	</td>		
	</tr>		
	</table>
	<P></P>

</xsl:template>

<xsl:template name="trace">
	
				<xsl:for-each select="TracedMethod">	
				<table><tr>
		
				   <xsl:call-template name="addIndent">
				       <xsl:with-param name="N">1</xsl:with-param>
					      <xsl:with-param name="MAX">
					          <xsl:value-of select="@id"/>
				        </xsl:with-param>
				     </xsl:call-template>
	
				<td>
				
				<xsl:call-template name="tracedMethodInside"/>	
				</td></tr></table>
				
			</xsl:for-each>
			
</xsl:template>

<xsl:template name="tracedMethodInside">
	<table>
		<xsl:for-each select="Inside">
		<tr>
		
			
		<xsl:if test="@type = 'key'">
		
			<td class="TD-ENDPOINT">    
      		<a>

				<xsl:attribute name="alt">
					<xsl:text><![CDATA[lala]]></xsl:text>
				</xsl:attribute>

				<xsl:attribute name="href">
					<xsl:text><![CDATA[source/]]></xsl:text>
					<xsl:value-of select="Method/@source-url"/>
					<xsl:text><![CDATA[.html#line_]]></xsl:text>
					<xsl:value-of select="Method/@line"/>

			</xsl:attribute>
			
			<xsl:value-of select="Method/@key" /> 
			</a>
		</td>
		
		</xsl:if>			
		
		<xsl:if test="@type = 'mother'">
		
			<td class="TD-TRACENODE">    
				<table><tr><td class="TD-TRACENODE">    
						<xsl:value-of select="Method/@Ma" /> 
						<xsl:text><![CDATA[   ]]></xsl:text>
						</td><td class="TD-TRACENODE">    
			
      		<a>
				<xsl:attribute name="alt">
					<xsl:text><![CDATA[lala]]></xsl:text>
				</xsl:attribute>

				<xsl:attribute name="href">
					<xsl:text><![CDATA[source/]]></xsl:text>
					<xsl:value-of select="Method/@source-url"/>
					<xsl:text><![CDATA[.html#line_]]></xsl:text>
					<xsl:value-of select="Method/@line"/>

			</xsl:attribute>

			<xsl:value-of select="Method/@key" /> 

		</a>
			</td><td class="TD-TRACENODE">    
			<xsl:text><![CDATA[   ]]></xsl:text>
			<xsl:value-of select="Method/@Me" /> 
		</td></tr></table>
				</td>
		</xsl:if>
							   
		<xsl:if test="@type = 'sibling'">
			<td class="TD-SIBLING">    
      				<table><tr><td class="TD-SIBLING">    
				<xsl:value-of select="Method/@Ma" /> 
				<xsl:text><![CDATA[   ]]></xsl:text>
				</td><td class="TD-SIBLING">    
      			<a>

				<xsl:attribute name="alt">
					<xsl:text><![CDATA[lala]]></xsl:text>
				</xsl:attribute>

				<xsl:attribute name="href">
					<xsl:text><![CDATA[source/]]></xsl:text>
					<xsl:value-of select="Method/@source-url"/>
					<xsl:text><![CDATA[.html#line_]]></xsl:text>
					<xsl:value-of select="Method/@line"/>

			</xsl:attribute>

			<xsl:value-of select="Method/@key" /> 

		</a>
			</td><td class="TD-SIBLING">    
			<xsl:text><![CDATA[   ]]></xsl:text>
			<xsl:value-of select="Method/@Me" /> 
		</td></tr></table>

		
		</td>
					
		</xsl:if>
		</tr>
		
		</xsl:for-each>		
	</table>
</xsl:template>
 
 
<xsl:template name="addIndent">
   <xsl:param name="N" />
   <xsl:param name="MAX" />
   <!-- If N is less than or equal to the maximum
    number of entries, then we produce a new table row.-->
   <xsl:if test="$MAX >= $N">
        <td></td>
      
     
     <!-- The recursive call for the next larger
       value of N -->
       
     <xsl:call-template name="addIndent">
       <xsl:with-param name="N">
          <xsl:value-of select="$N + 1"/>
       </xsl:with-param>
           <xsl:with-param name="MAX">
          <xsl:value-of select="$MAX"/>
       </xsl:with-param>
     </xsl:call-template>
   </xsl:if>
</xsl:template>


</xsl:stylesheet>
