package com.shopmate.shopmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateServiceProvider;
import com.shopmate.api.model.result.SendInviteResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.AdapterView.OnItemClickListener;
import static com.facebook.FacebookSdk.getApplicationContext;

public class SharingListsActivity extends AppCompatActivity {

    private static String TAG = "SharingListsActivity";

    private ArrayList<HashMap<String, String>> friends = new ArrayList(); //stores friends that are in your friend list
    private long listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_lists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add A Friend To A List");
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        listId = extras.getLong("listId");

        final ListView tempFriendsList = (ListView) findViewById(R.id.friends);   /* Sets names of friends to view */
        final FriendsAdapter friendAdapter = new FriendsAdapter(getApplicationContext(), friends);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        //Create API call to get the friends who have installed the app on your Facebook list
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends?fields=id,name,picture.type(large)",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject test = response.getJSONObject();
                        try {
                            JSONArray friendsList = test.getJSONArray("data");
                            for (int i = 0; i < friendsList.length(); i++) {             /* Loop to go through every friend that the user has and collect their classes */
                                JSONObject friendMap = (JSONObject)friendsList.get(i);
                                System.out.println("This is the map: " + friendMap);
                                String name = friendMap.getString("name");
                                String firstName = name.substring(0, name.indexOf(" "));
                                String lastName = name.substring(name.indexOf(" ") + 1);
                                String id = friendMap.getString("id");

                                final JSONObject picObj = friendMap.getJSONObject("picture");
                                final JSONObject dataObj = picObj.getJSONObject("data");
                                String picUrl = dataObj.getString("url");
                                HashMap<String, String> friend = new HashMap();
                                friend.put("firstName", firstName);
                                friend.put("lastName", lastName);
                                friend.put("id", id);
                                friend.put("picUrl", picUrl);
                                friends.add(friend);
                            }

                            //no sorting for now
                            //sortFriends(friends);

                            friendAdapter.notifyDataSetChanged();
                            tempFriendsList.setAdapter(friendAdapter);

                            //Added functionality to open a new activity for each friend clicked
                            tempFriendsList.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    HashMap<String, String> friend = (HashMap) parent.getItemAtPosition(position);
                                    String friendID = friend.get("id");
                                    String first = friend.get("firstName");
                                    String last = friend.get("lastName");
                                    inviteFriend(friendID, first, last);
                                }

                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException n){ //means that the user is offline
                            Toast.makeText(getApplicationContext(), "Lost connect.", Toast.LENGTH_LONG).show();
                            AlertDialog alertDialog = new AlertDialog.Builder(SharingListsActivity.this).create();
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Oops! Looks like you aren't connected to Wifi or a mobile network at the moment. Would you like to connect or exit?");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();

                            n.printStackTrace();
                        }
                    }

                }
        ).executeAsync();

    }

    private void inviteFriend(String userId, final String firstName, final String lastName) {
        String fbToken = AccessToken.getCurrentAccessToken().getToken();
        ShopMateService service = ShopMateServiceProvider.get();
        Log.d(TAG, "Sending invite for list " + listId + " to " + userId);
        Futures.addCallback(service.sendInviteAsync(fbToken, listId, userId), new FutureCallback<SendInviteResult>() {
            @Override
            public void onSuccess(SendInviteResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String toastMsg = "Invite sent to " + firstName + " " + lastName + "!";
                        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failed to send invite", t);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Failed to send invite!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

class FriendsAdapter extends ArrayAdapter<HashMap<String, String>> {
    private ArrayList<HashMap<String, String>> friends;
    private Context context;

    public FriendsAdapter(Context context, ArrayList<HashMap<String, String>> friends) {
        super(context, R.layout.friends_list_item, friends);
        this.friends = friends;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = View.inflate(context, R.layout.friends_list_item, null);
        } else {
            view = convertView;
        }

        String itemName = friends.get(position).get("firstName") + " " + friends.get(position).get("lastName");
        String itemPicture = friends.get(position).get("picUrl");


        TextView textViewName = (TextView) view.findViewById(R.id.friendsName);
        textViewName.setTextColor(Color.BLACK);
        textViewName.setText(itemName);


        ImageView imageView = (ImageView) view.findViewById(R.id.friendImage);
        Picasso.with(getApplicationContext()).load(itemPicture).into(imageView);

        return view;
    }
}
