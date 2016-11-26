package com.shopmate.shopmate;

import android.util.Log;

import com.facebook.AccessToken;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateServiceProvider;

public class InstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "InstanceIdService";

    public InstanceIdService() {
    }

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "FCM token refreshed, attempting registration");
        registerFcmToken();
    }

    public static void registerFcmToken() {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken == null) {
            Log.d(TAG, "FCM token is not available yet and will be sent on next refresh");
            return;
        }
        Log.d(TAG, "Registering FCM token: " + fcmToken);
        String fbToken = AccessToken.getCurrentAccessToken().getToken();
        ShopMateService service = ShopMateServiceProvider.get();
        Futures.addCallback(service.registerFcmTokenAsync(fbToken, fcmToken), new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "FCM token registration successful");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "FCM token registration failed", t);
            }
        });
    }
}
