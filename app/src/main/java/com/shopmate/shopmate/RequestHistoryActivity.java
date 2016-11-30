package com.shopmate.shopmate;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.ViewFlipper;

import com.shopmate.shopmate.dummy.DummyContent;

/**
 * Class to take care of anything reimbursement related. I.e.:
 * - requesting reimbursement for items that were bought,
 * - showing outstanding reimbursement requests,
 * - and showing the user’s overall request history.
 * Created by menane on 11/15/2016.
 **/

public class RequestHistoryActivity extends AppCompatActivity implements ItemsBoughtFragment.OnListFragmentInteractionListener{

    RadioButton RB0;
    RadioButton RB1;
    RadioButton RB2;
    ViewFlipper VF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_hist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         /*
         * Find the views declared in main.xml.
         */
        RB0 = (RadioButton) findViewById(R.id.radio0);
        RB1 = (RadioButton) findViewById(R.id.radio1);
        RB2 = (RadioButton) findViewById(R.id.radio2);
        VF = (ViewFlipper) findViewById(R.id.ViewFlipper01);

        /*
         * Set a listener that will listen for clicks on the radio buttons and
         * perform suitable actions.
         */
        RB0.setOnClickListener(radio_listener);
        RB1.setOnClickListener(radio_listener);
        RB2.setOnClickListener(radio_listener);

    }

    /*
     * Define a OnClickListener that will change which view that is displayed by
     * the ViewFlipper
     */
    private View.OnClickListener radio_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.radio0:
                    VF.setDisplayedChild(0);
                    break;
                case R.id.radio1:
                    VF.setDisplayedChild(1);
                    break;
                case R.id.radio2:
                    VF.setDisplayedChild(2);
                    break;
            }
        }
    };

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        //do nothing yet. TODO: Add interaction shiz here...
    }
}
