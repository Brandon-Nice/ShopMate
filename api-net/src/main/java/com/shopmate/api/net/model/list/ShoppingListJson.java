package com.shopmate.api.net.model.list;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.shopmate.api.model.item.ShoppingListItemHandle;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.net.model.item.ShoppingListItemHandleJson;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListJson {
    private long id;
    private String title = "";
    private String creator = "";
    private List<ShoppingListItemHandleJson> items = new ArrayList<>();

    public ShoppingListJson() {
    }

    public ShoppingList toShoppingList() {
        return new ShoppingList(
                creator,
                title,
                ImmutableSet.<String>of(creator),
                buildItemHandles());
    }

    public long getId() {
        return id;
    }

    private ImmutableList<ShoppingListItemHandle> buildItemHandles() {
        List<ShoppingListItemHandle> handles = new ArrayList<>();
        for (ShoppingListItemHandleJson item : items) {
            handles.add(item.toHandle());
        }
        return ImmutableList.copyOf(handles);
    }
}
