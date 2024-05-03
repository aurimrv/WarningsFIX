/*
  * Hammurapi
 * Automated Java code review system. 
 * Copyright (C) 2004  Hammurapi Group
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * URL: http://www.hammurapi.org
 * e-Mail: support@hammurapi.biz
 
 */
package org.hammurapi.inspectors.samples;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hammurapi.InspectorBase;
import org.hammurapi.HammurapiException;
import org.hammurapi.HammurapiRuntimeException;
import org.hammurapi.results.AnnotationContext;
import org.hammurapi.results.LinkedAnnotation;
import org.hammurapi.results.AnnotationContext.FileEntry;

import com.pavelvlasov.jsel.Repository;
import com.pavelvlasov.jsel.expressions.StringConstant;
import com.pavelvlasov.review.SourceMarker;
import com.pavelvlasov.sql.Parameterizer;
import com.pavelvlasov.sql.RowProcessor;
import com.pavelvlasov.sql.SQLProcessor;

/**
 * This inspector demonstrates usage of annotations and persistency of inspector findings.
 * It collects all string literals and then outputs them alphabetially sorted
 * as an annotation in Summary.
 * This inspector cleans results from previous run before scan. As such it will report string literals only from modified
 * sources. 
 * @author Pavel Vlasov
 * @version $Revision: 1.4 $
 */
public class CollectStringLiterals extends InspectorBase {
	
	/**
	 * Unique table name
	 */
	private String tableName="STRING_LITERALS";
	
	/**
	 * Method to inject table name from parameters.
	 */
	public void setTableName(String tableName) {
		this.tableName=tableName;
	}
	
	private String[] initSQL= {
			"CREATE CACHED TABLE "+tableName+
			" (LITERAL VARCHAR(250), SOURCE VARCHAR(250), LINE INTEGER, COL INTEGER)",
			"CREATE INDEX IX_"+tableName+" ON "+tableName+" (LITERAL, SOURCE, LINE, COL)"	
	};			
	
//	private String[] destroySQL= {
//			"DROP INDEX IX_"+tableName,	
//			"DROP TABLE "+tableName
//	};
	
	/**
	 * Creating a table to store results
	 */
	public void initDb(SQLProcessor processor, Properties dbProperties) {
		for (int i=0; i<initSQL.length; i++) {
			try {
				processor.processUpdate(initSQL[i], null);
			} catch (SQLException e) {
				throw new HammurapiRuntimeException(e);
			}
		}		
	}
	
	public void init() throws HammurapiException {
		super.init();
		SQLProcessor processor=getContext().getSession().getProcessor();
		try {
			processor.processUpdate("DELETE FROM "+tableName, null);
		} catch (SQLException e) {
			disable("Could not clean "+tableName+": "+e);
		}				
	}
		
	public void visit(final StringConstant constant) {
		SQLProcessor processor=getContext().getSession().getProcessor();
		try {
			processor.processUpdate(
					"INSERT INTO "+tableName+
					" (LITERAL, SOURCE, LINE, COL) " +
					"VALUES (?,?,?,?)",
					new Parameterizer() {
						public void parameterize(PreparedStatement ps) throws SQLException {
							ps.setString(1, constant.getValue());
							SourceMarker sourceMarker = (SourceMarker) constant;
							ps.setString(2, sourceMarker.getSourceURL());
							ps.setInt(3, sourceMarker.getLine());
							ps.setInt(4, sourceMarker.getColumn());
						}
					});			
		} catch (SQLException e) {
			context.warn((SourceMarker) constant, e);
		}
	}
	
	public void leave(Repository repo) {
		final SQLProcessor processor=getContext().getSession().getProcessor();
		if (processor!=null) {
			context.annotate(new LinkedAnnotation() {
				String path;

				public String getPath() {
					return path;
				}

				public String getName() {
					return "String literals";
				}

				public void render(AnnotationContext context) throws HammurapiException {
					FileEntry a = context.getNextFile(".html");
					path=a.getPath();
					try {
						final Writer w=new FileWriter(a.getFile());
						try {
							w.write("<HTML><BODY><TABLE border=\"1\"><TR><TH>Literal</TH><TH>File</TH><TH>Line</TH><TH>Column</TH></TR>");
							processor.processSelect(
									"SELECT * FROM "+tableName+" ORDER BY LITERAL, SOURCE, LINE, COL",
									null,
									new RowProcessor() {
										public boolean process(ResultSet rs) throws SQLException {
											try {
												w.write("<TR><TD>");
												w.write(rs.getString("LITERAL"));
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
					}					
				}

				public Properties getProperties() {
					return null;
				}				
			});
		}
		
	}
}
