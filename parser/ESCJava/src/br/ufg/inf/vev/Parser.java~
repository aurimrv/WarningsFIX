/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.inf.vev;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julliano
 */
public class Parser {

    static Conexao con = new Conexao();
    static ArrayList<String> error = new ArrayList<>();
    static ArrayList<String> fatalError = new ArrayList<>();
    static ArrayList<String> caution = new ArrayList<>();
    static String nomeclasse;
    static String nomeprog;
    static String tipo;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        try {
            FileReader arq = new FileReader(args[0]);
            BufferedReader lerArq = new BufferedReader(arq);
            nomeclasse = args[1];
            nomeprog = args[2];
            tipo="";
            String linha;
            String linha2;

            while ((linha = lerArq.readLine()) != null) {
                //System.out.printf("%s\n", linha);
                   String aux = nomeclasse.replace("source-", "");
                 String aux2 = aux.replace("-", ".");
                String aux3 = aux2.replace(".java", "");
                String aux4 = aux3.replace(".","/");
                
                    if(linha.indexOf("/source/"+aux4)!=-1){
                        //System.out.println();
                    String[] dados = linha.split(":");

 if((dados.length)>3){

 aux=dados[3];
 String[] tipos01 = aux.split("\\(");

if((tipos01.length)>1){
aux2=tipos01[1];
String[] tipos02 = aux2.split("\\)");
Conexao.insert(nomeprog, aux3, dados[2], dados[1],dados[3],tipos02[0]); 
}
else{
Conexao.insert(nomeprog, aux3, dados[2], dados[1],dados[3],"all");
}

            }else{                   
                     }                
               }
         }            
            arq.close();

        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n",
                    e.getMessage());
        }

        //System.out.println();
    }

}
