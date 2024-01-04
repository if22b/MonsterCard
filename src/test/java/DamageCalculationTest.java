import mtcg.Card;
import mtcg.managers.CardManager;
import mtcg.managers.BattleManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DamageCalculationTest {

    @Test
    public void FireElf_ability() {
        CardManager cardManager = CardManager.getInstance();
        String CardName1 = "FireElf";
        float CardDamage1 = 15;
        String CardName2 = "WaterDragon";
        float CardDamage2 = 15;
        Card card1 = new Card("1",CardName1,CardDamage1, cardManager.createCardType(CardName1), cardManager.createElementType(CardName1));
        Card card2 = new Card("2",CardName2,CardDamage2, cardManager.createCardType(CardName2), cardManager.createElementType(CardName2));
        BattleManager manager = BattleManager.getInstance();
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card2,card1);
        assertEquals(15,result1);
        assertEquals(0,result2);
    }

    @Test
    public void Knight_ability() {
        CardManager cardManager = CardManager.getInstance();
        String CardName1 = "Knight";
        float CardDamage1 = 50;
        String CardName2 = "WaterSpell";
        float CardDamage2 = 100;
        Card card1 = new Card("1",CardName1,CardDamage1, cardManager.createCardType(CardName1), cardManager.createElementType(CardName1));
        Card card2 = new Card("2",CardName2,CardDamage2, cardManager.createCardType(CardName2), cardManager.createElementType(CardName2));
        BattleManager manager = BattleManager.getInstance();
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card2,card1);
        assertEquals(-1,result1);
        assertEquals(50,result2);
    }

    @Test
    public void Kraken_ability() {
        CardManager cardManager = CardManager.getInstance();
        String CardName1 = "Kraken";
        float CardDamage1 = 50;
        String CardName2 = "Spell";
        float CardDamage2 = 100;
        Card card1 = new Card("1",CardName1,CardDamage1, cardManager.createCardType(CardName1), cardManager.createElementType(CardName1));
        Card card2 = new Card("2",CardName2,CardDamage2, cardManager.createCardType(CardName2), cardManager.createElementType(CardName2));
        BattleManager manager = BattleManager.getInstance();
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card2,card1);
        assertEquals(50,result1);
        assertEquals(0,result2);
    }

    @Test
    public void Ork_ability() {
        CardManager cardManager = CardManager.getInstance();
        String CardName1 = "Ork";
        float CardDamage1 = 50;
        String CardName2 = "Wizard";
        float CardDamage2 = 100;
        Card card1 = new Card("1",CardName1,CardDamage1, cardManager.createCardType(CardName1), cardManager.createElementType(CardName1));
        Card card2 = new Card("2",CardName2,CardDamage2, cardManager.createCardType(CardName2), cardManager.createElementType(CardName2));
        BattleManager manager = BattleManager.getInstance();
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card2,card1);
        assertEquals(0,result1);
        assertEquals(100,result2);
    }

    @Test
    public void WaterSpell_ability() {
        CardManager cardManager = CardManager.getInstance();
        String CardName1 = "WaterSpell";
        String CardName2 = "FireSpell";
        String CardName3 = "NormalSpell";
        float damage = 50;
        Card card1 = new Card("1",CardName1,damage, cardManager.createCardType(CardName1), cardManager.createElementType(CardName1));
        Card card2 = new Card("2",CardName2,damage, cardManager.createCardType(CardName2), cardManager.createElementType(CardName2));
        Card card3 = new Card("2",CardName3,damage, cardManager.createCardType(CardName3), cardManager.createElementType(CardName3));
        BattleManager manager = BattleManager.getInstance();
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card1,card3);
        assertEquals(100,result1);
        assertEquals(25,result2);
    }
    @Test
    public void FireSpell_ability() {
        CardManager cardManager = CardManager.getInstance();
        String CardName1 = "WaterSpell";
        String CardName2 = "FireSpell";
        String CardName3 = "NormalSpell";
        float damage = 50;
        Card card1 = new Card("1",CardName1,damage, cardManager.createCardType(CardName1), cardManager.createElementType(CardName1));
        Card card2 = new Card("2",CardName2,damage, cardManager.createCardType(CardName2), cardManager.createElementType(CardName2));
        Card card3 = new Card("2",CardName3,damage, cardManager.createCardType(CardName3), cardManager.createElementType(CardName3));
        BattleManager manager = BattleManager.getInstance();
        float result1 = manager.calculateDamage(card2,card1);
        float result2 = manager.calculateDamage(card2,card3);
        assertEquals(25,result1);
        assertEquals(100,result2);
    }
    @Test
    public void NormalSpell_ability() {
        CardManager cardManager = CardManager.getInstance();
        String CardName1 = "WaterSpell";
        String CardName2 = "FireSpell";
        String CardName3 = "NormalSpell";
        float damage = 50;
        Card card1 = new Card("1",CardName1,damage, cardManager.createCardType(CardName1), cardManager.createElementType(CardName1));
        Card card2 = new Card("2",CardName2,damage, cardManager.createCardType(CardName2), cardManager.createElementType(CardName2));
        Card card3 = new Card("2",CardName3,damage, cardManager.createCardType(CardName3), cardManager.createElementType(CardName3));
        BattleManager manager = BattleManager.getInstance();
        float result1 = manager.calculateDamage(card3,card1);
        float result2 = manager.calculateDamage(card3,card2);
        assertEquals(100,result1);
        assertEquals(25,result2);
    }
    @Test
    public void NormalMonsterDamage() {
        CardManager cardManager = CardManager.getInstance();
        String CardName1 = "Dragon";
        String CardName2 = "Knight";
        String CardName3 = "Wizard";
        float damage = 50;
        Card card1 = new Card("1",CardName1,damage, cardManager.createCardType(CardName1), cardManager.createElementType(CardName1));
        Card card2 = new Card("2",CardName2,damage, cardManager.createCardType(CardName2), cardManager.createElementType(CardName2));
        Card card3 = new Card("2",CardName3,damage, cardManager.createCardType(CardName3), cardManager.createElementType(CardName3));
        BattleManager manager = BattleManager.getInstance();
        float result1 = manager.calculateDamage(card1,card2);
        float result2 = manager.calculateDamage(card1,card3);
        float result3 = manager.calculateDamage(card2,card1);
        float result4 = manager.calculateDamage(card2,card3);
        float result5 = manager.calculateDamage(card3,card1);
        float result6 = manager.calculateDamage(card3,card2);
        assertEquals(50,result1);
        assertEquals(50,result2);
        assertEquals(50,result3);
        assertEquals(50,result4);
        assertEquals(50,result5);
        assertEquals(50,result6);
    }
}
