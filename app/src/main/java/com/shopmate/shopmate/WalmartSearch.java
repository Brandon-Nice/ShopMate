package com.shopmate.shopmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WalmartSearch extends AppCompatActivity {

    public static EditText walmartEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walmart_search);


        ((Button)findViewById(R.id.walmartSearchButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walmartEditText = (EditText) findViewById(R.id.walmartEditText);
                String searchTerms = walmartEditText.getText().toString();
                if(searchTerms != null || !searchTerms.equals(" ")) {
                    //TODO: Search the Walmart API database and populate the listview of results.
                    //use gson to parse walmart api returns
                    //TODO: Extra features: category, sorting (increasing/decreasing price, relevant, bestsellers)

                    //Prepare the URL
                    String url = "http://api.walmartlabs.com/v1/search?query=";
                    String sort = "";
                    String order = "";

                    //Add the search terms to the query url
                    searchTerms = searchTerms.replace(" ","");
                    url += searchTerms;

                    //Add the format and apiKey
                    url += "&format=json";
                    url += "&apiKey=hk42mtbf4g9y59gvb4z4swuu";

                    /*
                    if sort changed from relevance to price, title, bestseller, customerrating, new, add it to the search query
                    if order is changed from asc to desc, add it to the search query (only needed for price, title, customerrating)
                     */
                    System.out.println("URL:" + url);
                    //Use URLConnection to connect with walmart
                    try {
                        JSONObject jsonObject = getJSONObjectFromURL(url);
                        //TODO: parse json object
                    }
                    catch (JSONException e) {
                        //Toast message saying bad URL, try again
                        System.out.println("Bad URL");
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        //Toast message saying unknown error, try again
                        System.out.println("IO Exception");
                        e.printStackTrace();
                    }
                }
                else {
                    //TODO: Send a toast message saying to please enter a search value
                }
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
}
