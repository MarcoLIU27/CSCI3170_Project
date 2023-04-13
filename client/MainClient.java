package client;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;
import models.*;

public class MainClient implements ClientInterface{
    private Connection conn;
    private Scanner scanner;

    public MainClient(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void clientPage()
    {
        while (true) {
            printMainPage();
            try {
                int userInput = scanner.nextInt();
                scanner.nextLine(); // consume the new line character
                System.out.println();
                ClientInterface newClientPage = null;
                switch (userInput) {
                    case 1:
                        newClientPage = new InitClient(conn);
                        break;
                    case 2:
                        newClientPage = new CustomerClient(conn);
                        break;
                    case 3:
                        newClientPage = new BookstoreClient(conn);
                        break;
                    case 4:
                        System.out.println("Exiting program...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid input\n");
                        break;
                }
                if (newClientPage != null) newClientPage.clientPage();
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer\n");
                scanner.nextLine(); // consume the invalid input
            }
            
        }
    }

    public void printMainPage() {
        System.out.println("===== Welcome to Book Ordering Management System =====");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = now.format(formatter);
        System.out.println("+ System Date: " + formattedDate);
        Database.countRecords(conn);
        System.out.println("> 1. Database Initialization");
        System.out.println("> 2. Customer Operations");
        System.out.println("> 3. Bookstore Operations");
        System.out.println("> 4. Quit");
        System.out.println(">>> Please Enter Your Query:");
    }
    
}
