package com.shopmate.shopmate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateServiceProvider;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.GetAllShoppingListsResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static LoginButton loginButton;
    static CallbackManager callbackManager;
    static int i = 1;

    private UpdateListener updateListener;

    private class ShoppingListEntry {
        private final long id;
        private final ShoppingList list;

        public ShoppingListEntry(long id, ShoppingList list) {
            this.id = id;
            this.list = list;
        }

        public long getId() {
            return id;
        }

        public ShoppingList getList() {
            return list;
        }
    }

    private class ShoppingListAdapter extends ArrayAdapter<ShoppingListEntry> {
        private List<ShoppingListEntry> items;
        private Context context;
        private int layout;

        ShoppingListAdapter(Context context, int resourceId, List<ShoppingListEntry> items) {
            super(context, resourceId, items);
            this.items = items;
            this.context = context;
            this.layout = resourceId;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(context, layout, null);
            } else {
                view = convertView;
            }
            TextView title = (TextView) view.findViewById(R.id.label);
            title.setText(items.get(position).getList().getTitle());

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        final View navHeader = navView.getHeaderView(0);

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
                        TextView user_name = (TextView) navHeader.findViewById(R.id.usertextView);
                        ImageView user_picture = (ImageView) navHeader.findViewById(R.id.userimageView);

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
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //make a dummy list for the home screen
        final ListView listview = (ListView) findViewById(R.id.userlistView);
        //using arraylists to store data just for the sake of having data
        //TODO: Create a custom Adapter class to take in HashMaps instead to make this more efficient

        final ShoppingListAdapter a = new ShoppingListAdapter(this, R.layout.rowlayout, new ArrayList<ShoppingListEntry>());
        listview.setAdapter(a);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), ShoppingListActivity.class);
                Bundle extras = new Bundle();
                ShoppingListEntry entry = (ShoppingListEntry) parent.getItemAtPosition(position);
                extras.putString("title", entry.getList().getTitle());
                extras.putString("listId", Long.toString(entry.getId()));
                i.putExtras(extras);
                startActivity(i);
            }
        });
        
        final ShopMateService service = ShopMateServiceProvider.get();
        String fbToken = AccessToken.getCurrentAccessToken().getToken();
        Futures.addCallback(service.getAllListsAndItemsAsync(fbToken), new FutureCallback<GetAllShoppingListsResult>() {
            @Override
            public void onSuccess(GetAllShoppingListsResult result) {
                final List<ShoppingListEntry> tmp = new ArrayList<>();
                for (Map.Entry<Long, ShoppingList> i : result.getLists().entrySet()) {
                    tmp.add(new ShoppingListEntry(i.getKey(), i.getValue()));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        a.addAll(tmp);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(listview, "defeat", Snackbar.LENGTH_LONG).show();
            }
        });

        ((Button)findViewById(R.id.addNewListButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fbToken = AccessToken.getCurrentAccessToken().getToken();
                final ImmutableSet<String> invites = ImmutableSet.of();


                final EditText txtUrl = new EditText(MainActivity.this);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("New List Title")
                        .setView(txtUrl)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String listTitle = txtUrl.getText().toString();
                                if (listTitle.length() == 0) {
                                    return;
                                }
                                Futures.addCallback(service.createListAsync(fbToken, listTitle, invites), new FutureCallback<CreateShoppingListResult>() {
                                    @Override
                                    public void onSuccess(CreateShoppingListResult result) {
                                        final ShoppingListEntry tmp = new ShoppingListEntry(result.getId(), result.getList());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                a.insert(tmp, 0);
                                            }
                                        });
                                        Snackbar.make(listview, "success", Snackbar.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Snackbar.make(listview, "defeat", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                return;
                            }
                        })
                        .show();


            }
        });

        // Always register the FCM token with the server
        InstanceIdService.registerFcmToken();

        updateListener = new UpdateListener(this, new UpdateHandler() {
            @Override
            public void onListShared(final long listId) {
                for (int i = 0; i < a.getCount(); i++) {
                    ShoppingListEntry entry = a.getItem(i);
                    if (entry.getId() == listId) {
                        return; // List entry already exists
                    }
                }
                String fbToken = AccessToken.getCurrentAccessToken().getToken();
                Futures.addCallback(service.getListAndItemsAsync(fbToken, listId), new FutureCallback<ShoppingList>() {
                    @Override
                    public void onSuccess(ShoppingList result) {
                        final ShoppingListEntry entry = new ShoppingListEntry(listId, result);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                a.insert(entry, 0);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onListDeleted(long listId) {
                for (int i = 0; i < a.getCount(); i++) {
                    ShoppingListEntry entry = a.getItem(i);
                    if (entry.getId() == listId) {
                        a.remove(entry);
                        break;
                    }
                }
            }

            @Override
            public void onListMemberLeft(long listId, String userId) {
                if (userId == AccessToken.getCurrentAccessToken().getUserId()) {
                    onListDeleted(listId);
                }
            }
        });
        updateListener.register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateListener.unregister();
    }

    //    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
//        TextView textView = (TextView) rowView.findViewById(R.id.label);
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//        textView.setText(values[position]);
//        // change the icon for Windows and iPhone
//        String s = values[position];
//        if (s.startsWith("iPhone")) {
//            imageView.setImageResource(R.drawable.no);
//        } else {
//            imageView.setImageResource(R.drawable.ok);
//        }
//
//        return rowView;
//    }

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

        if (id == R.id.nav_logout) {
            // Handle the logout action
            Intent init = new Intent(MainActivity.this, LoginActivity.class);
            init.putExtra("FromNavMenu", true);
            startActivity(init);

        } else if (id == R.id.nav_shared) {

        } else if (id == R.id.nav_personal) {

        } else if (id == R.id.nav_list_invites) {
            startActivity(new Intent(MainActivity.this, InviteRequestsActivity.class));
        } else if (id == R.id.nav_list_req_history) {
            startActivity(new Intent(MainActivity.this, RequestHistoryActivity.class));
        } else if (id == R.id.nav_share) {
            String appLinkUrl, previewImageUrl;

            appLinkUrl = "https://fb.me/1210406722382112";
            previewImageUrl = "https://i.imgur.com/UWuC035.png";

            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(appLinkUrl)
                        .setPreviewImageUrl(previewImageUrl)
                        .build();
                AppInviteDialog.show(this, content);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
