package com.shopmate.shopmate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
            invites = service.getAllInvites(AccessToken.getCurrentAccessToken().getToken()).get().getIncomingInvites();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
