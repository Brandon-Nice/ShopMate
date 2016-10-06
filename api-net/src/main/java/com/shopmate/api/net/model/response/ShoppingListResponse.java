package com.shopmate.api.net.model.response;

import com.google.common.collect.ImmutableSet;
import com.shopmate.api.model.list.ShoppingList;

public class ShoppingListResponse {
    private long id;
    private String title = "";
    private String creator = "";

    public ShoppingListResponse() {
    }

    public ShoppingListResponse(long id, String title, String creator) {
        this.id = id;
        this.title = title;
        this.creator = creator;
    }

    public ShoppingList toShoppingList() {
        return new ShoppingList(creator, title, ImmutableSet.<String>of(creator), ImmutableSet.<Long>of());
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }
}
