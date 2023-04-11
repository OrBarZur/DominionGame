package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class CouncilRoom extends Card {
    public CouncilRoom() {
        super("CouncilRoom", 5, R.mipmap.councilroom, R.mipmap.councilroom_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(4, gameActivity, game, game.getTabs());
        game.getTurn().addBuys(1);
        game.useAfterPlay(this.getName(), true);
    }

    @Override
    public void enemyPlay(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(1, gameActivity, game, game.getTabs());
    }

    @Override
    public String getNameToDisplay() {
        return "Council Room";
    }
}
