/*
 * Created on Oct 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hammurapi.HammurapiException;
import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.InspectorContext;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.AnnotationContext.FileEntry;

import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.sql.Parameterizer;
import com.pavelvlasov.sql.RowProcessor;
import com.pavelvlasov.sql.SQLProcessor;

/**
 * @author 111001082
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SqlExtractorHyperSonicStanaloneServer 
	extends SqlExtractorHyperSonicDb
	implements SqlExtractorPersistencyService {
	
	private static int counter = 0;
	 private Connection c = null;
	/**
	 * Unique table name
	 */
	private String sqlTableName = "SQL_STRING_LITERALS";
	protected String varTableName = "VAR_DEF_LITERALS";
	protected String[] initSQL = {
					"CREATE CACHED TABLE "+sqlTableName+
					" (LITERAL VARCHAR(250), SOURCE VARCHAR(250), LINE INTEGER, COL INTEGER,  CLASS_NAME VARCHAR(250), CLASS_FCN VARCHAR(250))",
					"CREATE INDEX IX_"+sqlTableName+" ON "+sqlTableName+" (LITERAL, SOURCE, LINE, COL)"	,
					
					"CREATE CACHED TABLE "+varTableName+
					" (VAR_NAME VARCHAR(250), VAR_VALUE  VARCHAR(250), SOURCE VARCHAR(250), LINE INTEGER, COL INTEGER, CLASS_NAME VARCHAR(250), CLASS_FCN VARCHAR(250))",
					"CREATE INDEX IX_"+varTableName+" ON "+varTableName+" (VAR_NAME, VAR_VALUE, SOURCE, LINE, COL)"	
		
			};
	protected String[] destroySQL = {
			"CREATE INDEX IX_"+sqlTableName,	
			"DELETE * FROM "+sqlTableName,
			
			"CREATE INDEX IX_"+varTableName,	
			"DELETE *  FROM "+varTableName
	};
	 
	public SqlExtractorHyperSonicStanaloneServer(InspectorContext _context)
	throws HammurapiException {
		super();
		context = _context;
		init();
		}
	
	public void init() throws HammurapiException {
		// super.init();
		
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			// Connection c =
			// DriverManager.getConnection("jdbc:hsqldb:file:D:/anwend/java/hsqldb/data/myjobdb",
			// "sa", "");
			c = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/job",
					"sa", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new HammurapiException(e);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hammurapi.inspectors.metrics.SqlExtractorPersistencyService#saveLanguageElement(org.hammurapi.inspectors.metrics.StringVariable)
	 */
	public void saveLanguageElement(StringVariable strVar) {
        System.out.println("** saveLanguageElement "+ strVar.langElement.toString() );
        
        
    	final boolean[] ret={false};
            try {
                 String templangElementName = "<unresolved>";
                if(  strVar.langElement instanceof VariableDefinition) {
                    templangElementName = ((VariableDefinition) strVar.langElement).getName();
                } else if (  strVar.langElement instanceof Parameter ) {
                    templangElementName = ((Parameter) strVar.langElement).getName();
                } 
                final String langElementName = templangElementName;
               // System.out.println(langElementName + " :- "+ currentStringValue );
                // check for String Only !
                
                //-- is entry already available?
                
                SourceMarker sourceMarker = (SourceMarker) strVar.langElement;
                
					String sqlQuery=
						"SELECT 'variable', SOURCE, LINE, COL,  VAR_NAME, VAR_VALUE , CLASS_NAME, CLASS_FCN FROM "+varTableName
						+ " WHERE VAR_NAME = '" + langElementName +"'" +
								"AND CLASS_NAME = '" + strVar.className +"'" +
								"AND CLASS_FCN  = '" + strVar.classFcn +"'" ;

					Statement stmt = c.createStatement();
					ResultSet rs = stmt.executeQuery(sqlQuery); 
					
			   				    try {
									String tmpStr = rs.getString( "VAR_NAME");
									    ret[0] =  langElementName.equals(tmpStr) ;
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									// e1.printStackTrace();
								}
				
								rs.close();
								stmt.close();
                //-- update entry
                
				stmt = c.createStatement();								
                if( ret[0] ){
                  sqlQuery=
                            "UPDATE " + varTableName
                                    + " SET "
                                    + " VAR_VALUE =  '" + strVar.varValue.toString() +"'" 
            						+ " WHERE VAR_NAME = '" + langElementName +"'" +
    								"AND CLASS_NAME = '" + strVar.className +"'" +
    								"AND CLASS_FCN  = '" +  strVar.classFcn +"'" ;
                	stmt.executeQuery(sqlQuery); 
					stmt.close();
               	
                } else {
                 //   System.out.println (" KKKKKKK INSERT" );
                //-- insert entry
                	sqlQuery=
                "INSERT INTO " + varTableName
                        + " (VAR_NAME, VAR_VALUE, SOURCE, LINE, COL,  CLASS_NAME, CLASS_FCN) "
                        + "VALUES ("
                        + "'" + langElementName +"'," 
                        + "'" + strVar.varValue.toString() +"',"                        		
                        
                        + "'" + sourceMarker.getSourceURL() +"',"						
                        + "'" + sourceMarker.getLine() +"',"						
                        + "'" + sourceMarker.getColumn() +"',"
                        + "'" + strVar.className +"',"	
                        + "'" + strVar.classFcn +"')"	;
                	stmt.executeQuery(sqlQuery);        
					stmt.close();
                	
                } //fi
            } catch (SQLException e) {
                context.warn((SourceMarker) strVar.langElement, e);
            }
        }


	/* (non-Javadoc)
	 * @see org.hammurapi.inspectors.metrics.SqlExtractorPersistencyService#render(org.hammurapi.results.AnnotationContext, java.lang.String)
	 */
	public void render(AnnotationContext context, String path)
			throws HammurapiException {
		try {
			FileEntry a = context.getNextFile(".html");
			path=a.getPath();

			final Writer w=new FileWriter(a.getFile());
			try {
				w.write("<HTML><BODY><TABLE border=\"1\"><TR><TH>Var Name</TH><TH>Var Value</TH><TH>File</TH><TH>Line</TH><TH>Column</TH><TH>Class</TH><TH>FCN</TH></TR>");
				Statement stmt = c.createStatement();
				String sqlString = 
						"SELECT * FROM (" +
						"SELECT 'variable', SOURCE, LINE, COL,  VAR_NAME, VAR_VALUE , CLASS_NAME, CLASS_FCN FROM "+varTableName 
						+ ") ORDER BY SOURCE, LINE, COL";
						ResultSet rs = stmt.executeQuery( sqlString ); 
						
						while( rs.next()){
						try {
									w.write("<TR><TD>");
									// w.write(rs.getString("LITERAL"));
									w.write(rs.getString( 5 ));
									w.write("</TD><TD>");
									
									w.write(rs.getString( 6 )); // VAR_VALUE or LITERAL
									w.write("</TD><TD>");
									// w.write(rs.getString( 1 ));
									// w.write("</TD><TD>");
																		
									w.write(rs.getString("SOURCE"));
									w.write("</TD><TD aligh=\"right\">");
									w.write(rs.getString("LINE"));
									w.write("</TD><TD aligh=\"right\">");
									w.write(rs.getString("COL"));
									w.write("</TD><TD>");
									w.write(rs.getString("CLASS_NAME"));
									w.write("</TD><TD>");
									w.write(rs.getString("CLASS_FCN"));
									
									w.write("</TD><TR>");
									
								} catch (IOException e) {
									throw new HammurapiRuntimeException(e);
								}
						}
							
			
						;	
				w.write("</TABLE></BODY></HTML>");
			} finally {
				w.close();
			}
		} catch (SQLException e) {
			throw new HammurapiException(e);
		} catch (HammurapiRuntimeException e) {
			throw new HammurapiException(e.getCause());
		} catch (IOException e) {
			throw new HammurapiException(e);
		
		} catch (HammurapiException e) {
			throw new HammurapiException(e);
	    }


	}

	public void destroy() {
		try {
		Statement stmt = c.createStatement();
		for (int i=0; i<destroySQL.length; i++) {
			try {
				ResultSet rs = stmt.executeQuery(destroySQL[i]); 
				
			} catch (SQLException e) {
				System.err.println("Could not delete tables");
				e.printStackTrace();
			}
		}		
		} catch (SQLException e) {
			System.err.println("Could not delete tables");
			e.printStackTrace();
		}
		
	}

}
