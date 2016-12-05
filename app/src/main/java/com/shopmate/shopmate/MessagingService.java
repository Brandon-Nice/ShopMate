package com.shopmate.shopmate;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";

    public static final String INTENT_FILTER = "com.shopmate.shopmate.MESSAGE_RECEIVED";

    private final UpdateListener listener = new UpdateListener(this, new NotificationUpdateHandler(this));

    @Override
    public void onCreate() {
        super.onCreate();
        listener.register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener.unregister();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Received message: " + remoteMessage.getData());
        Intent intent = new Intent(INTENT_FILTER);
        for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        sendBroadcast(intent);
    }
}
