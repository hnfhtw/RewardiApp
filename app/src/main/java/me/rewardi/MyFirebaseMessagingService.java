/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.rewardi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            JsonElement element = new JsonParser().parse(remoteMessage.getData().toString());
            JsonObject fcmMessageObject = element.getAsJsonObject();
            JsonObject entity = fcmMessageObject.get("entity").getAsJsonObject();
            int fcmMessageType = fcmMessageObject.get("type").getAsInt();

            String notificationMessage = "";
            String notificationMessageTitle = "";

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = null;

            switch(fcmMessageType){
                case 200:   {   // SOCKET_PROXIMATE_TIMEOUT_WARNING
                    String socketBoardName = entity.get("Name").getAsString();
                    int rewardiPerHour = entity.get("RewardiPerHour").getAsInt();
                    int maxTime = entity.get("MaxTime").getAsInt();
                    String usedSince = entity.get("UsedSince").getAsString();
                    int socketBoardId = entity.get("Id").getAsInt();

                    try {
                        date = format.parse(usedSince);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    notificationMessageTitle = "Socket Board Max Time Expiring";
                    notificationMessage = "Socket board \"" + socketBoardName + "\" max time expires in " + Integer.toString(maxTime/10) + " seconds. It is switched on since " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date) + ", do you want to extend max time?";
                    break;
                }
                case 2100:  {   // TODOHISTORY_GRANT_REQUEST
                    String userName = entity.get("FkToDo").getAsJsonObject().get("FkUser").getAsJsonObject().get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                    String todoListPointName = entity.get("FkToDo").getAsJsonObject().get("Name").getAsString();
                    int acquiredRewardi = entity.get("AcquiredRewardi").getAsInt();
                    String timestamp = entity.get("Timestamp").getAsString();
                    int todoHistoryId = entity.get("Id").getAsInt();

                    try {
                        date = format.parse(timestamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    notificationMessageTitle = "Todo List Point Finished";
                    notificationMessage = "User \"" + userName + "\" finished todo list point \"" + todoListPointName + "\" and earned " + Integer.toString(acquiredRewardi) + " Rewardi on " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date) + ". Please confirm in Rewardi app.";
                    break;
                }
                case 2000:  {   // ACTIVITYHISTORY_GRANT_REQUEST
                    String userName = entity.get("FkActivity").getAsJsonObject().get("FkUser").getAsJsonObject().get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                    String activityName = entity.get("FkActivity").getAsJsonObject().get("Name").getAsString();
                    double acquiredRewardi = entity.get("AcquiredRewardi").getAsDouble();
                    int duration = entity.get("Duration").getAsInt();
                    String timestamp = entity.get("Timestamp").getAsString();
                    int activityHistoryId = entity.get("Id").getAsInt();

                    try {
                        date = format.parse(timestamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    notificationMessageTitle = "Activity Performed";
                    notificationMessage = "User \"" + userName + "\" performed activity \"" + activityName + "\" for " + Integer.toString(duration) + " seconds and earned " + Double.toString(acquiredRewardi) + " Rewardi on " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date) + ". Please confirm in Rewardi app.";
                    break;
                }
                case 1000:  {   // SUPERVISOR_LINK_REQUEST
                    String userName = entity.get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                    int userId = entity.get("Id").getAsInt();

                    notificationMessageTitle = "New Supervisor Link Request";
                    notificationMessage = "User \"" + userName + "\" selected you as Rewardi supervisor. Please confirm this.";
                    break;
                }
                case 1001:  {   // SUPERVISOR_UNLINK_REQUEST
                    String userName = entity.get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                    int userId = entity.get("Id").getAsInt();

                    notificationMessageTitle = "Supervisor Unlink Request";
                    notificationMessage = "User \"" + userName + "\" removed you as Rewardi supervisor. Please confirm this.";
                    break;
                }
                case 2151:  {   // TODOHISTORY_GRANT_RESPONSE
                    String supervisorName = entity.get("FkToDo").getAsJsonObject().get("FkUser").getAsJsonObject().get("FkSupervisorUser").getAsJsonObject().get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                    String todoListPointName = entity.get("FkToDo").getAsJsonObject().get("Name").getAsString();
                    int acquiredRewardi = entity.get("AcquiredRewardi").getAsInt();
                    String timestamp = entity.get("Timestamp").getAsString();
                    boolean granted = entity.get("Granted").getAsBoolean();

                    try {
                        date = format.parse(timestamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    notificationMessageTitle = "Todo List Point Finished - Supervisor Response";
                    if(granted){
                        notificationMessage = "Congratulations, you earned " + Integer.toString(acquiredRewardi) + " Rewardi! Your supervisor \"" + supervisorName + "\" confirmed your finished todo list point \"" + todoListPointName + "\" on " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date) + ".";
                    }
                    else{
                        notificationMessage = "Sorry - no Rewardi this time. Your supervisor \"" + supervisorName + "\" denied your finished todo list point \"" + todoListPointName + "\" on " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date) + ".";
                    }
                    break;
                }
                case 2051:  {   // ACTIVITYHISTORY_GRANT_RESPONSE
                    String supervisorName = entity.get("FkActivity").getAsJsonObject().get("FkUser").getAsJsonObject().get("FkSupervisorUser").getAsJsonObject().get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                    String activityName = entity.get("FkActivity").getAsJsonObject().get("Name").getAsString();
                    double acquiredRewardi = entity.get("AcquiredRewardi").getAsDouble();
                    int duration = entity.get("Duration").getAsInt();
                    String timestamp = entity.get("Timestamp").getAsString();
                    boolean granted = entity.get("Granted").getAsBoolean();

                    try {
                        date = format.parse(timestamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    notificationMessageTitle = "Activity Performed - Supervisor Response";
                    if(granted){
                        notificationMessage = "Congratulations, you earned " + Double.toString(acquiredRewardi) + " Rewardi! Your supervisor \"" + supervisorName + "\" confirmed your performed activity \"" + activityName + "\" (" + Integer.toString(duration) + " seconds) on " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date) + ".";
                    }
                    else{
                        notificationMessage = "Sorry - no Rewardi this time. Your supervisor \"" + supervisorName + "\" denied your performed activity \"" + activityName + "\" (" + Integer.toString(duration) + " seconds) on " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date) + ".";
                    }
                    break;
                }
                case 1051:  {   // SUPERVISOR_LINK_RESPONSE
                    String userName = entity.get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                    int userId = entity.get("Id").getAsInt();
                    JsonObject status = fcmMessageObject.get("status").getAsJsonObject();
                    boolean granted = status.get("Granted").getAsBoolean();
                    String remark = status.get("Remark").getAsString();

                    notificationMessageTitle = "New Response to your Supervisor Link Request";
                    if(granted){
                        notificationMessage = "User \"" + userName + "\" confirmed to be your Rewardi supervisor.";

                    }
                    else{
                        notificationMessage = "Sorry, user \"" + userName + "\" denied to be your Rewardi supervisor.";
                    }
                    break;
                }
                case 1052:  {   // SUPERVISOR_UNLINK_RESPONSE
                    String userName = entity.get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                    int userId = entity.get("Id").getAsInt();
                    JsonObject status = fcmMessageObject.get("status").getAsJsonObject();
                    boolean granted = status.get("Granted").getAsBoolean();
                    String remark = status.get("Remark").getAsString();

                    notificationMessageTitle = "New Response to your Supervisor Unlink Request";
                    if(granted){
                        notificationMessage = "User \"" + userName + "\" confirmed to stop being your Rewardi supervisor.";

                    }
                    else{
                        notificationMessage = "Sorry, user \"" + userName + "\" refused to stop being your Rewardi supervisor.";
                    }
                    break;
                }
                default:    {
                    return;
                }
            }

            sendNotification(notificationMessage, notificationMessageTitle);
        }

        Intent localMessage = new Intent(CurrentActivityReceiver.CURRENT_ACTIVITY_ACTION);
        LocalBroadcastManager.getInstance(this).
                sendBroadcast(localMessage);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String messageTitle) {
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_rewardi_logo)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(messageTitle);
        bigTextStyle.bigText(messageBody);
        notificationBuilder.setStyle(bigTextStyle);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(new Random().nextInt() /* ID of notification */, notificationBuilder.build());
    }
}