package mtcg;

import mtcg.enums.ElementEnum;
import mtcg.enums.CardEnum;

import lombok.Getter;


public class Card {

    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private float damage;

    @Getter
    private CardEnum cardEnum;

    @Getter
    private ElementEnum elementEnum;

    public Card() {

    }

    public Card(String id, String name, float damage) {
        this.id = id;
        this.name = name;
        this.damage = damage;
    }

    public Card(String id, String name, float damage, CardEnum type, ElementEnum element) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        cardEnum = type;
        elementEnum = element;
    }
}