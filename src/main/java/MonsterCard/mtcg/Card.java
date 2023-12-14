package MonsterCard.mtcg;

import MonsterCard.mtcg.types.CardType;
import MonsterCard.mtcg.types.ElementType;

import lombok.Getter;


public class Card {

    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private float damage;

    @Getter
    private CardType cardType;

    @Getter
    private ElementType elementType;

    public Card() {

    }

    public Card(String id, String name, float damage) {
        this.id = id;
        this.name = name;
        this.damage = damage;
    }

    public Card(String id, String name, float damage,CardType type, ElementType element) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        cardType = type;
        elementType = element;
    }
}