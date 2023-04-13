package models;

import java.sql.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.InputMismatchException;

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
            System.out.println("Book table created successfully!");
            // printTableColumns(conn, "Book_table");

            sql = "CREATE TABLE Customer_table ("
                    + "\"UID\" VARCHAR2(10) PRIMARY KEY,"
                    + "\"Name\" VARCHAR2(50) NOT NULL,"
                    + "\"Address\" VARCHAR2(200) NOT NULL,"
                    + "CONSTRAINT Name_format CHECK (NOT REGEXP_LIKE(\"Name\", '[%_]')),"
                    + "CONSTRAINT Address_format CHECK (NOT REGEXP_LIKE(\"Address\", '[%_]')))";
            stmt.executeUpdate(sql);
            System.out.println("Customer_table table created successfully!");
            // printTableColumns(conn, "Customer_table");

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
            System.out.println("Order_table table created successfully!");
            // printTableColumns(conn, "Order_table");
            System.out.println("All tables are created");
        } catch (SQLException e) {
            System.out.println("CREATE TABLE Error: Tables are already exist");
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
            System.err.println("there is an error in loading data files");
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
            // System.out.println("Book data loaded successfully.");
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
            // System.out.println("Customer Data loaded successfully.");
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
            // System.out.println("Order Data loaded successfully.");
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
            System.out.println("All tables are droped");
        } catch (SQLException e) {
            System.out.println("Error dropping table: tables do not exist");
        }

    }

    private static void dropTable(Connection conn, Statement stmt, String tableName) {
        try {
            stmt.executeUpdate("DROP TABLE " + tableName);
            System.out.println(tableName + " dropped successfully.");
        } catch (SQLException e) {
            System.out.println(tableName + ": table does not exist");
            // e.printStackTrace();

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

    public static void updateOrderStatus(Connection conn, String orderID) {
        try {
            // Check if the order exists
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Order_table WHERE \"OID\" = ?");
            pstmt.setString(1, orderID);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Update failed: Order not found.\n");
                return;
            }
            // Check the shipping status
            String shippingStatus = rs.getString("\"ShippingStatus\"");
            if (shippingStatus.equals("shipped") || shippingStatus.equals("received")) {
                System.out.println("Error: The order has already been shipped or received.\n");
                return;
            }
            // Update the shipping status to "shipped"
            String sql = "UPDATE Order_table SET \"ShippingStatus\" = 'shipped' WHERE \"OID\" = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, orderID);
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 1) {
                System.out.println("Order updated successfully.\n");
            } else {
                System.out.println("Error: Failed to update the order.\n");
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter a valid order ID\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void queryOrderByStatus(Connection conn, String shippingStatus) {
        try {
            if (shippingStatus.equals("shipped") || shippingStatus.equals("received") ||
                    shippingStatus.equals("ordered")) {
                // Query the orders by shipping status
                String sql = "SELECT * FROM Order_table WHERE \"ShippingStatus\" = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.setString(1, shippingStatus);
                ResultSet resultSet = statement.executeQuery();

                // Display the results in a well-formatted output
                System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s\n", "\"OID\"", "\"UID\"", "\"OrderDate\"",
                        "\"OrderISBN\"", "\"OrderQuantity\"", "\"ShippingStatus\"");
                while (resultSet.next()) {
                    String orderID = resultSet.getString("OID");
                    String userID = resultSet.getString("UID");
                    Date orderDate = resultSet.getDate("OrderDate");
                    String orderISBN = resultSet.getString("OrderISBN");
                    int orderQuantity = resultSet.getInt("OrderQuantity");
                    String orderStatus = resultSet.getString("ShippingStatus");
                    System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s\n", orderID, userID, orderDate, orderISBN,
                            orderQuantity, orderStatus);
                }
                System.out.println();
            } else {
                System.out.println("Error: Please enter a valid shipping status!\n");
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void queryMostPopularBooks(Connection conn, int n) {
        try {
            // Query the N most popular books
            String sql = "SELECT Book_table.ISBN, Title, Authors, Price, Inventory_Quantity, " +
                    "SUM(Order_table.\"OrderQuantity\") as Total_Quantity_Sold " +
                    "FROM Book_table " +
                    "INNER JOIN Order_table ON Book_table.ISBN = Order_table.\"OrderISBN\" " +
                    "GROUP BY Book_table.ISBN, Title, Authors, Price, Inventory_Quantity " +
                    "ORDER BY Total_Quantity_Sold DESC " +
                    "FETCH FIRST " + n + " ROWS ONLY";

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Display the results with book information in detail
            System.out.printf("%-20s%-50s%-50s%-10s%-15s%-15s\n", "ISBN", "Title", "Authors", "Price", "Inventory",
                    "Total Sold");
            while (resultSet.next()) {
                String ISBN = resultSet.getString("ISBN");
                String title = resultSet.getString("Title");
                String authors = resultSet.getString("Authors");
                int price = resultSet.getInt("Price");
                int inventory = resultSet.getInt("Inventory_Quantity");
                int quantitySold = resultSet.getInt("Total_Quantity_Sold");
                System.out.printf("%-20s%-50s%-50s%-10d%-15d%-15d\n", ISBN, title, authors, price, inventory,
                        quantitySold);
            }
            System.out.println();
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter a valid integer\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void placeOrder(Connection conn, String userID, String ISBN, int quantity) {
        try {
            // check if userID valid
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Customer_table WHERE \"UID\" = ?");
            pstmt.setString(1, userID);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Error: User not found.\n");
                return;
            }

            // Check if book exists and if there is enough inventory for the book
            String checkInventorySql = "SELECT Inventory_Quantity FROM Book_table WHERE ISBN = ?";
            PreparedStatement checkInventoryStmt = conn.prepareStatement(checkInventorySql);
            checkInventoryStmt.setString(1, ISBN);
            ResultSet checkInventoryResult = checkInventoryStmt.executeQuery();
            if (checkInventoryResult.next()) {
                int inventoryQuantity = checkInventoryResult.getInt("Inventory_Quantity");
                if (inventoryQuantity < quantity) {
                    System.out.println("Error: Not enough inventory for this book\n");
                    return;
                }
            } else {
                System.out.println("Error: Book not found\n");
                return;
            }

            // Generate the order ID
            String orderID = generateOrderID(conn);

            // Insert the order into the Order_table
            String insertOrderSql = "INSERT INTO Order_table (\"OID\", \"UID\", \"OrderDate\", \"OrderISBN\", \"OrderQuantity\", \"ShippingStatus\") "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement insertOrderStmt = conn.prepareStatement(insertOrderSql);
            insertOrderStmt.setString(1, orderID);
            insertOrderStmt.setString(2, userID);
            insertOrderStmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            insertOrderStmt.setString(4, ISBN);
            insertOrderStmt.setInt(5, quantity);
            insertOrderStmt.setString(6, "ordered");
            int rowsUpdated = insertOrderStmt.executeUpdate();
            if (rowsUpdated == 1) {
                System.out.println("Order placed successfully!");
                System.out.println("Book with ISBN " + ISBN + " and quantity " + quantity
                        + " added to order. Order ID: " + orderID);
                System.out.println();
            } else {
                System.out.println("Error: Failed to place order\n");
            }

            // Update the inventory of the book
            String updateInventorySql = "UPDATE Book_table SET Inventory_Quantity = Inventory_Quantity - ? WHERE ISBN = ?";
            PreparedStatement updateInventoryStmt = conn.prepareStatement(updateInventorySql);
            updateInventoryStmt.setInt(1, quantity);
            updateInventoryStmt.setString(2, ISBN);
            updateInventoryStmt.executeUpdate();

            // Schedule a task to update the shipping status to "shipped" after 30 seconds
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String updateShippingStatusSql = "UPDATE Order_table SET \"ShippingStatus\" = 'shipped' WHERE OID = ?";
                        PreparedStatement updateShippingStatusStmt = conn.prepareStatement(updateShippingStatusSql);
                        updateShippingStatusStmt.setString(1, orderID);
                        updateShippingStatusStmt.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
            }, 30000);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String generateOrderID(Connection conn) {
        String sql = "SELECT COUNT(*) as count FROM Order_table";
        String orderID = null;

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                orderID = String.format("%08d", count + 1);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return orderID;
    }

    public static void checkHistoryOrders(Connection conn, String userID) {
        try {
            // check if userID valid
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Customer_table WHERE \"UID\" = ?");
            pstmt.setString(1, userID);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Error: User not found.\n");
                return;
            }
            // Prepare the SQL query to retrieve the user's order history
            String sql = "SELECT Order_table.\"OID\", Order_table.\"OrderDate\", Order_table.\"OrderISBN\", " +
                    "Book_table.Title, Order_table.\"OrderQuantity\", Order_table.\"ShippingStatus\" " +
                    "FROM Order_table " +
                    "JOIN Book_table ON Order_table.\"OrderISBN\" = Book_table.ISBN " +
                    "WHERE Order_table.\"UID\" = ? " +
                    "ORDER BY Order_table.\"OID\" DESC";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, userID);

            // Execute the query and display the results
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.printf("%-15s %-15s %-15s %-50s %-15s %-15s\n",
                        "OID", "Order Date", "ISBN", "Title", "Quantity", "Shipping Status");
                do {
                    String orderId = resultSet.getString("OID");
                    Date orderDate = resultSet.getDate("OrderDate");
                    String isbn = resultSet.getString("OrderISBN");
                    String title = resultSet.getString("Title");
                    int quantity = resultSet.getInt("OrderQuantity");
                    String status = resultSet.getString("ShippingStatus");
                    System.out.printf("%-15s %-15s %-15s %-50s %-15d %-15s\n",
                            orderId, orderDate.toString(), isbn, title, quantity, status);
                } while (resultSet.next());
                System.out.println();
            } else {
                System.out.println("No order history found for user " + userID);
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    public static void searchBookByISBN(Connection conn, String ISBN) {
        try {
            String sql = "SELECT * FROM Book_table WHERE ISBN = '" + ISBN + "'";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            // Display the results with book information in detail
            if (resultSet.next()) {
                System.out.printf("%-20s%-50s%-50s%-10s%-15s\n", "ISBN", "Title", "Authors", "Price", "Inventory");
                String title = resultSet.getString("Title");
                String authors = resultSet.getString("Authors");
                int price = resultSet.getInt("Price");
                int inventory = resultSet.getInt("Inventory_Quantity");
                System.out.printf("%-20s%-50s%-50s%-10d%-15d\n", ISBN, title, authors, price, inventory);
            } else {
                System.out.println("Book not found.\n");
            }
            System.out.println();
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter a valid input.\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void searchBookByTitle(Connection conn, String title) {
        try {
            String sql = "SELECT * FROM Book_table WHERE Title LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "%" + title + "%");
            ResultSet resultSet = statement.executeQuery();
            // Display the results with book information in detail
            if (resultSet.next()) {
                System.out.printf("%-20s%-50s%-50s%-10s%-15s\n", "ISBN", "Title", "Authors", "Price", "Inventory");
                String ISBN = resultSet.getString("ISBN");
                String bookTitle = resultSet.getString("Title");
                String authors = resultSet.getString("Authors");
                int price = resultSet.getInt("Price");
                int inventory = resultSet.getInt("Inventory_Quantity");
                System.out.printf("%-20s%-50s%-50s%-10d%-15d\n", ISBN, bookTitle, authors, price, inventory);
            } else {
                System.out.println("Book not found.\n");
            }
            System.out.println();
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter a valid input.\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void searchBookByAuthor(Connection conn, String author) {
        try {
            String sql = "SELECT * FROM Book_table WHERE Authors LIKE '%" + author + "%'";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            // Display the results with book information in detail
            if (resultSet.next()) {
                System.out.printf("%-20s%-50s%-50s%-10s%-15s\n", "ISBN", "Title", "Authors", "Price", "Inventory");
                String ISBN = resultSet.getString("ISBN");
                String title = resultSet.getString("Title");
                String authors = resultSet.getString("Authors");
                int price = resultSet.getInt("Price");
                int inventory = resultSet.getInt("Inventory_Quantity");
                System.out.printf("%-20s%-50s%-50s%-10d%-15d\n", ISBN, title, authors, price, inventory);
            } else {
                System.out.println("Book not found.\n");
            }
            System.out.println();
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter a valid input.\n");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
