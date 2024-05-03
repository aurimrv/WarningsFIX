/*
 * Created on Oct 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.hammurapi.inspectors.metrics;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author 111001082
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HsqlTest {

    public static void main(String[] args) {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            // Connection c = DriverManager.getConnection("jdbc:hsqldb:file:D:/anwend/java/hsqldb/data/myjobdb", "sa", "");
            Connection c = DriverManager.getConnection(
                    "jdbc:hsqldb:hsql://localhost/job", "sa", "");
            Statement stmt = c.createStatement();

            String sqlQuery = "CREATE CACHED TABLE SQL_STRING_LITERALS (LITERAL VARCHAR(250), SOURCE VARCHAR(250), LINE INTEGER, COL INTEGER,  CLASS_NAME VARCHAR(250), CLASS_FCN VARCHAR(250));";
            ResultSet rs = stmt.executeQuery(sqlQuery);
            sqlQuery = "CREATE INDEX IX_SQL_STRING_LITERALS ON SQL_STRING_LITERALS (LITERAL, SOURCE, LINE, COL);";
             rs = stmt.executeQuery(sqlQuery);
            sqlQuery = "CREATE CACHED TABLE VAR_DEF_LITERALS (VAR_NAME VARCHAR(250), VAR_VALUE  VARCHAR(250), SOURCE VARCHAR(250), LINE INTEGER, COL INTEGER, CLASS_NAME VARCHAR(250), CLASS_FCN VARCHAR(250));";
             rs = stmt.executeQuery(sqlQuery);
            sqlQuery = "CREATE INDEX IX_VAR_DEF_LITERALS ON VAR_DEF_LITERALS (VAR_NAME, VAR_VALUE, SOURCE, LINE, COL);";
             rs = stmt.executeQuery(sqlQuery);

             /*
             String sqlQuery =    "CREATE TABLE cdShop (cdNr INTEGER, cdArtist CHAR(20),cdTitle CHAR(20));"; 
             ResultSet rs = stmt.executeQuery(sqlQuery); 

             sqlQuery = "INSERT INTO cdShop VALUES (1,'Groeni','Mensch')"; 
             rs = stmt.executeQuery(sqlQuery); 
             sqlQuery = "INSERT INTO cdShop VALUES (2,'Sting','Fields of Gold')"; 
             rs = stmt.executeQuery(sqlQuery); 
             sqlQuery = "INSERT INTO cdShop VALUES (3,'Bach','Pluratorium')"; 
             rs = stmt.executeQuery(sqlQuery); 

             sqlQuery = "UPDATE cdShop SET cdTitle='W-Oratorium' WHERE cdNr=1"; 
             rs = stmt.executeQuery(sqlQuery); 

             sqlQuery = "SELECT * FROM cdShop"; 
             rs = stmt.executeQuery(sqlQuery); 
             System.out.println("\n\n\nCD Shop DB\n=============\n"); 
             int counter = 0; 
             while (rs.next()) { 
             System.out.println(counter++ +".Datensatz:"); 
             int cdNr = rs.getInt("cdNr"); 
             System.out.println("\t[cdNr ->" + cdNr + "]"); 
             String cdArtist = rs.getString("cdArtist"); 
             System.out.println("\t[cdArtist ->" + cdArtist + "]"); 
             String cdTitle = rs.getString("cdTitle"); 
             System.out.println("\t[cdTitle ->" + cdTitle + "]\n");
             
             sqlQuery =             "DROP TABLE cdShop ;"; 
             rs = stmt.executeQuery(sqlQuery); 
             }
             */
            c.close();

            System.out.println("Done ");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
