package br.inf.ufg.es.vv.checkstyle.parser;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
           Relatorio relatorio = CheckStyleParser.parse(new java.io.File(args[0]),args[1],args[2]);
        
        /*
         * Teste para persistencia Decomente para testar.
        Relatorio relatorio = new Relatorio();
        Error erro = new Error("1", "1", "1", "1", "1");
        ArrayList<Error> lista_de_erros = new ArrayList<Error>();
        lista_de_erros.add(erro);
        File file = new File("teste", lista_de_erros);
        ArrayList<File> lista_de_files = new ArrayList<File>();
        lista_de_files.add(file);
        relatorio.setFiles(lista_de_files);
        relatorio.setVersion("Versao de teste");
        */
        
        //GenericDAO<Relatorio> dao = new GenericDAO<Relatorio>(JpaUtil.getInstance()
	//			.getEntityManager(), Relatorio.class);
      //  dao.salva(relatorio);
       // System.out.println("Relatorio salvado com sucesso");
    }
}
