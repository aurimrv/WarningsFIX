

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Conexao {

    public static Connection cn;

    public static void open() throws SQLException, ClassNotFoundException {
        final String url = "jdbc:postgresql://localhost:5432/conquest";
        final String usuario = "postgres";
        final String senha = "postgres";
        Class.forName("org.postgresql.Driver");
        cn = DriverManager.getConnection(url, usuario, senha);

    }

    public static void insert(String prog, String info) throws SQLException, ClassNotFoundException {
        open();
       
        try {
            PreparedStatement ps = cn.prepareStatement("insert into runtime"
                    + "(nameprogram, "
                    + "info) values (?,?)");


            ps.setString(1, prog);
            ps.setString(2, info);        
            ps.execute();
            ps.close();

        } catch (SQLException ex) {
            System.out.println("erro:" + ex);
        }

    }
}
