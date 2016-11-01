package com.shopmate.api.net.model.response;

import com.google.common.collect.ImmutableMap;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.GetAllShoppingListsResult;
import com.shopmate.api.net.model.list.ShoppingListJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllListsResponse {
    private List<ShoppingListJson> lists = new ArrayList<>();

    public GetAllShoppingListsResult toResult() {
        Map<Long, ShoppingList> resultLists = new HashMap<>();
        for (ShoppingListJson list : lists) {
            resultLists.put(list.getId(), list.toShoppingList());
        }
        return new GetAllShoppingListsResult(ImmutableMap.copyOf(resultLists));
    }
}
