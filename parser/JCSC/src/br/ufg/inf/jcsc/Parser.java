package br.ufg.inf.jcsc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;

public class Parser {

    static Conexao con = new Conexao();
    BufferedReader in;
    static String nomeCLASSE;
    static String nomePROGRAMA;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        nomeCLASSE = args[1];
        nomePROGRAMA = args[2];
        new Parser().doit(args[0]);
    }

    public void doit(String s) throws SQLException, ClassNotFoundException {
          String aux = nomeCLASSE.replace("source-", "");
          String aux2 = aux.replace("-", ".");
           String aux3 = aux2.replace(".java", "");
          
          
        
        try {
            in = new BufferedReader(new FileReader(s));
            String str;

            while ((str = in.readLine()) != null) {
                if (str.contains(".java")) {
                    Conexao.insert( nomePROGRAMA, aux3, str);
                }

            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}