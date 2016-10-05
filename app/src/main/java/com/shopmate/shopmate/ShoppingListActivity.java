package com.shopmate.shopmate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

        // These are just some mock items to add to the list.
        // TODO retrieve these items from the database
        final List<String> items = new ArrayList<>();
        for (int i = 0; i < 10; i ++) {
            items.add("Food " + i);
        }

        // Creates an adapter which is used maintain and render a list of items
        ListView shoppingList = (ListView) findViewById(R.id.shoppingList);
        final ShoppingListItemAdapter shoppingListItemAdapter = new ShoppingListItemAdapter(this, R.layout.shopping_list_item, items);
        assert shoppingList != null;
        shoppingList.setAdapter(shoppingListItemAdapter);

        // Adds a new item to this shopping list when the button is pressed
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Either bring user to new item activity or allow user to enter item info here
                items.add("New Food Item");
                shoppingListItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private class ShoppingListItemAdapter extends ArrayAdapter<String> {
        // TODO create an item class that contains things like price, quantity, brand, etc.
        private List<String> items;
        private Context context;

        ShoppingListItemAdapter(Context context, int resourceId, List<String> items) {
            super(context, resourceId, items);
            this.items = items;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.shopping_list_item, parent);
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
