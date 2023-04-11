/**
 * HandAdapter is the adapter of the RecyclerView of hand of the player.
 * The adapter updates the cards shown according to his hand.
 */
package com.example.dominion_game.activities;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.Help;

import java.util.ArrayList;

public class HandAdapter extends RecyclerView.Adapter<HandAdapter.MyViewHolder> {

    private ArrayList<Pair<String, Integer>> objects;
    private GameActivity gameActivity;

    /**
     * The constructor
     * @param objects An ArrayList of Pairs of the card name with the count of this card in hand.
     * @param gameActivity A reference to GameActivity
     */
    public HandAdapter(ArrayList<Pair<String, Integer>> objects, GameActivity gameActivity) {
        this.objects = objects;
        this.gameActivity = gameActivity;
    }

    /**
     * A view holder for every item in RecyclerView
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        ImageView ivAction, ivGreenMargin, ivRedMargin, ivYellowMargin, ivGreenX, ivRedX, ivYellowX;
        TextView countAction, countActionForPlayAction;

        /**
         * The constructor
         * @param view A View of one item in RecyclerView
         */
        public MyViewHolder(View view) {
            super(view);
            ivAction = view.findViewById(R.id.ivAction);
            ivGreenMargin = view.findViewById(R.id.ivGreenMargin);
            ivRedMargin = view.findViewById(R.id.ivRedMargin);
            ivYellowMargin = view.findViewById(R.id.ivYellowMargin);
            ivGreenX = view.findViewById(R.id.ivGreenX);
            ivRedX = view.findViewById(R.id.ivRedX);
            ivYellowX = view.findViewById(R.id.ivYellowX);
            countActionForPlayAction = view.findViewById(R.id.countActionForPlayAction);
            countAction = view.findViewById(R.id.countAction);
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
        }

        /**
         * A function that handles long press on an item in the RecyclerView
         * The function shows a dialog of the card pressed in big.
         * @param view A View of an item in RecyclerView that was pressed long
         * @return A Boolean which is always true
         */
        @Override
        public boolean onLongClick(View view) {
            ImageView iv = gameActivity.cardDialog.findViewById(R.id.ivAction);
            ImageView resourceIv = view.findViewById(R.id.ivAction);
            iv.setImageResource(Help.nameToCard(resourceIv.getContentDescription().toString()).getImageSource());
            gameActivity.cardDialog.show();
            return true;
        }

