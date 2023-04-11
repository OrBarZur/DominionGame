/**
 * Copper is an example to a card in game that extends Card
 * This class overrides an abstract function from Card: play
 */
package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;

public class Copper extends Card {
    /**
     * The Constructor with the Copper Card attributes
     */
    public Copper() {
        super("Copper", 0, R.mipmap.copper, R.mipmap.copper_sh, "treasure");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getTurn().addTreasure(1);
        game.useAfterPlay(this.getName(), false);
    }

}
