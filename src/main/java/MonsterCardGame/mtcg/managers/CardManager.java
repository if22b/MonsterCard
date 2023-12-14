/*
package MonsterCard.mtcg.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import MonsterCard.database.DatabaseService;
import MonsterCard.mtcg.Card;
import MonsterCard.mtcg.User;
import MonsterCard.mtcg.collections.Deck;
import MonsterCard.mtcg.types.CardType;
import MonsterCard.mtcg.types.ElementType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardManager {

    private static CardManager single_instance = null;

    public static CardManager getInstance()
    {
        if (single_instance == null) {
            single_instance = new CardManager();
        }
        return single_instance;
    }

    public boolean createPackage(List<Card> cards){
        if (cards.size() != 5){
            return false;
        }
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            for (Card card: cards){
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(cardid) FROM cards WHERE cardid = ? AND collection IS NULL;");
                ps.setString(1, card.getId());
                ResultSet rs = ps.executeQuery();
                ps.close();
                if (!rs.next() || rs.getInt(1) != 1) {
                    rs.close();
                    conn.close();
                    return false;
                }
                rs.close();
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO packages(cardID_1, cardID_2, cardID_3, cardID_4, cardID_5) VALUES(?,?,?,?,?);");
            ps.setString(1, cards.get(0).getId());
            ps.setString(2, cards.get(1).getId());
            ps.setString(3, cards.get(2).getId());
            ps.setString(4, cards.get(3).getId());
            ps.setString(5, cards.get(4).getId());
            int affectedRows = ps.executeUpdate();
            ps.close();
            conn.close();
            if (affectedRows != 1) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String showUserCards (User user){
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT cardID, name, damage FROM cards WHERE owner = ?;");
            ps.setString(1,user.getUsername());
            String json = result2Json(ps.executeQuery());
            ps.close();
            conn.close();
            return json;
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String showUserDeck (User user){
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT cardID, name, damage FROM cards WHERE owner = ? AND collection = 'deck';");
            ps.setString(1,user.getUsername());
            String json = result2Json(ps.executeQuery());
            ps.close();
            conn.close();
            return json;
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String result2Json(ResultSet rs) throws SQLException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        while (rs.next()){
            ObjectNode card = mapper.createObjectNode();
            card.put("ID",rs.getString(1));
            card.put("Name",rs.getString(2));
            card.put("Damage",rs.getString(3));
            arrayNode.add(card);
        }
        rs.close();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
    }

    public boolean acquirePackage2User(User user){
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            // Check Existing Package
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM packages;");
            ResultSet rs = ps.executeQuery();
            ps.close();
            if (!rs.next()) {
                rs.close();
                conn.close();
                return false;
            }
            int packageID = rs.getInt(1);
            // Decrease Coins User
            if (!user.buyPackage()){
                return false;
            }
            // Update Cards Owner and Collection
            PreparedStatement ps_Card = conn.prepareStatement("UPDATE cards SET owner = ?, collection = 'stack' WHERE cardID = ?;");
            for (int i = 2; i < 7; i++){
                ps_Card.setString(1,user.getUsername());
                ps_Card.setString(2,rs.getString(i));
                ps_Card.executeUpdate();
            }
            rs.close();
            ps_Card.close();
            // Delete Package
            PreparedStatement ps_Package = conn.prepareStatement("DELETE FROM packages WHERE packageID = ?;");
            ps_Package.setInt(1,packageID);
            ps_Package.executeUpdate();
            ps_Package.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ElementType createElementType (String element){
        if (element.toLowerCase().contains("water")){
            return ElementType.water;
        } else if (element.toLowerCase().contains("fire")){
            return ElementType.fire;
        } else {
            return ElementType.normal;
        }
    }

    public CardType createCardType (String name){
        if (name.toLowerCase().contains("spell")){
            return CardType.Spell;
        } else if (name.toLowerCase().contains("dragon")){
            return CardType.Dragon;
        } else if (name.toLowerCase().contains("fireelf")){
            return CardType.FireElf;
        } else if (name.toLowerCase().contains("goblin")){
            return CardType.Goblin;
        } else if (name.toLowerCase().contains("knight")){
            return CardType.Knight;
        } else if (name.toLowerCase().contains("kraken")){
            return CardType.Kraken;
        } else if (name.toLowerCase().contains("ork")){
            return CardType.Ork;
        } else if (name.toLowerCase().contains("wizard")){
            return CardType.Wizard;
        } else if (name.toLowerCase().contains("magicdice")){
            return CardType.magicdice;
        }
        return null;
    }

    public Deck getDeckUser (User user){
        Deck deck = null;
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT cardID, name, damage FROM cards WHERE owner = ? AND collection = 'deck';");
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();
            ps.close();
            List<Card> cards = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString(2);
                cards.add(new Card(rs.getString(1), name, rs.getFloat(3),createCardType(name),createElementType(name)));
            }
            deck = new Deck(cards);
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deck;
    }

    public boolean registerCard(String id, String name, float damage) {
        if (!id.isEmpty() && !name.isEmpty()) {
            ElementType element = createElementType(name);
            CardType cardType = createCardType(name);
            if ( element != null && cardType != null){
                try {
                    Connection conn = DatabaseService.getInstance().getConnection();
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO cards(cardid, name, damage) VALUES(?,?,?);");
                    ps.setString(1, id);
                    ps.setString(2, name);
                    ps.setFloat(3, damage);
                    int affectedRows = ps.executeUpdate();
                    ps.close();
                    conn.close();
                    if (affectedRows == 0) {
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void deleteCard(String id) {
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM cards WHERE cardid = ? AND collection IS NULL");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createDeck(User user, List<String> id){
        if (id.size() != 4){
            return false;
        }
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            // Check if user owns Cards
            List<String> checkTwice = new LinkedList<>();
            for (String cardID: id){
                if (checkTwice.contains(cardID)){
                    conn.close();
                    return false;
                }
                checkTwice.add(cardID);
                if (TradeManager.getInstance().marketplaceContains(cardID)){
                    return false;
                }
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(cardid) FROM cards WHERE cardid = ? AND owner = ?;");
                ps.setString(1,cardID);
                ps.setString(2,user.getUsername());
                ResultSet rs = ps.executeQuery();
                if (!rs.next() || rs.getInt(1) != 1) {
                    ps.close();
                    rs.close();
                    conn.close();
                    return false;
                }
            }
            // Set all Cards to Stack
            PreparedStatement ps = conn.prepareStatement("UPDATE cards SET collection = 'stack' WHERE owner = ?;");
            ps.setString(1,user.getUsername());
            ps.executeUpdate();
            // Change Cards to Deck
            for (String cardID: id){
                ps = conn.prepareStatement("UPDATE cards SET collection = 'deck' WHERE owner = ? AND cardID = ?;");
                ps.setString(1,user.getUsername());
                ps.setString(2,cardID);
                ps.executeUpdate();
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

*/

