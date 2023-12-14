package org.example.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Shop {
    public static final int PACKAGE_SIZE = 5;
    private List<Package> packages = new ArrayList<>();

    public Shop(List<Package> packages) {
        this.packages = packages;
    }

    public void purchaseCards(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Available Packages:");

        for (int i = 0; i < packages.size(); i++) {
            System.out.println((i + 1) + ". Package with " + packages.get(i).getCards().size() + " cards - Cost: " + packages.get(i).getCost() + " coins");
        }

        System.out.println("Enter the number of the package you want to buy:");
        int choice = scanner.nextInt();

        if (choice > 0 && choice <= packages.size()) {
            Package selectedPackage = packages.get(choice - 1);
            if (selectedPackage.buy(user)) {
                System.out.println("Successfully purchased the package!");
            }
        } else {
            System.out.println("Invalid choice. Please choose a valid package number.");
        }
    }

    public void tradeCards(Card card) {
        // For simplicity: Let's say a user can trade a card for 10 coins.
        System.out.println("Trading card " + card.getName() + " for 10 coins.");
        // Add logic to remove card from user's collection and add 10 coins to user's balance.
    }

    public void shopMenu() {
        System.out.println("Welcome to the Shop!");
        System.out.println("1. Purchase Cards");
        System.out.println("2. Trade Cards");
        System.out.println("3. Exit");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                purchaseCards(/* User instance needed here */);
                break;
            case 2:
                System.out.println("Select a card to trade: ");
                // Display user's cards and let them select one to trade.
                // tradeCards(selectedCard);
                break;
            case 3:
                System.out.println("Exiting the shop. Goodbye!");
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
                shopMenu();
                break;
        }
    }

    public void printInstructions(boolean wrongInput) {
        if (wrongInput) {
            System.out.println("Invalid input. Please follow the instructions carefully.");
        }
        System.out.println("Instructions:");
        System.out.println("1. You can purchase card packages from the shop.");
        System.out.println("2. Each package contains a set of random cards.");
        System.out.println("3. You can also trade in your existing cards for coins or other rewards.");
    }
}
