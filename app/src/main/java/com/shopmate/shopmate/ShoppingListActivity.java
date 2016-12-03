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
        final List<ShoppingListItem> items = new ArrayList<ShoppingListItem>();

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
                double price = Double.parseDouble(d.getStringExtra("item_price"));
                price *= 100;
                final ShoppingListItemBuilder bld = new ShoppingListItemBuilder(null)
                        .name(d.getStringExtra("item_name"))
                        .imageUrl(d.getStringExtra("item_img"))
                        .priority(convertPriority(d.getStringExtra("item_prio")))
                        .quantity(Integer.parseInt(d.getStringExtra("item_quan")))
                        .maxPriceCents(((int) price));
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

            //Puts the name in for each item
            String itemName = items.get(position).getName();

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.itemCheckBox);
            checkBox.setText(itemName);

            ImageView imageView = (ImageView) view.findViewById(R.id.itemImageView);
            String imageURL = "http://1030news.com/wp-content/themes/fearless/images/missing-image-640x360.png";
                    if(items.get(position).getImageUrl().isPresent() && items.get(position).getImageUrl().get() != "") {
                        imageURL = items.get(position).getImageUrl().get();
                    }
            if(imageURL.contains("http")) {
                //web location
                Picasso.with(getContext())
                        .load(imageURL)
                        .resize(150, 150)
                        .into(imageView);
            }
            else {
                //phone location
                Picasso.with(getContext())
                        .load(new File(imageURL))
                        .resize(150, 150)
                        .into(imageView);
            }

            //Puts the quantity in for each item
            TextView listItemQuantity = (TextView) view.findViewById(R.id.listItemQuantity);
            listItemQuantity.setText("Quantity: " + Integer.toString(items.get(position).getQuantity()));

            //Puts the price in for each item
            TextView listItemPrice = (TextView) view.findViewById(R.id.listItemPrice);
            double price = ((double) items.get(position).getMaxPriceCents().get() / 100);
            listItemPrice.setText("Price: $" + Double.toString(price));

            return view;
        }
    }
}
