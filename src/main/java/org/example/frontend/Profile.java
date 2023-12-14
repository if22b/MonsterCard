package org.example.frontend;

public class Profile {
    private String username;
    private String password;
    private int coins = 20;
    private int gamesPlayed = 0;
    private int elo = 100;

    public Profile(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
