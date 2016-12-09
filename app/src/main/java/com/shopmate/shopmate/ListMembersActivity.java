package com.shopmate.shopmate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.GraphRequestBatch;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.shopmate.api.ShopMateServiceProvider;
import com.shopmate.api.model.list.ShoppingList;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ListMembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_members);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // TODO send listId through the intent
        Bundle extras = getIntent().getExtras();
        long listId = extras.getLong("listId");

        final Context context = this;

        final MemberListAdapter memberListAdapter = new MemberListAdapter(context, R.layout.member_list_item, new ArrayList<String>());
        final ListView memberList = (ListView) findViewById(R.id.memberList);
        memberList.setAdapter(memberListAdapter);

        // Find the names ids of all members joined in this group
        Futures.addCallback(ShopMateServiceProvider.get().getListAndItemsAsync(AccessToken.getCurrentAccessToken().getToken(), listId), new FutureCallback<ShoppingList>() {
            @Override
            public void onSuccess(final ShoppingList result) {
                final ArrayList<String> names = new ArrayList<String>();

                ArrayList<GraphRequest> requests = new ArrayList<GraphRequest>();

                for (String id : result.getMemberIds()) {
                    requests.add(GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), id + "?fields=name", new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse graphResponse) {
                            try {
                                names.add((String) graphResponse.getJSONObject().get("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }));
                }


                GraphRequestBatch batch = new GraphRequestBatch(requests);
                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                memberListAdapter.addAll(names);
                            }
                        });
                    }
                });


                batch.executeAsync();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private class MemberListAdapter extends ArrayAdapter<String> {
        private List<String> memberNames;
        private Context context;
        private int layout;

        MemberListAdapter(Context context, int resourceId, List<String> memberIds) {
            super(context, resourceId, memberIds);
            this.memberNames = memberIds;
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

            String id = memberNames.get(position);

            System.out.println("DRAWING");

            TextView textView = (TextView) view.findViewById(R.id.textView);
            // TODO convert member id into member name
            textView.setText(id);

            return view;
        }
    }
}
