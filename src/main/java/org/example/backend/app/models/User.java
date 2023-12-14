package org.example.backend.app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    @JsonAlias({"user_id"})
    private int userId;

    @JsonAlias({"name"})
    private String name;

    @JsonAlias({"elopoints"})
    private int eloPoints;

    @JsonAlias({"coins"})
    private int coins;

    // Jackson needs the default constructor
    public User() {}
}
