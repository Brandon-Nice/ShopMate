package com.shopmate.shopmate;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.model.item.ShoppingListItemBuilder;
import com.squareup.picasso.Picasso;

public class WalmartSearch extends AppCompatActivity {

    public static EditText walmartEditText;
    String searchTerms = "";
    public String url;
    private ListView walmartList;
    private ArrayList<ShoppingListItem> walmartResult;
    private ShoppingListItem Item;
    static ShoppingListItemAdapter sla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walmart_search);

        findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

        (findViewById(R.id.walmartSearchButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walmartEditText = (EditText) findViewById(R.id.walmartEditText);
                searchTerms = walmartEditText.getText().toString();
                if (!searchTerms.equals(" ") && !searchTerms.equals("")) {
                    new JSONParse().execute();
                }
                else {
                    walmartEditText.setError("Search text is required!");
                }
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

        });
        walmartList = (ListView) findViewById(R.id.walmartListView);
        walmartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShoppingListItem resultItem = (ShoppingListItem) walmartList.getItemAtPosition(position);
                Bundle b = new Bundle();
                b.putString("itemName",resultItem.getName());
                b.putString("itemPrice", resultItem.getMaxPriceCents().get().toString());
                b.putString("itemDesc", resultItem.getDescription());
                b.putString("itemImage", resultItem.getImageUrl().get());

                Intent i = new Intent();
                i.putExtras(b);
                setResult(RESULT_OK, i);
                finish();

            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        walmartEditText = (EditText) findViewById(R.id.walmartEditText);
        walmartEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchTerms = v.getText().toString();
                    if (!searchTerms.equals(" ") && !searchTerms.equals("")) {
                        new JSONParse().execute();
                    }
                    else {
                        walmartEditText.setError("Search text is required!");
                    }

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                else if(event.getKeyCode() == KeyEvent.KEYCODE_SPACE) {
                    searchTerms = v.getText().toString();
                    new JSONParse().execute();
                }
                return true;
            }
        });
    }

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString;

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();

        jsonString = sb.toString();

        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

    public class JSONParse extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            String searchTerms = walmartEditText.getText().toString();
                //TODO: Extra features: category, sorting (increasing/decreasing price, relevant, bestsellers)

                //Prepare the URL
                url = "http://api.walmartlabs.com/v1/search?query=";
                String sort = "";
                String order = "";

                //Add the search terms to the query url
                searchTerms = searchTerms.replace(" ", "");
                url += searchTerms;

                //Add the format and apiKey
                url += "&format=json";
                url += "&apiKey=hk42mtbf4g9y59gvb4z4swuu";

                    /*
                    if sort changed from relevance to price, title, bestseller, customerrating, new, add it to the search query
                    if order is changed from asc to desc, add it to the search query (only needed for price, title, customerrating)
                     */
                //Add the number of displayed items to 25 for more results
                url += "&numItems=25";

                System.out.println("URL:" + url);

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                JSONObject json = getJSONObjectFromURL(url);
                    JSONArray jsonArray = json.getJSONArray("items");
                    int numItems = json.getInt("numItems");
                    System.out.println("numItems: " + numItems);

                    walmartResult = new ArrayList<ShoppingListItem>();

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject JSONitem = jsonArray.getJSONObject(i);
                        String name = JSONitem.getString("name");
                        Double price = JSONitem.getDouble("salePrice");
                        int priceCents = (int)(price*100);
                        String descr;
                        if (JSONitem.has("shortDescription")) {
                            descr = JSONitem.getString("shortDescription");
                        } else { // I haven't been able to get shortDescription to work consistently and Walmart says that longDescription will always work
                            descr = JSONitem.getString("longDescription");
                        }
                        String image = JSONitem.getString("thumbnailImage");

                        System.out.println(name + ": " + price);

                        Item = new ShoppingListItemBuilder(name)
                                .description(descr)
                                .imageUrl(image)
                                .maxPriceCents(priceCents)
                                .build();

                        walmartResult.add(Item);
                        if(numItems == 0) {
                            walmartResult = null;
                        }
                    }
                return json;
            }
            catch(JSONException e) {
                Toast.makeText(WalmartSearch.this, "didn't work", Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
            finally {
                return null;
            }
        }

        protected void onPostExecute(JSONObject result) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            if(walmartResult != null && walmartResult.size() > 0) {
                ShoppingListItemAdapter shoppingListItemAdapter = new ShoppingListItemAdapter(getApplicationContext(), R.layout.shopping_list_item, walmartResult);
                walmartList.setAdapter(shoppingListItemAdapter);
            }
            else {
                Toast.makeText(WalmartSearch.this, "Resulsts not found! Please try again.", Toast.LENGTH_SHORT);
            }
        }
    }
    private class ShoppingListItemAdapter extends ArrayAdapter<ShoppingListItem> {
        private List<ShoppingListItem> items;
        private Context context;

        ShoppingListItemAdapter(Context context, int resourceId, List<ShoppingListItem> items) {
            super(context, resourceId, items);
            this.items = items;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(context, R.layout.walmart_list_item, null);
            } else {
                view = convertView;
            }

            String itemName = items.get(position).getName();
            double itemPrice = ((double)items.get(position).getMaxPriceCents().get() / 100);
            String itemPriceName = Double.toString(itemPrice);
            String itemPicture = items.get(position).getImageUrl().get();


            TextView textViewName = (TextView) view.findViewById(R.id.itemName);
            textViewName.setTextColor(Color.BLACK);
            textViewName.setText(itemName);

            TextView textViewPrice = (TextView) view.findViewById(R.id.itemPriceList);
            textViewPrice.setTextColor(Color.BLACK);
            textViewPrice.setText("$" + itemPriceName);

            ImageView imageView = (ImageView) view.findViewById(R.id.itemImage);
            Picasso.with(getApplicationContext()).load(itemPicture).into(imageView);

            return view;
        }
    }
}
