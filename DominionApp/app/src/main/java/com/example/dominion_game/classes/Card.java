/**
 * Card is an abstract class which is the base of all card classes.
 * This class has an abstract function: "play" that has to be overrided for all cards.
 * This class has also many functions that are not abstract but are also
 * overrided for some special cards, and here these functions are empty.
 */
package com.example.dominion_game.classes;

import com.example.dominion_game.activities.GameActivity;

public abstract class Card {
    private String type;
    private String name;
    private int price;
    private int imageSource;
    private int shortImageSource;

    /**
     * The constructor
     * @param name A String with the name of the card
     * @param price An Integer with the price of the card
     * @param imageSource An Integer with the image resource of the card
     * @param shortImageSource An Integer with the short image resource of the card
     * @param type A String with the type of the card (action, treasure or victory)
     */
    public Card(String name, int price, int imageSource, int shortImageSource, String type) {
        this.name = name;
        this.price = price;
        this.imageSource = imageSource;
        this.shortImageSource = shortImageSource;
        this.type = type;
    }

    /**
     * The constructor with default values
     * @param type
     */
    public Card(String type) {
        this.name = "";
        this.price = 0;
        this.imageSource = 0;
        this.shortImageSource = 0;
        this.type = type;
    }

    public int getImageSource() {
        return this.imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }

    public int getShortImageSource() {
        return this.shortImageSource;
    }

    public void setShortImageSource(int shortImageSource) {
        this.shortImageSource = shortImageSource;
    }

    public int getPrice() {
        return this.price;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * An abstract function that plays the card.
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     */
    public abstract void play(GameManager game, GameActivity gameActivity);

    /**
     * A function that plays the attack of the card for the enemy.
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     */
    public void attack(GameManager game, GameActivity gameActivity) {
        game.setDoneAttack(true);
    }

    /**
     * A function that plays the reaction of the card for the enemy.
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     */
    public void reaction(GameManager game, GameActivity gameActivity) {

    }

    /**
     * A function that plays an action of the card for the enemy.
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     */
    public void enemyPlay(GameManager game, GameActivity gameActivity) {
    }

    /**
     * A function that handles clicking on a button that was enabled after playing this card.
     * @param buttonText A String with the text on the button
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     */
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
    }

    /**
     * A function that handles clicks on hand or on dialog after playing a card
     * that should wait for clicking on hand or on dialog.
     * @param cardName A String which is the name of the card which
     *                 was clicked on hand or on dialog
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     */
    public void handleClickOnHandOrDialog(String cardName, GameManager game, GameActivity gameActivity) {
    }

    /**
     * @return A Boolean which is true if cards selected from hand should be
     * marked or not after playing that card and false if not
     */
    public boolean isMarkCardSelectedFromHandWhenHandle() {
        return false;
    }

    /**
     * A function that returns whether the card can be used after playing this card.
     * @param cardName A String which is the name of the card which was pressed
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     * @return A Boolean which is true if the card can be used after playing
     * this card and false if not
     */
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return false;
    }

    /**
     * A function that returns whether the card can be bought after playing this card.
     * @param cardName A String which is the name of the card which was pressed
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     * @return A Boolean which is true if the card can be bought after playing
     * this card and false if not
     */
    public boolean isCardToGetFromBoard(String cardName, GameManager game, GameActivity gameActivity) {
        return false;
    }

    /**
     * A function that that handles click on a card from board after playing this card.
     * @param cardName A String which is the name of the card which was clicked
     * @param game A reference to gameManager
     * @param gameActivity A reference to gameActivity
     */
    public void clickOnBoard(String cardName, GameManager game, GameActivity gameActivity) {
    }

    /**
     * A function that returns the value of the card.
     * @param gameManager A reference to gameManager
     * @return An Integer with the value of the card
     */
    public int getValue(GameManager gameManager) {
        return 0;
    }

    /**
     * A function that is overrided if the name of the card is more than one word.
     * @return A String with the the card name for display
     */
    public String getNameToDisplay() {
        return this.name;
    }

    /**
     * A function that plays the after play for the card (when a card used after played this card).
     * @param game A reference to gameManager
     * @param cardNameUsed A String with the name of card used
     */
    public void afterPlay(GameManager game, String cardNameUsed) {}

    @Override
    public String toString() {
        return this.getNameToDisplay();
    }
}
