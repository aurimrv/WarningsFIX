package br.inf.ufg.es.vv.checkstyle.parser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Henrique Hirako
 */
public class CheckStyleParser {

    public static Connection conn;

    public static Relatorio parse(java.io.File checkStyleResultsFile, String programa, String path) throws SQLException, ClassNotFoundException {
        conn = conectar();
        if (!checkStyleResultsFile.exists()) {
            return null;
        }

        Relatorio relatorio = new Relatorio();
        SAXBuilder builder = new SAXBuilder();

        String sql5 = "select * from ferramenta where nome='checkstyle'";


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
            stat3.setString(1, "CS0004");
            stat3.setString(2, "");
            stat3.setString(3, "java");
            stat3.setString(4, "checkstyle");
            stat3.setString(5, "codigo fonte");
            stat3.setString(6, "");
            stat3.executeUpdate();
        }

        try {
            Document document = builder.build(checkStyleResultsFile);
            Element rootNode = document.getRootElement();

            List filesXml = rootNode.getChildren("file");
            List<File> files = new ArrayList<File>();
            for (int i = 0; i < filesXml.size(); i++) {
                Element fileNode = (Element) filesXml.get(i);
                String name = fileNode.getAttributeValue("name");

                List errorsXml = fileNode.getChildren("error");
                List<Error> errors = new ArrayList<Error>();
                for (int j = 0; j < errorsXml.size(); j++) {
                    Element errorNode = (Element) errorsXml.get(j);
                    String line = errorNode.getAttributeValue("line");
                    String column = errorNode.getAttributeValue("column");
                    String severity = errorNode.getAttributeValue("severity");
                    String message = errorNode.getAttributeValue("message");
                    String source = errorNode.getAttributeValue("source");

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
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, "CS0004");
                    ps.setString(2, programa);
                    String aux = name.replace(path+"/", "");
                    String aux2 = aux.replace("/", ".");
                    String aux3 = aux2.replace(".java", "");
                    ps.setString(3, aux3); 
                    ps.setString(4, "");
                    ps.setInt(5, Integer.parseInt(line));
                    ps.setInt(6, Integer.parseInt(line));
                    
                    
                    
                    
                    
                    if(column==null){
                    ps.setInt(7, -1);
                    ps.setInt(8, -1);
                    }else{
                    ps.setInt(7, Integer.parseInt(column));
                    ps.setInt(8, Integer.parseInt(column));     
                    }
                    
                    ps.setString(9, source);
                    ps.setString(10, message);
                    int num = 0;
                    if (severity.equals("ignore")) {
                        num = 1;

                    }
                    if (severity.equals("info")) {
                        num = 2;

                    }
                    if (severity.equals("warning")) {
                        num = 3;
                    }
                    if (severity.equals("error")) {
                        num = 4;
                    }

                    ps.setInt(11, num);
                    ps.setString(12, "");
                    ps.setString(13, "");
                    ps.executeUpdate();

                    //System.out.println(" " + line + " " + column + " " + severity + " " + message + " " + source);
                    errors.add(new Error(line, column, severity, message, source));

                }

                files.add(new File(name, errors));
            }
            relatorio.setFiles(files);

        } catch (JDOMException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        desconectar(conn);
        return relatorio;
    }
}
