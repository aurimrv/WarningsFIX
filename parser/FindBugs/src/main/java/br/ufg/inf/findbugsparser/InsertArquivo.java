package br.ufg.inf.findbugsparser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertArquivo {

    public boolean insertArquivo(Connection con, int args1, String username, String password) throws SQLException {
        boolean resultado = false;

        String nome = null;
        String descricao = null;

        Scanner in = new Scanner(System.in);

        String query = "SELECT codigoArquivo from arquivo_findbugs where codigoArquivo = ?";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, args1);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next() == false) {
            String sql = "insert into arquivo_findbugs (codigoArquivo, nomeArquivo, descricaoArquivo) values (?,?,?)";
            //Statement st = con.createStatement();
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, args1);
            System.out.println("Insira o nome do arquivo");
            nome = in.next();
            st.setString(2, nome);
            System.out.println("Insira a descricao do arquivo");
            descricao = in.next();
            st.setString(3, descricao);

            st.execute();
            st.close();
            resultado = true;
            //System.out.println("1 row affected");
        }
        return resultado;
    }
}
