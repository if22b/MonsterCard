package org.example.frontend;

import java.util.ArrayList;
import java.util.List;

class Package {
    private List<Card> cards = new ArrayList<>();
    private int cost;

    // Constructor
    public Package(List<Card> cards, int cost) {
        this.cards = cards;
        this.cost = cost;
    }

    public boolean buy(User user) {
        if(user.getCoins() >= cost) {
            user.setCoins(user.getCoins() - cost); // Deduct the cost from the user's coins

            // Add the cards from the package to the user's collection
            for(Card card : cards) {
                user.addCard(card);
            }

            return true; // Successful purchase
        } else {
            System.out.println("Insufficient coins! You need " + (cost - user.getCoins()) + " more coins to buy this package.");
            return false; // Failed purchase due to insufficient coins
        }
    }

    // Getters and setters
    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
