/**
 * Festival is an example to a card in game that extends Card
 * This class overrides an abstract function from Card: play
 */
package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Festival extends Card {
    /**
     * The Constructor with the Festival Card attributes
     */
    public Festival() {
        super("Festival", 5, R.mipmap.festival, R.mipmap.festival_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getTurn().addActions(2);
        game.getTurn().addBuys(1);
        game.getTurn().addTreasure(2);
        game.useAfterPlay(this.getName(), false);
    }
}
