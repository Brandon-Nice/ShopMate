package com.shopmate.shopmate;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.shopmate.api.ShopMateService;
import com.shopmate.api.ShopMateServiceProvider;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class InviteService extends IntentService {
    private static final String TAG = "IntentService";

    public static final String ACTION_ACCEPT = "com.shopmate.shopmate.action.ACCEPT";
    public static final String ACTION_DECLINE = "com.shopmate.shopmate.action.DECLINE";

    public static final String EXTRA_INVITE_ID = "com.shopmate.shopmate.extra.INVITE_ID";

    private Handler handler;

    public InviteService() {
        super("InviteService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionAccept(Context context, String inviteId) {
        Intent intent = new Intent(context, InviteService.class);
        intent.setAction(ACTION_ACCEPT);
        intent.putExtra(EXTRA_INVITE_ID, inviteId);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionDecline(Context context, String inviteId) {
        Intent intent = new Intent(context, InviteService.class);
        intent.setAction(ACTION_DECLINE);
        intent.putExtra(EXTRA_INVITE_ID, inviteId);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ACCEPT.equals(action)) {
                final long inviteId = intent.getLongExtra(EXTRA_INVITE_ID, 0);
                handleActionAccept(inviteId);
            } else if (ACTION_DECLINE.equals(action)) {
                final long inviteId = intent.getLongExtra(EXTRA_INVITE_ID, 0);
                handleActionDecline(inviteId);
            }
        }
    }

    /**
     * Handle action Accept in the provided background thread with the provided
     * parameters.
     */
    private void handleActionAccept(final long inviteId) {
        if (!checkLoggedIn()) {
            return;
        }
        String fbToken = AccessToken.getCurrentAccessToken().getToken();
        ShopMateService service = ShopMateServiceProvider.get();
        Futures.addCallback(service.acceptInviteAsync(fbToken, inviteId), new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showToast("Invite accepted!");
                dismissNotification(inviteId);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failed to accept invite!", t);
                showToast("Unable to accept the invite!");
            }
        });
    }

    /**
     * Handle action Decline in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDecline(final long inviteId) {
        if (!checkLoggedIn()) {
            return;
        }
        String fbToken = AccessToken.getCurrentAccessToken().getToken();
        ShopMateService service = ShopMateServiceProvider.get();
        Log.d(TAG, "fbToken=" + fbToken + ", inviteId=" + inviteId);
        Futures.addCallback(service.declineInviteAsync(fbToken, inviteId), new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showToast("Invite declined.");
                dismissNotification(inviteId);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failed to decline invite!", t);
                showToast("Unable to decline the invite!");
            }
        });
    }

    private boolean checkLoggedIn() {
        if (AccessToken.getCurrentAccessToken() != null && AccessToken.getCurrentAccessToken().getToken() != null) {
            return true;
        }
        showToast("Not logged in!");
        return false;
    }

    private void dismissNotification(long inviteId) {
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NotificationUpdateHandler.INVITE_NOTIFY_TAG, (int)inviteId);
    }

    private void showToast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InviteService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