package MonsterCardGame.mtcg.managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import MonsterCardGame.database.DatabaseService;
import MonsterCardGame.mtcg.Card;
import MonsterCardGame.mtcg.User;
import MonsterCardGame.mtcg.collections.Deck;
import MonsterCardGame.mtcg.enums.CardType;
import MonsterCardGame.mtcg.enums.ElementType;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CardManager {

    private static CardManager single_instance = null;

    public static CardManager getInstance() {
        if (single_instance == null) {
            single_instance = new CardManager();
        }
        return single_instance;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseService.getInstance().getConnection();
    }

    public boolean createPackage(List<Card> cards) {
        if (cards.size() != 5) {
            return false;
        }

        try (Connection conn = getConnection()) {
            for (Card card : cards) {
                if (!validateCardForPackage(conn, card.getId())) {
                    return false;
                }
            }

            if (!insertPackage(conn, cards)) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean validateCardForPackage(Connection conn, String cardId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(cardid) FROM cards WHERE cardid = ? AND collection IS NULL;")) {
            ps.setString(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }

    private boolean insertPackage(Connection conn, List<Card> cards) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO packages(cardID_1, cardID_2, cardID_3, cardID_4, cardID_5) VALUES(?,?,?,?,?);")) {
            for (int i = 0; i < 5; i++) {
                ps.setString(i + 1, cards.get(i).getId());
            }
            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        }
    }

    public String showUserCards(User user) {
        return queryAndConvertToJson("SELECT cardID, name, damage FROM cards WHERE owner = ?;", user.getUsername());
    }

    public String showUserDeck(User user) {
        return queryAndConvertToJson("SELECT cardID, name, damage FROM cards WHERE owner = ? AND collection = 'deck';", user.getUsername());
    }

    private String queryAndConvertToJson(String query, String... params) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            return result2Json(ps.executeQuery());
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String result2Json(ResultSet rs) throws SQLException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
        while (rs.next()) {
            ObjectNode card = mapper.createObjectNode();
            card.put("ID", rs.getString(1));
            card.put("Name", rs.getString(2));
            card.put("Damage", rs.getString(3));
            arrayNode.add(card);
        }
        rs.close();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
    }

    public boolean acquirePackage2User(User user) {
        try (Connection conn = getConnection()) {
            // Check Existing Package
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM packages;");
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                int packageID = rs.getInt(1);
                // Decrease Coins User
                if (!user.buyPackage()) {
                    return false;
                }
                // Update Cards Owner and Collection
                updateCardsOwnerAndCollection(conn, user.getUsername(), rs);
                // Delete Package
                deletePackage(conn, packageID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void updateCardsOwnerAndCollection(Connection conn, String username, ResultSet rs) throws SQLException {
        try (PreparedStatement ps_Card = conn.prepareStatement("UPDATE cards SET owner = ?, collection = 'stack' WHERE cardID = ?;")) {
            for (int i = 2; i < 7; i++) {
                ps_Card.setString(1, username);
                ps_Card.setString(2, rs.getString(i));
                ps_Card.executeUpdate();
            }
        }
    }

    private void deletePackage(Connection conn, int packageID) throws SQLException {
        try (PreparedStatement ps_Package = conn.prepareStatement("DELETE FROM packages WHERE packageID = ?;")) {
            ps_Package.setInt(1, packageID);
            ps_Package.executeUpdate();
        }
    }

    public ElementType createElementType(String element) {
        if (element.toLowerCase().contains("water")) {
            return ElementType.water;
        } else if (element.toLowerCase().contains("fire")) {
            return ElementType.fire;
        } else {
            return ElementType.normal;
        }
    }

    public CardType createCardType(String name) {
        String lowerName = name.toLowerCase();
        if (lowerName.contains("spell")) {
            return CardType.Spell;
        } else if (lowerName.contains("dragon")) {
            return CardType.Dragon;
        } else if (lowerName.contains("fireelf")) {
            return CardType.FireElf;
        } else if (lowerName.contains("goblin")) {
            return CardType.Goblin;
        } else if (lowerName.contains("knight")) {
            return CardType.Knight;
        } else if (lowerName.contains("kraken")) {
            return CardType.Kraken;
        } else if (lowerName.contains("ork")) {
            return CardType.Ork;
        } else if (lowerName.contains("wizard")) {
            return CardType.Wizard;
        } else if (lowerName.contains("magicdice")) {
            return CardType.magicdice;
        }
        return null;
    }

    public Deck getDeckUser(User user) {
        Deck deck = null;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT cardID, name, damage FROM cards WHERE owner = ? AND collection = 'deck';")) {
            ps.setString(1, user.getUsername());
            try (ResultSet rs = ps.executeQuery()) {
                List<Card> cards = new ArrayList<>();
                while (rs.next()) {
                    String name = rs.getString(2);
                    cards.add(new Card(rs.getString(1), name, rs.getFloat(3), createCardType(name), createElementType(name)));
                }
                deck = new Deck(cards);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deck;
    }

    public boolean registerCard(String id, String name, float damage) {
        if (!id.isEmpty() && !name.isEmpty()) {
            ElementType element = createElementType(name);
            CardType cardType = createCardType(name);
            if (element != null && cardType != null) {
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO cards(cardid, name, damage) VALUES(?,?,?);")) {
                    ps.setString(1, id);
                    ps.setString(2, name);
                    ps.setFloat(3, damage);
                    int affectedRows = ps.executeUpdate();
                    if (affectedRows == 0) {
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void deleteCard(String id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cards WHERE cardid = ? AND collection IS NULL")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createDeck(User user, List<String> id) {
        if (id.size() != 4) {
            return false;
        }

        try (Connection conn = getConnection()) {
            List<String> checkTwice = new LinkedList<>();
            for (String cardID : id) {
                if (checkTwice.contains(cardID)) {
                    return false;
                }
                checkTwice.add(cardID);
                if (TradeManager.getInstance().marketplaceContains(cardID)) {
                    return false;
                }
                if (!validateCardForUser(conn, user.getUsername(), cardID)) {
                    return false;
                }
            }

            updateCardsToStack(conn, user.getUsername());
            updateCardsToDeck(conn, user.getUsername(), id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean validateCardForUser(Connection conn, String username, String cardID) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(cardid) FROM cards WHERE cardid = ? AND owner = ?;")) {
            ps.setString(1, cardID);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }

    private void updateCardsToStack(Connection conn, String username) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE cards SET collection = 'stack' WHERE owner = ?;")) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }

    private void updateCardsToDeck(Connection conn, String username, List<String> id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE cards SET collection = 'deck' WHERE owner = ? AND cardID = ?;")) {
            for (String cardID : id) {
                ps.setString(1, username);
                ps.setString(2, cardID);
                ps.executeUpdate();
            }
        }
    }
}
