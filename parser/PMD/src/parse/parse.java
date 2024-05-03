/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author ubuntu
 */
public class parse {
public static Connection conn;
    public static NodeList leArquicoXml(String arquivo) {
        NodeList nodes = null;
        try {
            // nome do arquivo xml  
            String xmlFilename = arquivo;
            // Instancia parser DOM do xerces  
            DOMParser parser = new DOMParser();
            // processa o arquivo XML  
            parser.parse(xmlFilename);
            // recupera o documento em forma de objeto DOM  
            Document doc = parser.getDocument();
            // recupera elementos com tag "Nome"   
            nodes = doc.getElementsByTagName("violation");
            insereArquivoxml(nodes);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return nodes;
    }

    public static void insereArquivoxml(NodeList nodes) throws SQLException, ClassNotFoundException {
        ArrayList<String> lista = new ArrayList();
 conn = conectar();
        for (int i = 0; i < nodes.getLength(); i++) {
            // imprime valor do primeiro nó que ele contém  
            for (int j = 0; j < 9; j++) {
                System.out.println(" - " + nodes.item(i).getAttributes().item(j).getTextContent());
                lista.add(nodes.item(i).getAttributes().item(j).getTextContent());
            }
                      
           
            String sql = "INSERT INTO ocorrenciawarning_pmd "
                    + "(begincolumn,"
                    + "beginline,"
                     + " classe,"
                    + "endcolumn, "
                    + "endline,"                    
                    + "externalinfourl,"
                    + "priority,"
                    + "rules,"
                    + "ruleset)" 
                    + "VALUES (?,?,?,?,?,?,?,?,?)";            
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, lista.get(0));
            pstm.setString(2,lista.get(1));
            pstm.setString(3, lista.get(2));
            pstm.setString(4, lista.get(3));
            pstm.setString(5,lista.get(4));
            pstm.setString(6, lista.get(5));
            pstm.setString(7, lista.get(6));
            pstm.setString(8,lista.get(7));
            pstm.setString(9, lista.get(8));
               
            
            pstm.executeUpdate();

            //System.out.println("Registro inserido com sucesso: " + res);
          
            
            
            //BancoDeDados.inserir(lista);
            //lista.clear();

            System.out.println("--------------------------------------------");
            System.out.println("--------------------------------------------");


        }
          desconectar(conn);
    }
}
