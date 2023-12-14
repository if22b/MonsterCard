/*
package MonsterCard.mtcg.collections;

import java.util.ArrayList;
import java.util.List;

import MonsterCard.mtcg.Card;

public class Deck {

    private List<Card> cards = new ArrayList<>();

    public Deck (List<Card> deck) {
        if (deck != null) {
            for (int i = 0; deck.size() > i && i < 4; i++) {
                this.cards.add(deck.get(i));
            }
        }
    }

    public void removeCard (Card card){
        if (cards != null){
            cards.remove(card);
        }
    }

    public void addCard (Card card){
        if (!cards.contains(card)){
            cards.add(card);
        }
    }

    public Card getRandomCard(){
        if (cards != null && cards.size() > 0){
            return cards.get((int)(Math.random() * cards.size()));
        }
        return null;
    }

    public boolean isEmpty(){
        return cards.isEmpty();
    }

    public int getSize(){
        if (!isEmpty()){
            return cards.size();
        }
        return 0;
    }
}
*/

package MonsterCardGame.mtcg.collections;

import java.util.ArrayList;
// import java.util.Collections;
import java.util.List;
import java.util.Random;

import MonsterCardGame.mtcg.Card;

public class Deck {

    private List<Card> cards;
    private static final int MAX_DECK_SIZE = 4; // Define max deck size if needed

    public Deck(List<Card> deck) {
        this.cards = new ArrayList<>();
        if (deck != null) {
            for (int i = 0; i < Math.min(deck.size(), MAX_DECK_SIZE); i++) {
                this.cards.add(deck.get(i));
            }
        }
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public void addCard(Card card) {
        if (!cards.contains(card) && cards.size() < MAX_DECK_SIZE) {
            cards.add(card);
        }
    }

    public Card getRandomCard() {
        if (!cards.isEmpty()) {
            return cards.get(new Random().nextInt(cards.size()));
        }
        return null;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int getSize() {
        return cards.size();
    }
}
