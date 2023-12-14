package bif3.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {

    private static DatabaseService instance;

    private static final String DB_URL = "jdbc:postgresql://localhost/mtcg";
    private static final String USER = "vincent";
    private static final String PASS = "";

    public static DatabaseService getInstance() {
        if (DatabaseService.instance == null) {
            DatabaseService.instance = new DatabaseService();
        }
        return DatabaseService.instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
