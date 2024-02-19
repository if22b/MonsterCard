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
import java.util.Random;

public class BattleManager {

    private static BattleManager single_instance = null;

    private User user1;
    private User user2;

    private String response;

    private final Object lock = new Object();

    public static BattleManager getInstance()
    {
        if (single_instance == null) {
            single_instance = new BattleManager();
        }
        return single_instance;
    }

    public String addUser(User user){
        synchronized (lock) {
            if (this.user1 == null) {
                this.user1 = user;
                return "User 1 added. Waiting for another player...";

            } else if (this.user2 == null) {
                this.user2 = user;
                return initiateBattle();

            } else {
                return "Battle already in progress. Please wait.";
            }
        }
    }

    private String initiateBattle() {
        CardManager manager = new CardManager();

        Deck deck1 = manager.getDeckUser(user1);
        Deck deck2 = manager.getDeckUser(user2);

        String battleResult = battle(user1, user2, deck1, deck2);

        resetUsers();

        return battleResult;
    }

    private void resetUsers() {
        user1 = null;
        user2 = null;
    }

    public String battle(User user1, User user2, Deck deck1, Deck deck2){
        if (user1 == null || user2 == null || deck1 == null || deck2 == null){

            this.user1 = null;
            this.user2 = null;
            this.response = null;

            System.out.println("Battle aborted: Missing user or deck");

            return null;
        }

        System.out.println("Battle started: " + user1.getName() + " vs " + user2.getName());
        System.out.println("Initial Deck Sizes - User1: " + deck1.getSize() + ", User2: " + deck2.getSize());

        int turns = 0;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();

            String log;
            while (!deck1.isEmpty() && !deck2.isEmpty() && ++turns <= 100){
                ObjectNode round = mapper.createObjectNode();

                Card card1 = deck1.getRandomCard();
                Card card2 = deck2.getRandomCard();

                float damage1 = calculateDamage(card1,card2);
                float damage2 = calculateDamage(card2,card1);

                // Logging
                round.put("Round",turns);
                System.out.println("Round " + turns);

                round.put("User_1",user1.getName());
                round.put("User_2",user2.getName());

                round.put("DeckSizeBefore_1",deck1.getSize());
                round.put("DeckSizeBefore_2",deck2.getSize());
                System.out.println("Deck Size User1: " + deck1.getSize() + ", Deck Size User2: " + deck2.getSize());

                round.put("CardID_1",card1.getId());
                round.put("CardID_2",card2.getId());

                round.put("CardName_1",card1.getName());
                round.put("CardName_2",card2.getName());

                round.put("CardDamage_1",damage1);
                round.put("CardDamage_2",damage2);

                System.out.println("Card1: " + card1.getName() + " (Damage: " + damage1 + "), Card2: " + card2.getName() + " (Damage: " + damage2 + ")");

                if (damage1 > damage2){
                    deck2.removeCard(card2);
                    deck1.addCard(card2);

                    round.put("Won",user1.getName());
                    System.out.println("Round " + turns + " winner: " + user1.getName());

                } else if (damage1 < damage2){
                    deck2.addCard(card1);
                    deck1.removeCard(card1);

                    round.put("Won",user2.getName());
                    System.out.println("Round " + turns + " winner: " + user2.getName());

                } else {
                    round.put("Won","Draw");
                    System.out.println("Round " + turns + ": Draw");
                }

                round.put("DeckSizeAfter_1",deck1.getSize());
                round.put("DeckSizeAfter_2",deck2.getSize());

                arrayNode.add(round);
            }

            // Log end of the battle here
            System.out.println("Battle ended after " + turns + " rounds.");
            System.out.println("Final Deck Size User1: " + deck1.getSize() + ", Final Deck Size User2: " + deck2.getSize());
            System.out.println("Battle result: " + (deck1.isEmpty() ? user2.getName() + " wins" : deck2.isEmpty() ? user1.getName() + " wins" : "Draw"));

            log = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);

            if (deck1.isEmpty()){
                user1.battleLost();
                user2.battleWon();
                return log;

            } else if (deck2.isEmpty()){
                user1.battleWon();
                user2.battleLost();
                return log;
            }

            user1.battleDraw();
            user2.battleDraw();
            return log;

        }  catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Exception in battle processing: " + e.getMessage());
        }
        return null;
    }

    public float calculateDamage(Card card1, Card card2){
        if (card1.getCardEnum() == CardEnum.Chaos){

            Random rand = new Random();

            if (rand.nextInt(7) > 4){
                return 999;
            }
        }

        if (card1.getCardEnum() != CardEnum.Spell){
            if (card2.getCardEnum() != CardEnum.Spell) {
                switch (card1.getCardEnum()){

                    case Dragon:
                        if (card2.getCardEnum() == CardEnum.FireElf){
                            return 0;
                        }
                        break;

                    case Goblin:
                        if (card2.getCardEnum() == CardEnum.Dragon){
                            return 0;
                        }
                        break;

                    case Ork:
                        if (card2.getCardEnum() == CardEnum.Wizard){
                            return 0;
                        }
                        break;

                    default:
                        break;
                }
                return card1.getDamage();

            } else {
                if (card1.getCardEnum() == CardEnum.Knight && card2.getElementEnum() == ElementEnum.water){
                    return -1;
                }
            }
        }

        if (card2.getCardEnum() == CardEnum.Kraken && card1.getCardEnum() == CardEnum.Spell){
            return 0;
        }

        switch (card1.getElementEnum()) {

            case water -> {
                if (card2.getElementEnum() == ElementEnum.fire) {
                    return card1.getDamage() * 2;
                }

                if (card2.getElementEnum() == ElementEnum.normal) {
                    return card1.getDamage() / 2;
                }
            }

            case fire -> {
                if (card2.getElementEnum() == ElementEnum.normal) {
                    return card1.getDamage() * 2;
                }

                if (card2.getElementEnum() == ElementEnum.water) {
                    return card1.getDamage() / 2;
                }
            }

            default -> {
                if (card2.getElementEnum() == ElementEnum.water) {
                    return card1.getDamage() * 2;
                }

                if (card2.getElementEnum() == ElementEnum.fire) {
                    return card1.getDamage() / 2;
                }
            }

        }
        return card1.getDamage();
    }

    public String getScoreboard(){
        try {
            Connection conn = Database.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT name, wins, games, elo FROM users WHERE name IS NOT NULL ORDER BY elo DESC;");
            ResultSet rs = ps.executeQuery();
            ps.close();

            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();

            while (rs.next()){
                ObjectNode entry = mapper.createObjectNode();

                entry.put("Name",rs.getString(1));
                entry.put("Wins",rs.getString(2));
                entry.put("Games",rs.getString(3));
                entry.put("Elo",rs.getString(4));

                arrayNode.add(entry);
            }
            rs.close();

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode);

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
