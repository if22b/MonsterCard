package org.example.backend.app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Battle {
    @JsonAlias({"battle_id"})
    private int battleId;

    @JsonAlias({"user_winner_id"})
    private int userWinnerId;

    @JsonAlias({"user_loser_id"})
    private int userLoserId;

    @JsonAlias({"log"})
    private String log;

    @JsonAlias({"time"})
    private String time;

    // Jackson needs the default constructor
    public Battle() {}
}
