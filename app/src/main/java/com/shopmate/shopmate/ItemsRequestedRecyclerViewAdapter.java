
package com.shopmate.shopmate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateServiceProvider;
import com.shopmate.api.model.purchase.ShoppingItemPurchase;
import com.shopmate.api.model.result.CreateShoppingListItemResult;
import com.shopmate.shopmate.ItemsRequestedFragment.OnListFragmentInteractionListener;
import com.shopmate.shopmate.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ItemsRequestedRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRequestedRecyclerViewAdapter.ViewHolder> {

    private List<ShoppingItemPurchase> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final ListenableFuture<ImmutableList<ShoppingItemPurchase>> future; //contains list of item purchases
    private int index;

    public ItemsRequestedRecyclerViewAdapter(List<ShoppingItemPurchase> items, OnListFragmentInteractionListener listener) {
        mValues = items; //contains the dummy values
        mListener = listener; //contains the listener that interacts with those values

        final String fbToken = AccessToken.getCurrentAccessToken().getToken();
        ShopMateService service = ShopMateServiceProvider.get();
        future = service.getAllPurchasesAsync(fbToken);
        //make API call to get the actual data from server
        Futures.addCallback(future, new FutureCallback<ImmutableList<ShoppingItemPurchase>>() {
            @Override
            public void onSuccess(ImmutableList<ShoppingItemPurchase> result) {
                //ImmutableList<ImmutableList<ShoppingItemPurchase>> items = new <ImmutableList<ShoppingItemPurchase>>();
                //items = result;
                //filter out these results from the result obj
                List<ShoppingItemPurchase> filtereditems = new ArrayList<ShoppingItemPurchase>();
                for(ShoppingItemPurchase r : result){
                    if(!r.isComplete() && (r.getReceiverId().equals(AccessToken.getCurrentAccessToken().getUserId()))){
                        filtereditems.add(r);
                    }
                }
                mValues = filtereditems;

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        index = 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_itemsrequested, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //index++;
        //holder.fromView.setText("Items Bought: " + mValues.get(position).id);
        //holder.mContentView.setText(mValues.get(position).content);

        holder.fromView.setText("From: " + holder.mItem.getPurchaserId());
        holder.itemNameView.setText("Item Name: " + holder.mItem.getItemName());
        holder.itemPriceView.setText("Item Price: " + holder.mItem.getTotalPriceCents());
        holder.itemQtyView.setText("Item Quantity: " + holder.mItem.getQuantity());
        holder.reimb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fbToken = AccessToken.getCurrentAccessToken().getToken();
                ShopMateService service = ShopMateServiceProvider.get();
                Futures.addCallback(service.completePurchaseAsync(fbToken, holder.mItem.getId()), new FutureCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView fromView;
        public final TextView itemNameView;
        public final TextView itemPriceView;
        public final TextView itemQtyView;
        public final Button reimb;
        public ShoppingItemPurchase mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            fromView = (TextView) view.findViewById(R.id.from);
            itemNameView = (TextView) view.findViewById(R.id.item_name);
            itemPriceView = (TextView) view.findViewById(R.id.item_price);
            itemQtyView = (TextView) view.findViewById(R.id.item_quantity);
            reimb = (Button) view.findViewById(R.id.acceptreq);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + itemNameView.getText() + "'";
        }
    }
}