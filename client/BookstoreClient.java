package client;

import java.sql.Connection;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import models.*;

public class BookstoreClient implements ClientInterface {
    private Connection conn;
    private Scanner scanner;

    public BookstoreClient(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }
    @Override
    public void clientPage() {
        while (true) {
            printBookstorePage();
            try {
                int userInput = scanner.nextInt();
                scanner.nextLine(); // consume the new line character
                System.out.println();
                switch (userInput) {
                    case 1:
                        Database.updateOrderStatus(conn);
                        break;
                    case 2:
                        Database.queryOrderByStatus(conn);    
                        break;
                    case 3:
                        Database.queryMostPopularBook(conn);   
                        break;
                    case 4:
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
    public void printBookstorePage() {
        System.out.println("===== Welcome to Book Ordering Management System =====");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = now.format(formatter);
        System.out.println("+ System Date: " + formattedDate);
        Database.countRecords(conn);
        System.out.println("> 1. Order Update: Update the shipping status of an order");
        System.out.println("> 2. Order Query: Query orders by shipping status");
        System.out.println("> 3. Most Popular Book: Search for the Most Popular Book");
        System.out.println("> 4. Return to the main menu");
        System.out.println(">>> Please Enter Your Query:");
    }
}
