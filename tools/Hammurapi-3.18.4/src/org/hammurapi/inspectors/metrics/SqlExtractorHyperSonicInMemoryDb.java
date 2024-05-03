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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hammurapi.HammurapiException;
import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.InspectorContext;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.AnnotationContext.FileEntry;

import com.pavelvlasov.jsel.Parameter;
import com.pavelvlasov.jsel.VariableDefinition;
import com.pavelvlasov.persistence.CompositeStorage;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.sql.JdbcStorage;
import com.pavelvlasov.sql.Parameterizer;
import com.pavelvlasov.sql.RowProcessor;
import com.pavelvlasov.sql.SQLProcessor;

/**
 * @author 111001082
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SqlExtractorHyperSonicInMemoryDb 
			extends SqlExtractorHyperSonicDb
			implements SqlExtractorPersistencyService {
	
	private static int counter = 0;
	/**
	 * Unique table name
	 */
	private String sqlTableName = "SQL_STRING_LITERALS_"+Long.toString(System.currentTimeMillis(),32)+"_"+ ++counter;
	protected String varTableName = "VAR_DEF_LITERALS_"+Long.toString(System.currentTimeMillis(),32)+"_"+ ++counter;
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
					"DROP TABLE "+sqlTableName,
					
					"CREATE INDEX IX_"+varTableName,	
					"DROP TABLE "+varTableName
			};
	
	public SqlExtractorHyperSonicInMemoryDb(InspectorContext _context)
	throws HammurapiException {
	super();
	context = _context;
	init();
}

	public void init() throws HammurapiException {
		// super.init();
		SQLProcessor processor=getProcessor(null);
		if (processor==null) {
			throw new HammurapiException("Could not obtain SQLProcessor");
		}
		
		for (int i=0; i<initSQL.length; i++) {
			try {
				System.out.println(" init " + initSQL[i]);
				processor.processUpdate(initSQL[i], null);
			} catch (SQLException e) {
				throw new HammurapiException(e);
			}
		}		
	}

	public void saveLanguageElement(final StringVariable strVar ) {
        System.out.println("** saveLanguageElement "+ strVar.langElement.toString() );
        
        
    	final boolean[] ret={false};
        SQLProcessor processor = getProcessor((SourceMarker) strVar.langElement);
        if (processor != null) {
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
                processor.processSelect(
						"SELECT 'variable', SOURCE, LINE, COL,  VAR_NAME, VAR_VALUE , CLASS_NAME, CLASS_FCN FROM "+varTableName
						+ " WHERE VAR_NAME = ? " +
								"AND CLASS_NAME = ?" +
								"AND CLASS_FCN  = ?",
								(new Parameterizer() {
				                    public void parameterize(PreparedStatement ps)
				                            throws SQLException {
				                        ps.setString(1, langElementName);
				                        ps.setString(2, strVar.className);
				                        ps.setString(3, strVar.classFcn);
				                    }}),
				            new RowProcessor() {
							public boolean process(ResultSet rs) throws SQLException {
								try {
								    String tmpStr = rs.getString( "VAR_NAME");
								    // System.out.println (" KKKKKKK " + tmpStr + " --"+ currentStringValue );
								    
								    ret[0] =  langElementName.equals(tmpStr) ;
								    return true;
								} catch (Exception e) {
									throw new HammurapiRuntimeException(e);
								}
							}
						});	
				
                
                //-- update entry
                
                if( ret[0] ){
                    // System.out.println (" KKKKKKK UPDATE" );
                    processor.processUpdate(

                            "UPDATE " + varTableName
                                    + " SET "
                                    + " VAR_VALUE =  ?"
            						+ " WHERE VAR_NAME = ? " +
    								"AND CLASS_NAME = ?" +
    								"AND CLASS_FCN  = ?",
                                    
                                     new Parameterizer() {
                                public void parameterize(PreparedStatement ps)
                                        throws SQLException {
                                    ps.setString(1, strVar.varValue.toString());
			                        ps.setString(2, langElementName);
			                        ps.setString(3, strVar.className);
			                        ps.setString(4, strVar.classFcn);
                                }
                            });

                } else {
                 //   System.out.println (" KKKKKKK INSERT" );
                //-- insert entry
                processor.processUpdate(

                "INSERT INTO " + varTableName
                        + " (VAR_NAME, VAR_VALUE, SOURCE, LINE, COL,  CLASS_NAME, CLASS_FCN) "
                        + "VALUES (?,?,?,?,?,?,?)", new Parameterizer() {
                    public void parameterize(PreparedStatement ps)
                            throws SQLException {
                        ps.setString(1, langElementName);
                        ps.setString(2, strVar.varValue.toString());
                        SourceMarker sourceMarker = (SourceMarker) strVar.langElement;
                        ps.setString(3, sourceMarker.getSourceURL());
                        ps.setInt(4, sourceMarker.getLine());
                        ps.setInt(5, sourceMarker.getColumn());
                        ps.setString(6, strVar.className);
                        ps.setString(7, strVar.classFcn);
                    }
                });
                } //fi
            } catch (SQLException e) {
                context.warn((SourceMarker) strVar.langElement, e);
            }
        }

    }

    public void render(AnnotationContext context, String path) throws HammurapiException{
    	final SQLProcessor processor=getProcessor(null);
    	
	try {
		FileEntry a = context.getNextFile(".html");
		path=a.getPath();

		final Writer w=new FileWriter(a.getFile());
		try {
			w.write("<HTML><BODY><TABLE border=\"1\"><TR><TH>Var Name</TH><TH>Var Value</TH><TH>File</TH><TH>Line</TH><TH>Column</TH><TH>Class</TH><TH>FCN</TH></TR>");
			processor.processSelect(
					"SELECT * FROM (" +
				//	"SELECT 'constant', SOURCE, LINE, COL, '<->',     LITERAL,    CLASS_NAME, CLASS_FCN FROM "+sqlTableName
				//	+" UNION " +
					"SELECT 'variable', SOURCE, LINE, COL,  VAR_NAME, VAR_VALUE , CLASS_NAME, CLASS_FCN FROM "+varTableName
					+ ") ORDER BY SOURCE, LINE, COL",
					null,
					new RowProcessor() {
						public boolean process(ResultSet rs) throws SQLException {
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
								return true;
							} catch (IOException e) {
								throw new HammurapiRuntimeException(e);
							}
						}
					});	
			/*
			processor.processSelect(
					"SELECT * FROM "+varTableName+" ORDER BY SOURCE, LINE, COL, VAR_VALUE, VAR_NAME",
					null,
					new RowProcessor() {
						public boolean process(ResultSet rs) throws SQLException {
							try {
								w.write("<TR><TD>");
								w.write(rs.getString("VAR_VALUE"));
								w.write("   :- ");
								w.write(rs.getString("VAR_NAME"));
								w.write("</TD><TD>");
								w.write(rs.getString("SOURCE"));
								w.write("</TD><TD aligh=\"right\">");
								w.write(rs.getString("LINE"));
								w.write("</TD><TD aligh=\"right\">");
								w.write(rs.getString("COL"));
								w.write("</TD><TR>");
								return true;
							} catch (IOException e) {
								throw new HammurapiRuntimeException(e);
							}
						}
					});						
				*/
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
	SQLProcessor processor=getProcessor(null);
	if (processor==null) {
		System.err.println("Could not obtain SQLProcessor");
	}
	
	for (int i=0; i<destroySQL.length; i++) {
		try {
			processor.processUpdate(initSQL[i], null);
		} catch (SQLException e) {
			System.err.println("Could not delete tables");
			e.printStackTrace();
		}
	}		
	}
}
