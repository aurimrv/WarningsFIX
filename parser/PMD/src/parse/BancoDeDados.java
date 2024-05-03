/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

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

    public static void inserir(ArrayList<String> res) {
        try {
          
            for(int i=0;i<res.size();i++){
                
                
            conn = conectar();
            String sql = "INSERT INTO ocorrenciawarning_pmd "
                    + "(beginline,"
                    + "linhafinal,"
                    + " begincolumn,"
                    + "endcolumn,"
                    + "rules,"
                    + "ruleset,"
                    + " classe,"
                    + "externalinfourl,"
                    + "priority)"
                    + "VALUES (?,?,?,?,?,?,?,?,?)";            
            PreparedStatement pstm = conn.prepareStatement(sql);
 
               
            
            pstm.executeUpdate();

            System.out.println("Registro inserido com sucesso: " + res);
            desconectar(conn);
            
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao inserir registro! || " + e.getMessage());
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Erro ao procurar classe! || " + e.getMessage());
        }
    }

    public static void desconectar(Connection conn) throws SQLException, ClassNotFoundException {
        conn.close();
    }
}
