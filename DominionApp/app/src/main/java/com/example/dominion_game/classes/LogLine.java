/**
 * LogLine is a class that keeps all the relevant data of every line in the log.
 */
package com.example.dominion_game.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class LogLine {
    private String text;
    private String playerId;
    private boolean isBold;
    private boolean isItalic;
    private String color;
    private int tabs;
    private String type;

    /**
     * A constructor with all attributes but color
     * @param text A String with the text for the line
     * @param playerId A String with the playerId that this line belongs to
     * @param isBold A Boolean which is true if the line should be bold
     * @param isItalic A Boolean which is true if the line should be italic
     * @param tabs An Integer with the count of tabs for this line
     * @param type A String with type of the line
     */
    public LogLine(String text, String playerId, boolean isBold, boolean isItalic, int tabs, String type) {
        this.text = text;
        this.playerId = playerId;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.color = "white";
        this.tabs = tabs;
        this.type = type;
    }

    /**
     * A constructor with all attributes
     * @param text A String with the text for the line
     * @param playerId A String with the playerId that this line belongs to
     * @param isBold A Boolean which is true if the line should be bold
     * @param isItalic A Boolean which is true if the line should be italic
     * @param tabs An Integer with the count of tabs for this line
     * @param type A String with type of the line
     * @param color A String with the color for the line
     */
    public LogLine(String text, String playerId, boolean isBold, boolean isItalic, String color, int tabs, String type) {
        this.text = text;
        this.playerId = playerId;
        this.isBold = isBold;
        this.isItalic = isItalic;
        this.color = color;
        this.tabs = tabs;
        this.type = type;
    }

    /**
     * A constructor with all attributes as JSONObject
     * @param jsonObject A JSONObject that is given from the server
     */
    public LogLine(JSONObject jsonObject) {
        try {
            this.text = jsonObject.getString("text");
            this.playerId = jsonObject.getString("playerId");
            this.isBold = jsonObject.getBoolean("isBold");
            this.isItalic = jsonObject.getBoolean("isItalic");
            this.color = jsonObject.getString("color");
            this.tabs = jsonObject.getInt("tabs");
            this.type = jsonObject.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public boolean isBold() {
        return this.isBold;
    }

    public void setBold(boolean bold) {
        this.isBold = bold;
    }

    public boolean isItalic() {
        return this.isItalic;
    }

    public void setItalic(boolean italic) {
        this.isItalic = italic;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getTabs() {
        return this.tabs;
    }

    public void setTabs(int tabs) {
        this.tabs = tabs;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * A function that creates a JSONObject from the data in
     * logLine to upload to server.
     * @return A JSONObject of the login details
     */
    public JSONObject logLineToJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("text", this.text);
            jsonObject.put("playerId", this.playerId);
            jsonObject.put("isBold", this.isBold);
            jsonObject.put("isItalic", this.isItalic);
            jsonObject.put("color", this.color);
            jsonObject.put("tabs", this.tabs);
            jsonObject.put("type", this.type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
