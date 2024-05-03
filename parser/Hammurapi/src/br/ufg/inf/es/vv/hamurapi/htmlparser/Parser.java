package br.ufg.inf.es.vv.hamurapi.htmlparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;

public class Parser {

    static Conexao con = new Conexao();
    BufferedReader in;
    static String nomePrograma;
    static String nomeClasse;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        nomePrograma = args[1];
        nomeClasse = args[2];
        new Parser().doit(args[0]);
    }

    public void doit(String s) throws SQLException, ClassNotFoundException {
        try {
            in = new BufferedReader(new FileReader(s));
            String str, str2;

            while ((str = in.readLine()) != null) {
                if (str.contains("<B style=\"color:blue\">Violations</B>")) {
                          while ((str2= in.readLine()) !="</table>") {
                   System.out.println(str2);
                }

            }
            in.close();
        }
            }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}