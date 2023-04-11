/**
 * Laboratory is an example to a card in game that extends Card
 * This class overrides an abstract function from Card: play
 */
package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Laboratory extends Card {
    /**
     * The Constructor with the Laboratory Card attributes
     */
    public Laboratory() {
        super("Laboratory", 5, R.mipmap.laboratory, R.mipmap.laboratory_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(2, gameActivity, game, game.getTabs());
        game.getTurn().addActions(1);
        game.useAfterPlay(this.getName(), false);
    }
}
