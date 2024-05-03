package br.ufg.inf.findbugsparser;

import java.sql.*;

/**
 *
 * @author auri
 */
public class ConexaoBD {

    final static String url = "jdbc:postgresql://localhost:5432/conquest";

    public static Connection getConnection(String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
