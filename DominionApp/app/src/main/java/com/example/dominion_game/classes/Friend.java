package com.example.dominion_game.classes;

public class Friend {
    private String name;
    private int id;
    private boolean areFriends;

    public Friend(String name, int id, boolean areFriends) {
        this.name = name;
        this.id = id;
        this.areFriends = areFriends;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean areFriends() {
        return this.areFriends;
    }

    public void setFriends() {
        this.areFriends = true;
    }
}
