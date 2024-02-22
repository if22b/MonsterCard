import mtcg.Card;
import mtcg.User;
import mtcg.collections.Deck;
import mtcg.enums.CardEnum;
import mtcg.managers.BattleManager;
import mtcg.enums.ElementEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CombatTest {

    @Mock
    User userA;
    @Mock
    User userB;

    Deck deck_0;
    Deck deck_1; // Zweites Deck hinzugefügt

    @BeforeEach
    void setUp() {
        // Erstellen des ersten Decks
        List<Card> cards_0 = new ArrayList<>();

        cards_0.add(new Card("1", "Kraken_0", 0, CardEnum.Kraken, ElementEnum.water));
        cards_0.add(new Card("2", "Kraken_0", 0, CardEnum.Kraken, ElementEnum.water));
        cards_0.add(new Card("3", "Kraken_0", 0, CardEnum.Kraken, ElementEnum.water));
        cards_0.add(new Card("4", "Kraken_0", 0, CardEnum.Kraken, ElementEnum.water));

        deck_0 = new Deck(cards_0);

        // Erstellen des zweiten Decks
        List<Card> cards_1 = new ArrayList<>();

        cards_1.add(new Card("1", "Kraken_0", 0, CardEnum.Kraken, ElementEnum.water));
        cards_1.add(new Card("2", "Kraken_0", 0, CardEnum.Kraken, ElementEnum.water));
        cards_1.add(new Card("3", "Kraken_0", 0, CardEnum.Kraken, ElementEnum.water));
        cards_1.add(new Card("4", "Kraken_0", 0, CardEnum.Kraken, ElementEnum.water));

        deck_1 = new Deck(cards_1);
    }

    @Test
    public void drawCombatTest() {
        BattleManager manager = BattleManager.getInstance();

        when(userA.getName()).thenReturn("MockUser_1");
        when(userB.getName()).thenReturn("MockUser_2");

        // Verwenden von deck_0 für userA und deck_1 für userB, um ein Unentschieden zu simulieren
        manager.battle(userA, userB, deck_0, deck_1);

        verify(userA).battleDraw();
        verify(userB).battleDraw();
    }

    @Test
    public void winCombatTest() {
        BattleManager manager = BattleManager.getInstance();

        List<Card> cards_1 = new ArrayList<>();
        Card strongCard = new Card("5", "Kraken_30", 30, CardEnum.Kraken, ElementEnum.water);

        cards_1.add(strongCard);
        cards_1.add(strongCard);
        cards_1.add(strongCard);
        cards_1.add(strongCard);

        deck_1 = new Deck(cards_1);

        when(userA.getName()).thenReturn("MockUser_1");
        when(userB.getName()).thenReturn("MockUser_2");

        // Verwenden von deck_0 für userA und deck_1 für userB, um einen Sieg für userB zu simulieren
        manager.battle(userA, userB, deck_0, deck_1);

        verify(userA).battleLost();
        verify(userB).battleWon();
    }
}
