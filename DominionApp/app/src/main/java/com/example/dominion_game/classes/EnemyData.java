/**
 * EnemyData is a class that keeps all the relevant data about the enemy
 * that is given from the server.
 */
package com.example.dominion_game.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class EnemyData {
    private String lastCardOnDiscard;
    private int deckSize;
    private int handSize;
    private int victoryPoints;
    private boolean isEmpty;
    private boolean resigned;

    /**
     * The constructor with default values for the attributes
     */
    public EnemyData() {
        this.isEmpty = true;
        this.lastCardOnDiscard = "";
        this.deckSize = 0;
        this.handSize = 0;
        this.victoryPoints = 0;
        this.resigned = false;
    }

    public String getLastCardOnDiscard() {
        return this.lastCardOnDiscard;
    }

    public void setLastCardOnDiscard(String lastCardOnDiscard) {
        this.lastCardOnDiscard = lastCardOnDiscard;
    }

    public int getDeckSize() {
        return this.deckSize;
    }

    public void setDeckSize(int deckSize) {
        this.deckSize = deckSize;
    }

    public int getHandSize() {
        return this.handSize;
    }

    public void setHandSize(int handSize) {
        this.handSize = handSize;
    }

    public int getVictoryPoints() {
        return this.victoryPoints;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public boolean isResigned() {
        return this.resigned;
    }

    /**
     * A function that updates the values on the attributes
     * @param jsonObject A JSONObject that is given from the server
     * @param gameManager A reference to gameManager
     */
    public void jsonToEnemyData(JSONObject jsonObject, GameManager gameManager) {
        try {
            this.isEmpty = false;
            if (this.handSize != jsonObject.getInt("handSize")
                    || !this.lastCardOnDiscard.equals(jsonObject.getString("lastCardOnDiscard"))
                    || this.deckSize != jsonObject.getInt("deckSize")
                    || this.victoryPoints != jsonObject.getInt("victoryPoints")) {
                this.handSize = jsonObject.getInt("handSize");
                if (gameManager.isStarted()) {
                    this.lastCardOnDiscard = jsonObject.getString("lastCardOnDiscard");
                    this.deckSize = jsonObject.getInt("deckSize");
                    this.victoryPoints = jsonObject.getInt("victoryPoints");
                }
            }
            this.resigned = jsonObject.getBoolean("resigned");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
