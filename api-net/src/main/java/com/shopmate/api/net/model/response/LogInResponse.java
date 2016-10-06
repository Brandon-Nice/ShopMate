package com.shopmate.api.net.model.response;

import com.google.common.collect.ImmutableMap;
import com.shopmate.api.ShopMateSession;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.LogInResult;

import java.util.ArrayList;
import java.util.List;

public class LogInResponse {
    private String fbid = "";
    private String token = "";
    private List<ShoppingListResponse> lists = new ArrayList<>();

    public LogInResult toLogInResult() {
        ShopMateSession session = new ShopMateSession(fbid, token);
        ImmutableMap<Long, ShoppingList> listMap = new AllListsResponse(lists).toShoppingListMap();
        return new LogInResult(session, listMap);
    }

    public String getFbid() {
        return fbid;
    }

    public String getToken() {
        return token;
    }

    public List<ShoppingListResponse> getLists() {
        return lists;
    }
}
