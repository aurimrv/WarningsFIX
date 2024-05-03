package br.ufg.inf.es.vv.hamurapi.htmlparser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Conexao {

    public static Connection cn;

    public static void open() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:postgresql://localhost:5432/conquest";
        final String usuario = "postgres";
        final String senha = "postgres";
        Class.forName("org.postgresql.Driver");
        cn = DriverManager.getConnection(url, usuario, senha);

    }

    public static void insert(String prog, String arq, String nome) throws SQLException, ClassNotFoundException {
        open();
       String[] nometmp = nome.split(":");
              String x = nometmp[1].toString();              
       String y = nometmp[2].toString();
        
        try {
            PreparedStatement ps = cn.prepareStatement("insert into ocorrenciawarning_jlint (nomeprograma, nomeclasse, line, descricaolongawarning)values(?,?,?,?)");
            ps.setString(1, prog);
            ps.setString(2, arq);
            ps.setString(3, x);
            ps.setString(4, y);
            ps.execute();
            ps.close();

        } catch (SQLException ex) {
            System.out.println("erro:" + ex);
        }

    }
}
