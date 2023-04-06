package client;

import java.sql.Connection;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import models.*;

public class CustomerClient implements ClientInterface {
    private Connection conn;
    private Scanner scanner;

    public CustomerClient(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }
    @Override
    public void clientPage() {
        while (true) {
            printCustomerPage();
            try {
                int userInput = scanner.nextInt();
                scanner.nextLine(); // consume the new line character
                System.out.println();
                switch (userInput) {
                    case 1:
                        searchBook();
                        break;
                    case 2:
                        Database.placeOrder(conn, getUser(), getISBN(), getOrderQuantity());
                        break;
                    case 3:
                        Database.checkHistoryOrders(conn, getUser());
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid input\n");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer\n");
                scanner.nextLine(); // consume the invalid input
            }
        }
    }

    private void searchBook() {
        while (true) {
            printSearchBookPage();
            try {
                int userInput = scanner.nextInt();
                scanner.nextLine(); // consume the new line character
                System.out.println();
                switch (userInput) {
                    case 1:
                        Database.searchBookByISBN(conn, getISBN());
                        break;
                    case 2:
                        Database.searchBookByTitle(conn, getTitle());
                        break;
                    case 3:
                        Database.searchBookByAuthor(conn, getAuthor());
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid input\n");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer\n");
                scanner.nextLine(); // consume the invalid input
            }
        }    
    }

    private String getAuthor() {
        System.out.print("Enter the author name: ");
        String userInput = scanner.nextLine();
        return userInput;
    }
    private String getTitle() {
        System.out.print("Enter the book title: ");
        String userInput = scanner.nextLine();
        return userInput;
    }

    private String getISBN() {
        System.out.print("Enter the book ISBN: ");
        String userInput = scanner.nextLine();
        return userInput;
    }
    
    private String getUser() {
        System.out.print("Enter the user ID: ");
        String userInput = scanner.nextLine();
        return userInput;
    }

    private int getOrderQuantity() {
        System.out.print("Enter the order quantity: ");
        int userInput = scanner.nextInt();
        return userInput;
    }

    private void printSearchBookPage() {
        System.out.println("===== Welcome to Book Ordering Management System =====");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = now.format(formatter);
        System.out.println("+ System Date: " + formattedDate);
        Database.countRecords(conn);
        System.out.println("Choose the Query Criterion:");
        System.out.println("> 1. ISBN");
        System.out.println("> 2. Book Title");
        System.out.println("> 3. Author Name");
        System.out.println("> 4. Return to the customer menu");
        System.out.println(">>> Please Enter the query criterion:");
    }

    private void printCustomerPage() {
        System.out.println("===== Welcome to Book Ordering Management System =====");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = now.format(formatter);
        System.out.println("+ System Date: " + formattedDate);
        Database.countRecords(conn);
        System.out.println("> 1. Book Search: Query a book by ISBN, Book Title or Author Name");
        System.out.println("> 2. Place an Order");
        System.out.println("> 3. Check History Orders: Query the history orders for a specific user");
        System.out.println("> 4. Return to the main menu");
        System.out.println(">>> Please Enter Your Query:");
    }
}
