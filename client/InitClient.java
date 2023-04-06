package client;

import java.sql.Connection;
import models.*;

public class InitClient implements ClientInterface {
    private Connection conn;

    public InitClient(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void clientPage() {
        System.out.println("Database Initializing");
        System.out.println();

        // initialize the database
        Database.dropTable(conn);
        Database.createTable(conn);
        Database.loadInitData(conn);

        System.out.println("Finished Database Initializing");
        System.out.println();
    }
}
