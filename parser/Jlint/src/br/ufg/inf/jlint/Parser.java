package br.ufg.inf.jlint;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;

public class Parser {

    static Conexao con = new Conexao();
    BufferedReader in;
    static String nomePrograma;
    static String nomeClasse;
    static String nomeType;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        nomePrograma = args[1];
        nomeClasse = args[2];
        nomeType = args[3];
        new Parser().doit(args[0]);
    }

    public void doit(String s) throws SQLException, ClassNotFoundException {
        String aux = nomeClasse.replace("source-", "");
        String aux2 = aux.replace("-", ".");
        String aux3 = aux2.replace(".java", "");
        String aux4 = aux3.replace(".", "/");
        String[] aux5 = aux3.split("$");      
        
        
        

        try {
            in = new BufferedReader(new FileReader(s));
            String str;

            while ((str = in.readLine()) != null) {
                if (str.indexOf(aux4 + ".java") != -1) {
                    Conexao.insert(nomePrograma, aux5[0], str, nomeType);
                }
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
