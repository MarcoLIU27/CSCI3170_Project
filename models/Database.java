package models;

import java.sql.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Database {

    public static void createTable(Connection conn) {

        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE Book_table ("
                    + "ISBN VARCHAR2(13) PRIMARY KEY,"
                    + "Title VARCHAR2(100) NOT NULL,"
                    + "Authors VARCHAR2(50) NOT NULL,"
                    + "Price NUMBER(10) NOT NULL CHECK (Price >= 0),"
                    + "Inventory_Quantity NUMBER(10) NOT NULL CHECK (Inventory_Quantity >= 0),"
                    + "CONSTRAINT ISBN_format CHECK (REGEXP_LIKE(ISBN, '^[0-9]-[0-9]{4}-[0-9]{4}-[0-9]$')),"
                    + "CONSTRAINT Title_format CHECK (NOT REGEXP_LIKE(Title, '%|_')),"
                    + "CONSTRAINT Authors_format CHECK (NOT REGEXP_LIKE(Authors, '%|_')))";
            stmt.executeUpdate(sql);
            //System.out.println("Book table created successfully!");
            //printTableColumns(conn, "Book_table");

            sql = "CREATE TABLE Customer_table ("
                    + "\"UID\" VARCHAR2(10) PRIMARY KEY,"
                    + "\"Name\" VARCHAR2(50) NOT NULL,"
                    + "\"Address\" VARCHAR2(200) NOT NULL,"
                    + "CONSTRAINT Name_format CHECK (NOT REGEXP_LIKE(\"Name\", '[%_]')),"
                    + "CONSTRAINT Address_format CHECK (NOT REGEXP_LIKE(\"Address\", '[%_]')))";
            stmt.executeUpdate(sql);
            //System.out.println("Customer_table table created successfully!");
            //printTableColumns(conn, "Customer_table");

            sql = "CREATE TABLE Order_table ("
                    + "\"OID\" VARCHAR2(8) NOT NULL CHECK (REGEXP_LIKE(\"OID\", '^[0-9]{8}$')),"
                    + "\"UID\" VARCHAR2(10) NOT NULL,"
                    + "\"OrderDate\" DATE NOT NULL,"
                    + "\"OrderISBN\" VARCHAR2(13) NOT NULL,"
                    + "\"OrderQuantity\" NUMBER(10) NOT NULL CHECK (\"OrderQuantity\" >= 0),"
                    + "\"ShippingStatus\" VARCHAR2(10) NOT NULL,"
                    + "CONSTRAINT ShippingStatus_format CHECK (\"ShippingStatus\" IN ('ordered', 'shipped', 'received')),"
                    + "CONSTRAINT Order_table_fk_1 FOREIGN KEY (\"UID\") REFERENCES Customer_table(\"UID\"),"
                    + "CONSTRAINT Order_table_fk_2 FOREIGN KEY (\"OrderISBN\") REFERENCES Book_table(\"ISBN\")"
                    + ")";
            stmt.executeUpdate(sql);
            //System.out.println("Order_table table created successfully!");
            //printTableColumns(conn, "Order_table");

        } catch (SQLException e) {
            System.out.println("CREATE TABLE Error: " + e.getMessage());
        } finally {
            System.out.println("Table created successfully!");
        }
    }

    public static void loadInitData(Connection conn) {
        String booksFile = "books.txt";
        String customersFile = "customers.txt";
        String ordersFile = "orders.txt";
        try {
            insertBookData(conn, booksFile, "Book_table", "ISBN, Title, Authors, Price, Inventory_Quantity");
            insertCustomerData(conn, customersFile, "Customer_table", "\"UID\", \"Name\", \"Address\"");
            insertOrderData(conn, ordersFile, "Order_table",
                    "\"OID\", \"UID\", \"OrderDate\", \"OrderISBN\", \"OrderQuantity\", \"ShippingStatus\"");
            System.out.println("Data files loaded successfully!");
        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertBookData(Connection connection, String fileName, String table, String columns)
            throws Exception {
        String sql = "INSERT INTO " + table + "(" + columns + ") VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        BufferedReader reader = null;
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir + "/data/" + fileName);
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\t");
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                statement.setInt(4, Integer.parseInt(fields[3]));
                statement.setInt(5, Integer.parseInt(fields[4]));
                statement.executeUpdate();
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } finally {
            //System.out.println("Book data loaded successfully.");
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
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
            //System.out.println("Customer Data loaded successfully.");
            if (statement != null) {
                statement.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static void insertOrderData(Connection conn, String fileName, String tableName, String columnNames)
            throws SQLException, ParseException {
        String sql = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        BufferedReader reader = null;
        String curDir = System.getProperty("user.dir");
        File file = new File(curDir + "/data/" + fileName);
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\t");
                if (fields.length != 6) {
                    throw new IllegalArgumentException("Invalid record: " + line);
                }

                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = null;
                try {
                    date = dateFormat.parse(fields[2]);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid date: " + fields[2]);
                }
                statement.setDate(3, new java.sql.Date(date.getTime()));
                statement.setString(4, fields[3]);
                try {
                    statement.setInt(5, Integer.parseInt(fields[4]));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid quantity: " + fields[4]);
                }
                statement.setString(6, fields[5]);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error inserting record: " + e.getMessage());
        } finally {
            //System.out.println("Order Data loaded successfully.");
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("Error closing statement: " + e.getMessage());
                }
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
        } finally {
            System.out.println("Table droped successfully!");
        }
    }

    private static void dropTable(Connection conn, Statement stmt, String tableName) {
        try {
            stmt.executeUpdate("DROP TABLE " + tableName);
            //System.out.println(tableName + " dropped successfully.");
        } catch (SQLException e) {
            System.out.println(tableName + " : drop error");
            e.printStackTrace();

        }
    }

    public static void countRecords(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Book_table")) {
                if (rs.next()) {
                    int numBooks = rs.getInt(1);
                    System.out.print("+ Database Records: Books (" + numBooks + "), ");
                }
            }

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Customer_table")) {
                if (rs.next()) {
                    int numCustomers = rs.getInt(1);
                    System.out.print("Customers (" + numCustomers + "), ");
                }
            }

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Order_table")) {
                if (rs.next()) {
                    int numOrders = rs.getInt(1);
                    System.out.println("Orders (" + numOrders + ")");
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
            while (rs.next()) {
                System.out.println(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public static void printTableColumns(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM " + tableName;
            try (ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("All Columns in " + tableName + ":");
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1)
                        System.out.print(",  ");
                    String columnName = rsmd.getColumnName(i);
                    System.out.print(columnName);
                }
                System.out.println("");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        }
    }

    public static void updateOrderStatus(Connection conn) {
    }

    public static void queryOrderByStatus(Connection conn) {
    }

    public static void queryMostPopularBook(Connection conn) {
    }
}
