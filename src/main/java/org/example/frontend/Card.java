package org.example.frontend;

class Card {
    private int cardID;
    private String name;
    private int damage;
    private ElementType type;
    private User owner;

    public Card(int cardID, String name, int damage, ElementType type, User owner) {
        this.cardID = cardID;
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.owner = owner;
    }

    // Getters
    public int getCardID() {
        return cardID;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public ElementType getType() {
        return type;
    }

    public User getOwner() {
        return owner;
    }

    // Setters
    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    // Methods
    @Override
    public String toString() {
        return "Card [ID: " + cardID + ", Name: " + name + ", Damage: " + damage + ", Type: " + type + ", Owner: " + owner.getProfile().getUser() + "]";
    }
}
