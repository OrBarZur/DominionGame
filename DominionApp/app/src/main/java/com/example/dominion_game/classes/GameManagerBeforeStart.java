/**
 * GameManagerBeforeStart is a class that keeps all the relevant data
 * before starting the game.
 */
package com.example.dominion_game.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class GameManagerBeforeStart implements Serializable {
    private String gameId;
    private String idP1;
    private String idP2;
    private boolean isCreator;
    private boolean isReady1;
    private boolean isReady2;
    private boolean isStart1;
    private boolean isStart2;
    private boolean isRated;

    /**
     * The constructor for the creator
     * @param creatorId A String with the id of the creator
     */
    public GameManagerBeforeStart(String creatorId) {
        this.gameId = "";
        this.idP1 = creatorId;
        this.idP2 = "";
        this.isCreator = true;
        restartReady();
        this.isRated = false;
    }

    /**
     * The constructor for the non creator
     * @param tableId A String with the table id
     * @param nonCreatorId A String with the id of the non creator
     */
    public GameManagerBeforeStart(String tableId, String nonCreatorId) {
        this.gameId = tableId;
        this.idP1 = "";
        this.idP2 = nonCreatorId;
        this.isCreator = false;
        restartReady();
        this.isRated = false;
    }

    /**
     * A function that restart the ready and start attributes for both players to be false.
     * The function is called before the game starts.
     */
    public void restartReady() {
        this.isReady1 = false;
        this.isReady2 = false;
        this.isStart1 = false;
        this.isStart2 = false;
    }

    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getIdP1() {
        return this.idP1;
    }

    public void setIdP1(String idP1) {
        this.idP1 = idP1;
    }

    public String getIdP2() {
        return this.idP2;
    }

    public void setIdP2(String idP2) {
        this.idP2 = idP2;
    }

    public boolean isCreator() {
        return this.isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public boolean isReady1() {
        return this.isReady1;
    }

    public void setReady1(boolean ready1) {
        isReady1 = ready1;
    }

    public boolean isReady2() {
        return this.isReady2;
    }

    public void setReady2(boolean ready2) {
        isReady2 = ready2;
    }

    public boolean isStart1() {
        return this.isStart1;
    }

    public void setStart1(boolean start1) {
        this.isStart1 = start1;
    }

    public boolean isStart2() {
        return this.isStart2;
    }

    public void setStart2(boolean start2) {
        this.isStart2 = start2;
    }

    public boolean isRated() {
        return this.isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    /**
     * A function that gives my id.
     * @return A String with my id
     */
    public String getMyId() {
        if (this.isCreator)
            return this.idP1;
        return this.idP2;
    }

    /**
     * A function that gives the enemy id.
     * @return A String with the enemy id
     */
    public String getEnemyId() {
        if (this.isCreator)
            return this.idP2;
        return this.idP1;
    }

    /**
     * A function that creates a JSONObject from the data in
     * gameManagerBeforeStart to upload to server.
     * @return A JSONObject of the gameManagerBeforeStart attributes
     */
    public JSONObject gameManagerBeforeStartToJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("gameId", this.gameId);
            jsonObject.put("idP1", this.idP1);
            jsonObject.put("idP2", this.idP2);
            jsonObject.put("isCreator", this.isCreator);
            jsonObject.put("isReady1", this.isReady1);
            jsonObject.put("isReady2", this.isReady2);
            jsonObject.put("isStart1", this.isStart1);
            jsonObject.put("isStart2", this.isStart2);
            jsonObject.put("isRated", this.isRated);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * A function that updates the values on the attributes
     * @param jsonObject A JSONObject that is given from the server
     * @param afterReady A Boolean which is true if the game started and false if not
     */
    public void jsonToGameManagerBeforeStart(JSONObject jsonObject, boolean afterReady) {
        try {
            if (!isCreator) {
                this.idP1 = jsonObject.getString("idP1");
                this.isRated = jsonObject.getBoolean("isRated");
                if (!afterReady)
                    this.isReady1 = jsonObject.getBoolean("isReady1");
                else
                    this.isStart1 = jsonObject.getBoolean("isStart1");
            }
            else {
                this.idP2 = jsonObject.getString("idP2");
                if (!afterReady)
                    this.isReady2 = jsonObject.getBoolean("isReady2");
                else
                    this.isStart2 = jsonObject.getBoolean("isStart2");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
