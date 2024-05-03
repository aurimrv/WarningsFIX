/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.inf.es.vv.hamurapi.htmlparser;

/**
 *
 * @author klevlon
 */
import java.sql.*;

public class Persistencia {

    public Connection getConnection() {
        String url = "jdbc:postgresql://localhost:5432/conquest";
        String nome = "postgres";
        String senha = "postgres";
        Connection conn = null;

        try {
            /**
             * Carrega o DRIVER na memória ao iniciar o ECLIPSE.
             */
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, nome, senha);

        } catch (Exception e) {
            System.out.println("NÃO FOI POSSÍVEL REALIZAR A CONEXÃO !");
            e.printStackTrace();

        }

        return conn;

    }

    public void persisteConteudo(String Dados, String Programa, String Classe) throws SQLException {






        //System.out.println(Dados);

        String[] aux = Dados.split(" O_O ");
        //eliminando <a href="#line_25">25</a>
        
        String[] aux2 = aux[1].split(">");
        String[] aux3 = aux2[1].split("</a");
        //eliminando <a href="../../inspectors/inspector_ER-105.html">ER-105</a>
        String[] aux4 = aux[3].split(">");
        String[] aux5 = aux4[1].split("</a");

        inserir(getConnection(), aux3[0], aux[2], aux5[0], aux[4], aux[5], Programa, Classe);
    }

    private static void inserir(Connection conexao,
            String Line, String Column, String TypeWarning, String Severity, String Description, String Programa, String Classe)
            throws SQLException {
        try {
                    String sql = "insert into warning"
                    + "(tool,"
                    + "nameprogram,"
                    + "nameclass,"
                    + "namemethod,"
                    + "beginline,"
                    + "endline,"
                    + "begincolumn,"
                    + "endcolumn,"
                    + "typewarning,"
                    + "description,"
                    + "priority,"
                    + "externalinfourl,"
                    + "ruleset)"
                    + " values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement stat3 = conexao.prepareStatement(sql);
            stat3.setString(1, "HA0001");
            stat3.setString(2, Programa);
            String s = Classe.replace(".java.html", "");
            stat3.setString(3, s);
            stat3.setString(4, "");
            stat3.setInt(5,Integer.parseInt(Line));
            stat3.setInt(6,Integer.parseInt(Line));
            stat3.setInt(7,Integer.parseInt(Column));
            stat3.setInt(8, Integer.parseInt(Column));
            stat3.setString(9, TypeWarning);
            stat3.setString(10, Description);
            int buff = Integer.parseInt(Severity);
            stat3.setInt(11, buff);
            stat3.setString(12, "");
            stat3.setString(13, "");
            stat3.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Não foi possível gravar os DADOS !");
        } finally {
            conexao.close();
        }

    }
}
