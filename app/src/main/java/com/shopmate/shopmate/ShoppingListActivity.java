package com.shopmate.shopmate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.common.collect.ImmutableSet;
import com.shopmate.api.net.NetShopMateService;
import com.squareup.picasso.Picasso;
import com.facebook.AccessToken;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.shopmate.api.ShopMateServiceProvider;
import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.model.item.ShoppingListItemBuilder;
import com.shopmate.api.model.item.ShoppingListItemHandle;
import com.shopmate.api.model.item.ShoppingListItemPriority;
import com.shopmate.api.model.list.ShoppingList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ShoppingListActivity extends AppCompatActivity {

    static final int ADD_ITEM_REQUEST = 1;
    static ShoppingListItemAdapter sla;
    private String listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle extras = getIntent().getExtras();
        final String title = extras.getString("title");
        listId = extras.getString("listId");
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        // These are just some mock items to add to the list.
        // TODO retrieve these items from the database
        final List<ShoppingListItem> items = new ArrayList<ShoppingListItem>();

        // Creates an adapter which is used maintain and render a list of items
        final ListView shoppingList = (ListView) findViewById(R.id.shoppingList);
        ShoppingListItemAdapter shoppingListItemAdapter = new ShoppingListItemAdapter(this, R.layout.shopping_list_item, items);
        sla = shoppingListItemAdapter;
        assert shoppingList != null;
        shoppingList.setAdapter(shoppingListItemAdapter);

        Futures.addCallback(ShopMateServiceProvider.get().getListAndItemsAsync(AccessToken.getCurrentAccessToken().getToken(), Long.parseLong(listId)), new FutureCallback<ShoppingList>() {
            @Override
            public void onSuccess(ShoppingList result) {
                final ArrayList<ShoppingListItem> tmp = new ArrayList<ShoppingListItem>();
                for (ShoppingListItemHandle i : result.getItems()) {
                    tmp.add(i.getItem().or(new ShoppingListItemBuilder("garbage").build()));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sla.addAll(tmp);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(shoppingList, "defeat", Snackbar.LENGTH_LONG).show();
            }
        });

        // Adds a new item to this shopping list when the button is pressed
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Either bring user to new item activity or allow user to enter item info here
                Intent i = new Intent(view.getContext(), AddItemActivity.class);
                Bundle extras = new Bundle();
                extras.putString("title", title);
                extras.putString("listId", listId);
                i.putExtras(extras);
                startActivityForResult(i, ADD_ITEM_REQUEST);
            }
        });
    }

    //Converts a string priority to the ShoppingListItemPriority enum
    private ShoppingListItemPriority convertPriority(String priority){
        switch(priority){
            case "Low":
                return ShoppingListItemPriority.LOW;
            case "Medium":
                return ShoppingListItemPriority.NORMAL;
            case "High":
                return ShoppingListItemPriority.HIGH;
            default:
                return ShoppingListItemPriority.LOW;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_ITEM_REQUEST) {
                final Intent d = data;
                final ShoppingListItemBuilder bld = new ShoppingListItemBuilder(null)
                        .name(d.getStringExtra("item_name"))
                        .priority(convertPriority(d.getStringExtra("item_prio")));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sla.add(bld.build());
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting, menu);
        return true;
    }

    private class AlphaComparator implements Comparator<ShoppingListItem> {

        @Override
        public int compare(ShoppingListItem lhs, ShoppingListItem rhs) {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }

    private class PrioComparator implements Comparator<ShoppingListItem> {

        @Override
        public int compare(ShoppingListItem lhs, ShoppingListItem rhs) {
            return rhs.getPriority().compareTo(lhs.getPriority()); // given current enum, this orders HIGH to LOW
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_alpha) {
            sla.sort(new AlphaComparator());
            return true;
        } else if (id == R.id.sort_prio) {
            sla.sort(new PrioComparator());
            return true;
        } else if (id == R.id.show_members) {
            Intent intent = new Intent(this, ListMembersActivity.class);
            intent.putExtra("listId", listId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ShoppingListItemAdapter extends ArrayAdapter<ShoppingListItem> {
        private List<ShoppingListItem> items;
        private Context context;
        private int layout;

        ShoppingListItemAdapter(Context context, int resourceId, List<ShoppingListItem> items) {
            super(context, resourceId, items);
            this.items = items;
            this.context = context;
            this.layout = resourceId;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(context, layout, null);
            } else {
                view = convertView;
            }

            String itemName = items.get(position).getName();

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
