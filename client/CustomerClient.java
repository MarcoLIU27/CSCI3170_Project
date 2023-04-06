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

    }

}
