package org.example.backend.app.services;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {
    private String connectionString = "jdbc:postgresql://localhost:5432/mtcgdb?user=postgres&password=postgres";

    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Connection connection;

    public DatabaseService() {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            setConnection(connection);
        } catch (SQLException e) {
            System.err.println("Database connection failed.");
            e.printStackTrace();
            // It might be a good idea to throw a runtime exception or handle this error appropriately.
            // Depending on your application, you might not want to continue if the database connection fails.
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    // You may also want to provide methods for closing the database connection
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close database connection.");
                e.printStackTrace();
            }
        }
    }
}
