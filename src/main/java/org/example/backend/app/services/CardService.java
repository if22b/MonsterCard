package org.example.backend.app.services;

import org.example.backend.app.models.Card;
import lombok.AccessLevel;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardService {

    @Setter(AccessLevel.PRIVATE)
    private Connection connection;

    public CardService(Connection connection) {
        setConnection(connection);
    }

    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();
        String query = "SELECT * FROM cards"; // Adjust SQL query according to your database schema

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getInt("card_id"),
                        resultSet.getInt("fk_user_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("damage"),
                        resultSet.getInt("element_type"),
                        resultSet.getBoolean("isInDeck")
                );
                cards.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception or propagate it
        }

        return cards;
    }

    public void addCard(Card card) {
        String query = "INSERT INTO cards (fk_user_id, name, damage, element_type, isInDeck) VALUES (?, ?, ?, ?, ?)"; // Adjust SQL query according to your database schema

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, card.getFkUserId());
            statement.setString(2, card.getName());
            statement.setInt(3, card.getDamage());
            statement.setInt(4, card.getElementType());
            statement.setBoolean(5, card.isInDeck());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception or propagate it
        }
    }

    // Additional methods can be added for updating, deleting, and other operations.
}
