import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk",
                    "h042", "KlairkEe");
            System.out.println("Database connection established");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Oracle JDBC driver not found");
            scanner.close();
            return;
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            scanner.close();
            return;
        }

        while (true) {
            System.out.println("===== Welcome to Book Ordering Management System =====");
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = now.format(formatter);
            System.out.println("+ System Date: " + formattedDate);
            function1.countRecords(conn);
            System.out.println("> 1. Database Initialization");
            System.out.println("> 2. Customer Operation (not implemented yet)");
            System.out.println("> 3. Bookstore Operation (not implemented yet)");
            System.out.println("> 4. Quit");
            System.out.println(">>> Please Enter Your Query:");

            try {
                int userInput = scanner.nextInt();
                scanner.nextLine(); // consume the new line character
                switch (userInput) {
                    case 1:
                        System.out.println("Database Initializing");
                        function1.dropTable(conn);
                        function1.initialize(conn);
                        function1.loadInitData(conn);
                        break;
                    case 4:
                        System.out.println("Exiting program...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid input");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer");
                scanner.nextLine(); // consume the invalid input
            }
        }
    }
}
