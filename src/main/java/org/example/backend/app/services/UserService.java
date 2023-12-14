package org.example.backend.app.services;

import org.example.backend.app.models.User;
import lombok.AccessLevel;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    @Setter(AccessLevel.PRIVATE)
    private DatabaseService dbService; // Using DatabaseService as a field

    public UserService() {
        setDbService(new DatabaseService()); // Initializing the DatabaseService

        // Initial mock data (remove if not needed)
        // If you are using a real database, you might not need to manually add these users
        addUser(new User(0, "Luca", 100, 20));
        addUser(new User(1, "Marcus", 100, 20));
        addUser(new User(2, "Anna", 100, 20));
    }

    public User getUserById(int id) {
        // Assuming you're fetching from the database
        String stmt = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = dbService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(stmt)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("elopoints"),
                        rs.getInt("coins")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getUsers() {
        // Assuming you're fetching from the database
        List<User> users = new ArrayList<>();
        String stmt = "SELECT * FROM users";
        try (Connection connection = dbService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(stmt);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("elopoints"),
                        rs.getInt("coins")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public void addUser(User user) {
        String stmt = "INSERT INTO users (name, elopoints, coins) VALUES (?, ?, ?)";
        try (Connection connection = dbService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(stmt)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setInt(2, user.getElopoints());
            preparedStatement.setInt(3, user.getCoins());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeUser(int id) {
        String stmt = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = dbService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(stmt)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
