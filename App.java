import java.sql.*;
import client.*;


public class App {

    public static void main(String[] args) {
        
        // connect to database
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk",
                    "h042", "KlairkEe");
            //System.out.println("Database connection established");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Oracle JDBC driver not found");
            return;
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            return;
        }

        // start client interface
        MainClient client = new MainClient(conn);
        client.clientPage();

        
    }
}
