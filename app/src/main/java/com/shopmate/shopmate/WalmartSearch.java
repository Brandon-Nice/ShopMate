package com.shopmate.shopmate;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

public class WalmartSearch extends AppCompatActivity {

    public static EditText walmartEditText;
    public String url;
    private ListView walmartList;
    private ArrayList<ShoppingListItem> walmartResult;
    private ShoppingListItem Item;
    static ShoppingListItemAdapter sla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walmart_search);


        ((Button)findViewById(R.id.walmartSearchButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walmartEditText = (EditText) findViewById(R.id.walmartEditText);
                String searchTerms = walmartEditText.getText().toString();
                if (searchTerms != null || !searchTerms.equals(" ")) {
                    new JSONParse().execute();
                }
                else {
                    //TODO: Toast message saying to enter a field in the textbox
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

        String jsonString = new String();

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
            String searchTerms = walmartEditText.getText().toString();
                //TODO: Search the Walmart API database and populate the listview of results.
                //use gson to parse walmart api returns
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
                System.out.println("URL:" + url);

        }
        @Override
        protected JSONObject doInBackground(String... args) {

            try {
                JSONObject json = getJSONObjectFromURL(url);
                    JSONArray jsonArray = json.getJSONArray("items");
                    System.out.println(jsonArray.toString());

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

                        //Item tempItem = new Item(name, price, descr, image);
                        //walmartResult.add(tempItem);

                        System.out.println(name + ": " + price);

                        Item = new ShoppingListItemBuilder(name)
                                .description(descr)
                                .imageUrl(image)
                                .maxPriceCents(priceCents)
                                .build();

                        walmartResult.add(Item);
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
            //TODO: Place arraylist in listView
            ShoppingListItemAdapter shoppingListItemAdapter = new ShoppingListItemAdapter(getApplicationContext(), R.layout.shopping_list_item, walmartResult);
            walmartList.setAdapter(shoppingListItemAdapter);
        }
    }
    private class ShoppingListItemAdapter extends ArrayAdapter<ShoppingListItem> {
        // TODO create an item class that contains things like price, quantity, brand, etc.
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
            int itemPrice = items.get(position).getMaxPriceCents().get();
            String itemPicture = items.get(position).getImageUrl().get();


            TextView textViewName = (TextView) view.findViewById(R.id.itemName);
            textViewName.setTextColor(Color.BLACK);
            textViewName.setText(itemName);

            TextView textViewPrice = (TextView) view.findViewById(R.id.itemPrice);
            //textViewPrice.setText(itemPrice);

            ImageView imageView = (ImageView) view.findViewById(R.id.itemImage);
            //TODO: use Picasso to add image

            return view;
        }
    }
}
