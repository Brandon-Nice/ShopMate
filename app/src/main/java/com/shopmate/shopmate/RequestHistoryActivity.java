package com.shopmate.shopmate;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.shopmate.shopmate.dummy.DummyContent;

/**
 * Class to take care of anything reimbursement related. I.e.:
 * - requesting reimbursement for items that were bought,
 * - showing outstanding reimbursement requests,
 * - and showing the user’s overall request history.
 * Created by menane on 11/15/2016.
 **/

public class RequestHistoryActivity extends AppCompatActivity implements ItemsBoughtFragment.OnListFragmentInteractionListener, ItemsRequestedFragment.OnListFragmentInteractionListener, ItemsHistoryFragment.OnListFragmentInteractionListener{

    RadioButton RB0;
    RadioButton RB1;
    RadioButton RB2;
    ViewFlipper VF;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_hist);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //title = (TextView)findViewById(R.id.reqTitle);

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("My Requests");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("My History");
        host.addTab(spec);

        //Tab 3
//        spec = host.newTabSpec("Tab Three");
//        spec.setContent(R.id.tab3);
//        spec.setIndicator("Tab Three");
//        host.addTab(spec);


         /*
         * Find the views declared in main.xml.
         */
//        RB0 = (RadioButton) findViewById(R.id.radio0);
//        RB1 = (RadioButton) findViewById(R.id.radio1);
//        RB2 = (RadioButton) findViewById(R.id.radio2);
//        VF = (ViewFlipper) findViewById(R.id.ViewFlipper01);

        /*
         * Set a listener that will listen for clicks on the radio buttons and
         * perform suitable actions.
         */
//        RB0.setOnClickListener(radio_listener);
//        RB1.setOnClickListener(radio_listener);
//        RB2.setOnClickListener(radio_listener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
     * Define a OnClickListener that will change which view that is displayed by
     * the ViewFlipper
     */
//    private View.OnClickListener radio_listener = new View.OnClickListener() {
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.radio0:
//                    //VF.setDisplayedChild(0); used to display text at the bottom - we don't need this
//                    title.setText("My Items Bought");
//                    changeFragment(0);
//                    //bListener
//                    break;
//                case R.id.radio1:
//                    //VF.setDisplayedChild(1);
//                    title.setText("My Requests");
//                    changeFragment(1);
//                    break;
//                case R.id.radio2:
//                    //VF.setDisplayedChild(2);
//                    title.setText("My History");
//                    changeFragment(2);
//                    break;
//            }
//        }
//    };

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        //do nothing yet. TODO: Add interaction shiz here...
    }

    @Override
    public void changeFragment(int id) {
//        switch (id) {
//            case 0: //My items bought activity
//                System.out.println("Case 0");
//                //Remove all views before replacing the fragment
//                FrameLayout fl1 = (FrameLayout) findViewById(R.id.fragment_layout);
//                fl1.removeAllViews();
//
//                ItemsBoughtFragment fragment1 = new ItemsBoughtFragment();
//                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
//                ft1.replace(R.id.fragment_layout, fragment1);
//                ft1.commit();
//                break;
//            case 1: //My requests activity
//                System.out.println("Case 1");
//                //Remove all views before replacing the fragment
//                FrameLayout fl2 = (FrameLayout) findViewById(R.id.fragment_layout);
//                fl2.removeAllViews();
//
//                ItemsRequestedFragment fragment2 = new ItemsRequestedFragment();
//                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
//                ft2.replace(R.id.fragment_layout, fragment2);
//                ft2.commit();
//                break;
//            case 2: //My history activity
//                System.out.println("Case 3");
//                //Remove all views before replacing the fragment
//                FrameLayout fl3 = (FrameLayout) findViewById(R.id.fragment_layout);
//                fl3.removeAllViews();
//
//                ItemsHistoryFragment fragment3 = new ItemsHistoryFragment();
//                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
//                ft3.replace(R.id.fragment_layout, fragment3);
//                ft3.commit();
//                break;
//            //default:
//        }
    }
}
