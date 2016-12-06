package com.shopmate.shopmate;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.provider.MediaStore.Images.Media;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateServiceProvider;
import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.model.item.ShoppingListItemBuilder;
import com.shopmate.api.model.item.ShoppingListItemPriority;
import com.shopmate.api.model.result.CreateShoppingListItemResult;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.GetAllShoppingListsResult;

import java.io.File;
import java.io.FileDescriptor;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class AddItemActivity extends AppCompatActivity {

    public static EditText name;
    public static EditText quantity;
    public static EditText price;
    static final int WALMART_SEARCH = 2;
    private static final int SELECT_PICTURE = 1;
    private static final int WRITE_PERMISSION = 0x01;
    private static Uri selectedImageUri; //stores the image url for the item
    private Spinner spinner; //stores the selected item importance taken from the dropdown menu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add Item");
        requestWritePermission(); //used for accessing photos: ask for permission first



        //For populating the Spinner object (item importance)
        spinner = (Spinner)findViewById(R.id.itemImp);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.importance_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // For the checkmark to be pressed (on the bottom) once the user adds an item
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() == 0) {
                    name.setError("No item specified");
                    return;
                }
                if (quantity.getText().length() == 0 ||
                        Integer.parseInt(quantity.getText().toString()) == 0) {
                    quantity.setError("No valid quantity specified");
                    return;
                }
                if (price.getText().length() == 0) {
                    price.setError("No valid price specified");
                    return;
                }
                syncItem();
            }
        });
        name = (EditText)findViewById(R.id.itemName);
        quantity = (EditText)findViewById(R.id.itemQty);
        price = (EditText)findViewById(R.id.itemPrice);

        // For a user adding a picture while adding an item
        ((ImageButton)findViewById(R.id.itemPhoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Triggers intent for the Image Gallery to be loaded
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    //Gets called once an image is selected. Taken from http://viralpatel.net/blogs/pick-image-from-galary-android-app/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                try {
                    //Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    ImageButton imgButton = (ImageButton)findViewById(R.id.itemPhoto);
                    //imgButton.setImageBitmap(b);
                    imgButton.setImageBitmap(decodeSampledBitmap(selectedImageUri, 100, 100));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == WALMART_SEARCH) {
                    String walmartItemName = data.getStringExtra("itemName");
                    int walmartItemPrice = Integer.parseInt(data.getStringExtra("itemPrice"));
                    String walmartItemDesc = data.getStringExtra("itemDesc");
                    String walmartItemURL = data.getStringExtra("itemImage");

                    name.setText(walmartItemName);

                    double walmartItemDouble = ((double)walmartItemPrice)/100;
                    price.setText(Double.toString(walmartItemDouble));

                    TextView desc = (TextView) findViewById(R.id.itemDesc);
                    desc.setText(walmartItemDesc);

                    ImageButton image = (ImageButton) findViewById(R.id.itemPhoto);
                    Picasso.with(this).load(walmartItemURL).into(image);
                    selectedImageUri = Uri.parse(walmartItemURL);

                quantity.setText("1");
            }
        }
    }

    //Generates a bitmap from a given URI path
    private Bitmap decodeBitmapFromUri(Uri uri, BitmapFactory.Options options) throws IOException {
        InputStream is = getContentResolver().openInputStream(uri);
        Bitmap b = BitmapFactory.decodeStream(is, null, options);
        is.close();
        return b;
    }

    //Decodes a bitmap with a given width and height.
    public Bitmap decodeSampledBitmap(Uri uri, int reqWidth, int reqHeight) throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeBitmapFromUri(uri, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return decodeBitmapFromUri(uri, options);
    }

    //Calculates the sample size from given Bitmap features (i.e. loads a smaller image into memory)
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //Checks to see if we have permission to access the user's photos. If not, prompt the user for access.
    private void requestWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
            }
        }
    }

    //Ties all of the information passed from the client to the backend server via ShopMateService API
    private void syncItem() {

        //Get the list name & list id values from the ShoppingListActivity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String listName = extras.getString("title");
        int listId = Integer.parseInt(extras.getString("listId"));

        // Get the ShopMateService singleton
        ShopMateService service = ShopMateServiceProvider.get();

        // Get the user's Facebook token
        String fbToken = AccessToken.getCurrentAccessToken().getToken();

        //Get user data from the screen
        EditText itemDescription = (EditText)findViewById(R.id.itemDesc);
        EditText itemPrice = (EditText)findViewById(R.id.itemPrice);
        EditText itemQuantity = (EditText)findViewById(R.id.itemQty);


        //Create a shopping list item to be used in the next API call
        ShoppingListItem testItem = new ShoppingListItemBuilder(name.getText().toString())
                .description(itemDescription.getText().toString().trim())
                .imageUrl(selectedImageUri == null ? Optional.<String>absent() : Optional.of(selectedImageUri.toString()))
                .maxPriceCents( Math.round(Float.parseFloat(itemPrice.getText().toString()) * 100) )
                .quantity(Integer.parseInt(itemQuantity.getText().toString()))
                .quantityPurchased(0) //TODO: Maybe change this val?
                .priority(convertPriority(spinner.getSelectedItem().toString()))
                .build();


        // Start an API call in the background
        ListenableFuture<CreateShoppingListItemResult> future = service.createItemAsync(fbToken, listId, testItem);

        //CreateShoppingListResult createListResult = service.createListAsync(fbToken, listName, ImmutableSet.<String>of()).get();
        //CreateShoppingListItemResult createItemResult = service.createItemAsync(fbToken, createListResult.getId(), testItem).get();
        //ShoppingListItem createdItem = createItemResult.getItem();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Adding item...");
        progress.show();

        //Register callbacks to run on the main thread once the API call completes
        Futures.addCallback(future, new FutureCallback<CreateShoppingListItemResult>() {
            public void onSuccess(final CreateShoppingListItemResult result) {
                //Use runOnUiThread to update UI controls
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        EditText itemPrice = (EditText)findViewById(R.id.itemPrice);
                        EditText itemQuantity = (EditText)findViewById(R.id.itemQty);

                        progress.dismiss();
                        Intent res = new Intent();
                        res.putExtra("item_name", name.getText().toString());
                        res.putExtra("item_prio", spinner.getSelectedItem().toString());
                        res.putExtra("item_id", Long.toString(result.getId()));
                        res.putExtra("item_price", itemPrice.getText().toString());
                        res.putExtra("item_quan", itemQuantity.getText().toString());
                        if(selectedImageUri != null) {
                            res.putExtra("item_img", selectedImageUri.toString());
                            selectedImageUri = null;
                        }
                        setResult(RESULT_OK, res);
                        finish();
                    }
                });
            }
            public void onFailure(final Throwable t) {
                //Use runOnUiThread to update UI controls
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        Toast.makeText(AddItemActivity.this, "Unable to sync item.", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                        Log.e("ErrorStuff",  Log.getStackTraceString(t), t);
                    }
                });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.walmart_search_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.walmart_search_bar) {
            Intent init = new Intent(AddItemActivity.this, WalmartSearch.class);
            init.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(init, WALMART_SEARCH);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
