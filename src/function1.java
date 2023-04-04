import java.sql.*;
import java.io.*;

public class function1 {
    public static void initialize(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql = "CREATE TABLE Book ( " +
                    "ISBN VARCHAR2(13) NOT NULL, " +
                    "Title VARCHAR2(100) NOT NULL, " +
                    "Price NUMBER(10,0) NOT NULL, " +
                    "InventoryQuantity NUMBER(10,0) NOT NULL, " +
                    "PRIMARY KEY (ISBN), " +
                    "CHECK (REGEXP_LIKE(ISBN, '^\\d{1}-\\d{4}-\\d{4}-\\d{1}$')), " +
                    "CHECK (Title NOT LIKE '%\\_%' ESCAPE '\\'), " +
                    "CHECK (Title NOT LIKE '%\\%%' ESCAPE '\\'), " +
                    "CHECK (Price >= 0), " +
                    "CHECK (InventoryQuantity >= 0) " +
                    ")";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE Customer ( UID VARCHAR(10) NOT NULL, Name VARCHAR(50) NOT NULL, Address VARCHAR(200) NOT NULL, PRIMARY KEY (UID), CHECK (Name NOT LIKE '%[_%]%' AND Address NOT LIKE '%[_%]%') )";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE Order ( OID VARCHAR(8) NOT NULL, UID VARCHAR(10) NOT NULL, OrderDate DATE, OrderISBN VARCHAR(13), OrderQuantity INT, ShippingStatus VARCHAR(10), PRIMARY KEY (OID), FOREIGN KEY (UID) REFERENCES Customer(UID), FOREIGN KEY (OrderISBN) REFERENCES Book(ISBN) )";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE contain (OID VARCHAR(8), ISBN VARCHAR(13), Quantity INT, FOREIGN KEY (OID) REFERENCES Order(OID), FOREIGN KEY (ISBN) REFERENCES Book(ISBN), PRIMARY KEY (OID, ISBN))";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE make (UID VARCHAR(10), OID VARCHAR(8), FOREIGN KEY (UID) REFERENCES Customer(UID), FOREIGN KEY (OID) REFERENCES Order(OID), PRIMARY KEY (UID, OID))";
            stmt.executeUpdate(sql);
            System.out.println("All the tables are created!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void loadInitData(Connection conn) {
        String booksFile = "books.txt";
        String customersFile = "customers.txt";
        String ordersFile = "orders.txt";
        try {
            insertBooks(conn, booksFile);
            insertCustomers(conn, customersFile);
            insertOrders(conn, ordersFile);
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertBooks(Connection connection, String file) throws Exception {
        String query = "INSERT INTO Book(ISBN, Title, Authors, Price, Inventory_Quantity) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(", ");
            statement.setString(1, data[0]);
            statement.setString(2, data[1]);
            statement.setString(3, data[2]);
            statement.setInt(4, Integer.parseInt(data[3]));
            statement.setInt(5, Integer.parseInt(data[4]));
            statement.executeUpdate();
        }
        statement.close();
        reader.close();
    }

    private static void insertCustomers(Connection connection, String file) throws Exception {
        String query = "INSERT INTO Customer(UID, Name, Address) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(", ");
            statement.setString(1, data[0]);
            statement.setString(2, data[1]);
            statement.setString(3, data[2]);
            statement.executeUpdate();
        }
        statement.close();
        reader.close();
    }

    private static void insertOrders(Connection connection, String file) throws Exception {
        String query = "INSERT INTO Order(OID, UID, Order_Date, Order_ISBN, Order_Quantity, Shipping_Status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(", ");
            statement.setString(1, data[0]);
            statement.setString(2, data[1]);
            statement.setString(3, data[2]);
            statement.setString(4, data[3]);
            statement.setInt(5, Integer.parseInt(data[4]));
            statement.setString(6, data[5]);
            statement.executeUpdate();
        }
        statement.close();
        reader.close();
    }

    public static void dropTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                    "DROP TABLE IF EXISTS Order; DROP TABLE IF EXISTS Book; DROP TABLE IF EXISTS Customer; DROP TABLE IF EXISTS contain; DROP TABLE IF EXISTS make;");
            System.out.println("ALL tables dropped successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void countRecord(Connection conn) {

        try {
            Statement stmt = conn.createStatement();

            // Count the number of records in the Books table
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Book");
            rs.next();
            int numBooks = rs.getInt(1);

            // Count the number of records in the Customers table
            rs = stmt.executeQuery("SELECT COUNT(*) FROM Customer");
            rs.next();
            int numCustomers = rs.getInt(1);

            // Count the number of records in the Orders table
            rs = stmt.executeQuery("SELECT COUNT(*) FROM Order");
            rs.next();
            int numOrders = rs.getInt(1);

            // Print the results to the screen
            System.out.println("+ Database Records: Books (" + numBooks + "), Customers (" + numCustomers
                    + "), Orders (" + numOrders + ")");

            // Close the ResultSet and Statement objects
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

}