package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Market extends Card {
    public Market() {
        super("Market", 5, R.mipmap.market, R.mipmap.market_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(1, gameActivity, game, game.getTabs());
        game.getTurn().addActions(1);
        game.getTurn().addBuys(1);
        game.getTurn().addTreasure(1);
        game.useAfterPlay(this.getName(), false);
    }
}
