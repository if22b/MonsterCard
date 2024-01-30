import mtcg.Card;
import mtcg.collections.Deck;
import mtcg.enums.CardEnum;
import mtcg.enums.ElementEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckTest {

    List<Card> cards = new ArrayList<>();
    Deck deck;
    @BeforeEach
    void setUp() {
        cards.add(new Card("0","Blue Dragon",117, CardEnum.Dragon, ElementEnum.water));
        cards.add(new Card("1","Old Fire Elf",107, CardEnum.FireElf, ElementEnum.fire));
        cards.add(new Card("2","Green Goblin",87, CardEnum.Goblin, ElementEnum.normal));
        cards.add(new Card("3","Heavy Knight",120, CardEnum.Knight, ElementEnum.normal));
        cards.add(new Card("4","Deep Blue Kraken",140, CardEnum.Kraken, ElementEnum.water));
        cards.add(new Card("5","White Ork",98, CardEnum.Ork, ElementEnum.normal));
        cards.add(new Card("6","Dark Wizard",117, CardEnum.Wizard, ElementEnum.fire));
        cards.add(new Card("7","Blue Wave Spell",89, CardEnum.Spell, ElementEnum.water));
        cards.add(new Card("8","Red Fire Spell",100, CardEnum.Spell, ElementEnum.fire));
        cards.add(new Card("9","Normal Spell",100, CardEnum.Spell, ElementEnum.normal));
        cards.add(new Card("10","Red Dragon",123, CardEnum.Dragon, ElementEnum.fire));
        cards.add(new Card("11","Old Water Elf",106, CardEnum.FireElf, ElementEnum.water));
        cards.add(new Card("12","Dark Goblin",102, CardEnum.Goblin, ElementEnum.normal));
        cards.add(new Card("13","Strong Knight",119, CardEnum.Knight, ElementEnum.normal));
        cards.add(new Card("14","Deep Black Kraken",143, CardEnum.Kraken, ElementEnum.water));
        cards.add(new Card("15","Gray Ork",87, CardEnum.Ork, ElementEnum.normal));
        cards.add(new Card("16","White Wizard",110, CardEnum.Wizard, ElementEnum.normal));
        cards.add(new Card("17","Deep Ocean Spell",90, CardEnum.Spell, ElementEnum.water));
        cards.add(new Card("18","Flame Spell",112, CardEnum.Spell, ElementEnum.fire));
        cards.add(new Card("19","Normal Magic Spell",100, CardEnum.Spell, ElementEnum.normal));
        List<Card> tmp = new ArrayList<>();
        Card card1 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card1);
        tmp.add(card1);
        Card card2 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card2);
        tmp.add(card2);
        Card card3 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card3);
        tmp.add(card3);
        Card card4 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card4);
        tmp.add(card4);
        deck = new Deck(tmp);
    }

    @Test
    public void deck_size() {
        List<Card> tmp = new ArrayList<>();
        Card card1 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card1);
        tmp.add(card1);
        Card card2 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card2);
        tmp.add(card2);
        Card card3 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card3);
        tmp.add(card3);
        Card card4 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card4);
        tmp.add(card4);
        Card card5 = cards.get((int)(Math.random() * cards.size()));
        cards.remove(card5);
        tmp.add(card5);
        Deck deck1 = new Deck(tmp);
        assertEquals(4,deck1.getSize());
    }

    @Test
    public void deck_remove() {
        deck.removeCard(deck.getRandomCard());
        assertEquals(3,deck.getSize());
    }
}
