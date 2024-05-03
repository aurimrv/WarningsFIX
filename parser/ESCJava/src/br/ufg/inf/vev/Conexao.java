package br.ufg.inf.vev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jean Lucas
 */
public class Conexao {

    public static Connection cn;

    public static void open() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:postgresql://localhost:5432/conquest";
        final String usuario = "postgres";
        final String senha = "postgres";
        Class.forName("org.postgresql.Driver");
        cn = DriverManager.getConnection(url, usuario, senha);

    }

    public static void insert(String prog, String arq, String priority, String line, String description, String tipo) throws SQLException, ClassNotFoundException {
        open();


        //System.out.println(" " + prog + " " + arq + " " + priority + " " + line + " " + description);
        /* Verificando se a ferramenta existe na severty */
        String sql5 = "select * from ferramenta where nome='escjava'";


        Statement stmt = cn.createStatement();
        ResultSet rs3 = stmt.executeQuery(sql5);


        /* Inserindo classe se não existir */

        if (!rs3.next()) {
            String sql6 = "insert into ferramenta "
                    + "(id ,"
                    + "descricao, "
                    + "linguagem, "
                    + "nome,"
                    + "tipo,"
                    + "versao) "
                    + "values (?,?,?,?,?,?)";
            PreparedStatement stat3 = cn.prepareStatement(sql6);
            stat3.setString(1, "EJ0007");
            stat3.setString(2, "");
            stat3.setString(3, "java");
            stat3.setString(4, "escjava");
            stat3.setString(5, "codigo fonte");
            stat3.setString(6, "");
            stat3.executeUpdate();
            stat3.close();
        }
rs3.close();
        try {
            String sql3 = "insert into warning"
                    + "(tool, "
                    + "nameprogram, "
                    + "nameclass, "
                    + "namemethod, "
                    + "beginline, "
                    + "endline,"
                    + "begincolumn, "
                    + "endcolumn,"
                    + "typewarning,"
                    + "description,"
                    + "priority,"
                    + "externalinfourl,"
                    + "ruleset ) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = cn.prepareStatement(sql3);
            ps.setString(1, "EJ0007");
            ps.setString(2, prog);
            ps.setString(3, arq);
            ps.setString(4, "");
            ps.setInt(5, Integer.parseInt(line));
            ps.setInt(6, Integer.parseInt(line));
            ps.setInt(7, -1);
            ps.setInt(8, -1);
            ps.setString(9, tipo);
            ps.setString(10, description);
            int prioridade = 1;

            if (priority.indexOf("Caution")!=-1) {
                prioridade = 2;
            }
            
            if (priority.indexOf("Error")!=-1) {
                prioridade = 3;
            }

            if (priority.indexOf("FatalError")!=-1) {
                prioridade = 4;
            }

            ps.setInt(11, prioridade);
            ps.setString(12, "");
            ps.setString(13, "");
            ps.execute();
            ps.close();

        } catch (SQLException ex) {
            System.out.println("erro:" + ex);
        }

cn.close();
    }
}
