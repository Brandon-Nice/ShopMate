package com.shopmate.shopmate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Class to take care of anything reimbursement related. I.e.:
 * - requesting reimbursement for items that were bought,
 * - showing outstanding reimbursement requests,
 * - and showing the user’s overall request history.
 * Created by menane on 11/15/2016.
 **/

public class RequestHistoryActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_hist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
