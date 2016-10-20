package com.shopmate.shopmate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddItemActivity extends AppCompatActivity {

    public static EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add Item");

        ((ImageButton)findViewById(R.id.addWalmart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent init = new Intent(AddItemActivity.this, WalmartSearch.class);
                init.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(init);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() != 0) {
                    Intent res = new Intent();
                    res.putExtra("item", name.getText().toString());
                    setResult(RESULT_OK, res);
                }
                finish();
            }
        });
        name = (EditText)findViewById(R.id.itemName);
    }

}
