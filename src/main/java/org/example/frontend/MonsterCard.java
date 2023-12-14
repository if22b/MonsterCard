package org.example.frontend;

import org.example.frontend.Card;

class MonsterCard extends Card {
    private String specialAbility;

    // Constructor
    public MonsterCard(int cardID, String name, int damage, ElementType type, User owner, String specialAbility) {
        super(cardID, name, damage, type, owner);
        this.specialAbility = specialAbility;
    }

    // Getter and Setter for specialAbility
    public String getSpecialAbility() {
        return specialAbility;
    }

    public void setSpecialAbility(String specialAbility) {
        this.specialAbility = specialAbility;
    }

    // Overridden makeDamage method
    public void makeDamage() {
        // Basic logic for monster card damage using the special ability
        System.out.println("Monster " + getName() + " used " + specialAbility + " to deal " + getDamage() + " damage!");

        public void makeDamage() {
            int actualDamage = getDamage();

            switch(specialAbility) {
                case "DoubleStrike":
                    actualDamage *= 2;
                    System.out.println(getName() + " used DoubleStrike! Damage is doubled to " + actualDamage + "!");
                    break;
                case "Poison":
                    System.out.println(getName() + " used Poison! Enemy will take " + getDamage() + " damage over the next three turns!");
                    // Here, you'd integrate the mechanics for damage over time in your game.
                    break;
                case "Stun":
                    System.out.println(getName() + " used Stun! Enemy is stunned and can't move next turn!");
                    // Mechanism to disable the enemy's next move would be implemented in your game logic.
                    break;
                default:
                    System.out.println("Monster " + getName() + " deals " + getDamage() + " damage!");
                    break;
            }

            // Later to implement logic to deal the damage to the enemy, enemy.takeDamage(actualDamage);
        }
    }
    @Override
    public String toString() {
        return super.toString() + " [Special Ability: " + specialAbility + "]";
    }
}
