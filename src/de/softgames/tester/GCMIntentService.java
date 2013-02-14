package de.softgames.tester;


import static de.softgames.tester.CommonUtilities.SENDER_ID;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;


public class GCMIntentService extends GCMBaseIntentService {

    private static int _message_id = 0;
    private static final String STAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    public void onRegistered(Context context, String regId) {
        Log.d(STAG, "onRegistered: " + regId);
        ServerUtilities.register(context, regId);
    }

    public void onUnregistered(Context context, String regId) {
        Log.d(STAG, "onUnregistered: " + regId);
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, regId);
        } else {
            /**
             * This callback results from the call to unregister made on
             * ServerUtilities when the registration to the server failed.
             */
            Log.i(STAG, "Ignoring unregister callback");
        }
    }

    public void onMessage(Context context, Intent intent) {
        Log.d(STAG, "onMessage: New message");
        generateNotification(context, intent);
    }

    public void onError(Context context, String errorId) {
        Log.d(STAG, "onError: " + errorId);
    }

    public boolean onRecoverableError(Context context, String errorId) {
        Log.d(STAG, "onRecoverableError: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generateNotification(Context context, Intent intent) {

        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");

        Log.v(STAG, "Received message: " + title);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher)
                .setContentText(message).setContentTitle(title);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        /**
         * The stack builder object will contain an artificial back stack for
         * the started Activity. This ensures that navigating backward from the
         * Activity leads out of your application to the Home screen.
         */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // _message_id allows you to update the notification later on.
        notificationManager.notify(_message_id++, mBuilder.build());
    }

}
