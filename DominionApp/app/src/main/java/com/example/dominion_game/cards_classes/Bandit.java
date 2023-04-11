package com.example.dominion_game.cards_classes;

import android.view.View;

import com.example.dominion_game.R;
import com.example.dominion_game.activities.GameActivity;
import com.example.dominion_game.classes.Card;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.Help;

import java.util.ArrayList;

public class Bandit extends Card {
    public Bandit() {
        super("Bandit", 5, R.mipmap.bandit, R.mipmap.bandit_sh, "action");
    }
    @Override
    public void play(GameManager game, GameActivity gameActivity) {
        game.getPlayer().addToDiscard(game.getCard("Gold"));
        game.useAfterPlay(this.getName(), true);
    }

    @Override
    public void attack(GameManager game, GameActivity gameActivity) {
        ArrayList<String> cards = game.getPlayer().takeCards(2, game, game.getLastLineFromLog().getTabs());
        if (cards.size() == 0) {
            game.setDoneAttack(true);
            return;
        }
        game.getTurn().getWaitForFunction().handleWaitingForActionCardsDialog(this.getName(), 1, 1, "", false, cards);
        gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
        gameActivity.setVisibilityForRVActionCardsPlaying(View.VISIBLE);
        boolean canRemove1 = Help.nameToCard(cards.get(0)).getType().equals("treasure") && !cards.get(0).equals("Copper");
        boolean canRemove2 = false;
        if (cards.size() > 1)
            canRemove2 = Help.nameToCard(cards.get(1)).getType().equals("treasure") && !cards.get(1).equals("Copper");

        if (!(canRemove1 && canRemove2) || cards.get(0).equals(cards.get(1))) {
            if (canRemove1 || canRemove2) {
                game.addToTrash(cards.get(canRemove1 ? 0 : 1), 1);
                cards.remove(canRemove1 ? 0 : 1);
                game.getPlayer().putArrayInDiscard(cards);
            }
            game.getTurn().getWaitForFunction().clear();
            gameActivity.turnUI();
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.setVisibilityForRVActionCardsPlaying(View.INVISIBLE);
            game.getPlayer().putArrayInDiscard(cards);
            gameActivity.updateCards(true);
            game.setDoneAttack(true);
            return;
        }

        game.getTurn().getWaitForFunction().setTypeOfAction("trash");
        gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
        gameActivity.uploadActionButtons(new String[]{gameActivity.getString(R.string.undo), gameActivity.getString(R.string.confirm_trash)}, true);
    }

    @Override
    public void handleButtonClicks(String buttonText, GameManager game, GameActivity gameActivity) {
        if (buttonText.equals(gameActivity.getString(R.string.undo))) {
            game.getTurn().getWaitForFunction().undo();
            gameActivity.updateActionButtons();
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
        }
        else if (buttonText.equals(gameActivity.getString(R.string.confirm_trash))) {
            ArrayList<String> cardSelected = game.getTurn().getWaitForFunction().cardsSelectedForDialog();
            game.addToTrash(cardSelected.get(0), 1);
            game.getPlayer().putArrayInDiscard(game.getTurn().getWaitForFunction().cardsLeftForDialog());
            game.getTurn().getWaitForFunction().clear();
            gameActivity.updateActionButtons();
            game.getPlayer().updateArrayHand();
            gameActivity.getHandAdapter().notifyDataSetChanged();
            gameActivity.getActionCardsPlayingAdapter().notifyDataSetChanged();
            gameActivity.setVisibilityForRVActionCardsPlaying(View.INVISIBLE);
            game.setDoneAttack(true);
        }
    }

    @Override
    public boolean isCardToUse(String cardName, GameManager game, GameActivity gameActivity) {
        return true;
    }
}
