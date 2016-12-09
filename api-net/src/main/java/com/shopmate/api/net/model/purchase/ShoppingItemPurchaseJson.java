package com.shopmate.api.net.model.purchase;

import com.shopmate.api.model.purchase.ShoppingItemPurchase;

public class ShoppingItemPurchaseJson {
    private long id;
    private String name = "";
    private int price;
    private int quantity;
    private String purchaser = "";
    private String receiver = "";
    private boolean complete;

    public ShoppingItemPurchaseJson() {
    }

    public ShoppingItemPurchase toPurchase() {
        return new ShoppingItemPurchase(id, name, price, quantity, purchaser, receiver, complete);
    }
}
