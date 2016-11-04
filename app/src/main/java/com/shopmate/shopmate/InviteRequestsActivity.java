package com.shopmate.shopmate;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.common.collect.ImmutableSet;
import com.shopmate.api.model.list.ShoppingListInvite;
import com.shopmate.api.model.result.CreateShoppingListItemResult;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.GetAllInvitesResult;
import com.shopmate.api.net.NetShopMateService;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class InviteRequestsActivity extends AppCompatActivity {

    NetShopMateService service = new NetShopMateService();
    List<ShoppingListInvite> invites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_requests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        try {
            ListView listView = (ListView) findViewById(R.id.inviteList);
            invites = service.getAllInvites(AccessToken.getCurrentAccessToken().getToken()).get().getIncomingInvites();
            RequestListItemAdapter inviteListAdapter = new RequestListItemAdapter(this, R.layout.invite_list_item, invites);
            listView.setAdapter(inviteListAdapter);

            if (invites.isEmpty()) {
                ((TextView) findViewById(R.id.pendingInvitesText)).setText("No pending invites");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class RequestListItemAdapter extends ArrayAdapter<ShoppingListInvite> {

        Context context;

        RequestListItemAdapter(Context context, int resourceId, List<ShoppingListInvite> invites) {
            super(context, resourceId, invites);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(context, R.layout.invite_list_item, null);
            } else {
                view = convertView;
            }

            String listTitle = invites.get(position).getListTitle();

            TextView textView = (TextView) view.findViewById(R.id.listTitle);
            textView.setText(listTitle);

            ((Button) view.findViewById(R.id.accept)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    service.acceptInviteAsync(AccessToken.getCurrentAccessToken().getToken(), invites.get(position).getId());

                    finish();
                    startActivity(getIntent());
                }
            });

            ((Button) view.findViewById(R.id.decline)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    service.declineInviteAsync(AccessToken.getCurrentAccessToken().getToken(), invites.get(position).getId());

                    finish();
                    startActivity(getIntent());
                }
            });

            return view;
        }
    }
}
