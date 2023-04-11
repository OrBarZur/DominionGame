/**
 * LogAdapter is the adapter of the ListView of the log of the game.
 * The adapter updates the log shown according to the actions of the players.
 */
package com.example.dominion_game.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.GameManager;
import com.example.dominion_game.classes.LogLine;

import java.util.ArrayList;


public class LogAdapter extends ArrayAdapter<LogLine> {

    Context context;
    ArrayList<LogLine> data;
    GameManager gameManager;

    /**
     * The constructorך
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param data An ArrayList of LogLine with all lines in log
     * @param gameManager A reference to gameManager
     */
    public LogAdapter(Context context, int resource, int textViewResourceId, ArrayList<LogLine> data, GameManager gameManager) {
        super(context, resource, textViewResourceId, data);

        this.context = context;
        this.data = data;
        this.gameManager = gameManager;
    }

    /**
     * A function that creates the layout of every item in ListView
     * and updates the data inside it according to the Log Line attributes.
     * @param position An Integer of the position of the view in the ArrayList
     * @param convertView
     * @param parent A ViewGroup of the views in ListView according to the size of data
     * @return A View which is the view of the item in ListView according to the Log Line attributes
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.log_layout, parent, false);

        TextView tvLine = view.findViewById(R.id.tvLine);
        TextView tvPlayer = view.findViewById(R.id.tvPlayer);
        RelativeLayout rl = view.findViewById(R.id.rl);
        LogLine logLine = data.get(position);

        tvLine.setTextSize(10);
        tvLine.setText(logLine.getText());
        tvLine.setTextColor(context.getResources().getColor(context.getResources().getIdentifier(logLine.getColor(), "color", context.getPackageName())));

        if (logLine.getPlayerId().equals(""))
            tvPlayer.setVisibility(View.GONE);
        else {
            tvPlayer.setVisibility(View.VISIBLE);
            tvPlayer.setTextSize(10);
            tvPlayer.setText(String.valueOf(logLine.getPlayerId().charAt(0)));
            if (logLine.getPlayerId().equals(this.gameManager.getGameManagerBeforeStart().getMyId()))
                tvPlayer.setTextColor(context.getResources().getColor(R.color.red));
            else
                tvPlayer.setTextColor(context.getResources().getColor(R.color.green));
        }

        if (logLine.isBold() && logLine.isItalic())
            tvLine.setTypeface(null, Typeface.BOLD_ITALIC);

        else if (logLine.isBold())
            tvLine.setTypeface(null, Typeface.BOLD);

        else if (logLine.isItalic())
            tvLine.setTypeface(null, Typeface.ITALIC);

        rl.setPadding(logLine.getTabs()*40, 1, 1, 1);
        /*
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tvPlayer.getLayoutParams();
        // params.setMargins(logLine.getTabs()*20, params.topMargin, params.rightMargin, params.bottomMargin);
        tvPlayer.setLayoutParams(params);
         */

        return view;
    }

    /**
     * A function that disables the option to click on any item in the ListView.
     * @param position An Integer of the position of the view in the ArrayList
     * @return A Boolean which is always false
     */
    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
