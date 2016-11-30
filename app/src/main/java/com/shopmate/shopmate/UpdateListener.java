package com.shopmate.shopmate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Utility class for getting realtime updates.
 */
public class UpdateListener {
    private static final String TAG = "UpdateListener";

    private static final String KEY_ACTION = "action";
    private static final String KEY_LIST_TITLE = "listTitle";
    private static final String KEY_INVITE_ID = "inviteId";
    private static final String KEY_LIST_ID = "listId";
    private static final String KEY_ITEM_ID = "itemId";

    private static final String ACTION_INVITED = "invited";
    private static final String ACTION_LIST_SHARED = "listShared";
    private static final String ACTION_ITEM_ADDED = "itemAdded";
    private static final String ACTION_ITEM_UPDATED = "itemUpdated";

    private final Activity activity;
    private final UpdateHandler handler;

    public UpdateListener(Activity activity, UpdateHandler handler) {
        this.activity = activity;
        this.handler = handler;
    }

    public void register() {
        activity.registerReceiver(receiver, new IntentFilter(MessagingService.INTENT_FILTER));
    }

    public void unregister() {
        activity.unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                handleMessage(intent);
            } catch (Exception e) {
                Log.e(TAG, "Unable to handle message", e);
            }
        }
    };

    private void handleMessage(Intent intent) {
        String action = intent.getStringExtra(KEY_ACTION);
        Log.d(TAG, "Received action " + action);
        switch (action) {
            case ACTION_INVITED:
                handleInvited(intent);
                break;
            case ACTION_LIST_SHARED:
                handleListShared(intent);
                break;
            case ACTION_ITEM_ADDED:
                handleItemAdded(intent);
                break;
            case ACTION_ITEM_UPDATED:
                handleItemUpdated(intent);
                break;
            default:
                Log.w(TAG, "Unsupported action " + action);
                break;
        }
    }

    private void handleInvited(Intent intent) {
        String listTitle = intent.getStringExtra(KEY_LIST_TITLE);
        long inviteId = Long.parseLong(intent.getStringExtra(KEY_INVITE_ID));
        handler.onInvited(listTitle, inviteId);
    }

    private void handleListShared(Intent intent) {
        long listId = Long.parseLong(intent.getStringExtra(KEY_LIST_ID));
        handler.onListShared(listId);
    }

    private void handleItemAdded(Intent intent) {
        long listId = Long.parseLong(intent.getStringExtra(KEY_LIST_ID));
        long itemId = Long.parseLong(intent.getStringExtra(KEY_ITEM_ID));
        handler.onItemAdded(listId, itemId);
    }

    private void handleItemUpdated(Intent intent) {
        long listId = Long.parseLong(intent.getStringExtra(KEY_LIST_ID));
        long itemId = Long.parseLong(intent.getStringExtra(KEY_ITEM_ID));
        handler.onItemUpdated(listId, itemId);
    }
}
