package server;
import mtcg.User;
import server.context.RequestContext;
import server.context.ResponseContext;
import database.Database;
import mtcg.Card;
import mtcg.managers.CardManager;
import mtcg.managers.BattleManager;
import mtcg.managers.TradeManager;
import mtcg.managers.UserManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// Handle request and send response
public class ResponseHandler {

    BufferedWriter writer;

    private static final Logger logger = Logger.getLogger(ResponseHandler.class.getName());

    public ResponseHandler(BufferedWriter writer) {
        this.writer = writer;
    }

    public void response(RequestContext request) {
        logger.info("Processing request: " + request.getHttp_verb() + " " + request.getRequested());
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        if ( request != null && request.getHeader_values() != null ){
            String[] parts = request.getRequested().split("/");
            User user;

            if ((parts.length == 2 || parts.length == 3)) {
                switch (parts[1]){

                    case "delete":
                        response = deleteAll(request);
                        break;

                    case "users":
                        response = users(request);
                        break;

                    case "sessions":
                        response = sessions(request);
                        break;

                    case "packages":
                        response = packages(request);
                        break;

                    case "transactions":
                        if (parts.length != 3){
                            break;
                        }

                        if (parts[2].equals("packages")){
                            user = authorize(request);
                            if (user != null){
                                response = transactionsPackages(user,request);
                            } else {
                                response = new ResponseContext("401 Unauthorized", "application/json");
                            }
                        }
                        break;

                    case "cards":
                        user = authorize(request);
                        if (user != null){
                            response = showCards(user,request);
                        } else {
                            response = new ResponseContext("401 Unauthorized", "application/json");
                        }
                        break;

                    case "deck":
                        user = authorize(request);
                        if (user != null){
                            response = requestDeck(user,request);
                        } else {
                            response = new ResponseContext("401 Unauthorized", "application/json");
                        }
                        break;

                    case "deck?format=plain":
                        user = authorize(request);
                        if (user != null) {
                            response = handleFormatPlainDeck(user, request);
                        } else {
                            response = new ResponseContext("401 Unauthorized", "text/plain");
                        }
                        break;

                    case "stats":
                        user = authorize(request);
                        if (user != null){
                            response = stats(user,request);
                        } else {
                            response = new ResponseContext("401 Unauthorized", "application/json");
                        }
                        break;

                    case "score":
                        user = authorize(request);
                        if (user != null){
                            response = scoreboard(request);
                        } else {
                            response = new ResponseContext("401 Unauthorized", "application/json");
                        }
                        break;

                    case "tradings":
                        user = authorize(request);
                        if (user != null){
                            response = trade(request,user);
                        } else {
                            response = new ResponseContext("401 Unauthorized", "application/json");
                        }
                        break;

                    case "battles":
                        user = authorize(request);
                        if (user != null){
                            response = battle(request,user);
                        } else {
                            response = new ResponseContext("401 Unauthorized", "application/json");
                        }
                        break;
                        
                    default:
                        break;
                }
            }
        }

        // Send response
        try {
            logger.info("Writing response headers");
            writer.write(response.getHttp_version() + " " + response.getStatus() + "\r\n");

            logger.info("Response sent with status: " + response.getStatus());
            writer.write("Server: " + response.getServer() + "\r\n");
            writer.write("Content-Type: " + response.getContentType() + "\r\n");
            writer.write("Content-Length: " + response.getContentLength() + "\r\n\r\n");
            logger.info("Response Headers: Final Content-Type before sending = " + response.getContentType() +
                    ", Content-Length = " + response.getContentLength());

            logger.info("Writing response payload");
            writer.write(response.getPayload());
            logger.info("Final Payload before sending: " + response.getPayload());

            writer.flush();
            logger.info("Response sent successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error sending response: " + e.getMessage());
        }
    }

    private ResponseContext deleteAll(RequestContext request){
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");
        UserManager userManager = UserManager.getInstance();

        if (request.getHttp_verb().equals("DELETE")) {
            try {
                Connection conn = Database.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM packages;");
                ps.executeUpdate();
                ps = conn.prepareStatement("DELETE FROM marketplace;");
                ps.executeUpdate();
                ps = conn.prepareStatement("DELETE FROM cards;");
                ps.executeUpdate();
                ps = conn.prepareStatement("DELETE FROM users;");
                ps.executeUpdate();
                ps.close();
                conn.close();

                response.setStatus("200 OK");
            } catch (SQLException e) {
                e.printStackTrace();

                response.setStatus("409 Conflict");
                return response;
            }
        }
        return response;
    }

