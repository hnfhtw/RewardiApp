/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : Messages.java
 * Purpose    : List the received and unhandled messages of the current user;
 ********************************************************************************************/

package me.rewardi;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

public class Messages extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UpdateUserdata {

    Globals appState;
    private CustomListAdapterMessages listAdapter;
    FutureCallback<Response<String>> getPendingTodoListHistoryItemsCallback;    // callback function that is called on server response to the request "get all Pending TodoList History Items (that require supervisor confirmation) of the current user (if he is supervisor)"
    FutureCallback<Response<String>> getPendingActivityHistoryItemsCallback;    // callback function that is called on server response to the request "get all Pending Activity History Items (that require supervisor confirmation) of the current user (if he is supervisor)"
    FutureCallback<Response<String>> getPendingLinkRequestsCallback;            // callback function that is called on server response to the request "get all Pending Supervisor Link Request Items (that require supervisor confirmation) of the current user (if he is supervisor)"
    FutureCallback<Response<String>> getPendingUnlinkRequestsCallback;          // callback function that is called on server response to the request "get all Pending Supervisor Unlink Request Items (that require supervisor confirmation) of the current user (if he is supervisor)"
    private TextView toolbarRewardi;
    private BroadcastReceiver currentActivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarRewardi = (TextView) toolbar.findViewById(R.id.textViewRewardiAccountBalanceHeader);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        List<Message> items = new ArrayList<Message>();

        // ListAdapter is resonsible for conversion between java code and list items that can be used
        listAdapter = new CustomListAdapterMessages(this, items, R.layout.custom_row_messages);
        ListView listview1 = (ListView) findViewById(R.id.listview1);
        listview1.setAdapter(listAdapter);

