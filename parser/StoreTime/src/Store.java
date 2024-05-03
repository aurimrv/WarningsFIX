

import java.io.BufferedReader;
import java.sql.SQLException;

public class Store {

    
    BufferedReader in;
    static String nomePrograma;
    static String informacao;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        nomePrograma = args[0];
        informacao = args[1];
        Conexao.insert(nomePrograma, informacao);
    }

   
}