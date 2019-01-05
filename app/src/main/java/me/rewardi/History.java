package me.rewardi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
        implements NavigationView.OnNavigationItemSelectedListener {

    private CustomListAdapterGadgetHistory listAdapterGadgets;
    private CustomListAdapterEarnedRewardiHistory listAdapterEarnedRewardiHistory;
    FutureCallback<Response<String>> getFullGadgetHistoryCallback;
    FutureCallback<Response<String>> getFullActivityHistoryCallback;
    FutureCallback<Response<String>> getFullTodoListHistoryCallback;
    Globals appState;
    private ListView listViewGadgets;
    private ListView listViewEarnedRewardi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView toolbarRewardi = (TextView) toolbar.findViewById(R.id.textViewRewardiAccountBalanceHeader);
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

        buttonEarnedRewardi.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listViewGadgets.setVisibility(View.INVISIBLE);
                        listViewEarnedRewardi.setVisibility(View.VISIBLE);
                        buttonEarnedRewardi.setBackgroundColor(Color.GREEN);
                        buttonSpentRewardi.setBackgroundColor(Color.GRAY);
                    }
                });

        buttonSpentRewardi.setOnClickListener(
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

        getFullGadgetHistoryCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfGadgets = array.size();
                    JsonObject gadget = null;
                    for(int i = 0; i<nrOfGadgets; ++i){
                        gadget = array.get(i).getAsJsonObject();
                        int id = gadget.get("id").getAsInt();
                        String timestamp = gadget.get("timestamp").getAsString();
                        if(gadget.has("fkSocket")) {        // SocketBoard
                            JsonObject socketObj = gadget.get("fkSocket").getAsJsonObject();
                            int sockId = socketObj.get("id").getAsInt();
                            String trustNum = socketObj.get("trustNo").getAsString();
                            String name = socketObj.get("name").getAsString();
                            int rewardiPerHour = socketObj.get("rewardiPerHour").getAsInt();
                            int maxTime = socketObj.get("maxTime").getAsInt();

                            SocketBoard socket = new SocketBoard(sockId, trustNum, name, rewardiPerHour, maxTime, false);
                            int duration = gadget.get("duration").getAsInt();
                            boolean timeout = gadget.get("timeout").getAsBoolean();
                            double usedRewardi = gadget.get("usedRewardi").getAsDouble();
                            HistoryItemSocketBoard historyItemSocketBoard = new HistoryItemSocketBoard(id, socket, timestamp, duration, timeout, usedRewardi);
                            listAdapterGadgets.addItem(historyItemSocketBoard);
                            listAdapterGadgets.notifyDataSetChanged();
                        }
                        else if(gadget.has("fkBox")) {   // Box
                            JsonObject boxObj = gadget.get("fkBox").getAsJsonObject();
                            int boxId = boxObj.get("id").getAsInt();
                            String trustNum = boxObj.get("trustNo").getAsString();
                            String name = boxObj.get("name").getAsString();
                            int rewardiPerOpen = boxObj.get("rewardiPerOpen").getAsInt();
                            boolean isLocked = boxObj.get("isLocked").getAsBoolean();

                            Box box = new Box(boxId, trustNum, name, rewardiPerOpen, isLocked);
                            int usedRewardi = gadget.get("usedRewardi").getAsInt();
                            HistoryItemBox historyItemBox = new HistoryItemBox(id, box, timestamp, usedRewardi);
                            listAdapterGadgets.addItem(historyItemBox);
                            listAdapterGadgets.notifyDataSetChanged();
                        }
                    }
                }
                else{
                    Log.d("Gadgets", "Error = %s" + e.toString());
                }
                // Sort list according to timestamp
                listAdapterGadgets.sortItems();
                listAdapterGadgets.notifyDataSetChanged();
            }
        };

        getFullActivityHistoryCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfActivities = array.size();
                    JsonObject dataObj = null;
                    for (int i = 0; i < nrOfActivities; ++i) {
                        dataObj = array.get(i).getAsJsonObject();
                        int id = dataObj.get("id").getAsInt();
                        JsonObject activityObj = dataObj.get("fkActivity").getAsJsonObject();
                        int activityId = activityObj.get("id").getAsInt();
                        String name = activityObj.get("name").getAsString();
                        int rewardiPerHour = activityObj.get("rewardiPerHour").getAsInt();
                        ManualActivity act = new ManualActivity(activityId, name, rewardiPerHour, false, null);
                        String timestamp = dataObj.get("timestamp").getAsString();
                        int duration = dataObj.get("duration").getAsInt();
                        double acquiredRewardi = dataObj.get("acquiredRewardi").getAsDouble();

                        boolean supervised = false;
                        if(!dataObj.get("fkSupervisorId").isJsonNull()){    // user has a supervisor
                            supervised = true;
                        }

                        boolean granted = true;
                        String supervisorMessage = "";
                        String supervisorName = "";
                        if(supervised){
                            if(!dataObj.get("granted").isJsonNull()){
                                granted = dataObj.get("granted").getAsBoolean();
                            }
                            if(!dataObj.get("remark").isJsonNull()){
                                supervisorMessage = dataObj.get("remark").getAsString();
                            }
                            supervisorName = dataObj.get("fkSupervisor").getAsJsonObject().get("fkAspNetUsers").getAsJsonObject().get("userName").getAsString();
                        }

                        HistoryItemManualActivity historyItemManualActivity = new HistoryItemManualActivity(id, act, timestamp, duration, acquiredRewardi, granted, supervisorMessage, supervisorName);
                        listAdapterEarnedRewardiHistory.addItem(historyItemManualActivity);
                        listAdapterEarnedRewardiHistory.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("ManAct", "Error = %s" + e.toString());
                }
                // Sort list according to timestamp
                listAdapterEarnedRewardiHistory.sortItems();
                listAdapterEarnedRewardiHistory.notifyDataSetChanged();
            }
        };

        getFullTodoListHistoryCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfTodoListPoints = array.size();
                    JsonObject dataObj = null;
                    for (int i = 0; i < nrOfTodoListPoints; ++i) {
                        dataObj = array.get(i).getAsJsonObject();
                        int id = dataObj.get("id").getAsInt();
                        JsonObject todoListPointObj = dataObj.get("fkToDo").getAsJsonObject();
                        String name = todoListPointObj.get("name").getAsString();
                        int todoListPointId = todoListPointObj.get("id").getAsInt();
                        int rewardi = todoListPointObj.get("rewardi").getAsInt();
                        TodoListPoint todoListPoint = new TodoListPoint(todoListPointId, name, rewardi, true);
                        String timestamp = dataObj.get("timestamp").getAsString();
                        int acquiredRewardi = dataObj.get("acquiredRewardi").getAsInt();

                        boolean supervised = false;
                        if(!dataObj.get("fkSupervisorId").isJsonNull()){    // user has a supervisor
                            supervised = true;
                        }

                        boolean granted = true;
                        String supervisorMessage = "";
                        String supervisorName = "";
                        if(supervised){
                            if(!dataObj.get("granted").isJsonNull()){
                                granted = dataObj.get("granted").getAsBoolean();
                            }else{
                                granted = false;
                            }
                            if(!dataObj.get("remark").isJsonNull()){
                                supervisorMessage = dataObj.get("remark").getAsString();
                            }
                            supervisorName = dataObj.get("fkSupervisor").getAsJsonObject().get("fkAspNetUsers").getAsJsonObject().get("userName").getAsString();
                        }

                        HistoryItemTodoListPoint historyItemTodoListPoint = new HistoryItemTodoListPoint(id, todoListPoint, timestamp, acquiredRewardi, granted, supervisorMessage, supervisorName);
                        listAdapterEarnedRewardiHistory.addItem(historyItemTodoListPoint);
                        listAdapterEarnedRewardiHistory.notifyDataSetChanged();
                    }

                }
                else{
                    Log.d("TodoList", "Error = %s" + e.toString());
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

        appState.sendMessageToServer(Globals.messageID.BOX_HISTORY_GET_ALL, 0,null, getFullGadgetHistoryCallback);
        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_HISSTORY_GET_ALL, 0,null, getFullGadgetHistoryCallback);
        appState.sendMessageToServer(Globals.messageID.ACTIVITY_HISTORY_GET_ALL, 0,null, getFullActivityHistoryCallback);
        appState.sendMessageToServer(Globals.messageID.TODO_HISTORY_GET_ALL, 0,null, getFullTodoListHistoryCallback);
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
        /*menuItemGadgetHistory = menu.findItem(R.id.show_gadget_history);
        menuItemGadgetHistory.setVisible(true);
        menuItemGadgetHistory.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        listViewGadgets.setVisibility(View.VISIBLE);
                        listViewActivities.setVisibility(View.INVISIBLE);
                        listViewTodoList.setVisibility(View.INVISIBLE);
                        return true;
                    }
                }
        );

        menuItemActivityHistory = menu.findItem(R.id.show_activity_history);
        menuItemActivityHistory.setVisible(true);
        menuItemActivityHistory.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        listViewGadgets.setVisibility(View.INVISIBLE);
                        listViewActivities.setVisibility(View.VISIBLE);
                        listViewTodoList.setVisibility(View.INVISIBLE);
                        return true;
                    }
                }
        );

        menuItemTodoListHistory = menu.findItem(R.id.show_todolist_history);
        menuItemTodoListHistory.setVisible(true);
        menuItemTodoListHistory.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        listViewGadgets.setVisibility(View.INVISIBLE);
                        listViewActivities.setVisibility(View.INVISIBLE);
                        listViewTodoList.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
        );*/

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
}
