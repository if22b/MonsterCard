import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import MonsterCardGame.mtcg.Card;
import MonsterCardGame.mtcg.User;
import MonsterCardGame.mtcg.collections.Deck;
import MonsterCardGame.mtcg.enums.CardType;
import MonsterCardGame.mtcg.enums.ElementType;
import MonsterCardGame.mtcg.managers.BattleManager;

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

    @BeforeEach
    void setUp() {
        Card card1 = new Card ("1","Kraken_0",0, CardType.Kraken, ElementType.water);
        Card card2 = new Card ("2","Kraken_0",0, CardType.Kraken, ElementType.water);
        Card card3 = new Card ("3","Kraken_0",0, CardType.Kraken, ElementType.water);
        Card card4 = new Card ("4","Kraken_0",0, CardType.Kraken, ElementType.water);
        List<Card> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        deck_0 = new Deck(cards);
    }

    @Test
    public void drawCombatTest() {
        BattleManager manager = BattleManager.getInstance();
        when(userA.getName()).thenReturn("MockUser_1");
        when(userB.getName()).thenReturn("MockUser_2");
        manager.battle(userA,userB,deck_0,deck_0);
        verify(userA).battleDraw();
        verify(userB).battleDraw();
    }

    @Test
    public void winCombatTest() {
        BattleManager manager = BattleManager.getInstance();
        Card card = new Card ("2","Kraken_30",30, CardType.Kraken, ElementType.water);
        List<Card> cards = new ArrayList<>();
        cards.add(card);
        cards.add(card);
        cards.add(card);
        cards.add(card);
        Deck deck_1 = new Deck(cards);
        when(userA.getName()).thenReturn("MockUser_1");
        when(userB.getName()).thenReturn("MockUser_2");
        manager.battle(userA,userB,deck_0,deck_1);
        verify(userA).battleLost();
        verify(userB).battleWon();
    }
}
