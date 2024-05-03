/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.inf.es.vv.hamurapi.htmlparser;

/**
 *
 * @author klevlon
 */
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class MainHtmlParser {

    public static void main(String[] args) throws IOException {


        File arquivoDeTrabalho;
        final Pattern PADRAO = Pattern.compile("inspector_ER.*");
        Document docJsoup;
        JSONObject MontaString = new JSONObject();
        String tituloAnalise = "";
        String conteudoAnalise = "";
        try {
            File dir = new File(args[0] + "/source");
            System.out.println("diretorio atual é " + dir);
            for (File child : dir.listFiles()) {
                if (child.getName().contains("java")) {
                    arquivoDeTrabalho = child;
                    System.out.println(arquivoDeTrabalho);
                    docJsoup = Jsoup.parse(arquivoDeTrabalho, "UTF-8", args[0]);
                    tituloAnalise = docJsoup.getElementsByTag("a").text();
                    Elements propriedades = docJsoup.getElementsByTag("th");
                    Elements valores = docJsoup.getElementsByTag("td");
                    for (int i = 0; i < propriedades.size(); i++) {
                        MontaString.put(propriedades.get(i).text(), valores.get(i).text());
                    }
                    conteudoAnalise = MontaString.toString();
                    Persistencia.persisteConteudo(tituloAnalise, conteudoAnalise);


                }
            }

        } catch (SQLException ex) {
            System.out.println("erro:" + ex);
        } catch (JSONException ex2) {
            System.out.println("erro:" + ex2);
        }


        /*    //Iterator<File> arquivos= org.apache.commons.io.FileUtils.iterateFiles(input, null, false);
         PADRAO.matcher(((File) arquivos.next()).getName());
         while (arquivos.hasNext()) {
         if (PADRAO.matcher(((File) arquivos.next()).getName()).find()) {
         arquivoDeTrabalho = (File) arquivos.next();
         docJsoup = Jsoup.parse(arquivoDeTrabalho, "UTF-8", "c:/review/");
         //Element content = docJsoup.getElementById("content");
         //String titulo = docJsoup.getElementsByTag("a").text();
         tituloAnalise = docJsoup.getElementsByTag("a").text();
         Elements propriedades = docJsoup.getElementsByTag("th");
         Elements valores = docJsoup.getElementsByTag("td");
         for (int i = 0; i < propriedades.size(); i++) {
         MontaString.put(propriedades.get(i).text(), valores.get(i).text());
         }
         conteudoAnalise = MontaString.toString();
         Persistencia.persisteConteudo(tituloAnalise, conteudoAnalise);

         }
         }
         
         */

        System.out.println("Fim");
    }
}
