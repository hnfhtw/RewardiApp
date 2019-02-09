/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : CurrentActivityReceiver.java
 * Purpose    : Activities inform this class that they are active (onResume), then in the service
 *              responsible for receiving FCM messages (MyFirebaseMessagingService) it can be
 *              checked which activity is active - this activity is then reloaded in order to get
 *              the received message content / new information from server (e.g. new Rewardi balance)
 ********************************************************************************************/

package me.rewardi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class CurrentActivityReceiver extends BroadcastReceiver {
    private static final String TAG = CurrentActivityReceiver.class.getSimpleName();
    public static final String CURRENT_ACTIVITY_ACTION = "current.activity.action";
    public static final IntentFilter CURRENT_ACTIVITY_RECEIVER_FILTER = new IntentFilter(CURRENT_ACTIVITY_ACTION);

    private Activity receivingActivity;

    public CurrentActivityReceiver(Activity activity) {
        this.receivingActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive: finishing:" + receivingActivity.getClass().getSimpleName());
        receivingActivity.recreate();
    }
}
