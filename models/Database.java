package models;
import java.sql.*;
import java.io.*;

public class Database {

    final String[] tableNames = { "Book_table", "Customer_table", "Order_table", "contain", "make" };

    public static void createTable(Connection conn) {

        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE Book_table ("
                    + "ISBN VARCHAR2(13) PRIMARY KEY,"
                    + "Title VARCHAR2(100) NOT NULL,"
                    + "Authors VARCHAR2(50) NOT NULL,"
                    + "Price NUMBER(10,2) NOT NULL,"
                    + "InventoryQuantity NUMBER(10) NOT NULL,"
                    + "CONSTRAINT ISBN_format CHECK (REGEXP_LIKE(ISBN, '^[0-9]-[0-9]{4}-[0-9]{4}-[0-9]$')),"
                    + "CONSTRAINT Title_format CHECK (NOT REGEXP_LIKE(Title, '%|_')),"
                    + "CONSTRAINT Authors_format CHECK (NOT REGEXP_LIKE(Authors, '%|_')))";
            stmt.executeUpdate(sql);
            System.out.println("Book table created successfully!");

            sql = "CREATE TABLE Customer_table ("
                    + "UserID VARCHAR2(10) PRIMARY KEY,"
                    + "Name VARCHAR2(50) NOT NULL,"
                    + "Address VARCHAR2(200) NOT NULL,"
                    + "CONSTRAINT Name_format CHECK (NOT REGEXP_LIKE(Name, '[%_]')),"
                    + "CONSTRAINT Address_format CHECK (NOT REGEXP_LIKE(Address, '[%_]')))";
            // + "CONSTRAINT Address_components CHECK (Address LIKE '%(%),%'))";
            stmt.executeUpdate(sql);
            System.out.println("Customer_table table created successfully!");

            sql = "CREATE TABLE Order_table ("
                    + "OrderID VARCHAR2(8) PRIMARY KEY,"
                    + "UserID VARCHAR2(10) NOT NULL,"
                    + "OrderDate DATE NOT NULL,"
                    + "OrderISBN VARCHAR2(13) NOT NULL,"
                    + "OrderQuantity NUMBER(10) NOT NULL,"
                    + "ShippingStatus VARCHAR2(10) NOT NULL,"
                    + "CONSTRAINT ShippingStatus_format CHECK (ShippingStatus IN ('ordered', 'shipped', 'received')),"
                    + "FOREIGN KEY (UserID) REFERENCES Customer_table(UserID),"
                    + "FOREIGN KEY (OrderISBN) REFERENCES Book_table(ISBN))";
            stmt.executeUpdate(sql);
            System.out.println("Order_table table created successfully!");

        } catch (SQLException e) {
            System.out.println("CREATE TABLE Error: " + e.getMessage());
        }
    }

    public static void loadInitData(Connection conn) {
        String booksFile = "books.txt";
        String customersFile = "customers.txt";
        String ordersFile = "orders.txt";
        try {
            insertBookData(conn, booksFile, "Book_table", "ISBN, Title, Authors, Price, InventoryQuantity");
            insertCustomerData(conn, customersFile, "Customer_table", "UserID, Name, Address");
            insertOrderData(conn, ordersFile, "Order_table",
                    "OrderID, UserID, OrderDate, OrderISBN, OrderQuantity, ShippingStatus");
            System.out.println("All data loaded successfully.");
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static void insertBookData(Connection connection, String fileName, String table, String columns)
            throws Exception {
        String query = "INSERT INTO " + table + "(" + columns + ") VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        BufferedReader reader = null;
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir + "/data/" + fileName);
        try {
            reader = new BufferedReader(new FileReader(file));
            // Read each line of the file and insert the data into the database
            int lineNumber = 0;
            String line = null;
            String[] fields = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                fields = line.split("\\t");
                if (fields.length != 5) {
                    throw new Exception("Invalid data format at line " + lineNumber + " in " + file);
                }
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                try {
                    statement.setInt(4, Integer.parseInt(fields[3]));
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid data format at line " + lineNumber + " in " + file);
                }
                try {
                    statement.setInt(5, Integer.parseInt(fields[4]));
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid data format at line " + lineNumber + " in " + file);
                }
                statement.executeUpdate();
            }
        } finally {
            System.out.println("Book Data loaded successfully.");
            // Close the statement and reader
            if (statement != null) {
                statement.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    private static void insertCustomerData(Connection connection, String fileName, String table, String columns)
            throws Exception {
        String query = "INSERT INTO " + table + "(" + columns + ") VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        BufferedReader reader = null;
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir + "/data/" + fileName);
        try {
            reader = new BufferedReader(new FileReader(file));
            // Read each line of the file and insert the data into the database
            int lineNumber = 0;
            String line = null;
            String[] fields = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                fields = line.split("\\t");
                if (fields.length != 3) {
                    throw new Exception("Invalid data format at line " + lineNumber + " in " + file);
                }
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                statement.executeUpdate();
            }
        } finally {
            // Close the statement and reader
            System.out.println("Customer Data loaded successfully.");
            if (statement != null) {
                statement.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static void insertOrderData(Connection connection, String fileName, String table, String columns)
            throws Exception {
        String query = "INSERT INTO " + table + "(" + columns + ") VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        BufferedReader reader = null;
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir + "/data/" + fileName);
        try {
            reader = new BufferedReader(new FileReader(file));
            // Read each line of the file and insert the data into the database
            int lineNumber = 0;
            String line = null;
            String[] fields = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                fields = line.split("\\t");
                if (fields.length != 6) {
                    throw new Exception("Invalid data format at line " + lineNumber + " in " + file);
                }
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setDate(3, java.sql.Date.valueOf(fields[2]));
                statement.setString(4, fields[3]);

                try {
                    statement.setInt(5, Integer.parseInt(fields[4]));
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid data format at line " + lineNumber + " in " + file);
                }
                statement.setString(6, fields[5]);
                statement.executeUpdate();
            }
        } finally {
            System.out.println("Order data loaded successfully.");
            // Close the statement and reader
            if (statement != null) {
                statement.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }   

    
    public static void dropTable(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            dropTable(conn, stmt, "Order_table");
            dropTable(conn, stmt, "Book_table");
            dropTable(conn, stmt, "Customer_table");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void dropTable(Connection conn, Statement stmt, String tableName) {
        try {
            stmt.executeUpdate("DROP TABLE " + tableName);
            System.out.println(tableName + " dropped successfully.");
        } catch (SQLException e) {
            System.out.println(tableName + " : drop error");
            e.printStackTrace();

        }
    }

    public static void countRecords(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Count the number of records in the Books table
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Book_table")) {
                if (rs.next()) {
                    int numBooks = rs.getInt(1);
                    System.out.println("+ Database Records: Books (" + numBooks + ")");
                }
            }

            // Count the number of records in the Customers table
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Customer_table")) {
                if (rs.next()) {
                    int numCustomers = rs.getInt(1);
                    System.out.println("+ Database Records: Customers (" + numCustomers + ")");
                }
            }

            // Count the number of records in the Orders table
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Order_table")) {
                if (rs.next()) {
                    int numOrders = rs.getInt(1);
                    System.out.println("+ Database Records: Orders (" + numOrders + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public static void showAllTables(Connection conn) {
        String query = "SELECT table_name FROM user_tables";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("All tables in the database:");
            while (rs.next()) {
                System.out.println(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

}
