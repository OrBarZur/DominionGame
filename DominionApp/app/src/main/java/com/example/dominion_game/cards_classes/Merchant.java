package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Merchant extends Card {
    public Merchant() {
        super("Merchant", 3, R.mipmap.merchant, R.mipmap.merchant_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(1, gameActivity, game, game.getTabs());
        game.getTurn().addActions(1);
        game.useAfterPlay(this.getName(), false);
    }

    @Override
    public void afterPlay(GameManager game, String cardNameUsed) {
        if (game.getTurn().getTreasureCardsPlayed().get("Silver") != null
                && game.getTurn().getTreasureCardsPlayed().get("Silver") == 1
                && cardNameUsed.equals("Silver"))
            game.getTurn().addTreasure(1);
    }
}
