package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Gold extends Card {
    public Gold() {
        super("Gold", 6, R.mipmap.gold, R.mipmap.gold_sh, "treasure");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getTurn().addTreasure(3);
        game.useAfterPlay(this.getName(), false);
    }

}
