package client;

import java.sql.Connection;
import java.util.Scanner;
import models.*;

public class InitClient implements ClientInterface {
    private Connection conn;
    private Scanner scanner;

    public InitClient(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    public void clientPage() {
        while (true) {
            System.out.println("Database Initializing");
            System.out.println();

            // initialize the database
            Database.dropTable(conn);
            Database.createTable(conn);
            Database.loadInitData(conn);

            System.out.println("Finished Database Initializing");

            System.out.println("Press Enter to continue");
            scanner.nextLine();
        }
    }
}
