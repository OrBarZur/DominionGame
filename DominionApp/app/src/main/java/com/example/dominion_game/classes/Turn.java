/**
 * Turn is a class that keeps all the relevant data about the turn
 */
package com.example.dominion_game.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Turn {

    private String phase;
    private String turnId;
    private ArrayList<String> actionCardsPlayed;
    private HashMap<String, Integer> treasureCardsPlayed;
    private HashMap<String, Integer> cardsBought;
    private String lastActionCardForWait;
    private ArrayList<PlayCardArguments> cardsForWaitForPlay;
    private String lastCardForWaitForPlay;
    private boolean isWaitingForEnemy;
    private WaitForFunction waitForFunction;
    private int actions;
    private int buys;
    private int treasure;
    private int turnNumber;
    private boolean forcedActionEnd;

    /**
     * A constructor with default values for all attributes but turnId
     */
    public Turn(String turnId) {
        this.turnId = turnId;
        this.phase = "";
        this.actionCardsPlayed = new ArrayList<>();
        this.treasureCardsPlayed = new HashMap<>();
        this.cardsBought = new HashMap<>();
        this.lastActionCardForWait = "";
        this.cardsForWaitForPlay = new ArrayList<>();
        this.lastCardForWaitForPlay = "";
        this.actions = 1;
        this.buys = 1;
        this.treasure = 0;
        this.turnNumber = 1;
        this.isWaitingForEnemy = false;
        this.waitForFunction = new WaitForFunction();
        this.forcedActionEnd = false;
    }

    /**
     * A constructor with default values for all attributes
     */
    public Turn() {
        this.turnId = "";
        this.phase = "";
        this.actionCardsPlayed = new ArrayList<>();
        this.treasureCardsPlayed = new HashMap<>();
        this.cardsBought = new HashMap<>();
        this.lastActionCardForWait = "";
        this.cardsForWaitForPlay = new ArrayList<>();
        this.lastCardForWaitForPlay = "";
        this.actions = 1;
        this.buys = 1;
        this.treasure = 0;
        this.turnNumber = 1;
        this.isWaitingForEnemy = false;
        this.waitForFunction = new WaitForFunction();
        this.forcedActionEnd = false;
    }

    public ArrayList<String> getActionCardsPlayed() {
        return this.actionCardsPlayed;
    }

    public void setActionCardsPlayed(ArrayList<String> actionCardsPlayed) {
        this.actionCardsPlayed = actionCardsPlayed;
    }

    public HashMap<String, Integer> getTreasureCardsPlayed() {
        return this.treasureCardsPlayed;
    }

    public HashMap<String, Integer> getCardsBought() {
        return this.cardsBought;
    }

    public String getPhase() {
        return this.phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getTurnId() {
        return this.turnId;
    }

    public void setTurnId(String turnId) {
        this.turnId = turnId;
    }

    /**
     * A function that restart values in turn and changes the turnId.
     * @param gameManagerBeforeStart A reference to gameManagerBeforeStart
     */
    public void changeTurn(GameManagerBeforeStart gameManagerBeforeStart) {
        this.actionCardsPlayed.clear();
        this.treasureCardsPlayed.clear();
        this.cardsBought.clear();
        this.lastActionCardForWait = "";
        this.cardsForWaitForPlay.clear();
        this.lastCardForWaitForPlay = "";
        this.actions = 1;
        this.buys = 1;
        this.treasure = 0;
        this.turnNumber++;
        this.phase = "";
        if (this.turnId.equals(gameManagerBeforeStart.getIdP1()))
            this.turnId = gameManagerBeforeStart.getIdP2();
        else
            this.turnId = gameManagerBeforeStart.getIdP1();
        this.isWaitingForEnemy = false;
        this.waitForFunction.clear();
        this.forcedActionEnd = false;
    }

    /**
     * A function that returns if it is my turn or not.
     * @param gameManagerBeforeStart A reference to gameManagerBeforeStart
     * @return A Boolean which is true if it is my turn and false if not
     */
    public boolean isMyTurn(GameManagerBeforeStart gameManagerBeforeStart) {
        if (gameManagerBeforeStart.isCreator())
            return this.turnId.equals(gameManagerBeforeStart.getIdP1());
        else
            return this.turnId.equals(gameManagerBeforeStart.getIdP2());
    }

    public void addActionCard(String cardName) {
        this.actionCardsPlayed.add(cardName);
    }

    public void addWaitForEnemy(String cardName) {
        this.lastActionCardForWait = cardName;
        this.isWaitingForEnemy = true;
    }

    public void addWaitForPlay(String cardName, int n, int i, boolean forceUse, boolean autoPlay) {
        this.cardsForWaitForPlay.add(new PlayCardArguments(cardName, n, i, forceUse, autoPlay));
    }

    public void removeLastAttack() {
        this.lastActionCardForWait = "";
    }

    public String getLastActionCardForWait() {
        return this.lastActionCardForWait;
    }

    public void setLastActionCardForWait(String lastActionCardForWait) {
        this.lastActionCardForWait = lastActionCardForWait;
    }

    public ArrayList<PlayCardArguments> getCardsForWaitForPlay() {
        return this.cardsForWaitForPlay;
    }

    public void setCardsForWaitForPlay(ArrayList<PlayCardArguments> cardsForWaitForPlay) {
        this.cardsForWaitForPlay = cardsForWaitForPlay;
    }

    public String getLastCardForWaitForPlay() {
        return this.lastCardForWaitForPlay;
    }

    public void setLastCardForWaitForPlay(String lastCardForWaitForPlay) {
        this.lastCardForWaitForPlay = lastCardForWaitForPlay;
    }

    public void setWaitingForEnemy(boolean waitingForEnemy) {
        this.isWaitingForEnemy = waitingForEnemy;
    }

    public boolean isWaitingForEnemy() {
        return this.isWaitingForEnemy;
    }

    public void addTreasureCard(String cardName) {
        if (this.treasureCardsPlayed.containsKey(cardName))
            this.treasureCardsPlayed.put(cardName, this.treasureCardsPlayed.get(cardName) + 1);
        else
            this.treasureCardsPlayed.put(cardName, 1);
    }

    public void addCardBought(String cardName) {
        if (this.cardsBought.containsKey(cardName))
            this.cardsBought.put(cardName, this.cardsBought.get(cardName) + 1);
        else
            this.cardsBought.put(cardName, 1);
    }

    public int getActions() {
        return this.actions;
    }

    public void setActions(int actions) {
        this.actions = actions;
    }

    public void addActions(int actions) {
        this.actions += actions;
    }

    public void useAction() {
        this.actions -= 1;
    }

    public int getBuys() {
        return this.buys;
    }

    public void setBuys(int buys) {
        this.buys = buys;
    }

    public void addBuys(int buys) {
        this.buys += buys;
    }

    public void useBuy() {
        this.buys -= 1;
    }

    public int getTreasure() {
        return this.treasure;
    }

    public void setTreasure(int treasure) {
        this.treasure = treasure;
    }

    public void addTreasure(int treasure) {
        this.treasure += treasure;
    }

    public void useTreasure(int treasure) {
        this.treasure -= treasure;
    }

    public int getTurnNumber() {
        return this.turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public WaitForFunction getWaitForFunction() {
        return this.waitForFunction;
    }

    public boolean getForcedActionEnd() {
        return this.forcedActionEnd;
    }

    public void setForcedActionEnd(boolean forcedActionEnd) {
        this.forcedActionEnd = forcedActionEnd;
    }

    @Override
    public String toString() {
        return "actions=" + this.actions + ", buys=" + this.buys + ", treasure=" + this.treasure;
    }

    /**
     * A function that creates a JSONObject from the data in turn to upload to server.
     * @return A JSONObject of some attributes from Turn
     */
    public JSONObject turnToJson(boolean doneAttack) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("actions", this.actions);
            jsonObject.put("buys", this.buys);
            jsonObject.put("treasure", this.treasure);
            jsonObject.put("turnId", this.turnId);
            jsonObject.put("phase", this.phase);
            if (doneAttack)
                jsonObject.put("lastActionCardForWait", this.lastActionCardForWait);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * A function that updates the values on some attributes
     * @param jsonObject A JSONObject that is given from the server
     */
    public void jsonToTurn(JSONObject jsonObject, GameManager gameManager) {
        try {
            this.actions = jsonObject.getInt("actions");
            this.buys = jsonObject.getInt("buys");
            this.treasure = jsonObject.getInt("treasure");
            this.turnId = jsonObject.getString("turnId");
            this.phase = jsonObject.getString("phase");
            if (!this.isMyTurn(gameManager.getGameManagerBeforeStart()))
                this.lastActionCardForWait = jsonObject.getString("lastActionCardForWait");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