    private ResponseContext users(RequestContext request){
        UserManager manager = UserManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        ObjectMapper mapper;
        User user;

        switch (request.getHttp_verb()) {
            case "GET":
                user = authorize(request);

                if (user != null){
                    String[] parts = request.getRequested().split("/");

                    if (parts.length == 3 && user.getUsername().equals(parts[2])){
                        String userInfo = user.getInfo();

                        if (userInfo != null){
                            response.setStatus("200 OK");
                            response.setPayload(userInfo + "\n");

                        } else {
                            response.setStatus("404 Not Found");
                            response.setPayload("User information not found\n");
                        }

                    } else {
                        response.setStatus("401 Unauthorized");
                        response.setPayload("Unauthorized: You can only access your own information\n");
                    }

                } else {
                    response.setStatus("401 Unauthorized");
                    response.setPayload("Unauthorized: Invalid or missing token\n");
                }
                break;

            case "POST":
                mapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = mapper.readTree(request.getPayload());

                    if ( jsonNode.has("Username") && jsonNode.has("Password")){

                        if (manager.registerUser(jsonNode.get("Username").asText(),jsonNode.get("Password").asText())) {
                            response.setStatus("201 Created");
                            response.setPayload("Registration successful\n");

                        } else {
                            response.setStatus("409 Conflict");
                            response.setPayload("Registration failed: User already exists\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    response.setStatus("500 Internal Server Error");
                    response.setPayload("Registration failed: Internal server error\n");
                }
                break;

            case "PUT":
                user = authorize(request);

                if (user != null){
                    String[] editUser = request.getRequested().split("/");

                    if (editUser.length == 3 && user.getUsername().equals(editUser[2])){
                        mapper = new ObjectMapper();

                        try {
                            JsonNode jsonNode = mapper.readTree(request.getPayload());

                            if (jsonNode.has("Name") && jsonNode.has("Bio") && jsonNode.has("Image")){

                                if (user.setUserInfo(jsonNode.get("Name").asText(), jsonNode.get("Bio").asText(), jsonNode.get("Image").asText())){
                                    response.setStatus("200 OK");
                                    response.setPayload("User information updated successfully\n");

                                } else {
                                    response.setStatus("400 Bad Request");
                                    response.setPayload("Failed to update user information\n");
                                }

                            } else {
                                response.setStatus("400 Bad Request");
                                response.setPayload("Invalid request: Missing required fields (Name, Bio, Image)" + "\n");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            response.setStatus("500 Internal Server Error");
                            response.setPayload("Internal server error while updating user information\n");
                        }

                    } else {
                        response.setStatus("401 Unauthorized");
                        response.setPayload("Unauthorized: You can only update your own information\n");
                    }

                } else {
                    response.setStatus("401 Unauthorized");
                    response.setPayload("Unauthorized: Invalid or missing token\n");
                }
                break;

            default:
                break;
        }
        return response;
    }

    private ResponseContext sessions(RequestContext request){
        UserManager manager = UserManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        ObjectMapper mapper = new ObjectMapper();

        switch (request.getHttp_verb()){
            case "POST":
                try {
                    JsonNode jsonNode = mapper.readTree(request.getPayload());

                    if ( jsonNode.has("Username") && jsonNode.has("Password")){

                        if (manager.loginUser(jsonNode.get("Username").asText(),jsonNode.get("Password").asText())) {
                            response.setStatus("200 OK");
                            response.setPayload("Login successful\n");
                        }

                        else {
                            response.setStatus("401 Unauthorized");
                            response.setPayload("Login failed: Invalid username or password\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    response.setStatus("500 Internal Server Error");
                    response.setPayload("Login failed: Internal server error");
                }
                break;

            case "DELETE":
                try {
                    JsonNode jsonNode = mapper.readTree(request.getPayload());

                    if (jsonNode.has("Username") && jsonNode.has("Password")){

                        if (manager.logoutUser(jsonNode.get("Username").asText(), jsonNode.get("Password").asText())) {
                            response.setStatus("200 OK");
                            response.setPayload("Logout successful");

                        } else {
                            response.setStatus("401 Unauthorized");
                            response.setPayload("Logout failed: Invalid credentials or user not logged in");
                        }

                    } else {
                        response.setStatus("400 Bad Request");
                        response.setPayload("Invalid request: Username and password required");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    response.setStatus("500 Internal Server Error");
                    response.setPayload("Internal server error during logout");
                }
                break;

            default:
                break;
        }
        return response;
    }

    private ResponseContext packages(RequestContext request){
        CardManager manager = CardManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        if (request.getHttp_verb().equals("POST")){
            UserManager userManager = UserManager.getInstance();

            if (request.getHeader_values().containsKey("authorization:") && !userManager.isAdmin(request.getHeader_values().get("authorization:"))){
                response.setStatus("403 Forbidden");
                response.setPayload("Package creation failed: Unauthorized access");
                return response;
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                List<Card> cards = mapper.readValue(request.getPayload(), new TypeReference<>(){});

                if (cards.size() == 5){
                    List<Card> createdCards = new ArrayList<>();

                    for (Card card: cards){

                        if (manager.registerCard(card.getId(),card.getName(),card.getDamage())) {
                            createdCards.add(card);

                        } else {
                            for (Card card_tmp: createdCards){
                                manager.deleteCard(card_tmp.getId());
                            }

                            response.setStatus("409 Conflict");
                            response.setPayload("Package creation failed: Error in registering cards");
                            return response;
                        }
                    }

                    if (manager.createPackage(cards)){
                        response.setStatus("201 Created");
                        response.setPayload("Package creation successful\n");

                    } else {
                        for (Card card_tmp: createdCards){
                            manager.deleteCard(card_tmp.getId());
                        }
                        response.setStatus("409 Conflict");
                        response.setPayload("Package creation failed: Error in creating package");
                    }
                }

                else {
                    response.setStatus("400 Bad Request");
                    response.setPayload("Package creation failed: Invalid card count");
                }

            } catch (IOException e) {
                e.printStackTrace();
                response.setStatus("500 Internal Server Error");
                response.setPayload("Package creation failed: Internal server error");
            }
        }
        return response;
    }

    private ResponseContext transactionsPackages(User user, RequestContext request){
        CardManager manager = CardManager.getInstance();

        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        if (request.getHttp_verb().equals("POST")) {
            int result = manager.acquirePackage2User(user);

            if (result == 1) {
                response.setStatus("200 OK");
                response.setPayload("Package acquisition successful\n");

            } else if (result == -1) {
                response.setStatus("409 Conflict");
                response.setPayload("Package acquisition failed: Insufficient funds\n");

            } else if (result == 0) {
                response.setStatus("404 Not Found");
                response.setPayload("Package acquisition failed: No available packages\n");
            }
        }
        return response;
    }


    private ResponseContext showCards(User user, RequestContext request){
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        if ("GET".equals(request.getHttp_verb())) {

            if (user != null) {
                String json = CardManager.getInstance().showUserCards(user);

                if (json != null) {
                    response.setStatus("200 OK");
                    response.setPayload(json + "\n");

                } else {
                    response.setStatus("404 Not Found");
                    response.setPayload("No cards found for user");

                }
            } else {
                response.setStatus("401 Unauthorized");
                response.setPayload("User not authorized or token missing");
            }
        }
        return response;
    }


    private ResponseContext requestDeck(User user, RequestContext request){
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");
        CardManager manager = CardManager.getInstance();

        switch (request.getHttp_verb()) {
            case "GET":

                if (user != null) {
                    String json = manager.showUserDeck(user);

                    if (json != null){
                        response.setStatus("200 OK");
                        response.setPayload(json + "\n");

                    } else {
                        response.setStatus("404 Not Found");
                        response.setPayload("Deck not configured for user\n");
                    }

                } else {
                    response.setStatus("401 Unauthorized");
                    response.setPayload("User not authorized or token missing\n");
                }
                break;

            case "PUT":
                if (user != null) {

                    ObjectMapper mapper = new ObjectMapper();

                    try {
                        List<String> ids = mapper.readValue(request.getPayload(), new TypeReference<>(){});
                        if (ids.size() == 4){

                            if (manager.createDeck(user, ids)){
                                response.setStatus("201 Created");
                                response.setPayload("Deck successfully configured for user " + user.getUsername() + "\n");

                            } else {
                                response.setStatus("409 Conflict");
                                response.setPayload("Deck update failed\n");
                            }

                        } else {
                            response.setStatus("400 Bad Request");
                            response.setPayload("Invalid request: Incorrect number of cards\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        response.setStatus("500 Internal Server Error");
                        response.setPayload("Internal server error during deck update\n");
                    }

                } else {
                    response.setStatus("401 Unauthorized");
                    response.setPayload("User not authorized or token missing");
                }
                break;

            default:
                response.setStatus("405 Method Not Allowed");
                response.setPayload("Invalid request method.");
                break;
        }
        return response;
    }

    public ResponseContext handleFormatPlainDeck(User user, RequestContext request) {
        ResponseContext response = new ResponseContext("400 Bad Request", "text/plain");

        if ("GET".equals(request.getHttp_verb())) {

            String plainTextDeck = CardManager.getInstance().showUserPlainDeck(user);

            if (plainTextDeck != null && !plainTextDeck.isEmpty()) {
                logger.info("Setting content type to text/plain");
                response.setStatus("200 OK");
                response.setPayload(plainTextDeck);
                logger.info("Response content type set to: " + response.getContentType());

            } else {
                response.setStatus("404 Not Found");
                response.setPayload("Deck not configured for user\n");
            }

        } else {
            response.setStatus("405 Method Not Allowed");
            response.setPayload("Invalid request method: Only GET is supported for this endpoint.");
        }
        return response;
    }


    private ResponseContext stats(User user,RequestContext request){
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        if ("GET".equals(request.getHttp_verb())) {
            response.setStatus("200 OK");
            response.setPayload(user.getStats() + "\n");
        }
        return response;
    }

    private ResponseContext scoreboard(RequestContext request){
        BattleManager manager = BattleManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        if ("GET".equals(request.getHttp_verb())) {
            response.setPayload(manager.getScoreboard());
            response.setStatus("200 OK");
        }
        return response;
    }

    private ResponseContext trade(RequestContext request, User user) {
        TradeManager manager = TradeManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        String[] parts;
        ObjectMapper mapper = new ObjectMapper();

        switch (request.getHttp_verb()) {
            case "GET":
                String marketplace = manager.showMarketplace();
                response.setPayload(marketplace.isEmpty() ? "Marketplace is empty." : marketplace);
                response.setStatus("200 OK");
                break;

            case "POST":
                parts = request.getRequested().split("/");

                if (parts.length == 3) {
                    try {
                        JsonNode jsonNode = mapper.readTree(request.getPayload());

                        if (jsonNode.has("CardToTrade")) {

                            if (manager.tradeCards(user, parts[2], jsonNode.get("CardToTrade").asText())) {
                                response.setStatus("200 OK");
                                response.setPayload("Card trade successful.\n");

                            } else {
                                response.setStatus("400 Bad Request");
                                response.setPayload("Failed to trade card.\n");
                            }

                        } else {
                            response.setStatus("400 Bad Request");
                            response.setPayload("Failed to trade card with yourself.\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        response.setStatus("500 Internal Server Error");
                        response.setPayload("Error processing card trade request.\n");
                    }

                } else {
                    try {
                        JsonNode jsonNode = mapper.readTree(request.getPayload());
                        if (jsonNode.has("Id") && jsonNode.has("CardToTrade") && jsonNode.has("Type") && jsonNode.has("MinimumDamage")) {

                            if (manager.card2market(user, jsonNode.get("Id").asText(), jsonNode.get("CardToTrade").asText(), (float) jsonNode.get("MinimumDamage").asDouble(), jsonNode.get("Type").asText())) {
                                response.setStatus("201 Created");
                                response.setPayload("Trading deal successfully created.\n");

                            } else {
                                response.setStatus("400 Bad Request");
                                response.setPayload("Trading deal creation failed\n");
                            }

                        } else {
                            response.setStatus("400 Bad Request");
                            response.setPayload("Invalid request format for trading deal creation.\n");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        response.setStatus("500 Internal Server Error");
                        response.setPayload("Error processing trading deal creation request.\n");
                    }
                }
                break;

            case "DELETE":
                parts = request.getRequested().split("/");

                if (parts.length == 3) {

                    if (manager.removeTrade(user, parts[2])) {
                        response.setStatus("200 OK");
                        response.setPayload("Trade removed successfully.\n");

                    } else {
                        response.setStatus("400 Bad Request");
                        response.setPayload("Failed to remove trade.\n");
                    }

                } else {
                    response.setStatus("400 Bad Request");
                    response.setPayload("Invalid request format for removing trade.\n");
                }
                break;

            default:
                response.setStatus("405 Method Not Allowed");
                response.setPayload("Invalid request method.\n");
                break;
        }
        return response;
    }


    private ResponseContext battle(RequestContext request,User user){
        ResponseContext response = new ResponseContext("400 Bad Request", "application/json");

        if ("POST".equals(request.getHttp_verb())) {
            BattleManager manager = BattleManager.getInstance();
            String payload = manager.addUser(user);

            if (payload != null){
                response.setPayload(payload + "\n");
                response.setStatus("200 OK");
            }
        }
        return response;
    }

    private User authorize(RequestContext request){
        User user = null;

        if (request.getHeader_values().containsKey("authorization:")){
            UserManager manager = UserManager.getInstance();
            user = manager.authorizeUser(request.getHeader_values().get("authorization:"));
            logger.info("User authorized: " + user.getUsername());
        }

        return user;
    }
}
