package org.example.frontend;

public class BattleRound {
    private int roundID;
    private Card user1Card;
    private Card user2Card;
    private User winner;

    // Constructor
    public BattleRound(int roundID, Card user1Card, Card user2Card) {
        this.roundID = roundID;
        this.user1Card = user1Card;
        this.user2Card = user2Card;
    }

    // Getters and Setters
    public int getRoundID() {
        return roundID;
    }

    public void setRoundID(int roundID) {
        this.roundID = roundID;
    }

    public Card getUser1Card() {
        return user1Card;
    }

    public void setUser1Card(Card user1Card) {
        this.user1Card = user1Card;
    }

    public Card getUser2Card() {
        return user2Card;
    }

    public void setUser2Card(Card user2Card) {
        this.user2Card = user2Card;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    // Logic to determine round winner
    public void determineWinner(User user1, User user2) {
        if (user1Card.getDamage() > user2Card.getDamage()) {
            winner = user1;
            System.out.println("Winner of round " + roundID + ": " + user1.getProfile().getUsername());
        } else if (user1Card.getDamage() < user2Card.getDamage()) {
            winner = user2;
            System.out.println("Winner of round " + roundID + ": " + user2.getProfile().getUsername());
        } else {
            winner = null;  // It's a draw
            System.out.println("Round " + roundID + " is a draw!");
        }
    }
}
