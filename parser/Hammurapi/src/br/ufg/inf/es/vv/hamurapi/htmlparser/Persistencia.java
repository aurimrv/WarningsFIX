/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.inf.es.vv.hamurapi.htmlparser;

/**
 *
 * @author klevlon
 */

import java.sql.*;

public class Persistencia {
    public static void persisteConteudo(String titulo, String conteudo) throws SQLException{
        String url = "jdbc:postgresql://localhost:5432/conquest";  
        String nome = "postgres";  
        String senha = "postgres";
        Connection conn = null;
        
        try {  
            /** 
             * Carrega o DRIVER na memória ao iniciar o ECLIPSE. 
             */  
            Class.forName("org.postgresql.Driver");  
            conn = DriverManager.getConnection(url, nome, senha);  
  
        } catch (Exception e) {  
            System.out.println("NÃO FOI POSSÍVEL REALIZAR A CONEXÃO !");  
            e.printStackTrace();  
  
        }  
  
        inserir(conn, titulo, conteudo);
    }
    
    private static void inserir(Connection conexao, String iTitulo, String iConteudo) throws SQLException {  
          
        try{ 
            //String guardar = "insert into analise(titulo, conteudo) values ('"+iTitulo+"', '"+iConteudo+"')"; 
            String sql = "insert into ocorrenciawarning_hammurapi(titulo, conteudo) values (?,?)"; 
                            PreparedStatement stat3 = conexao.prepareStatement(sql);
                            stat3.setString(1, iTitulo);
                            stat3.setString(2, iConteudo);
                            stat3.executeUpdate();   
        }catch(SQLException e){  
            System.out.println("Não foi possível gravar os DADOS !"); 
        }finally{
            conexao.close();
        }  
  
    }
}
