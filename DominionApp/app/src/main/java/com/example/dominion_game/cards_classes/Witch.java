package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.LogLine;

public class Witch extends Card {
    public Witch() {
        super("Witch", 5, R.mipmap.witch, R.mipmap.witch_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(2, gameActivity, game, game.getTabs());
        game.useAfterPlay(this.getName(), true);
    }

    @Override
    public void attack(GameManager game, GameActivity gameActivity) {
        game.getPlayer().addToDiscard(game.getCard("Curse"));
        game.getLog().add(new LogLine("Gains a Curse", game.getGameManagerBeforeStart().getMyId(), false, false,
                game.getTabs(), "in action"));
        game.setDoneAttack(true);
    }
}
