/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufg.es.vv.checkstyle.parser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ubuntu
 */
public class BancoDeDados {

    final static String url = "jdbc:postgresql://localhost:5432/conquest";
    final static String usuario = "postgres";
    final static String senha = "postgres";
    public static Connection conn;

    public static Connection conectar() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url, usuario, senha);
        return conn;
    }

    public static void desconectar(Connection conn) throws SQLException, ClassNotFoundException {
        conn.close();
    }
}
