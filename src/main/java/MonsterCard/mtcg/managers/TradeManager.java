package MonsterCard.mtcg.managers;

import MonsterCard.mtcg.User;
import MonsterCard.mtcg.types.CardType;
import bif3.swe1.database.DatabaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TradeManager {

    private static TradeManager single_instance = null;

    public static TradeManager getInstance()
    {
        if (single_instance == null) {
            single_instance = new TradeManager();
        }
        return single_instance;
    }

    public String showMarketplace(){
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT tradeID, cards.cardID, name, damage, owner, mindamage, type FROM marketplace JOIN cards ON cards.cardID = marketplace.cardID;");
            ResultSet rs = ps.executeQuery();
            ps.close();
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();
            while (rs.next()){
                ObjectNode deal = mapper.createObjectNode();
                deal.put("TradeID",rs.getString(1));
                deal.put("CardID",rs.getString(2));
                deal.put("Name",rs.getString(3));
                deal.put("Damage",rs.getString(4));
                deal.put("Owner",rs.getString(5));
                deal.put("MinimumDamage",rs.getString(6));
                deal.put("Type",rs.getString(7));
                arrayNode.add(deal);
            }
            rs.close();
            conn.close();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean removeTrade(User user, String id){
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT cards.owner FROM cards JOIN marketplace ON cards.cardID = marketplace.cardID WHERE marketplace.tradeID = ?;");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            ps.close();
            if (!rs.next() || !rs.getString(1).equals(user.getUsername())) {
                rs.close();
                conn.close();
                return false;
            }
            rs.close();
            ps = conn.prepareStatement("DELETE FROM marketplace WHERE tradeID = ?;");
            ps.setString(1, id);
            int affectedRows = ps.executeUpdate();
            ps.close();
            if (affectedRows != 1) {
                conn.close();
                return false;
            }
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean card2market(User user, String tradeID, String cardID, float minimumDamage, String type){
        try {
            // Check if user owns card
            Connection conn = DatabaseService.getInstance().getConnection();
            if (marketplaceContains(cardID)){
                return false;
            }
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(cardid) FROM cards WHERE owner = ? AND cardid = ? AND collection LIKE 'stack';");
            ps.setString(1, user.getUsername());
            ps.setString(2, cardID);
            ResultSet rs = ps.executeQuery();
            ps.close();
            if (!rs.next() || rs.getInt(1) != 1) {
                rs.close();
                conn.close();
                return false;
            }
            rs.close();
            // Insert Marketplace
            ps = conn.prepareStatement("INSERT INTO marketplace(tradeid, cardid, mindamage, type) VALUES(?,?,?,?);");
            ps.setString(1, tradeID);
            ps.setString(2, cardID);
            ps.setFloat(3, minimumDamage);
            ps.setString(4, type);
            int affectedRows = ps.executeUpdate();
            ps.close();
            if (affectedRows != 1) {
                return false;
            }
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean tradeCards(User user, String tradeID, String cardID){
        if (user == null){
            return false;
        }
        try {
            // Exchanging
            String cardName;
            float cardDamage;
            // Marketplace
            String offeredCardID;
            String offeredCardOwner;
            float minDamage;
            String type;
            // Check if exchanging card in not on Marktplace
            Connection conn = DatabaseService.getInstance().getConnection();
            if (marketplaceContains(cardID)){
                return false;
            }
            // Get exchanging Card with ID
            PreparedStatement ps = conn.prepareStatement("SELECT name, damage FROM cards WHERE owner = ? AND cardid = ? AND collection LIKE 'stack';");
            ps.setString(1, user.getUsername());
            ps.setString(2, cardID);
            ResultSet rs = ps.executeQuery();
            ps.close();
            if (!rs.next()) {
                rs.close();
                conn.close();
                return false;
            }
            cardName = rs.getString(1);
            cardDamage = rs.getFloat(2);
            rs.close();
            // Get Trade from SQL
            ps = conn.prepareStatement("SELECT marketplace.cardID, owner, minDamage, type FROM marketplace JOIN cards ON marketplace.cardID = cards.cardID WHERE tradeID = ?;");
            ps.setString(1, tradeID);
            rs = ps.executeQuery();
            ps.close();
            if (!rs.next()) {
                rs.close();
                conn.close();
                return false;
            }
            offeredCardID = rs.getString(1);
            offeredCardOwner = rs.getString(2);
            minDamage = rs.getFloat(3);
            type = rs.getString(4);
            rs.close();
            // Check CardType and Damage and Owner
            CardManager manager = CardManager.getInstance();
            if (type.equalsIgnoreCase("monster")){
                if (manager.createCardType(cardName) == CardType.Spell){
                    return false;
                }
            } else if (manager.createCardType(cardName) != manager.createCardType(type)){
                return false;
            }
            if (cardDamage < minDamage){
                return false;
            }
            if (offeredCardOwner.equalsIgnoreCase(user.getUsername())){
                return false;
            }
            // Change cards owners
            ps = conn.prepareStatement("UPDATE cards SET owner = ? WHERE cardID = ?");
            ps.setString(1, offeredCardOwner);
            ps.setString(2, cardID);
            ps.executeUpdate();
            ps.close();
            ps = conn.prepareStatement("UPDATE cards SET owner = ? WHERE cardID = ?");
            ps.setString(1, user.getUsername());
            ps.setString(2, offeredCardID);
            ps.executeUpdate();
            ps.close();
            // Delete marketplace entry
            ps = conn.prepareStatement("DELETE FROM marketplace WHERE tradeID = ?;");
            ps.setString(1, tradeID);
            ps.executeUpdate();
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean marketplaceContains(String cardID){
        try {
            Connection conn = DatabaseService.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(cardid) FROM marketplace WHERE cardid = ?;");
            ps.setString(1, cardID);
            ResultSet rs = ps.executeQuery();
            ps.close();
            if (rs.next() && rs.getInt(1) == 1) {
                rs.close();
                conn.close();
                return true;
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
