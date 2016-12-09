package com.shopmate.api.net.model.response;

import com.google.common.collect.ImmutableList;
import com.shopmate.api.model.purchase.ShoppingItemPurchase;
import com.shopmate.api.net.model.purchase.ShoppingItemPurchaseJson;

import java.util.ArrayList;

public class GetAllPurchasesResponse {
    private ArrayList<ShoppingItemPurchaseJson> purchases = new ArrayList<>();

    public ImmutableList<ShoppingItemPurchase> toPurchaseList() {
        ArrayList<ShoppingItemPurchase> result = new ArrayList<>();
        for (ShoppingItemPurchaseJson json : purchases) {
            result.add(json.toPurchase());
        }
        return ImmutableList.copyOf(result);
    }
}
