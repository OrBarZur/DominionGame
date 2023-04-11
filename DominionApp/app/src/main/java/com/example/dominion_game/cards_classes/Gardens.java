package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;
import com.example.dominion_game.classes.Player;

import java.util.HashMap;

public class Gardens extends Card {
    public Gardens() {
        super("Gardens", 4, R.mipmap.gardens, R.mipmap.gardens_sh, "victory");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
    }

    @Override
    public int getValue(GameManager gameManager) {
        return Help.sizeOfHash(gameManager.getPlayer().allCards(gameManager))/10;
    }
}
