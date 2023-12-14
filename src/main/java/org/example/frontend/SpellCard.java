package org.example.frontend;

class SpellCard extends Card {
    private String effectType;

    // Constructor
    public SpellCard(int cardID, String name, int damage, ElementType type, User owner, String effectType) {
        super(cardID, name, damage, type, owner);
        this.effectType = effectType;
    }

    // Getter and Setter for effectType
    public String getEffectType() {
        return effectType;
    }

    public void setEffectType(String effectType) {
        this.effectType = effectType;
    }

    // Overridden makeDamage method
    @Override
    public void makeDamage() {
        // Depending on the effectType, the damage can vary or have additional effects
        switch(effectType.toLowerCase()) {
            case "fire":
                System.out.println(getName() + " casts a fire spell dealing " + getDamage() + " damage!");
                break;
            case "ice":
                System.out.println(getName() + " casts an ice spell dealing " + getDamage() + " damage and slowing the enemy!");
                break;
            case "poison":
                System.out.println(getName() + " casts a poison spell dealing " + getDamage() + " damage over time!");
                break;
            default:
                System.out.println(getName() + " casts a mysterious spell dealing " + getDamage() + " damage!");
                break;
        }
    }

    @Override
    public String toString() {
        return super.toString() + " [Effect Type: " + effectType + "]";
    }
}
