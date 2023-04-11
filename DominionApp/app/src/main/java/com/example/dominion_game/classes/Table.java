/**
 * Table is a class that keeps all the relevant data for every table in OnlineTablesActivity
 */
package com.example.dominion_game.classes;

public class Table {
    private String creator;
    private String id;

    /**
     * The constructor
     */
    public Table(String creator, String id) {
        this.creator = creator;
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
