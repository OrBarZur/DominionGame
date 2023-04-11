package com.example.dominion_game.cards_classes;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Player;

public class Victory extends Card {
    private int value;
    public Victory(String name) {
        super("victory");
        switch (name) {
            case "Estate":
                setName("Estate");
                setPrice(2);
                setImageSource(R.mipmap.estate);
                setShortImageSource(R.mipmap.estate_sh);
                this.value = 1;
                break;
            case "Duchy":
                setName("Duchy");
                setPrice(5);
                setImageSource(R.mipmap.duchy);
                setShortImageSource(R.mipmap.duchy_sh);
                this.value = 3;
                break;
            case "Province":
                setName("Province");
                setPrice(8);
                setImageSource(R.mipmap.province);
                setShortImageSource(R.mipmap.province_sh);
                this.value = 6;
                break;
            case "Curse":
                setName("Curse");
                setPrice(0);
                setImageSource(R.mipmap.curse);
                setShortImageSource(R.mipmap.curse_sh);
                this.value = -1;
                break;
        }
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {

    }

    @Override
    public int getValue(GameManager gameManager) {
        return this.value;
    }
}
