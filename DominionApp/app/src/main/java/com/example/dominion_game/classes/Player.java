/**
 * Player is a class that keeps the player cards and handles in-game functions.
 */
package com.example.dominion_game.classes;

import android.util.Pair;
import com.example.dominion_game.activities.GameActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Player {
    private ArrayList<String> discard;
    private ArrayList<String> deck;
    private HashMap<String, Integer> hand;
    private ArrayList<Pair<String, Integer>> arrayHand;

    /**
     * The constructor with default values for the attributes
     */
    public Player() {
        this.discard = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.hand = new HashMap<>();
        this.arrayHand = new ArrayList<>();
    }

    /**
     * A function that transfers all cards from discard to deck and shuffles the deck.
     * @param gameManager A reference to gameManager
     * @param tabs An Integer which is the count of tabs in log that
     *             the line that would be added will have
     */
    public void discardToDeck(GameManager gameManager, int tabs) {
        gameManager.getLog().add(new LogLine("Shuffles his deck", gameManager.getGameManagerBeforeStart().getMyId(), false, true, "pink", tabs, "shuffle"));
        this.deck.addAll(discard);
        Random rand = new Random(); // creating Random object
        for (int i = 0; i < this.deck.size(); i++) {
            // switches between value in i with the value in a random index
            int randomIndexToSwap = rand.nextInt(this.deck.size());
            String temp = this.deck.get(randomIndexToSwap);
            this.deck.set(randomIndexToSwap, this.deck.get(i));
            this.deck.set(i, temp);
        }
        this.discard.clear();
    }

    /**
     * A function that topdecks a card by removing it from hand and adding it to deck.
     * @param cardName A String with the card that should move from hand to deck
     * @param gameActivity A reference to gameActivity
     */
    public void handToDeck(String cardName, GameActivity gameActivity) {
        if (this.hand.get(cardName) == 1) {
            this.hand.remove(cardName);
            gameActivity.getHandAdapter().notifyItemRemoved(this.getPositionByName(cardName));
            this.updateArrayHand();
            this.deck.add(cardName);
        }
        else if (this.hand.get(cardName) > 1) {
            this.hand.put(cardName, this.hand.get(cardName) - 1);
            this.updateArrayHand();
            gameActivity.getHandAdapter().notifyItemChanged(this.getPositionByName(cardName));
            this.deck.add(cardName);
        }
    }

    /**
     * A function that puts an array to discard.
     * @param cards An ArrayList of Strings with the cards that should be added to discard
     */
    public void putArrayInDiscard(ArrayList<String> cards) {
        this.discard.addAll(cards);
    }

    /**
     * A function that puts an array to deck.
     * @param cards An ArrayList of Strings with the cards that should be added to deck
     */
    public void putArrayInDeck(ArrayList<String> cards) {
        this.deck.addAll(cards);
    }

    /**
     * A function that takes cards from deck to hand.
     * @param numberOfCards An Integer with the number of cards to take
     * @param gameActivity A reference to gameActivity
     * @param gameManager A reference to gameManager
     * @param tabs An Integer which is the count of tabs in log that
     *             the line that would be added will have
     */
    public void takeCardsToHand(int numberOfCards, GameActivity gameActivity, GameManager gameManager, int tabs) {
        for (int i = 0; i < numberOfCards && !(this.discard.isEmpty() && this.deck.isEmpty()); i++) {
            if (this.deck.isEmpty())
                discardToDeck(gameManager, tabs);
            if (this.hand.containsKey(this.deck.get(this.deck.size() - 1)))
                this.hand.put(this.deck.get(this.deck.size() - 1), this.hand.get(this.deck.get(this.deck.size() - 1)) + 1);
            else
                this.hand.put(this.deck.get(this.deck.size() - 1), 1);
            // removes the last index which is the first card to take from deck
            this.deck.remove(this.deck.size() - 1);
        }
        this.updateArrayHand();
        gameActivity.getHandAdapter().notifyDataSetChanged();

        gameManager.getLog().add(new LogLine("Draws " + numberOfCards + " card" + (numberOfCards == 1 ? "" : "s"), gameManager.getGameManagerBeforeStart().getMyId(), false, false, tabs, "take cards"));
    }

    /**
     * A function that takes cards from deck and returns them in ArrayList.
     * @param numberOfCards An Integer with the number of cards to take
     * @param gameManager A reference to gameManager
     * @param tabs An Integer which is the count of tabs in log that
     *             the line that would be added will have
     * @return An ArrayList with the cards removed from deck
     */
    public ArrayList<String> takeCards(int numberOfCards, GameManager gameManager, int tabs) {
        ArrayList<String> returnCards = new ArrayList<>();
        for (int i = 0; i < numberOfCards && !(this.discard.isEmpty() && this.deck.isEmpty()); i++) {
            if (this.deck.isEmpty())
                discardToDeck(gameManager, tabs);
            returnCards.add(this.deck.remove(this.deck.size() - 1));
        }
        return returnCards;
    }

    /**
     * A function that checks whether there are cards of this type in hand.
     * @param type A String with the type of the card (action, treasure or victory)
     * @return A Boolean which is true if there is any card in hand from this type and false if not
     */
    public boolean containsTypeCards(String type) {
        for (String cardName : this.hand.keySet())
            if (Help.nameToCard(cardName).getType().equals(type))
                return true;
        return false;
    }

    /**
     * A function that check if a card is in hand or not.
     * @param cardName A String with the card name
     * @return A Boolean which is true if the card is in hand and false if not
     */
    public boolean containsCard(String cardName) {
        return this.hand.containsKey(cardName);
    }

    /**
     * A function that returns all the cards that the player has in game as a HashMap.
     * @return A HashMap with all cards and count of them that the player has in game
     */
    public HashMap<String, Integer> allCards(GameManager gameManager) {
        HashMap <String, Integer> cards = new HashMap<>(this.hand);
        for (String cardName : this.deck)
            if (cards.containsKey(cardName))
                cards.put(cardName, cards.get(cardName) + 1);
            else
                cards.put(cardName, 1);

        for (String cardName : this.discard)
            if (cards.containsKey(cardName))
                cards.put(cardName, cards.get(cardName) + 1);
            else
                cards.put(cardName, 1);

        if (gameManager.getTurn().isMyTurn(gameManager.getGameManagerBeforeStart())) {
            for (String cardName : gameManager.getTurn().getActionCardsPlayed())
                if (cards.containsKey(cardName))
                    cards.put(cardName, cards.get(cardName) + 1);
                else
                    cards.put(cardName, 1);

            for (String cardName : gameManager.getTurn().getTreasureCardsPlayed().keySet())
                for (int i = 0; i < gameManager.getTurn().getTreasureCardsPlayed().get(cardName); i++)
                    if (cards.containsKey(cardName))
                        cards.put(cardName, cards.get(cardName) + 1);
                    else
                        cards.put(cardName, 1);
        }
        return cards;
    }

    /**
     * A function that returns the count of victory points that the player has.
     * @return An Integer which is the number of victory points that the player has
     */
    public int getVictoryPoints(GameManager gameManager) {
        int victoryPoints = 0;
        HashMap<String, Integer> cards = this.allCards(gameManager);
        for (String cardName : cards.keySet())
            if (Help.nameToCard(cardName).getType().equals("victory"))
                victoryPoints += Help.nameToCard(cardName).getValue(gameManager)*cards.get(cardName);

        return victoryPoints;
    }

    public ArrayList<String> getDiscard() {
        return this.discard;
    }

    public ArrayList<String> getDeck() {
        return deck;
    }

    /**
     * A function that adds a card to deck.
     * @param cardName A String with the card name
     */
    public void addToDeck(String cardName) {
        if (!cardName.equals(""))
            this.deck.add(cardName);
    }

    /**
     * A function that adds a card to discard.
     * @param cardName A String with the card name
     */
    public void addToDiscard(String cardName) {
        if (!cardName.equals(""))
            this.discard.add(cardName);
    }

    /**
     * A function that adds a card to hand.
     * @param cardName A String with the card name
     * @return A Boolean of success or not
     */
    public boolean addToHand(String cardName) {
        if (cardName.equals(""))
            return false;

        if (this.hand.containsKey(cardName))
            this.hand.put(cardName, this.hand.get(cardName) + 1);
        else
            this.hand.put(cardName, 1);
        return true;
    }

    /**
     * A function that removes a card to hand.
     * @param cardName A String with the card name
     * @return A Boolean of success or not
     */
    public boolean removeFromHand(String cardName) {
        if (!this.hand.containsKey(cardName))
            return false;
        if (this.hand.get(cardName) == 1) {
            this.hand.remove(cardName);
            return true;
        }
        if (this.hand.get(cardName) > 1) {
            this.hand.put(cardName, this.hand.get(cardName) - 1);
            return true;
        }
        return false;
    }

    public HashMap<String, Integer> getHand() {
        return this.hand;
    }

    public void setHand(HashMap<String, Integer> hand) {
        this.hand = hand;
    }

    public ArrayList<Pair<String,Integer>> getArrayHand() {
        return this.arrayHand;
    }

    /**
     * A function that returns the position of the card in arrayHand.
     * @param cardName A String with the name of card
     * @return An Integer which is the index of the card in arrayHand
     */
    public int getPositionByName(String cardName) {
        for (int i = 0; i < this.arrayHand.size(); i++) {
            if (this.arrayHand.get(i).first.equals(cardName))
                return i;
        }
        return -1;
    }

    /**
     * A function that updates the arrayHand to be same as in hand.
     */
    public void updateArrayHand() {
        this.arrayHand.clear();
        for (String cardName : this.hand.keySet()) {
            this.arrayHand.add(new Pair<>(cardName, this.hand.get(cardName)));
        }
    }
}
