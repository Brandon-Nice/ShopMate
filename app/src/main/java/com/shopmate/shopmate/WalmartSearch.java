package com.shopmate.shopmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                if(walmartEditText.getText() != null) {
                    //TODO: Search the Walmart API database and populate the listview of results.
                }
            }
        });
    }
}
