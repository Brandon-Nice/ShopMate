package com.shopmate.api.model.purchase;

public class ShoppingItemPurchase {
    private final long id;
    private final String itemName;
    private final int totalPriceCents;
    private final int quantity;
    private final String purchaserId;
    private final String receiverId;
    private final boolean complete;

    public ShoppingItemPurchase(
            long id,
            String itemName,
            int totalPriceCents,
            int quantity,
            String purchaserId,
            String receiverId,
            boolean complete)
    {
        this.id = id;
        this.itemName = itemName;
        this.totalPriceCents = totalPriceCents;
        this.quantity = quantity;
        this.purchaserId = purchaserId;
        this.receiverId = receiverId;
        this.complete = complete;
    }

    /**
     * @return The ID of the purchase.
     */
    public long getId() {
        return id;
    }

    /**
     * @return The name of the item that was purchased.
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @return The total requested payment amount in cents.
     */
    public int getTotalPriceCents() {
        return totalPriceCents;
    }

    /**
     * @return The number of items purchased.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @return The FBID of the person who purchased the item.
     */
    public String getPurchaserId() {
        return purchaserId;
    }

    /**
     * @return The FBID of the person who received the payment. For self-purchases, this can be the same as the sender.
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * @return True if the purchase is complete and payment was sent, false if payment is still needed. For self-purchases, this is always true.
     */
    public boolean isComplete() {
        return complete;
    }
}
