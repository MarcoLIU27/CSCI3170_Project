package client;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

import models.*;

public class InitClient implements ClientInterface {
    private Connection conn;
    private Scanner scanner;

    public InitClient(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void clientPage() {

        while (true) {
            printInitPage();
            try {
                int userInput = scanner.nextInt();
                scanner.nextLine(); // consume the new line character
                System.out.println();
                switch (userInput) {
                    case 1:
                        Database.dropTable(conn);
                        break;
                    case 2:
                        Database.createTable(conn);
                        break;
                    case 3:
                        Database.loadInitData(conn);
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

    public void printInitPage() {
        System.out.println("===== Welcome to Database Initialization System =====");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = now.format(formatter);
        System.out.println("+ System Date: " + formattedDate);
        Database.countRecords(conn);
        System.out.println("> 1. Drop Database Tables");
        System.out.println("> 2. Create Database Tables");
        System.out.println("> 3. Load Initial Data");
        System.out.println("> 4. Return to the main menu");
        System.out.println(">>> Please Enter the query:");
    }
}
