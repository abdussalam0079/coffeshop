package com.example.coffeshop2.Utils;

import android.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Provides randomized coffee suggestions per mood index (0=happy, 1=sad, 2=stressed, 3=relaxed).
 */
public class CoffeeRecommender {

    private static final Map<Integer, List<String>> coffeeMap = new HashMap<>();
    private static final Random random = new Random();

    static {
        coffeeMap.put(0, Arrays.asList( // Happy
                "Cappuccino",
                "Caramel Latte",
                "Vanilla Latte",
                "Hazelnut Coffee",
                "Iced Mocha",
                "White Chocolate Latte",
                "Honey Latte",
                "Irish Coffee"
        ));

        coffeeMap.put(1, Arrays.asList( // Sad
                "Mocha",
                "Dark Chocolate Coffee",
                "Hot Cocoa Coffee",
                "Brownie Latte",
                "Chocolate Cappuccino",
                "Nutella Coffee",
                "Milk Mocha"
        ));

        coffeeMap.put(2, Arrays.asList( // Stressed
                "Espresso",
                "Double Espresso",
                "Americano",
                "Black Coffee",
                "Flat White",
                "Ristretto",
                "Cold Brew",
                "Turkish Coffee"
        ));

        coffeeMap.put(3, Arrays.asList( // Relaxed
                "Latte",
                "Warm Milk Coffee",
                "Cinnamon Latte",
                "Caramel Macchiato",
                "Iced Latte",
                "Coconut Latte",
                "Almond Milk Coffee"
        ));
    }

    /**
     * Returns a random coffee and a short description for the given mood index.
     */
    public static Pair<String, String> getRandomCoffee(int moodIndex) {
        List<String> coffees = coffeeMap.get(moodIndex);
        if (coffees == null || coffees.isEmpty()) {
            return new Pair<>("House Blend", "A balanced pick for any mood.");
        }
        String coffee = coffees.get(random.nextInt(coffees.size()));
        String description = "Random pick for your mood: " + getMoodName(moodIndex);
        return new Pair<>(coffee, description);
    }

    public static String getMoodName(int index) {
        switch (index) {
            case 0:
                return "Happy";
            case 1:
                return "Sad";
            case 2:
                return "Stressed";
            case 3:
                return "Relaxed";
            default:
                return "Unknown";
        }
    }
}