        /**
         * A function that handles a press on an item in the RecyclerView
         * The function checks if the card can be played
         * and calls a function in gameActivity to play the card.
         * @param view A View of an item in RecyclerView that was pressed
         */
        @Override
        public void onClick(View view) {
            if ((!gameActivity.game.getTurn().getPhase().contains("play") && !gameActivity.game.getTurn().getWaitForFunction().isWaitingForHand())
                    || gameActivity.game.getTurn().isWaitingForEnemy()
                    || gameActivity.game.getTurn().getWaitForFunction().isWaitingForBoard()
                    || gameActivity.game.getTurn().getWaitForFunction().isWaitingForButtonsOnly()
                    || gameActivity.game.getTurn().getWaitForFunction().isWaitingForActionCardsDialog())
                return;

            if (gameActivity.game.getTurn().getWaitForFunction().isWaitingForHand()) {
                ImageView resourceIv = view.findViewById(R.id.ivAction);
                if (!Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName())
                        .isCardToUse(resourceIv.getContentDescription().toString(), gameActivity.game, gameActivity)
                        || Help.sizeOfHash(gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay()) == gameActivity.game.getTurn().getWaitForFunction().getMaxAmount())
                    return;

                if (gameActivity.game.getTurn().getWaitForFunction().isHandleClickOnCard()
                        && !Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName()).isMarkCardSelectedFromHandWhenHandle()) {
                    gameActivity.game.getTurn().getWaitForFunction().insertCardSelectedInHand(resourceIv.getContentDescription().toString());
                    Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName())
                            .handleClickOnHandOrDialog(resourceIv.getContentDescription().toString(), gameActivity.game, gameActivity);
                    return;
                }

                TextView tvCount = view.findViewById(R.id.countAction);
                int count = Integer.valueOf(tvCount.getText().toString());
                TextView tvCountSelected = view.findViewById(R.id.countActionForPlayAction);
                int countSelected = gameActivity.game.getTurn().getWaitForFunction().getCardAmountInCardsInHand(resourceIv.getContentDescription().toString());

                if (countSelected == count)
                    return;

                if (gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("trash"))
                    view.findViewById(R.id.ivRedX).setVisibility(View.VISIBLE);
                else
                    view.findViewById(R.id.ivRedX).setVisibility(View.INVISIBLE);

                if (!gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("trash"))
                    view.findViewById(R.id.ivYellowX).setVisibility(View.VISIBLE);
                else
                    view.findViewById(R.id.ivYellowX).setVisibility(View.INVISIBLE);

                tvCountSelected.setText(String.valueOf(countSelected + 1));
                if (count > 1)
                    tvCountSelected.setVisibility(View.VISIBLE);
                else
                    tvCountSelected.setVisibility(View.INVISIBLE);

                gameActivity.game.getTurn().getWaitForFunction().insertCardSelectedInHand(resourceIv.getContentDescription().toString());
                if (gameActivity.game.getTurn().getWaitForFunction().isHandleClickOnCard()
                        && Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName()).isMarkCardSelectedFromHandWhenHandle()) {
                    Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName())
                            .handleClickOnHandOrDialog(resourceIv.getContentDescription().toString(), gameActivity.game, gameActivity);
                    return;
                }
                if (gameActivity.game.getTurn().getWaitForFunction().isHasUndoAndConfirm())
                    gameActivity.updateActionButtons();

            }
            else if (gameActivity.game.getTurn().getPhase().equals("play-action")
                    && gameActivity.game.getTurn().isMyTurn(gameActivity.game.getGameManagerBeforeStart())) {
                ImageView resourceIv = view.findViewById(R.id.ivAction);
                if (!Help.nameToCard(resourceIv.getContentDescription().toString()).getType().equals("action"))
                    return;

                gameActivity.useCard(resourceIv.getContentDescription().toString());
            }
            else if (gameActivity.game.getTurn().getPhase().equals("play-treasure-buy")
                    && gameActivity.game.getTurn().isMyTurn(gameActivity.game.getGameManagerBeforeStart())) {
                ImageView resourceIv = view.findViewById(R.id.ivAction);
                if (!Help.nameToCard(resourceIv.getContentDescription().toString()).getType().equals("treasure"))
                    return;

                gameActivity.useCard(resourceIv.getContentDescription().toString());
            }
        }
    }

    /**
     * A function that creates the layout for every item in RecyclerView.
     * @param parent
     * @param viewType
     * @return A view for each item in RecyclerView
     */
    @Override
    public HandAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        return new MyViewHolder(itemView);
    }

    /**
     * // A function that replaces the contents of every item in RecyclerView.
     * @param holder A view for each item in RecyclerView
     * @param position An Integer of the position of the view in the ArrayList
     */
    @Override
    public void onBindViewHolder(HandAdapter.MyViewHolder holder, int position) {
        holder.ivAction.setImageResource(Help.nameToCard(this.objects.get(position).first).getImageSource());
        holder.countAction.setText(String.valueOf(this.objects.get(position).second));
        if (this.objects.get(position).second > 1)
            holder.countAction.setVisibility(View.VISIBLE);
        else
            holder.countAction.setVisibility(View.INVISIBLE);

        holder.ivAction.setContentDescription(this.objects.get(position).first);
        if (gameActivity.game.getTurn().getWaitForFunction().isWaitingForHand()
                && Help.nameToCard(gameActivity.game.getTurn().getWaitForFunction().getCardName())
                .isCardToUse(this.objects.get(position).first, gameActivity.game, gameActivity)) {
            if (gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("trash")) {
                holder.ivRedMargin.setVisibility(View.VISIBLE);
                if (gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(this.objects.get(position).first) == null) {
                    holder.ivRedX.setVisibility(View.INVISIBLE);
                    holder.countActionForPlayAction.setVisibility(View.INVISIBLE);
                }
                else {
                    holder.ivRedX.setVisibility(View.VISIBLE);
                    if (gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(this.objects.get(position).first) > 1) {
                        holder.countActionForPlayAction.setText(String.valueOf(gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(this.objects.get(position).first)));
                        holder.countActionForPlayAction.setVisibility(View.VISIBLE);
                    }
                    else
                        holder.countActionForPlayAction.setVisibility(View.INVISIBLE);
                }
            }
            else {
                holder.ivRedMargin.setVisibility(View.INVISIBLE);
                holder.ivRedX.setVisibility(View.INVISIBLE);
                holder.countActionForPlayAction.setVisibility(View.INVISIBLE);
            }

            if (gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("use"))
                holder.ivGreenMargin.setVisibility(View.VISIBLE);
            else
                holder.ivGreenMargin.setVisibility(View.INVISIBLE);

            if (!gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("trash")
                    && !gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("use")) {
                holder.ivYellowMargin.setVisibility(View.VISIBLE);
                if (gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(this.objects.get(position).first) == null) {
                    holder.ivYellowX.setVisibility(View.INVISIBLE);
                    holder.countActionForPlayAction.setVisibility(View.INVISIBLE);
                }
                else {
                    holder.ivYellowX.setVisibility(View.VISIBLE);
                    if (gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(this.objects.get(position).first) > 1) {
                        holder.countActionForPlayAction.setText(String.valueOf(gameActivity.game.getTurn().getWaitForFunction().getCardsForActionCardPlay().get(this.objects.get(position).first)));
                        holder.countActionForPlayAction.setVisibility(View.VISIBLE);
                    }
                    else
                        holder.countActionForPlayAction.setVisibility(View.INVISIBLE);
                }
            }
            else {
                holder.ivYellowMargin.setVisibility(View.INVISIBLE);
                holder.ivYellowX.setVisibility(View.INVISIBLE);
                holder.countActionForPlayAction.setVisibility(View.INVISIBLE);
            }
        }
        else {
            holder.ivGreenMargin.setVisibility(View.INVISIBLE);
            holder.ivRedMargin.setVisibility(View.INVISIBLE);
            holder.ivRedX.setVisibility(View.INVISIBLE);
            holder.ivYellowMargin.setVisibility(View.INVISIBLE);
            holder.ivYellowX.setVisibility(View.INVISIBLE);
            holder.countActionForPlayAction.setVisibility(View.INVISIBLE);
        }

        if (!gameActivity.game.getTurn().getWaitForFunction().isWaitingForHand()
                && !gameActivity.game.getTurn().isWaitingForEnemy()
                && !gameActivity.game.getTurn().getWaitForFunction().isWaitingForBoard()
                && !gameActivity.game.getTurn().getWaitForFunction().isWaitingForButtonsOnly()
                && !gameActivity.game.getTurn().getWaitForFunction().isWaitingForActionCardsDialog()
                && gameActivity.game.getTurn().isMyTurn(gameActivity.game.getGameManagerBeforeStart())
                && ((Help.nameToCard(this.objects.get(position).first).getType().equals("action")
                && gameActivity.game.getTurn().getPhase().equals("play-action"))
                || (Help.nameToCard(this.objects.get(position).first).getType().equals("treasure")
                && gameActivity.game.getTurn().getPhase().equals("play-treasure-buy"))))
            holder.ivGreenMargin.setVisibility(View.VISIBLE);
        else {
            if (!gameActivity.game.getTurn().getWaitForFunction().getTypeOfAction().equals("use"))
                holder.ivGreenMargin.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @return An Integer of the size of the ArrayList of objects
     */
    @Override
    public int getItemCount() {
        return this.objects.size();
    }
}
