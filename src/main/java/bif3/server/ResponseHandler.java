package bif3.server;
import bif3.database.DatabaseService;
import bif3.mtcg.*;
import bif3.mtcg.managers.CardManager;
import bif3.mtcg.managers.CombatManager;
import bif3.mtcg.managers.TradeManager;
import bif3.mtcg.managers.UserManager;
import bif3.server.context.RequestContext;
import bif3.server.context.ResponseContext;

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

// Handle request and send response
public class ResponseHandler {

    BufferedWriter writer;

    public ResponseHandler(BufferedWriter writer){
        this.writer = writer;
    }

    public void response(RequestContext request) {
        ResponseContext response = new ResponseContext("400 Bad Request");
        if ( request != null && request.getHeader_values() != null /*&& request.getHeader_values().containsKey("content-type:") && request.getHeader_values().get("content-type:").equalsIgnoreCase("application/json")*/ ){
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
                                response = new ResponseContext("401 Unauthorized");
                            }
                        }
                        break;
                    case "cards":
                        user = authorize(request);
                        if (user != null){
                            response = showCards(user,request);
                        } else {
                            response = new ResponseContext("401 Unauthorized");
                        }
                        break;
                    case "deck":
                        user = authorize(request);
                        if (user != null){
                            response = requestDeck(user,request);
                        } else {
                            response = new ResponseContext("401 Unauthorized");
                        }
                        break;
                    case "stats":
                        user = authorize(request);
                        if (user != null){
                            response = stats(user,request);
                        } else {
                            response = new ResponseContext("401 Unauthorized");
                        }
                        break;
                    case "score":
                        user = authorize(request);
                        if (user != null){
                            response = scoreboard(request);
                        } else {
                            response = new ResponseContext("401 Unauthorized");
                        }
                        break;
                    case "tradings":
                        user = authorize(request);
                        if (user != null){
                            response = trade(request,user);
                        } else {
                            response = new ResponseContext("401 Unauthorized");
                        }
                        break;
                    case "battles":
                        user = authorize(request);
                        if (user != null){
                            response = battle(request,user);
                        } else {
                            response = new ResponseContext("401 Unauthorized");
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        // Send response
        try {
            writer.write(response.getHttp_version() + " " + response.getStatus() + "\r\n");
            writer.write("Server: " + response.getServer() + "\r\n");
            writer.write("Content-Type: " + response.getContentType() + "\r\n");
            writer.write("Content-Length: " + response.getContentLength() + "\r\n\r\n");
            writer.write(response.getPayload());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ResponseContext deleteAll(RequestContext request){
        ResponseContext response = new ResponseContext("400 Bad Request");
        UserManager userManager = UserManager.getInstance();
        /*if (request.getHeader_values().containsKey("authorization:") && !userManager.isAdmin(request.getHeader_values().get("authorization:"))){
            response.setStatus("403 Forbidden");
            return response;
        }*/
        if (request.getHttp_verb().equals("DELETE")) {
            try {
                Connection conn = DatabaseService.getInstance().getConnection();
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
        ResponseContext response = new ResponseContext("400 Bad Request");
        ObjectMapper mapper;
        User user;
        switch (request.getHttp_verb()) {
            case "GET":
                user = authorize(request);
                if (user != null){
                    String[] parts = request.getRequested().split("/");
                    if (parts.length == 3){
                        if (user.getUsername().equals(parts[2])){
                            response.setPayload(user.getInfo());
                            if (response.getPayload() != null){
                                response.setStatus("200 OK");
                            } else {
                                response.setStatus("404 Not Found");
                            }
                        } else {
                            response.setStatus("401 Unauthorized");
                        }
                    }
                } else {
                    response.setStatus("401 Unauthorized");
                }
                break;
            case "POST":
                mapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = mapper.readTree(request.getPayload());
                    if ( jsonNode.has("Username") && jsonNode.has("Password")){
                        if (manager.registerUser(jsonNode.get("Username").asText(),jsonNode.get("Password").asText())) {
                            response.setStatus("201 Created");
                        } else {
                            response.setStatus("409 Conflict");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "PUT":
                user = authorize(request);
                if (user != null){
                    String[] editUser = request.getRequested().split("/");
                    if (editUser.length == 3){
                        if (user.getUsername().equals(editUser[2])){
                            mapper = new ObjectMapper();
                            try {
                                JsonNode jsonNode = mapper.readTree(request.getPayload());
                                if ( jsonNode.has("Name") && jsonNode.has("Bio") && jsonNode.has("Image")){
                                    if (user.setUserInfo(jsonNode.get("Name").asText(),jsonNode.get("Bio").asText(),jsonNode.get("Image").asText())){
                                        response.setStatus("200 OK");
                                    } else {
                                        response.setStatus("404 Not Found");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            response.setStatus("401 Unauthorized");
                        }
                    }
                } else {
                    response.setStatus("401 Unauthorized");
                }
                break;
            default:
                break;
        }
        return response;
    }

    private ResponseContext sessions(RequestContext request){
        UserManager manager = UserManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request");
        ObjectMapper mapper = new ObjectMapper();
        switch (request.getHttp_verb()){
            case "POST":
                try {
                    JsonNode jsonNode = mapper.readTree(request.getPayload());
                    if ( jsonNode.has("Username") && jsonNode.has("Password")){
                        if (manager.loginUser(jsonNode.get("Username").asText(),jsonNode.get("Password").asText())) {
                            response.setStatus("200 OK");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "DELETE":
                try {
                    JsonNode jsonNode = mapper.readTree(request.getPayload());
                    if ( jsonNode.has("Username") && jsonNode.has("Password")){
                        if (manager.logoutUser(jsonNode.get("Username").asText(),jsonNode.get("Password").asText())) {
                            response.setStatus("200 OK");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return response;
    }

    private ResponseContext packages(RequestContext request){
        CardManager manager = CardManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request");
        if (request.getHttp_verb().equals("POST")){
            UserManager userManager = UserManager.getInstance();
            if (request.getHeader_values().containsKey("authorization:") && !userManager.isAdmin(request.getHeader_values().get("authorization:"))){
                response.setStatus("403 Forbidden");
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
                            return response;
                        }
                    }
                    if(manager.createPackage(cards)){
                        response.setStatus("201 Created");
                    } else {
                        for (Card card_tmp: createdCards){
                            manager.deleteCard(card_tmp.getId());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    private ResponseContext transactionsPackages(User user,RequestContext request){
        CardManager manager = CardManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request");
        if (request.getHttp_verb().equals("POST")) {
            if (manager.acquirePackage2User(user)){
                response.setStatus("200 OK");
            } else {
                response.setStatus("409 Conflict");
            }
        }
        return response;
    }

    private ResponseContext showCards(User user,RequestContext request){
        ResponseContext response = new ResponseContext("400 Bad Request");
        if ("GET".equals(request.getHttp_verb())) {
            String json = CardManager.getInstance().showUserCards(user);
            if (json != null) {
                response.setStatus("200 OK");
                response.setPayload(json);
            } else {
                response.setStatus("410 Error");
            }
        }
        return response;
    }

    private ResponseContext requestDeck(User user,RequestContext request){
        ResponseContext response = new ResponseContext("400 Bad Request");
        CardManager manager = CardManager.getInstance();
        switch (request.getHttp_verb()) {
            case "GET":
                String json = manager.showUserDeck(user);
                if (json != null){
                    response.setStatus("200 OK");
                    response.setPayload(json);
                } else {
                    response.setStatus("410 Error");
                }
                break;
            case "PUT":
                ObjectMapper mapper = new ObjectMapper();
                try {
                    List<String> ids = mapper.readValue(request.getPayload(), new TypeReference<>(){});
                    if (ids.size() == 4){
                        if (manager.createDeck(user,ids)){
                            response.setStatus("201 Created");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return response;
    }

    private ResponseContext stats(User user,RequestContext request){
        ResponseContext response = new ResponseContext("400 Bad Request");
        if ("GET".equals(request.getHttp_verb())) {
            response.setStatus("200 OK");
            response.setPayload(user.getStats());
        }
        return response;
    }

    private ResponseContext scoreboard(RequestContext request){
        CombatManager manager = CombatManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request");
        if ("GET".equals(request.getHttp_verb())) {
            response.setPayload(manager.getScoreboard());
            response.setStatus("200 OK");
        }
        return response;
    }

    private ResponseContext trade(RequestContext request, User user){
        TradeManager manager = TradeManager.getInstance();
        ResponseContext response = new ResponseContext("400 Bad Request");
        String[] parts;
        switch (request.getHttp_verb()) {
            case "GET":
                response.setPayload(manager.showMarketplace());
                response.setStatus("200 OK");
                break;
            case "POST":
                parts = request.getRequested().split("/");
                ObjectMapper mapper = new ObjectMapper();
                if (parts.length == 3){
                    try {
                        JsonNode jsonNode = mapper.readTree(request.getPayload());
                        if (jsonNode.has("Card2Trade")){
                            if (manager.tradeCards(user,parts[2],jsonNode.get("Card2Trade").asText())){
                                response.setStatus("200 OK");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JsonNode jsonNode = mapper.readTree(request.getPayload());
                        if (jsonNode.has("Id") && jsonNode.has("CardToTrade") && jsonNode.has("Type") && jsonNode.has("MinimumDamage")){
                            if (manager.card2market(user,jsonNode.get("Id").asText(),jsonNode.get("CardToTrade").asText(),(float)jsonNode.get("MinimumDamage").asDouble(),jsonNode.get("Type").asText())){
                                response.setStatus("201 Created");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "DELETE":
                parts = request.getRequested().split("/");
                if (parts.length == 3){
                    if (manager.removeTrade(user,parts[2])){
                        response.setStatus("200 OK");
                    }
                }
                break;
            default:
                break;
        }
        return response;
    }

    private ResponseContext battle(RequestContext request,User user){
        ResponseContext response = new ResponseContext("400 Bad Request");
        if ("POST".equals(request.getHttp_verb())) {
            CombatManager manager = CombatManager.getInstance();
            String payload = manager.addUser(user);
            if (payload != null){
                response.setPayload(payload);
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
        }
        return user;
    }
}
