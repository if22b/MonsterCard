package org.example.backend.app.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Card {
    @JsonAlias({"card_id"})
    private int cardId;

    @JsonAlias({"fk_user_id"})
    private int fkUserId;

    @JsonAlias({"name"})
    private String name;

    @JsonAlias({"damage"})
    private int damage;

    @JsonAlias({"element_type"})
    private int elementType;

    @JsonAlias({"isInDeck"})
    private boolean isInDeck;

    // Jackson needs the default constructor
    public Card() {}
}
