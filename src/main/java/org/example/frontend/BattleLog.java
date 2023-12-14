package org.example.frontend;

public class BattleLog {
    private int battleID;
    private User user1;
    private User user2;
    private User winner;

    // Constructor
    public BattleLog(int battleID, User user1, User user2, User winner) {
        this.battleID = battleID;
        this.user1 = user1;
        this.user2 = user2;
        this.winner = winner;
    }

    // Getters and Setters
    public int getBattleID() {
        return battleID;
    }

    public void setBattleID(int battleID) {
        this.battleID = battleID;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    // Method to log battle
    public void logBattle(User user1, User user2, User winner) {
        this.user1 = user1;
        this.user2 = user2;
        this.winner = winner;
        // You might also want to increment the battleID or retrieve it from some central database
        // or perhaps generate a unique ID based on the current time and other factors.
        this.battleID = generateBattleID(); // Assuming you have a method to generate unique IDs for battles

        // Optionally, you can print or store this information elsewhere
        System.out.println("Battle logged: " + user1.getProfile().getUsername() + " vs " + user2.getProfile().getUsername() + ". Winner: " + winner.getProfile().getUsername());
    }

    // Method to generate a unique ID for each battle
    private int generateBattleID() {
        // This is a basic example. In real-world applications, you'd likely
        // fetch this from a database or some centralized location to ensure uniqueness.
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
}
