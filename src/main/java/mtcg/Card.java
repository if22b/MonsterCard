package mtcg;

import mtcg.enums.ElementEnum;
import mtcg.enums.CardEnum;

import lombok.Getter;


@Getter
public class Card {

    private String id;

    private String name;

    private float damage;

    private CardEnum cardEnum;

    private ElementEnum elementEnum;

    public Card() {

    }

    public Card(String id, String name, float damage, CardEnum type, ElementEnum element) {
        this.id = id;
        this.name = name;
        this.damage = damage;

        cardEnum = type;
        elementEnum = element;
    }
}