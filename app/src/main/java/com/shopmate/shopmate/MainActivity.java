package com.shopmate.shopmate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static LoginButton loginButton;
    static CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (isLoggedIn()) {
            loginButton = LoginActivity.getLoginButton();
            callbackManager = LoginActivity.getCallbackManager();

            //Gets the name of the user
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me?fields=id,name,picture.type(large)", null,
                    HttpMethod.GET, new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    //handle the response
                    final JSONObject jsonObject = response.getJSONObject();
                    String name = "";
                    try {
                        TextView user_name = (TextView) findViewById(R.id.usertextView);
                        ImageView user_picture = (ImageView) findViewById(R.id.userimageView);

                        name = jsonObject.getString("name");
                        String firstName = name.substring(0, name.indexOf(" "));
                        String lastName = name.substring(name.indexOf(" ") + 1);
                        user_name.setText(name);
                        final JSONObject picObj = jsonObject.getJSONObject("picture");
                        final JSONObject dataObj = picObj.getJSONObject("data");
                        String url = dataObj.getString("url");
                        System.out.println("URL: " + url);
                        Picasso.with(getApplicationContext()).load(url).into(user_picture);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).executeAsync();
            /*
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/picture?type=large", null,
                    HttpMethod.GET, new GraphRequest.Callback() {
                public void onCompleted(GraphResponse response) {
                    //handle the response
                    String URL = "";
                    try {
                        final JSONObject jsonObject = response.getJSONObject();
                        System.out.println("jsonArray" + jsonObject.toString());
                        String url = jsonObject.getString("url");
                        System.out.println("url" + url);


                        JSONObject data = jsonArray.getJSONObject("data");
                        System.out.println("data" + data);
                        ImageView user_picture = (ImageView) findViewById(R.id.userimageView);
                        URL = data.getString("url");
                        System.out.println("URL" + URL);
                        Picasso.with(getApplicationContext()).load(URL).into(user_picture);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).executeAsync();
            */
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
