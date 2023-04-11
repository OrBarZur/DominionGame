package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Smithy extends Card {
    public Smithy() {
        super("Smithy", 4, R.mipmap.smithy, R.mipmap.smithy_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(3, gameActivity, game, game.getTabs());
        game.useAfterPlay(this.getName(), false);
    }
}
