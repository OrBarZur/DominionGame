package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Silver extends Card {
    public Silver() {
        super("Silver", 3, R.mipmap.silver, R.mipmap.silver_sh, "treasure");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getTurn().addTreasure(2);
        game.useAfterPlay(this.getName(), false);
    }
}
