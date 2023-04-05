import java.sql.*;
import java.io.*;

public class function1 {
    public static void initialize(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE Book_table ( ISBN VARCHAR2(13) PRIMARY KEY, Title VARCHAR2(100) NOT NULL, Authors VARCHAR2(250) NOT NULL, Price NUMBER(10,2) NOT NULL, Inventory_Quantity NUMBER(10) NOT NULL )";
            stmt.executeUpdate(sql);
            System.out.println("Book table created successfully!");

            sql = "CREATE TABLE Customer_table ( \"UID\" VARCHAR2(10) PRIMARY KEY, \"Name\" VARCHAR2(50) NOT NULL, \"Address\" VARCHAR2(200) NOT NULL )";
            stmt.executeUpdate(sql);
            System.out.println("Customer_table table created successfully!");
            sql = "ALTER TABLE Customer_table ADD CONSTRAINT address_components CHECK (\"Address\" LIKE '%\\(%\\)%,%')";
            stmt.executeUpdate(sql);
            System.out.println("Address check constraint added successfully to Customer_table!");

            sql = "CREATE TABLE Order_table ( \"OID\" VARCHAR2(8) PRIMARY KEY, \"UID\" VARCHAR2(10) NOT NULL, \"OrderDate\" VARCHAR2(20) NOT NULL, \"OrderISBN\" VARCHAR2(13) NOT NULL, \"OrderQuantity\" NUMBER(10) NOT NULL, \"ShippingStatus\" VARCHAR2(10) NOT NULL, FOREIGN KEY (\"UID\") REFERENCES Customer_table(\"UID\"), FOREIGN KEY (\"OrderISBN\") REFERENCES Book_table(\"ISBN\") )";
            stmt.executeUpdate(sql);
            System.out.println("Order_table table created successfully!");

            sql = "CREATE TABLE contain (OID VARCHAR2(8), ISBN VARCHAR2(13), Quantity INT, FOREIGN KEY (OID) REFERENCES Order_table(OID), FOREIGN KEY (ISBN) REFERENCES Book_table(ISBN), PRIMARY KEY (OID, ISBN))";
            stmt.executeUpdate(sql);
            System.out.println("contain table created successfully!");

            sql = "CREATE TABLE make (\"UID\" VARCHAR2(10), \"OID\" VARCHAR2(8), FOREIGN KEY (\"UID\") REFERENCES Customer_table(\"UID\"), FOREIGN KEY (\"OID\") REFERENCES Order_table(\"OID\"), PRIMARY KEY (\"UID\", \"OID\"))";
            stmt.executeUpdate(sql);
            System.out.println("make table created successfully!");

        } catch (SQLException e) {
            System.out.println("CREATE TABLE Error: " + e.getMessage());
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
        String query = "INSERT INTO Book_table(ISBN, Title, Authors, Price, Inventory_Quantity) VALUES (?, ?, ?, ?, ?)";
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
        String query = "INSERT INTO Customer_table(UID, Name, Address) VALUES (?, ?, ?)";
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
        String query = "INSERT INTO Order_table(OID, UID, Order_Date, Order_ISBN, Order_Quantity, Shipping_Status) VALUES (?, ?, ?, ?, ?, ?)";
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
            try {
                stmt.executeUpdate("DROP TABLE Book_table");
                System.out.println("Book_table dropped successfully.");
            } catch (SQLException e) {
                System.out.println("Book_table does not exist.");
            }

            try {
                stmt.executeUpdate("DROP TABLE Customer_table");
                System.out.println("Customer_table dropped successfully.");
            } catch (SQLException e) {
                System.out.println("Customer_table does not exist.");
            }

            try {
                stmt.executeUpdate("DROP TABLE Order_table");
                System.out.println("Order_table dropped successfully.");
            } catch (SQLException e) {
                System.out.println("Order_table does not exist.");
            }

            try {
                stmt.executeUpdate("DROP TABLE contain");
                System.out.println("contain table dropped successfully.");
            } catch (SQLException e) {
                System.out.println("contain table does not exist.");
            }

            try {
                stmt.executeUpdate("DROP TABLE make");
                System.out.println("make table dropped successfully.");
            } catch (SQLException e) {
                System.out.println("make table does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void countRecords(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Count the number of records in the Books table
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Book_table")) {
                rs.next();
                int numBooks = rs.getInt(1);
                System.out.println("+ Database Records: Books (" + numBooks + ")");
            }

            // Count the number of records in the Customers table
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Customer_table")) {
                rs.next();
                int numCustomers = rs.getInt(1);
                System.out.println("+ Database Records: Customers (" + numCustomers + ")");
            }

            // Count the number of records in the Orders table
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Order_table")) {
                rs.next();
                int numOrders = rs.getInt(1);
                System.out.println("+ Database Records: Orders (" + numOrders + ")");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

}