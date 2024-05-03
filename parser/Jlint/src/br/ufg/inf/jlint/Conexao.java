package br.ufg.inf.jlint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexao {

    public static Connection cn;

    public static void open() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:postgresql://localhost:5432/conquest";
        final String usuario = "postgres";
        final String senha = "postgres";
        Class.forName("org.postgresql.Driver");
        cn = DriverManager.getConnection(url, usuario, senha);

    }

    public static void insert(String prog, String classe, String nome, String type) throws SQLException, ClassNotFoundException {
        open();
        String[] nometmp = nome.split(":");
        String line = nometmp[1].toString();
        String description = nometmp[2].toString();

        String sql5 = "select * from ferramenta where nome='jlint'";


        Statement stmt = cn.createStatement();
        ResultSet rs3 = stmt.executeQuery(sql5);


        /* Inserindo classe se não existir */

        if (!rs3.next()) {
            String sql6 = "insert into ferramenta "
                    + "(id,"
                    + "descricao, "
                    + "linguagem, "
                    + "nome,"
                    + "tipo,"
                    + "versao) "
                    + "values (?,?,?,?,?,?)";
            PreparedStatement stat3 = cn.prepareStatement(sql6);
            stat3.setString(1, "JL0005");
            stat3.setString(2, "");
            stat3.setString(3, "java");
            stat3.setString(4, "jlint");
            stat3.setString(5, "bytecode");
            stat3.setString(6, "");
            stat3.executeUpdate();
        }

        /* Verificando se a classe existe na tabela */

        String sql3 = "select * from arquivo where nomearquivo='" + classe + "' AND nomeprograma='" + prog + "'";



        ResultSet rs2 = stmt.executeQuery(sql3);



        /* Inserindo classe se não existir */


        if (!rs2.next()) {
            String sql4 = "insert into arquivo "
                    + "(nomeprograma, "
                    + "nomearquivo, "
                    + "versaoprograma, "
                    + "descricaoarquivo, "
                    + "localizacao, "
                    + "fonte) "
                    + "values (?,?,?,?,?,?)";
            PreparedStatement stat2 = cn.prepareStatement(sql4);
            stat2.setString(1, prog);
            stat2.setString(2, classe);
            stat2.setString(3, "");
            stat2.setString(4, "");
            stat2.setString(5, "");
            stat2.setString(6, "");
            stat2.executeUpdate();
        }



        try {
            PreparedStatement ps = cn.prepareStatement("insert into warning"
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
                    + "ruleset ) values (?,?,?,?,?,?,?,?,?,?,?,?,?)");


            ps.setString(1, "JL0005");
            ps.setString(2, prog);
            ps.setString(3, classe);
            ps.setString(4, "");
            ps.setInt(5, Integer.parseInt(line));
            ps.setInt(6, Integer.parseInt(line));
            ps.setInt(7, -1);
            ps.setInt(8, -1);
            ps.setString(9, type);
            ps.setString(10, description);
            ps.setInt(11, 1);
            ps.setString(12, "");
            ps.setString(13, "");
            //System.out.println(prog+" "+classe+" "+Integer.parseInt(line)+" "+type+" "+description);
            ps.execute();
            ps.close();

        } catch (SQLException ex) {
            System.out.println("erro:" + ex);
        }

    }
}
