package br.inf.ufg.es.vv.pmd.parser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Henrique Hirako
 */
public class PMDParser {

    public static Connection conn;

    public static Relatorio parse(java.io.File checkStyleResultsFile, String path, String programa) throws SQLException, ClassNotFoundException {
        conn = conectar();
        if (!checkStyleResultsFile.exists()) {
            return null;
        }

        Relatorio relatorio = new Relatorio();
        SAXBuilder builder = new SAXBuilder();
        String sql5 = "select * from ferramenta where nome='pmd'";


        Statement stmt = conn.createStatement();
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
            PreparedStatement stat3 = conn.prepareStatement(sql6);
            stat3.setString(1, "PM0006");
            stat3.setString(2, "");
            stat3.setString(3, "java");
            stat3.setString(4, "pmd");
            stat3.setString(5, "codigo fonte");
            stat3.setString(6, "");
            stat3.executeUpdate();
        }

        try {
            Document document = builder.build(checkStyleResultsFile);
            Element rootNode = document.getRootElement();

            List filesXml = rootNode.getChildren("file");
            List<Arquivo> files = new ArrayList<Arquivo>();
            for (int i = 0; i < filesXml.size(); i++) {
                Element fileNode = (Element) filesXml.get(i);
                String name = fileNode.getAttributeValue("name");

                List errorsXml = fileNode.getChildren("violation");
                List<Error> errors = new ArrayList<Error>();
                for (int j = 0; j < errorsXml.size(); j++) {
                    Element errorNode = (Element) errorsXml.get(j);
                    String beginline = errorNode.getAttributeValue("beginline");
                    String endline = errorNode.getAttributeValue("endline");
                    String begincolumn = errorNode.getAttributeValue("begincolumn");
                    String endcolumn = errorNode.getAttributeValue("endcolumn");
                    String classe2 = errorNode.getAttributeValue("class");
                    String externalinfourl = errorNode.getAttributeValue("externalInfoUrl");
                    String rules = errorNode.getAttributeValue("rule");
                    String ruleset = errorNode.getAttributeValue("ruleset");
                    String priority = errorNode.getAttributeValue("priority");

                    String sql = "insert into warning"
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
                            + "ruleset ) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    PreparedStatement pstm = conn.prepareStatement(sql);
                    pstm.setString(1, "PM0006");
                    pstm.setString(2, programa);
                    String aux = name.replace(path, "");
                    String aux3 = aux.replace("/", ".");
                    String aux4 = aux3.replace(".java", ""); 
                    pstm.setString(3, aux4);
                    pstm.setString(4, "");
                    pstm.setInt(5, Integer.parseInt(beginline));
                    pstm.setInt(6, Integer.parseInt(endline));
                    pstm.setInt(7, Integer.parseInt(begincolumn));
                    pstm.setInt(8, Integer.parseInt(endcolumn));
                    pstm.setString(9, rules);

                    String aux2 = "" + errorNode.getValue();
                    File arquivo = new File("/home/vr-pc/workspace/WarningsFIX/buffer.txt");
                    FileWriter fw = new FileWriter(arquivo, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(aux2);
                    bw.close();
                    fw.close();
                    //faz a leitura do arquivo


                    try {
                        FileReader fr = new FileReader(arquivo);

                        BufferedReader br = new BufferedReader(fr);

//equanto houver mais linhas
                        String linha;
                        while ((linha = br.readLine())!= null) {//lê a proxima linha                                                       
                             pstm.setString(10, linha);
                          
                        }

                        br.close();
                        fr.close();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    arquivo.delete();                   
                    pstm.setInt(11, Integer.parseInt(priority));
                    pstm.setString(12, externalinfourl);
                    pstm.setString(13, ruleset);
                    pstm.executeUpdate();


                   // System.out.println(" " + beginline + " " + endline + " " + begincolumn + " "
                     //       + endcolumn + " " + classe2 + " " + programa
                       //     + " " + externalinfourl
                         //   + " " + priority
                           // + " " + ruleset
                            //+ " " + rules);
                    errors.add(new Error(beginline, endline, begincolumn, endcolumn, classe2, externalinfourl, priority, ruleset, rules));

                }

                files.add(new Arquivo(name, errors));
            }
            relatorio.setFiles(files);

        } catch (JDOMException e) {
            e.printStackTrace();  //To change body of catch statement use Arquivo | Settings | Arquivo Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Arquivo | Settings | Arquivo Templates.
        }
        desconectar(conn);
        return relatorio;
    }
}
