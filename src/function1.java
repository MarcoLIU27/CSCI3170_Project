import java.sql.*;
import java.io.*;

public class function1 {

    final String[] tableNames = { "Book_table", "Customer_table", "Order_table", "contain", "make" };

    public static void initialize(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE Book_table ("
                    + "ISBN VARCHAR2(13) PRIMARY KEY,"
                    + "Title VARCHAR2(100) NOT NULL,"
                    + "Authors VARCHAR2(50) NOT NULL,"
                    + "Price NUMBER(10,2) NOT NULL,"
                    + "Inventory_Quantity NUMBER(10) NOT NULL,"
                    + "CONSTRAINT ISBN_format CHECK (REGEXP_LIKE(ISBN, '^[0-9]-[0-9]{4}-[0-9]{4}-[0-9]$')),"
                    + "CONSTRAINT Title_format CHECK (NOT REGEXP_LIKE(Title, '%|_')),"
                    + "CONSTRAINT Authors_format CHECK (NOT REGEXP_LIKE(Authors, '%|_')))";
            stmt.executeUpdate(sql);
            System.out.println("Book table created successfully!");

            sql = "CREATE TABLE Customer_table ("
                    + "\"UID\" VARCHAR2(10) PRIMARY KEY,"
                    + "\"Name\" VARCHAR2(50) NOT NULL,"
                    + "\"Address\" VARCHAR2(200) NOT NULL,"
                    + "CONSTRAINT Name_format CHECK (NOT REGEXP_LIKE(\"Name\", '[%_]')),"
                    + "CONSTRAINT Address_format CHECK (NOT REGEXP_LIKE(\"Address\", '[%_]')),"
                    + "CONSTRAINT Address_components CHECK (\"Address\" LIKE '%(%)%,%'))";
            stmt.executeUpdate(sql);
            System.out.println("Customer_table table created successfully!");

            sql = "CREATE TABLE Order_table ("
                    + "\"OID\" VARCHAR2(8) PRIMARY KEY,"
                    + "\"UID\" VARCHAR2(10) NOT NULL,"
                    + "\"OrderDate\" DATE NOT NULL,"
                    + "\"OrderISBN\" VARCHAR2(13) NOT NULL,"
                    + "\"OrderQuantity\" NUMBER(10) NOT NULL,"
                    + "\"ShippingStatus\" VARCHAR2(10) NOT NULL,"
                    + "FOREIGN KEY (\"UID\") REFERENCES Customer_table(\"UID\"),"
                    + "FOREIGN KEY (\"OrderISBN\") REFERENCES Book_table(\"ISBN\"),"
                    + "CONSTRAINT ShippingStatus_format CHECK (\"ShippingStatus\" IN ('ordered', 'shipped', 'received')))";
            stmt.executeUpdate(sql);
            System.out.println("Order_table table created successfully!");

            sql = "CREATE TABLE contain ("
                    + "OID VARCHAR2(8),"
                    + "ISBN VARCHAR2(13),"
                    + "Quantity INT,"
                    + "FOREIGN KEY (OID) REFERENCES Order_table(OID),"
                    + "FOREIGN KEY (ISBN) REFERENCES Book_table(ISBN),"
                    + "PRIMARY KEY (OID, ISBN))";
            stmt.executeUpdate(sql);
            System.out.println("contain table created successfully!");

            sql = "CREATE TABLE make ("
                    + "\"UID\" VARCHAR2(10),"
                    + "\"OID\" VARCHAR2(8),"
                    + "FOREIGN KEY (\"UID\") REFERENCES Customer_table(\"UID\"),"
                    + "FOREIGN KEY (\"OID\") REFERENCES Order_table(\"OID\"),"
                    + "PRIMARY KEY (\"UID\", \"OID\"))";
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
            insertData(conn, booksFile, "Book_table", "ISBN, Title, Authors, Price, Inventory_Quantity");
            insertData(conn, customersFile, "Customer_table", "UID, Name, Address");
            insertData(conn, ordersFile, "Order_table",
                    "OID, UID, Order_Date, Order_ISBN, Order_Quantity, Shipping_Status");
            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static void insertData(Connection connection, String file, String table, String columns) throws Exception {
        String query = "INSERT INTO " + table + "(" + columns + ") VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            int lineNumber = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] fields = line.split(",");
                if (fields.length != 5) {
                    throw new Exception("Invalid data format at line " + lineNumber + " in " + file);
                }
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                statement.setInt(4, Integer.parseInt(fields[3]));
                statement.setInt(5, Integer.parseInt(fields[4]));
                statement.executeUpdate();
            }
        } finally {
            statement.close();
            reader.close();
        }
    }

    public static void dropTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try {
                stmt.executeUpdate("DROP TABLE Book_table");
                System.out.println("Book_table dropped successfully.");
            } catch (SQLException e) {
                System.out.println("Book table does not exist.");
            }

            try {
                stmt.executeUpdate("DROP TABLE Customer_table");
                System.out.println("Customer_table dropped successfully.");
            } catch (SQLException e) {
                System.out.println("Customer table does not exist.");
            }

            try {
                stmt.executeUpdate("DROP TABLE Order_table");
                System.out.println("Order_table dropped successfully.");
            } catch (SQLException e) {
                System.out.println("Order table does not exist.");
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