        getPendingTodoListHistoryItemsCallback = new FutureCallback<Response<String>>() {   // callback function that is called on server response to the request "get all Pending TodoList History Items (that require supervisor confirmation) of the current user (if he is supervisor)"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Messages", "getPendingTodoListHistoryItemsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfMessages = array.size();
                    Log.d("Messages", "getPendingTodoListHistoryItemsCallback Server Response Number of Pending Todo List Items = " + nrOfMessages);
                    JsonObject pendingTodoListHistoryItem = null;
                    for (int i = 0; i < nrOfMessages; ++i) {
                        pendingTodoListHistoryItem = array.get(i).getAsJsonObject();
                        Log.d("Messages", "getPendingTodoListHistoryItemsCallback Server Response Pending Todo List History Item " + i + " = " + pendingTodoListHistoryItem.toString());

                        int id = pendingTodoListHistoryItem.get("Id").getAsInt();
                        String userName = pendingTodoListHistoryItem.get("FkToDo").getAsJsonObject().get("FkUser").getAsJsonObject().get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                        String todoListPointName = pendingTodoListHistoryItem.get("FkToDo").getAsJsonObject().get("Name").getAsString();
                        int acquiredRewardi = pendingTodoListHistoryItem.get("AcquiredRewardi").getAsInt();
                        String timestamp = pendingTodoListHistoryItem.get("Timestamp").getAsString();

                        String messageTitle = "Todo List Point Finished";
                        String messageText = "User \"" + userName + "\" finished todo list point \"" + todoListPointName + "\" and earned " + Integer.toString(acquiredRewardi) + " Rewardi on " + appState.parseServerTimeStampToLocalTimeFormat(timestamp) + ".";

                        Message msg = new Message(Message.messageTypes.TODO_HISTORY_GRANT_REQUEST, messageTitle, messageText, pendingTodoListHistoryItem, id);
                        listAdapter.addItem(msg);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("Messages", "getPendingTodoListHistoryItemsCallback Server Response Error = " + e.toString());
                }
            }
        };

        getPendingActivityHistoryItemsCallback = new FutureCallback<Response<String>>() {   // callback function that is called on server response to the request "get all Pending Activity History Items (that require supervisor confirmation) of the current user (if he is supervisor)"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Messages", "getPendingActivityHistoryItemsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfMessages = array.size();
                    Log.d("Messages", "getPendingActivityHistoryItemsCallback Server Response Number of Pending Activity Items = " + nrOfMessages);
                    JsonObject pendingActivityHistoryItem = null;
                    for (int i = 0; i < nrOfMessages; ++i) {
                        pendingActivityHistoryItem = array.get(i).getAsJsonObject();
                        Log.d("Messages", "getPendingActivityHistoryItemsCallback Server Response Pending Todo List History Item " + i + " = " + pendingActivityHistoryItem.toString());

                        int id = pendingActivityHistoryItem.get("Id").getAsInt();
                        String userName = pendingActivityHistoryItem.get("FkActivity").getAsJsonObject().get("FkUser").getAsJsonObject().get("FkAspNetUsers").getAsJsonObject().get("UserName").getAsString();
                        String activityName = pendingActivityHistoryItem.get("FkActivity").getAsJsonObject().get("Name").getAsString();
                        double acquiredRewardi = pendingActivityHistoryItem.get("AcquiredRewardi").getAsDouble();
                        int duration = pendingActivityHistoryItem.get("Duration").getAsInt();
                        String timestamp = pendingActivityHistoryItem.get("Timestamp").getAsString();

                        String messageTitle = "Activity Performed";
                        String messageText = "User \"" + userName + "\" performed activity \"" + activityName + "\" for " + Integer.toString(duration) + " seconds and earned " + Double.toString(acquiredRewardi) + " Rewardi on " + appState.parseServerTimeStampToLocalTimeFormat(timestamp) + ".";

                        Message msg = new Message(Message.messageTypes.ACTIVITY_HISTORY_GRANT_REQUEST, messageTitle, messageText, pendingActivityHistoryItem, id);
                        listAdapter.addItem(msg);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("Messages", "getPendingActivityHistoryItemsCallback Server Response Error = " + e.toString());
                }
            }
        };

        getPendingLinkRequestsCallback = new FutureCallback<Response<String>>() {   // callback function that is called on server response to the request "get all Pending Supervisor Link Request Items (that require supervisor confirmation) of the current user (if he is supervisor)"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Messages", "getPendingLinkRequestsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfMessages = array.size();
                    Log.d("Messages", "getPendingLinkRequestsCallback Server Response Number of Pending Link Requests = " + nrOfMessages);
                    JsonObject pendingLinkRequest = null;
                    for (int i = 0; i < nrOfMessages; ++i) {
                        pendingLinkRequest = array.get(i).getAsJsonObject();
                        Log.d("Messages", "getPendingLinkRequestsCallback Server Response Pending Link Request " + i + " = " + pendingLinkRequest.toString());

                        int id = pendingLinkRequest.get("id").getAsInt();
                        String userName = pendingLinkRequest.get("fkAspNetUsers").getAsJsonObject().get("userName").getAsString();
                        String mailAddress = pendingLinkRequest.get("fkAspNetUsers").getAsJsonObject().get("email").getAsString();

                        String messageTitle = "New Supervisor Link Request";
                        String messageText = "User \"" + userName + "\" (mail address \"" + mailAddress + "\" requested you as Rewardi supervisor.";

                        Message msg = new Message(Message.messageTypes.SUPERVISOR_LINK_REQUEST, messageTitle, messageText, pendingLinkRequest, id);
                        listAdapter.addItem(msg);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("Messages", "getPendingLinkRequestsCallback Server Response Error = " + e.toString());
                }
            }
        };

        getPendingUnlinkRequestsCallback = new FutureCallback<Response<String>>() { // callback function that is called on server response to the request "get all Pending Supervisor Unlink Request Items (that require supervisor confirmation) of the current user (if he is supervisor)"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Messages", "getPendingUnlinkRequestsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfMessages = array.size();
                    Log.d("Messages", "getPendingUnlinkRequestsCallback Server Response Number of Pending Unlink Requests = " + nrOfMessages);
                    JsonObject pendingUnlinkRequest = null;
                    for (int i = 0; i < nrOfMessages; ++i) {
                        pendingUnlinkRequest = array.get(i).getAsJsonObject();
                        Log.d("Messages", "getPendingUnlinkRequestsCallback Server Response Pending Unlink Request " + i + " = " + pendingUnlinkRequest.toString());

                        int id = pendingUnlinkRequest.get("id").getAsInt();
                        String userName = pendingUnlinkRequest.get("fkAspNetUsers").getAsJsonObject().get("userName").getAsString();
                        String mailAddress = pendingUnlinkRequest.get("fkAspNetUsers").getAsJsonObject().get("email").getAsString();

                        String messageTitle = "New Supervisor Unlink Request";
                        String messageText = "User \"" + userName + "\" (mail address \"" + mailAddress + "\" removed you as Rewardi supervisor.";

                        Message msg = new Message(Message.messageTypes.SUPERVISOR_UNLINK_REQUEST, messageTitle, messageText, pendingUnlinkRequest, id);
                        listAdapter.addItem(msg);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("Messages", "getPendingUnlinkRequestsCallback Server Response Error = " + e.toString());
                }
            }
        };
        appState = ((Globals)getApplicationContext());
        appState.setUserDataListener(this); // ensure that this activity is informed when new user data is received from the server
        appState.requestUserDataUpdate();   // request new user data from the server
        appState.sendMessageToServer(Globals.messageID.SUPERVISOR_TODO_HISTORY_PENDING_GET_ALL, 0,null, getPendingTodoListHistoryItemsCallback);        // send request: "get all Pending TodoList History Items (that require supervisor confirmation) of the current user (if he is supervisor)"
        appState.sendMessageToServer(Globals.messageID.SUPERVISOR_ACTIVITY_HISTORY_PENDING_GET_ALL, 0,null, getPendingActivityHistoryItemsCallback);    // send request: "get all Pending Activity History Items (that require supervisor confirmation) of the current user (if he is supervisor)"
        appState.sendMessageToServer(Globals.messageID.SUPERVISOR_LINK_REQUEST_PENDING_GET_ALL, 0,null, getPendingLinkRequestsCallback);                // send request: "get all Pending Supervisor Link Request Items (that require supervisor confirmation) of the current user (if he is supervisor)"
        appState.sendMessageToServer(Globals.messageID.SUPERVISOR_UNLINK_REQUEST_PENDING_GET_ALL, 0,null, getPendingUnlinkRequestsCallback);            // send request: "get all Pending Supervisor Unlink Request Items (that require supervisor confirmation) of the current user (if he is supervisor)"
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentActivityReceiver = new CurrentActivityReceiver(this);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(currentActivityReceiver, CurrentActivityReceiver.CURRENT_ACTIVITY_RECEIVER_FILTER);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(currentActivityReceiver);
        currentActivityReceiver = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messages, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        } else if (id == R.id.nav_todolist) {
            Intent intent = new Intent(this, TodoList.class);
            startActivity(intent);
        } else if (id == R.id.nav_activity) {
            Intent intent = new Intent(this, Activities.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(this, Messages.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_gadgets) {
            Intent intent = new Intent(this, Gadgets.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("logout", true);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onUserDataUpdate(User user) {
        if(toolbarRewardi != null){
            toolbarRewardi.setText(Double.toString(user.getTotalRewardi()));
        }
    }
}
