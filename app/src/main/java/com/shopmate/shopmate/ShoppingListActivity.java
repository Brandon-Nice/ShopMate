package com.shopmate.shopmate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    static final int ADD_ITEM_REQUEST = 1;
    static ShoppingListItemAdapter sla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);


        // These are just some mock items to add to the list.
        // TODO retrieve these items from the database
        final List<String> items = new ArrayList<>();
        items.add("Bread");
        items.add("Cheese");
        items.add("Wine");
        items.add("Toilet Paper");
        items.add("Socks");

        // Creates an adapter which is used maintain and render a list of items
        ListView shoppingList = (ListView) findViewById(R.id.shoppingList);
        ShoppingListItemAdapter shoppingListItemAdapter = new ShoppingListItemAdapter(this, R.layout.shopping_list_item, items);
        sla = shoppingListItemAdapter;
        assert shoppingList != null;
        shoppingList.setAdapter(shoppingListItemAdapter);

        // Adds a new item to this shopping list when the button is pressed
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Either bring user to new item activity or allow user to enter item info here
                startActivityForResult(new Intent(view.getContext(), AddItemActivity.class), ADD_ITEM_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_ITEM_REQUEST) {
                final Intent d = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sla.add(d.getStringExtra("item"));
                    }
                });
            }
        }
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
                view = View.inflate(context, R.layout.shopping_list_item, null);
            } else {
                view = convertView;
            }

            String itemName = items.get(position);

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.itemCheckBox);
            checkBox.setText(itemName);

            ImageView imageView = (ImageView) view.findViewById(R.id.itemImageView);
            Picasso.with(getContext())
                    .load("http://doseoffunny.com/wp-content/uploads/2014/04/tumblr_mtanx0poHz1qdlh1io1_400.gif")
                    .resize(150,150)
                    .into(imageView);

            return view;
        }
    }
}
