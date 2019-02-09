/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : History.java
 * Purpose    : List the Rewardi (spent/earned) history of the current user;
 ********************************************************************************************/

package me.rewardi;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.View;
import android.widget.Button;
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

public class History extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UpdateUserdata {

    Globals appState;
    private CustomListAdapterGadgetHistory listAdapterGadgets;
    private CustomListAdapterEarnedRewardiHistory listAdapterEarnedRewardiHistory;
    FutureCallback<Response<String>> getFullGadgetHistoryCallback;      // callback function that is called on server response to the request "get full Rewardi Gadget History of the current user"
    FutureCallback<Response<String>> getFullActivityHistoryCallback;    // callback function that is called on server response to the request "get full Rewardi Activity History of the current user"
    FutureCallback<Response<String>> getFullTodoListHistoryCallback;    // callback function that is called on server response to the request "get full Rewardi TodoList History of the current user"
    private ListView listViewGadgets;
    private ListView listViewEarnedRewardi;
    private TextView toolbarRewardi;
    private BroadcastReceiver currentActivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
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

        final Button buttonEarnedRewardi = (Button) findViewById(R.id.buttonEarnedRewardi);
        final Button buttonSpentRewardi = (Button) findViewById(R.id.buttonSpentRewardi);

        appState = ((Globals)getApplicationContext());
        toolbarRewardi.setText(Double.toString(appState.getUser().getTotalRewardi()));

