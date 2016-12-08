package com.shopmate.api.net.model.request;

public class MakePurchaseRequest extends AuthenticatedRequest {
    private final long itemId;
    private final String receiverFbid;
    private final int price;
    private final int quantity;

    public MakePurchaseRequest(String fbToken, long itemId, String receiverFbid, int price, int quantity) {
        super(fbToken);
        this.itemId = itemId;
        this.receiverFbid = receiverFbid;
        this.price = price;
        this.quantity = quantity;
    }
}
