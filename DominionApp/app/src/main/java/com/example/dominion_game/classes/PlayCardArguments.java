/**
 * PlayCardArguments is a class that contains all parameter of playing a future card.
 */
package com.example.dominion_game.classes;

public class PlayCardArguments {
    private String cardName;
    private int n;
    private int i;
    private boolean forceUse;
    private boolean autoPlay;

    /**
     * The constructor
     * @param cardName A String which is the name of the card which should be used
     * @param n An Integer which is the number of times that the card should be played
     * @param i An Integer which is the place of the card in the wait queue
     * @param forceUse A Boolean which is true if the use of the card was forced or not
     * @param autoPlay A Boolean which is true if useCard was played with autoPlay and false if not
     */
    public PlayCardArguments(String cardName, int n, int i, boolean forceUse, boolean autoPlay) {
        this.cardName = cardName;
        this.n = n;
        this.i = i;
        this.forceUse = forceUse;
        this.autoPlay = autoPlay;
    }

    public String getCardName() {
        return this.cardName;
    }

    public int getN() {
        return this.n;
    }

    public int getI() {
        return this.i;
    }

    public boolean isForceUse() {
        return this.forceUse;
    }

    public boolean isAutoPlay() {
        return this.autoPlay;
    }
}
