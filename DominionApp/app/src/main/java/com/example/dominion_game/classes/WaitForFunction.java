/**
 * WaitForFunction is a class that keeps all data about waiting for
 * things in game (enemy, clicks...).
 */
package com.example.dominion_game.classes;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class WaitForFunction {
    private boolean isWaitingForActionCardsDialog;
    private boolean isWaitingForButtonsOnly;
    private boolean isWaitingForBoard;
    private boolean isWaitingForHand;
    private String cardName;
    private int minAmount;
    private int maxAmount;
    private int minPriceForGain;
    private int maxPriceForGain;
    private String typeOfAction;
    private HashMap<String, Integer> cardsForActionCardPlay;
    private ArrayList<Pair<String, Boolean>> cardsForDialog;
    private boolean handleClickOnCard;
    private boolean hasUndoAndConfirm;

    /**
     * The constructor with default values.
     */
    public WaitForFunction() {
        this.isWaitingForActionCardsDialog = false;
        this.isWaitingForButtonsOnly = false;
        this.isWaitingForBoard = false;
        this.isWaitingForHand = false;
        this.cardName = "";
        this.minAmount = 0;
        this.maxAmount = 0;
        this.minPriceForGain = 0;
        this.maxPriceForGain = 0;
        this.typeOfAction = "";
        this.cardsForActionCardPlay = new HashMap<>();
        this.cardsForDialog = new ArrayList<>();
        this.handleClickOnCard = false;
        this.hasUndoAndConfirm = false;
    }

    /**
     * A function that handles waiting for hand.
     * @param cardName A String which is the name of the card that is waiting for clicking on hand
     * @param minAmount An Integer with the minimum of cards that should be selected from hand
     * @param maxAmount An Integer with the maximum of cards that should be selected from hand
     * @param typeOfAction A String with the type of action that will be
     *                     done with the selected cards (trash, discard...)
     * @param handleClickOnCard A Boolean which is true if the card should handle every
     *                          click on card in hand and false if not
     */
    public void handleWaitingForHand(String cardName, int minAmount, int maxAmount, String typeOfAction, boolean handleClickOnCard) {
        this.isWaitingForHand = true;
        this.cardName = cardName;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.typeOfAction = typeOfAction;
        this.handleClickOnCard = handleClickOnCard;
    }

    /**
     * A function that handles waiting for dialog.
     * @param cardName A String which is the name of the card that is waiting for clicking on hand
     * @param minAmount An Integer with the minimum of cards that should be selected from hand
     * @param maxAmount An Integer with the maximum of cards that should be selected from hand
     * @param typeOfAction A String with the type of action that will be
     *                     done with the selected cards (trash, discard...)
     * @param handleClickOnCard A Boolean which is true if the card should handle every
     *                          click on card in hand and false if not
     * @param cards An ArrayList of String with the cards should be displayed on the dialog
     */
    public void handleWaitingForActionCardsDialog(String cardName, int minAmount, int maxAmount, String typeOfAction, boolean handleClickOnCard, ArrayList<String> cards) {
        this.isWaitingForActionCardsDialog = true;
        this.cardName = cardName;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.typeOfAction = typeOfAction;
        this.handleClickOnCard = handleClickOnCard;
        for(String card : cards)
            this.addToCardsForDialog(card);
    }

    /**
     * A function that adds a card to the dialog.
     * @param card A String with the name of the card to be added
     */
    public void addToCardsForDialog(String card) {
        this.cardsForDialog.add(new Pair<>(card, false));
    }

    /**
     * A function that handles waiting for board.
     * @param cardName A String which is the name of the card that is waiting for clicking on board
     * @param minAmount An Integer with the minimum of cards that should be selected from board
     * @param maxAmount An Integer with the maximum of cards that should be selected from board
     */
    public void handleWaitingForBoard(String cardName, int minAmount, int maxAmount) {
        this.isWaitingForBoard = true;
        this.cardName = cardName;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    /**
     * A function that handles waiting only for buttons.
     * @param cardName A String with the name of card that is waiting for buttons
     */
    public void handleWaitingForButtonsOnly(String cardName) {
        this.isWaitingForButtonsOnly = true;
        this.cardName = cardName;
    }

    /**
     * A function that puts the default values for all arguments.
     */
    public void clear() {
        this.isWaitingForActionCardsDialog = false;
        this.isWaitingForButtonsOnly = false;
        this.isWaitingForBoard = false;
        this.isWaitingForHand = false;
        this.cardName = "";
        this.minAmount = 0;
        this.maxAmount = 0;
        this.minPriceForGain = 0;
        this.maxPriceForGain = 0;
        this.typeOfAction = "";
        this.cardsForActionCardPlay.clear();
        this.cardsForDialog.clear();
        this.handleClickOnCard = false;
        this.hasUndoAndConfirm = false;
    }

    /**
     * A function that handles undo and clears the cards that were selected.
     */
    public void undo() {
        this.cardsForActionCardPlay.clear();
        if (isWaitingForActionCardsDialog)
            for(int i = 0; i < this.cardsForDialog.size(); i++)
                this.cardsForDialog.set(i, new Pair<>(this.cardsForDialog.get(i).first, false));
    }

    /**
     * A function that returns all the selected cards in dialog.
     * @return An ArrayList of String with all the card selected in dialog
     */
    public ArrayList<String> cardsSelectedForDialog() {
        ArrayList<String> al = new ArrayList<>();
        for (int i = 0; i < this.cardsForDialog.size(); i++) {
            if (this.cardsForDialog.get(i).second)
                al.add(this.cardsForDialog.get(i).first);
        }
        return al;
    }

    /**
     * A function that returns all the non-selected cards in dialog.
     * @return An ArrayList of String with all the card non-selected in dialog
     */
    public ArrayList<String> cardsLeftForDialog() {
        ArrayList<String> al = new ArrayList<>();
        for (int i = 0; i < this.cardsForDialog.size(); i++) {
            if (!this.cardsForDialog.get(i).second)
                al.add(this.cardsForDialog.get(i).first);
        }
        return al;
    }

    /**
     * A function that updates the cards by position after selecting or unselecting in dialog.
     * @param position An Integer with the position of the card in cardsForDialog
     * @param isSelected A Boolean which is true if the card was selected and false if unselected.
     */
    public void updateCardsForDialogByPosition(int position, boolean isSelected) {
        this.cardsForDialog.set(position, new Pair<>(this.cardsForDialog.get(position).first, isSelected));
        String cardName = this.cardsForDialog.get(position).first;
        if (isSelected) {
            if (this.cardsForActionCardPlay.containsKey(cardName))
                this.cardsForActionCardPlay.put(cardName, this.cardsForActionCardPlay.get(cardName) + 1);
            else
                this.cardsForActionCardPlay.put(cardName, 1);
        }
        else {
            if (this.cardsForActionCardPlay.get(cardName) > 1)
                this.cardsForActionCardPlay.put(cardName, this.cardsForActionCardPlay.get(cardName) - 1);
            else if (this.cardsForActionCardPlay.get(cardName) == 1)
                this.cardsForActionCardPlay.remove(cardName);
        }
    }

    /**
     * A function that reorders and replaces between the two cards that should be reordered.
     */
    public void order() {
        this.cardsForActionCardPlay.clear();
        ArrayList<Integer> al = new ArrayList<>();
        for (int i = 0; i < this.cardsForDialog.size(); i++) {
            if (this.cardsForDialog.get(i).second) {
                al.add(i);
            }
        }
        if (al.size() != 2)
            return;

        String temp = this.cardsForDialog.get(0).first;
        this.cardsForDialog.set(0, new Pair<>(this.cardsForDialog.get(1).first, false));
        this.cardsForDialog.set(1, new Pair<>(temp, false));
    }

    /**
     * A function that inserts the card that selected to cardsForActionCardPlay.
     * @param cardName A String with the name of the card
     */
    public void insertCardSelectedInHand(String cardName) {
        if (this.cardsForActionCardPlay.containsKey(cardName))
            this.cardsForActionCardPlay.put(cardName, this.cardsForActionCardPlay.get(cardName) + 1);
        else
            this.cardsForActionCardPlay.put(cardName, 1);
    }

    /**
     * A function that returns the amount of a specific cards that selected.
     * @param cardName A String with the card name
     * @return An Integer with the amount of a specific cards that selected
     */
    public int getCardAmountInCardsInHand(String cardName) {
        if (this.cardsForActionCardPlay.containsKey(cardName))
            return this.cardsForActionCardPlay.get(cardName);
        return 0;
    }

    public boolean isHandleClickOnCard() {
        return this.handleClickOnCard;
    }

    public void setHandleClickOnCard(boolean handleClickOnCard) {
        this.handleClickOnCard = handleClickOnCard;
    }

    public int getMinPriceForGain() {
        return this.minPriceForGain;
    }

    public void setMinPriceForGain(int minPriceForGain) {
        this.minPriceForGain = minPriceForGain;
    }

    public int getMaxPriceForGain() {
        return this.maxPriceForGain;
    }

    public void setMaxPriceForGain(int maxPriceForGain) {
        this.maxPriceForGain = maxPriceForGain;
    }

    public HashMap<String, Integer> getCardsForActionCardPlay() {
        return this.cardsForActionCardPlay;
    }

    public void setCardsForActionCardPlay(HashMap<String, Integer> cardsForActionCardPlay) {
        this.cardsForActionCardPlay = cardsForActionCardPlay;
    }

    public ArrayList<Pair<String, Boolean>> getCardsForDialog() {
        return this.cardsForDialog;
    }

    public void setCardsForDialog(ArrayList<Pair<String, Boolean>> cardsForDialog) {
        this.cardsForDialog = cardsForDialog;
    }

    public boolean isWaitingForActionCardsDialog() {
        return this.isWaitingForActionCardsDialog;
    }

    public void setWaitingForActionCardsDialog(boolean waitingForActionCardsDialog) {
        this.isWaitingForActionCardsDialog = waitingForActionCardsDialog;
    }

    public boolean isWaitingForButtonsOnly() {
        return this.isWaitingForButtonsOnly;
    }

    public void setWaitingForButtonsOnly(boolean waitingForButtonsOnly) {
        this.isWaitingForButtonsOnly = waitingForButtonsOnly;
    }

    public boolean isWaitingForBoard() {
        return this.isWaitingForBoard;
    }

    public void setWaitingForBoard(boolean waitingForBoard) {
        this.isWaitingForBoard = waitingForBoard;
    }

    public boolean isWaitingForHand() {
        return this.isWaitingForHand;
    }

    public void setWaitingForHand(boolean waitingForHand) {
        this.isWaitingForHand = waitingForHand;
    }

    public String getCardName() {
        return this.cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getMinAmount() {
        return this.minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getTypeOfAction() {
        return this.typeOfAction;
    }

    public void setTypeOfAction(String typeOfAction) {
        this.typeOfAction = typeOfAction;
    }

    public boolean isHasUndoAndConfirm() {
        return this.hasUndoAndConfirm;
    }

    public void setHasUndoAndConfirm(boolean hasUndoAndConfirm) {
        this.hasUndoAndConfirm = hasUndoAndConfirm;
    }
}
