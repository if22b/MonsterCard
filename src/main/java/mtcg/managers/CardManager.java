package mtcg.managers;

import database.Database;
import mtcg.User;
import mtcg.Card;
import mtcg.collections.Deck;
import mtcg.enums.CardEnum;
import mtcg.enums.ElementEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
            Connection conn = Database.getInstance().getConnection();

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
            Connection conn = Database.getInstance().getConnection();
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
            Connection conn = Database.getInstance().getConnection();
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
            Connection conn = Database.getInstance().getConnection();
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

    public String showUserPlainDeck (User user){
        try {
            Connection conn = Database.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT cardID, name, damage FROM cards WHERE owner = ? AND collection = 'deck';");

            ps.setString(1,user.getUsername());
            String plainText = result2PlainText(ps.executeQuery());
            ps.close();
            conn.close();
            return plainText;

        } catch (SQLException e) {
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

    private String result2PlainText(ResultSet rs) throws SQLException {
        StringBuilder plainTextBuilder = new StringBuilder();

        while (rs.next()) {
            String id = rs.getString(1);
            String name = rs.getString(2);
            String damage = rs.getString(3);

            plainTextBuilder.append("ID: ").append(id)
                    .append(", Name: ").append(name)
                    .append(", Damage: ").append(damage)
                    .append("\n");
        }
        rs.close();

        return plainTextBuilder.toString();
    }

    public int acquirePackage2User(User user){
        try {
            Connection conn = Database.getInstance().getConnection();
            // Check Existing Package
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM packages;");
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                ps.close();
                conn.close();
                return 0; // No available packages
            }

            int packageID = rs.getInt(1);

            // Decrease Coins User
            if (!user.buyPackage()) {
                rs.close();
                ps.close();
                conn.close();
                return -1; // Insufficient funds
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
        }

        return 1; // Success
    }

    public ElementEnum createElementType (String element){

        if (element.toLowerCase().contains("water")){
            return ElementEnum.water;

        } else if (element.toLowerCase().contains("fire")){
            return ElementEnum.fire;

        } else {
            return ElementEnum.normal;
        }
    }

    public CardEnum createCardType (String name){

        if (name.toLowerCase().contains("spell")){
            return CardEnum.Spell;

        } else if (name.toLowerCase().contains("dragon")){
            return CardEnum.Dragon;

        } else if (name.toLowerCase().contains("fireelf")){
            return CardEnum.FireElf;

        } else if (name.toLowerCase().contains("goblin")){
            return CardEnum.Goblin;

        } else if (name.toLowerCase().contains("knight")){
            return CardEnum.Knight;

        } else if (name.toLowerCase().contains("kraken")){
            return CardEnum.Kraken;

        } else if (name.toLowerCase().contains("ork")){
            return CardEnum.Ork;

        } else if (name.toLowerCase().contains("wizard")){
            return CardEnum.Wizard;

        } else if (name.toLowerCase().contains("chaos")){
            return CardEnum.Chaos;
        }

        return null;
    }

    public Deck getDeckUser (User user){
        Deck deck = null;

        try {
            Connection conn = Database.getInstance().getConnection();
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
            ElementEnum element = createElementType(name);
            CardEnum cardEnum = createCardType(name);

            if ( element != null && cardEnum != null){
                try {
                    Connection conn = Database.getInstance().getConnection();
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
            Connection conn = Database.getInstance().getConnection();
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
            Connection conn = Database.getInstance().getConnection();
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
