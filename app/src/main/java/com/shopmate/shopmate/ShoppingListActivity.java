package com.shopmate.shopmate;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This action will normally add a new item to the shopping list", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // These are just some mock items to add to the list.
        // TODO retrieve these items from the database
        List<String> items = new ArrayList<String>();
        for (int i = 0; i < 10; i ++) {
            items.add("Food " + i);
        }

        ListView shoppingList = (ListView) findViewById(R.id.shoppingList);
        ShoppingListItemAdapter shoppingListItemAdapter = new ShoppingListItemAdapter(this, R.layout.shopping_list_item, items);
        shoppingList.setAdapter(shoppingListItemAdapter);
    }

    private class ShoppingListItemAdapter extends ArrayAdapter<String> {
        // TODO create an item class that contains things like price, quantity, brand, etc.
        private List<String> items;
        private Context context;

        public ShoppingListItemAdapter(Context context, int resourceId, List<String> items) {
            super(context, resourceId, items);
            this.items = items;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.shopping_list_item, null);
            } else {
                view = convertView;
            }

            String itemName = items.get(position);

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            checkBox.setText(itemName);

            return view;
        }
    }

}
