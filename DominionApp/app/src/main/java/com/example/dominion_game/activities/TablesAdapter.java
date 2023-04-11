/**
 * TablesAdapter is the adapter of the ListView of all the online tables.
 * The adapter updates the tables shown in real time.
 */
package com.example.dominion_game.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.dominion_game.R;
import com.example.dominion_game.classes.GameManagerBeforeStart;
import com.example.dominion_game.classes.Table;

import java.util.List;


public class TablesAdapter extends ArrayAdapter<Table> {

    Context context;
    List<Table> data;
    Table tempTable;
    OnlineTablesActivity onlineTablesActivity;
    SharedPreferences sharedPreferences;

    /**
     * The constructor
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param data An ArrayList of LogLine with all tables online
     * @param onlineTablesActivity A reference to onlineTablesActivity
     */
    public TablesAdapter(Context context, int resource, int textViewResourceId, List<Table> data, OnlineTablesActivity onlineTablesActivity) {
        super(context, resource, textViewResourceId, data);

        this.context = context;
        this.data = data;
        this.onlineTablesActivity = onlineTablesActivity;
        this.sharedPreferences = onlineTablesActivity.getSharedPreferences("account", Context.MODE_PRIVATE);
    }

    /**
     * A function that creates the layout of every item in ListView
     * and updates the data inside it according to the data ArrayList.
     * @param position An Integer of the position of the view in the ArrayList
     * @param convertView
     * @param parent A ViewGroup of the views in ListView according to the size of data
     * @return A View which is the view of the item in ListView according to the data ArrayList
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.table_layout, parent, false);
        TextView tvName = view.findViewById(R.id.tvName);
        Button btnJoin = view.findViewById(R.id.btnJoin);
        tempTable = data.get(position);
        tvName.setText(tempTable.getCreator());
        if (position == 0) {
            btnJoin.setVisibility(View.INVISIBLE);
            return view;
        }

        btnJoin.setOnClickListener(new View.OnClickListener() {
            /**
             * A function that handles a click on the "play" button on every item in the ListView.
             * @param view A View which is the item that was pressed
             */
            @Override
            public void onClick(View view) {
                onlineTablesActivity.setFinished(true);
                Intent intent = new Intent(context, PrepareGameActivity.class);
                intent.putExtra("gameManagerBeforeStart", new GameManagerBeforeStart(tempTable.getId(), sharedPreferences.getString("username", "")));
                context.startActivity(intent);
            }
        });
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
