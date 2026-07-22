package src;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/Tasks";
    private static final String USER = "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}





