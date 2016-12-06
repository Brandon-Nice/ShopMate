package com.shopmate.shopmate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationUpdateHandler extends UpdateHandler {

    private static final String TAG = "NotificationUpdateHndlr";

    private static final String NAME_GRAPH_URL = "/%s?fields=name";
    private static final String PHOTO_URL = "https://graph.facebook.com/%s/picture?type=large";

    private static final String DEFAULT_NAME = "Unknown User";

    public static final String INVITE_NOTIFY_TAG = "com.shopmate.shopmate.notify.INVITE";

    private static int nextRequestCode;
    private final Context context;

    public NotificationUpdateHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onInvited(final long inviteId, final String listTitle, final String senderId) {
        if (AccessToken.getCurrentAccessToken() == null) {
            return;
        }

        // Get the user's name
        String namePath = String.format(NAME_GRAPH_URL, senderId);
        GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), namePath, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                final String name = extractName(response);

                // Download the user's profile picture
                String photoUrl = String.format(PHOTO_URL, senderId);
                Picasso.with(context).load(photoUrl).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        showInviteNotification(inviteId, listTitle, name, bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        showInviteNotification(inviteId, listTitle, name, null);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            }
        }).executeAsync();
    }

    private void showInviteNotification(long inviteId, String listTitle, String senderName, Bitmap profilePicture) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_add_shopping_cart_white_24px)
                .setContentTitle(senderName)
                .setContentText("Invited you to shopping list " + listTitle)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setAutoCancel(true);

        if (profilePicture != null) {
            builder.setLargeIcon(profilePicture);
        }

        Intent acceptIntent = new Intent(context, InviteService.class);
        acceptIntent.setAction(InviteService.ACTION_ACCEPT);
        acceptIntent.putExtra(InviteService.EXTRA_INVITE_ID, inviteId);
        PendingIntent piAccept = PendingIntent.getService(context, nextRequestCode++, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_check_black_24dp, "Accept", piAccept);

        Intent declineIntent = new Intent(context, InviteService.class);
        declineIntent.setAction(InviteService.ACTION_DECLINE);
        declineIntent.putExtra(InviteService.EXTRA_INVITE_ID, inviteId);
        PendingIntent piDecline = PendingIntent.getService(context, nextRequestCode++, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_clear_black_24dp, "Decline", piDecline);

        Intent resultIntent = new Intent(context, InviteRequestsActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(InviteRequestsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(INVITE_NOTIFY_TAG, (int)inviteId, builder.build());
    }

    private static String extractName(GraphResponse response) {
        JSONObject json = response.getJSONObject();
        if (json != null) {
            try {
                return json.getString("name");
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse graph response", e);
            }
        } else if (response.getError() != null) {
            Log.e(TAG, "Graph request failed", response.getError().getException());
        }
        return DEFAULT_NAME;
    }
}
