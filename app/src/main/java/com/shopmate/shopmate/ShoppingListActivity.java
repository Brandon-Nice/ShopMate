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
import android.widget.TextView;

import com.google.common.base.Optional;
import com.shopmate.api.ShopMateService;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ShoppingListActivity extends AppCompatActivity {

    static final int ADD_ITEM_REQUEST = 1;

    private ShoppingListItemAdapter sla;
    private Comparator<ShoppingListItemHandle> comparator = new PrioComparator();
    private UpdateListener updateListener;
    private long listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle extras = getIntent().getExtras();
        final String title = extras.getString("title");
        listId = Long.parseLong(extras.getString("listId"));
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        // These are just some mock items to add to the list.
        // TODO retrieve these items from the database
        final List<ShoppingListItemHandle> items = new ArrayList<ShoppingListItemHandle>();

        // Creates an adapter which is used maintain and render a list of items
        final ListView shoppingList = (ListView) findViewById(R.id.shoppingList);
        ShoppingListItemAdapter shoppingListItemAdapter = new ShoppingListItemAdapter(this, R.layout.shopping_list_item, items);
        sla = shoppingListItemAdapter;
        assert shoppingList != null;
        shoppingList.setAdapter(shoppingListItemAdapter);

        Futures.addCallback(ShopMateServiceProvider.get().getListAndItemsAsync(AccessToken.getCurrentAccessToken().getToken(), listId), new FutureCallback<ShoppingList>() {
            @Override
            public void onSuccess(final ShoppingList result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sla.addAll(result.getItems());
                        sla.sort(comparator);
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
                extras.putString("listId", Long.toString(listId));
                i.putExtras(extras);
                startActivityForResult(i, ADD_ITEM_REQUEST);
            }
        });

        updateListener = new UpdateListener(this, new UpdateHandler() {
            @Override
            public void onItemAdded(long itemListId, final long itemId) {
                if (itemListId != listId || findItem(itemId) >= 0) {
                    return;
                }
                String fbToken = AccessToken.getCurrentAccessToken().getToken();
                ShopMateService service = ShopMateServiceProvider.get();
                Futures.addCallback(service.getItemAsync(fbToken, itemId), new FutureCallback<ShoppingListItem>() {
                    @Override
                    public void onSuccess(final ShoppingListItem result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (findItem(itemId) >= 0) {
                                    return;
                                }
                                sla.add(new ShoppingListItemHandle(itemId, Optional.of(result)));
                                sla.sort(comparator);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onItemUpdated(final long itemId) {
                if (findItem(itemId) < 0) {
                    return;
                }
                String fbToken = AccessToken.getCurrentAccessToken().getToken();
                ShopMateService service = ShopMateServiceProvider.get();
                Futures.addCallback(service.getItemAsync(fbToken, itemId), new FutureCallback<ShoppingListItem>() {
                    @Override
                    public void onSuccess(final ShoppingListItem result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int index = findItem(itemId);
                                if (index < 0) {
                                    return;
                                }
                                sla.remove(sla.getItem(index));
                                sla.add(new ShoppingListItemHandle(itemId, Optional.of(result)));
                                sla.sort(comparator);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onItemDeleted(long itemId) {
                int index = findItem(itemId);
                if (index >= 0) {
                    sla.remove(sla.getItem(index));
                }
            }

            @Override
            public void onListDeleted(long deletedListId) {
                if (deletedListId == listId) {
                    // TODO: Display a message telling the user that the list was deleted?
                    finish();
                }
            }

            @Override
            public void onListMemberLeft(long leftListId, String userId) {
                if (leftListId == listId && userId == AccessToken.getCurrentAccessToken().getUserId()) {
                    // TODO: Display a message telling the user that they were kicked?
                    finish();
                }
            }
        });
        updateListener.register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateListener.unregister();
    }

    private int findItem(long itemId) {
        for (int i = 0; i < sla.getCount(); i++) {
            if (sla.getItem(i).getId() == itemId) {
                return i;
            }
        }
        return -1;
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
                double price = Double.parseDouble(d.getStringExtra("item_price"));
                price *= 100;
                final ShoppingListItemBuilder bld = new ShoppingListItemBuilder(null)
                        .name(d.getStringExtra("item_name"))
                        .priority(convertPriority(d.getStringExtra("item_prio")))
                        .imageUrl(d.getStringExtra("item_img"))
                        .priority(convertPriority(d.getStringExtra("item_prio")))
                        .quantity(Integer.parseInt(d.getStringExtra("item_quan")))
                        .maxPriceCents(((int) price));

                final long id = Long.parseLong(d.getStringExtra("item_id"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (findItem(id) >= 0) {
                            return;
                        }
                        sla.add(new ShoppingListItemHandle(id, Optional.of(bld.build())));
                        sla.sort(comparator);
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sorting, menu);
        getMenuInflater().inflate(R.menu.share_button_menu, menu);
        return true;
    }

    private class AlphaComparator implements Comparator<ShoppingListItemHandle> {

        @Override
        public int compare(ShoppingListItemHandle lhs, ShoppingListItemHandle rhs) {
            return lhs.getItem().get().getName().compareToIgnoreCase(rhs.getItem().get().getName());
        }
    }

    private class PrioComparator implements Comparator<ShoppingListItemHandle> {

        @Override
        public int compare(ShoppingListItemHandle lhs, ShoppingListItemHandle rhs) {
            int comparison = rhs.getItem().get().getPriority().compareTo(lhs.getItem().get().getPriority()); // given current enum, this orders HIGH to LOW
            if (comparison != 0) {
                return comparison;
            }
            // Compare by name if two items have the same priority
            return lhs.getItem().get().getName().compareToIgnoreCase(rhs.getItem().get().getName());
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
            comparator = new AlphaComparator();
            sla.sort(comparator);
            return true;
        } else if (id == R.id.sort_prio) {
            comparator = new PrioComparator();
            sla.sort(comparator);
            return true;
        } else if (id == R.id.share_button){
            //Open up an activity to select the friend who you want to share a list with
            Intent intent = new Intent(ShoppingListActivity.this, SharingListsActivity.class);
            intent.putExtra("listId", listId);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private class ShoppingListItemAdapter extends ArrayAdapter<ShoppingListItemHandle> {
        private List<ShoppingListItemHandle> items;
        private Context context;
        private int layout;

        ShoppingListItemAdapter(Context context, int resourceId, List<ShoppingListItemHandle> items) {
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

            String itemName = items.get(position).getItem().get().getName();

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.itemCheckBox);
            checkBox.setText(itemName);

            //Puts the image in for each item
            ImageView imageView = (ImageView) view.findViewById(R.id.itemImageView);
            String imageURL = "http://1030news.com/wp-content/themes/fearless/images/missing-image-640x360.png";
            if(items.get(position).getItem().get().getImageUrl().isPresent() && items.get(position).getItem().get().getImageUrl().get() != "") {
                imageURL = items.get(position).getItem().get().getImageUrl().get();
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
            listItemQuantity.setText("Quantity: " + Integer.toString(items.get(position).getItem().get().getQuantity()));

            //Puts the price in for each item
            TextView listItemPrice = (TextView) view.findViewById(R.id.listItemPrice);
            double price = ((double) items.get(position).getItem().get().getMaxPriceCents().get() / 100);
            listItemPrice.setText("Price: $" + Double.toString(price));
            return view;
        }
    }
}
