package com.example.dominion_game.cards_classes;

import android.view.View;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

import java.util.ArrayList;

public class Harbinger extends Card {
    public Harbinger() {
        super("Harbinger", 3, R.mipmap.harbinger, R.mipmap.harbinger_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().takeCardsToHand(1, gameActivity, game, game.getTabs());
        game.getTurn().addActions(1);
        if (game.getPlayer().getDiscard().size() > 0) {
            game.getTurn().getWaitForFunction().handleWaitingForActionCardsDialog(this.getName(), 1, 1, "topdeck", true, game.getPlayer().getDiscard());
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.setVisibilityForRVActionCardsPlaying(View.VISIBLE);
            gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), "Don't Topdeck", gameActivity.getString(R.string.confirm_top_deck)}, false);
            gameActivity.setVisibilityForAction(1, View.VISIBLE);
        }
        else
            game.useAfterPlay(this.getName(), false);
    }

    @Override
    public boolean isMarkCardSelectedFromHandWhenHandle() {
        return true;
    }

    @Override
    public void handleClickOnHandOrDialog(String cardName, GameManager game, GameActivity gameActivity) {
        gameActivity.setVisibilityForAction(0, View.VISIBLE);
        gameActivity.setVisibilityForAction(2, View.VISIBLE);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals(gameActivity.getString(R.string.undo))) {
            game.getTurn().getWaitForFunction().undo();
            gameActivity.setVisibilityForAction(0, View.GONE);
            gameActivity.setVisibilityForAction(2, View.GONE);
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
        }
        else if (buttonText.equals("Don't Topdeck")) {
            game.getTurn().getWaitForFunction().clear();
            gameActivity.invisibleButtons(3);
            gameActivity.turnUI();game.getPlayer().updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.setVisibilityForRVActionCardsPlaying(View.INVISIBLE);
            game.useAfterPlay(this.getName(), false);
        }
        else if (buttonText.equals(gameActivity.getString(R.string.confirm_top_deck))) {
            ArrayList<String> cardSelected = Help.arrayListOfPairsToArrayList(game.getTurn().getWaitForFunction().getCardsForDialog());
            if (cardSelected.size() > 0) {
                game.getPlayer().getDiscard().remove(cardSelected.get(0));
                game.getPlayer().getDeck().add(cardSelected.get(0));
                gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
                gameActivity.updateCards(false);
            }
            game.getTurn().getWaitForFunction().clear();
            gameActivity.invisibleButtons(3);
            gameActivity.turnUI();
            game.getPlayer().updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.setVisibilityForRVActionCardsPlaying(View.INVISIBLE);
            game.useAfterPlay(this.getName(), false);
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return true;
    }
}
