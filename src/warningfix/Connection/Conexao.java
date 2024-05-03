package warningfix.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Jean Lucas
 */
public class Conexao {

	public static Connection cn;

	public static void open() throws SQLException, ClassNotFoundException {
		final String url = "jdbc:postgresql://localhost:5432/conquest";
		final String usuario = "postgres";
		final String senha = "postgres";
		Class.forName("org.postgresql.Driver");
		cn = DriverManager.getConnection(url, usuario, senha);
                
                
//                final String url = "jdbc:derby://localhost:1527/conquest";
//		final String usuario = "postgres";
//		final String senha = "postgres";
//		Class.forName("org.apache.derby.jdbc.ClientDriver");
//		cn = DriverManager.getConnection(url, usuario, senha);

	}

}
