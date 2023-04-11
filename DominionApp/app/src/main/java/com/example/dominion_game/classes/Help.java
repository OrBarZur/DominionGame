/**
 * Help is a class with static function in it that are general
 * and could be used in any class as a help.
 */
package com.example.dominion_game.classes;

import android.util.Pair;

import com.example.dominion_game.cards_classes.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Help {

    /*
    public static String toStringHash(HashMap<String, Integer> hm) {
        String s = "";
        for (String cardName : hm.keySet())
            s = s.concat(cardName + " - " + hm.get(cardName) + "  ");

        return s;
    }

    public static String toStringArray(ArrayList<String> al) {
        HashMap<String, Integer> hm = new HashMap<>();
        for (String cardName : al)
            if (hm.containsKey(cardName))
                hm.put(cardName, hm.get(cardName) + 1);
            else
                hm.put(cardName, 1);

        return toStringHash(hm);
    }
    */

    /**
     * A function that convert HashMap to an ArrayList.
     * @param hm A HashMap of card names with the amount of it
     * @return An ArrayList of all cards in hm
     */
    public static ArrayList<String> hashToArray(HashMap<String, Integer> hm) {
        ArrayList<String> al = new ArrayList<>();
        for (String cardName : hm.keySet())
            for (int i = 0; i < hm.get(cardName); i++)
                al.add(cardName);

        return al;
    }

    /**
     * A function that returns the size of HashMap according to the values.
     * @param hm A HashMap of card names with the amount of it
     * @return An Integer with the amount of cards in hm
     */
    public static int sizeOfHash(HashMap<String, Integer> hm) {
        int count = 0;
        for (String cardName : hm.keySet())
            count += hm.get(cardName);

        return count;
    }

    public static ArrayList<String> arrayListOfPairsToArrayList(ArrayList<Pair<String, Boolean>> alPairs) {
        ArrayList<String> al = new ArrayList<>();
        for (Pair<String, Boolean> pair : alPairs)
            al.add(pair.first);

        return al;
    }

    /**
     * A function that takes the name of a card and returns the class of this specific card.
     * This function is used for calling functions in this class, for example: play.
     * @param name A String with the name of a card
     * @return A Class that extends from Card - a specific card
     */
    public static Card nameToCard(String name) {
        switch (name) {
            case "Artisan":
                return new Artisan();
            case "Bandit":
                return new Bandit();
            case "Bureaucrat":
                return new Bureaucrat();
            case "Cellar":
                return new Cellar();
            case "Chapel":
                return new Chapel();
            case "Copper":
                return new Copper();
            case "CouncilRoom":
                return new CouncilRoom();
            case "Festival":
                return new Festival();
            case "Gardens":
                return new Gardens();
            case "Gold":
                return new Gold();
            case "Harbinger":
                return new Harbinger();
            case "Laboratory":
                return new Laboratory();
            case "Library":
                return new Library();
            case "Market":
                return new Market();
            case "Merchant":
                return new Merchant();
            case "Militia":
                return new Militia();
            case "Mine":
                return new Mine();
            case "Moat":
                return new Moat();
            case "Moneylender":
                return new Moneylender();
            case "Poacher":
                return new Poacher();
            case "Remodel":
                return new Remodel();
            case "Sentry":
                return new Sentry();
            case "Silver":
                return new Silver();
            case "Smithy":
                return new Smithy();
            case "ThroneRoom":
                return new ThroneRoom();
            case "Vassal":
                return new Vassal();
            case "Estate":
                return new Victory("Estate");
            case "Duchy":
                return new Victory("Duchy");
            case "Province":
                return new Victory("Province");
            case "Curse":
                return new Victory("Curse");
            case "Village":
                return new Village();
            case "Witch":
                return new Witch();
            case "Workshop":
                return new Workshop();
        }
        return null;
    }

    /**
     * A function that returns all the cards of the expansions.
     * @param expansion A String with the specific expansion
     * @return A Set of all cards of the specific expansion
     */
    public static Set<String> getExpansionCards(String expansion) {
        switch (expansion) {
            case "Base":
                return base();
        }
        return new HashSet<>();
    }

    /**
     * A function that returns all the cards of the base action cards game.
     * @return A set of all the cards of the base action cards game
     */
    public static Set<String> base() {
        return new HashSet<>(Arrays.asList(
                "Artisan",
                "Bandit",
                "Bureaucrat",
                "Cellar",
                "Chapel",
                "CouncilRoom",
                "Festival",
                "Gardens",
                "Harbinger",
                "Laboratory",
                "Library",
                "Market",
                "Merchant",
                "Militia",
                "Mine",
                "Moat",
                "Moneylender",
                "Poacher",
                "Remodel",
                "Sentry",
                "Smithy",
                "ThroneRoom",
                "Vassal",
                "Village",
                "Witch",
                "Workshop"));
    }

    /**
     * A function that randomizes all cards of the specific expansions
     * and returns the set of cards sorted for a new game.
     * @param expansions A String Array with the expansions that can be included in tis game
     * @param numberOfCards An Integer with the number of cards that should be in this set
     * @param cardsToInsert A String Array with specific cards that should be in this game
     * @return A String Array with randomized sorted cards for a new game
     */
    public static String[] getRandomCards(String[] expansions, int numberOfCards, String[] cardsToInsert) {
        Set<String> allCards = new HashSet<>();
        for (String expansion : expansions)
            allCards.addAll(getExpansionCards(expansion));

        allCards.removeAll(Arrays.asList(cardsToInsert));
        ArrayList<String> allCardsArray = new ArrayList<>(Arrays.asList(allCards.toArray(new String[0])));

        String[] cards = new String[numberOfCards];
        Random random = new Random();
        for (int i = 0; i < cards.length; i++)
            if (i < cardsToInsert.length)
                cards[i] = cardsToInsert[i];
            else
                cards[i] = allCardsArray.remove(random.nextInt(allCardsArray.size()));

        return Help.sort(cards);
    }

    /**
     * A function that sorts the cards by price and by name.
     * @param cards A String Array with the cards that are in this game
     * @return A String Array with the cards that are in this game sorted
     */
    public static String[] sort(String[] cards) {
        ArrayList<String> arrayCards = new ArrayList<>(Arrays.asList(cards));
        Collections.sort(arrayCards, new Comparator<String>() {
            /**
             * A function that compares between two values in the array of cards
             * and returns which is first by price and name.
             * @param s1 A String 1
             * @param s2 A String 2
             * @return An Integer that is zero if the objects are equal,
             * a positive value if s1 is greater than s2
             * and a negative value otherwise.
             */
            @Override
            public int compare(String s1, String s2) {
                int first = Integer.valueOf(Help.nameToCard(s1).getPrice()).compareTo(Help.nameToCard(s2).getPrice()); // compared by price

                if (first != 0) // if the cards don't have the same price
                    return first;

                return s1.compareTo(s2); // compared by name
            }
        });
        return arrayCards.toArray(new String[0]);
    }
}
