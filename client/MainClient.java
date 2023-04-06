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

    public void clientPage()
    {
        while (true) {
            printMainPage();
            try {
                int userInput = scanner.nextInt();
                scanner.nextLine(); // consume the new line character
                ClientInterface newClientPage = null;
                switch (userInput) {
                    case 1:
                        newClientPage = new InitClient(conn);
                        break;
                    case 2:
                        newClientPage = new InitClient(conn);
                        break;
                    case 3:
                        newClientPage = new InitClient(conn);
                        break;
                    case 4:
                        System.out.println("Exiting program...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid input");
                        break;
                }
                if (newClientPage != null) newClientPage.clientPage();
            } catch (InputMismatchException e) {
                System.out.println("Error: Please enter a valid integer");
                scanner.nextLine(); // consume the invalid input
            }
            
        }
    }

    public void printMainPage() {
        System.out.println("===== Welcome to Book Ordering Management System =====");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = now.format(formatter);
        System.out.println("+ System Date: " + formattedDate);
        Database.countRecords(conn);
        System.out.println("> 1. Database Initialization");
        System.out.println("> 2. Customer Operation (not implemented yet)");
        System.out.println("> 3. Bookstore Operation (not implemented yet)");
        System.out.println("> 4. Quit");
        System.out.println(">>> Please Enter Your Query:");
    }
    
}