        buttonEarnedRewardi.setOnClickListener( // switch to Earned Rewardi History view -> hide listViewGadgets, show listViewEarnedRewardi
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listViewGadgets.setVisibility(View.INVISIBLE);
                        listViewEarnedRewardi.setVisibility(View.VISIBLE);
                        buttonEarnedRewardi.setBackgroundColor(Color.GREEN);
                        buttonSpentRewardi.setBackgroundColor(Color.GRAY);
                    }
                });

        buttonSpentRewardi.setOnClickListener(  // switch to Spent Rewardi History view -> show listViewGadgets, hide listViewEarnedRewardi
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listViewGadgets.setVisibility(View.VISIBLE);
                        listViewEarnedRewardi.setVisibility(View.INVISIBLE);
                        buttonEarnedRewardi.setBackgroundColor(Color.GRAY);
                        buttonSpentRewardi.setBackgroundColor(Color.GREEN);
                    }
                });

        List<HistoryItemGadget> gadgetHistoryList = new ArrayList<HistoryItemGadget>();
        // ListAdapter is responsible for conversion between java code and list items that can be used
        listAdapterGadgets = new CustomListAdapterGadgetHistory(this, gadgetHistoryList);
        listViewGadgets = (ListView) findViewById(R.id.listViewGadgetHistory);
        listViewGadgets.setAdapter(listAdapterGadgets);

        List<HistoryItemEarnedRewardi> earnedRewardiHistoryList = new ArrayList<HistoryItemEarnedRewardi>();
        // ListAdapter is responsible for conversion between java code and list items that can be used
        listAdapterEarnedRewardiHistory = new CustomListAdapterEarnedRewardiHistory(this, earnedRewardiHistoryList);
        listViewEarnedRewardi = (ListView) findViewById(R.id.listViewEarnedRewardiHistory);
        listViewEarnedRewardi.setAdapter(listAdapterEarnedRewardiHistory);

        getFullGadgetHistoryCallback = new FutureCallback<Response<String>>() { // callback function that is called on server response to the request "get full Rewardi Gadget History of the current user"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfGadgets = array.size();
                    JsonObject obj = null;
                    for(int i = 0; i<nrOfGadgets; ++i){
                        obj = array.get(i).getAsJsonObject();
                        HistoryItemSocketBoard historyItemSocketBoard = HistoryItemSocketBoard.parseObject(obj);
                        if(historyItemSocketBoard != null) {
                            listAdapterGadgets.addItem(historyItemSocketBoard);
                        }
                        else {
                            HistoryItemBox historyItemBox = HistoryItemBox.parseObject(obj);
                            if(historyItemBox != null) {
                                listAdapterGadgets.addItem(historyItemBox);
                            }
                        }
                        listAdapterGadgets.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("History", "getFullGadgetHistoryCallback Server Response Error = " + e.toString());
                }
                // Sort list according to timestamp
                listAdapterGadgets.sortItems();
                listAdapterGadgets.notifyDataSetChanged();
            }
        };

        getFullActivityHistoryCallback = new FutureCallback<Response<String>>() {   // callback function that is called on server response to the request "get full Rewardi Activity History of the current user"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("History", "getFullActivityHistoryCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfActivities = array.size();
                    Log.d("History", "getFullActivityHistoryCallback Server Response Number of Activities = " + nrOfActivities);
                    JsonObject obj = null;
                    for (int i = 0; i < nrOfActivities; ++i) {
                        obj = array.get(i).getAsJsonObject();
                        Log.d("History", "getFullActivityHistoryCallback Server Response Activity" + i + " = " + obj.toString());
                        HistoryItemManualActivity historyItemManualActivity = HistoryItemManualActivity.parseObject(obj);
                        listAdapterEarnedRewardiHistory.addItem(historyItemManualActivity);
                        listAdapterEarnedRewardiHistory.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("History", "getFullActivityHistoryCallback Server Response Error = " + e.toString());
                }
                // Sort list according to timestamp
                listAdapterEarnedRewardiHistory.sortItems();
                listAdapterEarnedRewardiHistory.notifyDataSetChanged();
            }
        };

        getFullTodoListHistoryCallback = new FutureCallback<Response<String>>() {   // callback function that is called on server response to the request "get full Rewardi TodoList History of the current user"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfTodoListPoints = array.size();
                    JsonObject obj = null;
                    for (int i = 0; i < nrOfTodoListPoints; ++i) {
                        obj = array.get(i).getAsJsonObject();
                        HistoryItemTodoListPoint historyItemTodoListPoint = HistoryItemTodoListPoint.parseObject(obj);
                        listAdapterEarnedRewardiHistory.addItem(historyItemTodoListPoint);
                        listAdapterEarnedRewardiHistory.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("History", "getFullTodoListHistoryCallback Server Response Error = " + e.toString());
                }
                // Sort list according to timestamp
                listAdapterEarnedRewardiHistory.sortItems();
                listAdapterEarnedRewardiHistory.notifyDataSetChanged();
            }
        };

        // Default Ansicht -> Earned Rewardi History wird angezeigt
        listViewGadgets.setVisibility(View.INVISIBLE);
        listViewEarnedRewardi.setVisibility(View.VISIBLE);
        buttonEarnedRewardi.setBackgroundColor(Color.GREEN);
        buttonSpentRewardi.setBackgroundColor(Color.GRAY);

        appState.setUserDataListener(this); // ensure that this activity is informed when new user data is received from the server
        appState.requestUserDataUpdate();   // request new user data from the server
        appState.sendMessageToServer(Globals.messageID.BOX_HISTORY_GET_ALL, 0,null, getFullGadgetHistoryCallback);          // send request to server: "get all Rewardi Box History events of the current user"
        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_HISSTORY_GET_ALL, 0,null, getFullGadgetHistoryCallback); // send request to server: "get all Rewardi SocketBoard History events of the current user"
        appState.sendMessageToServer(Globals.messageID.ACTIVITY_HISTORY_GET_ALL, 0,null, getFullActivityHistoryCallback);   // send request to server: "get all Rewardi Activity History events of the current user"
        appState.sendMessageToServer(Globals.messageID.TODO_HISTORY_GET_ALL, 0,null, getFullTodoListHistoryCallback);       // send request to server: "get all Rewardi TodoList History events of the current user"
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
        getMenuInflater().inflate(R.menu.history, menu);
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
