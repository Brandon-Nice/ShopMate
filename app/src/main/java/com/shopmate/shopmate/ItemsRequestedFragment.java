package com.shopmate.shopmate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.google.common.collect.ImmutableList;
import com.shopmate.api.model.purchase.ShoppingItemPurchase;
import com.shopmate.shopmate.dummy.DummyContent;
import com.shopmate.shopmate.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.shopmate.shopmate.R.id.view;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemsRequestedFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private GoogleApiClient payClient;

    final int REQUEST_CODE_MASKED_WALLET = 5;
    final int REQUEST_CODE_FULL_WALLET = 7;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemsRequestedFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemsRequestedFragment newInstance(int columnCount) {
        ItemsRequestedFragment fragment = new ItemsRequestedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itemsrequested_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            List<ShoppingItemPurchase> items = new ArrayList<ShoppingItemPurchase>();
            recyclerView.setAdapter(new ItemsRequestedRecyclerViewAdapter(items, mListener));

            payClient = new GoogleApiClient.Builder(getContext())
                    .addApi(Wallet.API,
                            new Wallet.WalletOptions.Builder()
                                    .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                    .build())
                    .build();
            Toast.makeText(getContext(), "starting", Toast.LENGTH_SHORT).show();
            payClient.connect();
            Wallet.Payments.isReadyToPay(payClient, IsReadyToPayRequest.newBuilder().build())
                    .setResultCallback(new ResultCallback<BooleanResult>() {
                        @Override
                        public void onResult(@NonNull BooleanResult booleanResult) {
                            if (booleanResult.getStatus().isSuccess()) {
                                if (booleanResult.getValue()) {
                                    Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();

                                    PaymentMethodTokenizationParameters parameters = PaymentMethodTokenizationParameters.newBuilder()
                                            .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.NETWORK_TOKEN)
                                            .addParameter("publicKey", "BO39Rh43UGXMQy5PAWWe7UGWd2a9YRjNLPEEVe+zWIbdIgALcDcnYCuHbmrrzl7h8FZjl6RCzoi5/cDrqXNRVSo=")
                                            .build();
                                    MaskedWalletRequest request = MaskedWalletRequest.newBuilder()
                                            .setCurrencyCode("USD")
                                            .setEstimatedTotalPrice("15.00")
                                            .setPaymentMethodTokenizationParameters(parameters)
                                            .build();
                                    WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
                                            .setBuyButtonText(WalletFragmentStyle.BuyButtonText.BUY_WITH)
                                            .setBuyButtonAppearance(WalletFragmentStyle.BuyButtonAppearance.ANDROID_PAY_DARK)
                                            .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT);

                                    WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
                                            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                            .setFragmentStyle(walletFragmentStyle)
                                            .setTheme(WalletConstants.THEME_LIGHT)
                                            .setMode(WalletFragmentMode.BUY_BUTTON)
                                            .build();

                                    SupportWalletFragment mWalletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);

                                    WalletFragmentInitParams.Builder startParamsBuilder =
                                            WalletFragmentInitParams.newBuilder()
                                                    .setMaskedWalletRequest(request)
                                                    .setMaskedWalletRequestCode(REQUEST_CODE_MASKED_WALLET);
                                    mWalletFragment.initialize(startParamsBuilder.build());

                                    // add Wallet fragment to the UI
                                    getFragmentManager().beginTransaction()
                                            .replace(R.id.acceptreq, mWalletFragment)
                                            .commit();

                                    // show pay button
                                } else {
                                    Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();
                                    // show not pay button
                                }
                            } else {
                                Toast.makeText(getContext(), "wtf", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_MASKED_WALLET:
                if (resultCode == RESULT_OK) {
                    MaskedWallet maskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                    Log.d("things", maskedWallet.getEmail());
                    String googleTransactionId = maskedWallet.getGoogleTransactionId();

                    TextView txt = new TextView(getContext());
                    txt.setText("You are approving a transaction of $15.00");
                    final FullWalletRequest request = FullWalletRequest.newBuilder()
                            .setGoogleTransactionId(googleTransactionId)
                            .setCart(Cart.newBuilder()
                                    .setCurrencyCode("USD")
                                    .setTotalPrice("15.00")
                                    .addLineItem(LineItem.newBuilder()
                                            .setDescription("a thingy")
                                            .setQuantity("1")
                                            .setUnitPrice("15.00")
                                            .setTotalPrice("15.00")
                                            .setCurrencyCode("USD")
                                            .build()
                                    )
                                    .build())
                            .build();
                    new AlertDialog.Builder(getContext())
                            .setTitle("Are you sure?")
                            .setView(txt)
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Wallet.Payments.loadFullWallet(payClient, request, REQUEST_CODE_FULL_WALLET);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                }
                break;
            case WalletConstants.RESULT_ERROR:
                Log.d("things", Integer.toString(data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1)));
                break;
            case REQUEST_CODE_FULL_WALLET:
                if (resultCode == RESULT_OK) {
                    Log.d("things", "it worked");
                } else {
                    Log.d("things", "it didn't work");
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ShoppingItemPurchase item);
        void changeFragment(int id); //changes the fragment based on id
    }
}