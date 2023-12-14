package org.example.frontend;

import lombok.Getter;
import lombok.Setter;
import org.example.frontend.Card;
import org.example.frontend.Trade;

import java.util.List;
import java.util.Stack;

@Getter
@Setter
public class User {
    private Profile profile;
    private List<Card> deck;
    private List<Card> stack;
    private List<Trade> trades;
    private boolean isLoggedIn;

    // Constructors, getters, setters omitted for brevity

    // Logic to manage cards
    public void manageCards() {
        System.out.println("Managing cards...");
        // This could involve sorting, filtering, or other operations.
    }

    // Add card to user deck
    public void addCardToDeck(Card card) {
        if(!deck.contains(card) && stack.contains(card)) {
            deck.add(card);
            stack.remove(card); // Assuming when a card is added to the deck, it's removed from the stack.
            System.out.println(card.getName() + " added to deck!");
        } else {
            System.out.println("Cannot add card to deck. Either it's already in the deck or it's not in the user's stack.");
        }
    }

    // Add card to user stack
    public void addUserCard(Card card) {
        stack.add(card);
        System.out.println(card.getName() + " added to user's stack.");
    }

    // Print cards in the user's deck
    public void printDeck() {
        System.out.println("User's deck:");
        for(Card card : deck) {
            System.out.println(card.getName());
        }
    }

    // Logic to accept a trade
    public void acceptTrade(Trade trade) {
        if(trades.contains(trade)) {
            trade.accept(); // Using the accept() method from the Trade class.
            trades.remove(trade); // Removing the accepted trade from the list of trades.
            System.out.println("Trade accepted!");
        } else {
            System.out.println("Trade not found!");
        }
    }

    // Logic to reject a trade
    public void rejectTrade(Trade trade) {
        if(trades.contains(trade)) {
            trade.reject(); // Using the reject() method from the Trade class.
            trades.remove(trade); // Removing the rejected trade from the list of trades.
            System.out.println("Trade rejected!");
        } else {
            System.out.println("Trade not found!");
        }
    }

    // Logic to initiate a battle with an opponent
    public void battle(User opponent) {
        if (this.isLoggedIn && opponent.isLoggedIn()) {
            System.out.println("Battle initiated between " + this.profile.getName() + " and " + opponent.getProfile().getName());
            // Further logic to handle the battle mechanics, card plays, etc.
        } else {
            System.out.println("Both users should be logged in to initiate a battle.");
        }
    }
}