package com.shopmate.api.net.model.response;

import com.google.common.collect.ImmutableMap;
import com.shopmate.api.model.list.ShoppingList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllListsResponse {
    private List<ShoppingListResponse> lists = new ArrayList<>();

    public AllListsResponse() {
    }

    public AllListsResponse(List<ShoppingListResponse> lists) {
        this.lists = lists;
    }

    public ImmutableMap<Long, ShoppingList> toShoppingListMap() {
        Map<Long, ShoppingList> resultLists = new HashMap<>();
        for (ShoppingListResponse list : lists) {
            resultLists.put(list.getId(), list.toShoppingList());
        }
        return ImmutableMap.copyOf(resultLists);
    }
}
