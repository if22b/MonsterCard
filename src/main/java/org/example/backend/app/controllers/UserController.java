package org.example.backend.app.controllers;

import org.example.backend.app.models.User;
import org.example.backend.app.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.backend.http.ContentType;
import org.example.backend.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.server.Response;

import java.util.List;

public class UserController extends Controller {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private UserService userService;

    public UserController(UserService userService) {
        setUserService(userService);
    }

    // GET /users
    public Response getUsers() {
        try {
            List<User> userData = getUserService().getUsers();
            String userDataJSON = getObjectMapper().writeValueAsString(userData);

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": " + userDataJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Error processing JSON\", \"data\": null }"
            );
        }
    }

    // GET /users/:id
    public Response getUserById(int id) {
        try {
            User user = getUserService().getUserById(id);
            if (user != null) {
                String userDataJSON = getObjectMapper().writeValueAsString(user);
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"data\": " + userDataJSON + ", \"error\": null }"
                );
            } else {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"error\": \"User not found\", \"data\": null }"
                );
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Error processing JSON\", \"data\": null }"
            );
        }
    }

    // POST /users
    public Response createUser(String body) {
        try {
            User newUser = getObjectMapper().readValue(body, User.class);
            getUserService().addUser(newUser);
            String newUserJSON = getObjectMapper().writeValueAsString(newUser);
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"data\": " + newUserJSON + ", \"error\": null }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Error processing JSON\", \"data\": null }"
            );
        }
    }

    // DELETE /users/:id
    public Response deleteUser(int id) {
        if (getUserService().deleteUser(id)) {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"data\": null, \"error\": null }"
            );
        } else {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"User not found or could not be deleted\", \"data\": null }"
            );
        }
    }
}
