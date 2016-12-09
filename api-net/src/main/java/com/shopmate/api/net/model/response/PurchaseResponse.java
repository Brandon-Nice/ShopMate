package com.shopmate.api.net.model.response;

import com.shopmate.api.model.purchase.ShoppingItemPurchase;
import com.shopmate.api.net.model.purchase.ShoppingItemPurchaseJson;

public class PurchaseResponse {
    private ShoppingItemPurchaseJson purchase = new ShoppingItemPurchaseJson();

    public ShoppingItemPurchase toPurchase() {
        return purchase.toPurchase();
    }
}
