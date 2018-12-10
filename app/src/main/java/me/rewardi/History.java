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

import static android.view.View.VISIBLE;

public class History extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem menuItemGadgetHistory;
    private MenuItem menuItemActivityHistory;
    private MenuItem menuItemTodoListHistory;
    private CustomListAdapterGadgetHistory listAdapterGadgets;
    private CustomListAdapterActivityHistory listAdapterActivities;
    private CustomListAdapterTodoListHistory listAdapterTodoList;
    FutureCallback<Response<String>> getFullGadgetHistoryCallback;
    FutureCallback<Response<String>> getFullActivityHistoryCallback;
    FutureCallback<Response<String>> getFullTodoListHistoryCallback;
    Globals appState;
    private ListView listViewGadgets;
    private ListView listViewActivities;
    private ListView listViewTodoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        List<HistoryItemBox> historyItemBox = new ArrayList<HistoryItemBox>();
        // ListAdapter is resonsible for conversion between java code and list items that can be used
        listAdapterGadgets = new CustomListAdapterGadgetHistory(this, historyItemBox);
        listViewGadgets = (ListView) findViewById(R.id.listViewGadgetHistory);
        listViewGadgets.setAdapter(listAdapterGadgets);

        List<HistoryItemManualActivity> activityHistoryList = new ArrayList<HistoryItemManualActivity>();
        // ListAdapter is resonsible for conversion between java code and list items that can be used
        listAdapterActivities = new CustomListAdapterActivityHistory(this, activityHistoryList);
        listViewActivities = (ListView) findViewById(R.id.listViewActivities);
        listViewActivities.setAdapter(listAdapterActivities);

        List<HistoryItemTodoListPoint> todoListHistoryList = new ArrayList<HistoryItemTodoListPoint>();
        // ListAdapter is resonsible for conversion between java code and list items that can be used
        listAdapterTodoList = new CustomListAdapterTodoListHistory(this, todoListHistoryList);
        listViewTodoList = (ListView) findViewById(R.id.listViewTodoList);
        listViewTodoList.setAdapter(listAdapterTodoList);

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
                        int usedRewardi = gadget.get("usedRewardi").getAsInt();
                        if(gadget.has("fkSocketId")) {        // SocketBoard
                            int fkSocketId = gadget.get("fkSocketId").getAsInt();
                            int duration = gadget.get("duration").getAsInt();
                            boolean timeout = gadget.get("timeout").getAsBoolean();

                            HistoryItemSocketBoard historyItemSocketBoard = new HistoryItemSocketBoard(id, fkSocketId, timestamp, duration, timeout, usedRewardi);
                            //listAdapterGadgets.addItem(historyItemSocketBoard);
                            //listAdapterGadgets.notifyDataSetChanged();
                        }
                        else if(gadget.has("fkBoxId")) {   // Box
                            int fkBoxId = gadget.get("fkBoxId").getAsInt();
                            HistoryItemBox historyItemBox = new HistoryItemBox(id, fkBoxId, timestamp, usedRewardi);
                            listAdapterGadgets.addItem(historyItemBox);
                            listAdapterGadgets.notifyDataSetChanged();
                        }
                    }
                }
                else{
                    Log.d("Gadgets", "Error = %s" + e.toString());
                }
            }
        };

        getFullActivityHistoryCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfActivities = array.size();
                    JsonObject activityHistory = null;
                    for (int i = 0; i < nrOfActivities; ++i) {
                        activityHistory = array.get(i).getAsJsonObject();
                        int id = activityHistory.get("id").getAsInt();
                        int fkActivityId = activityHistory.get("fkActivityId").getAsInt();
                        String timestamp = activityHistory.get("timestamp").getAsString();
                        int duration = activityHistory.get("duration").getAsInt();
                        int acquiredRewardi = activityHistory.get("acquiredRewardi").getAsInt();

                        HistoryItemManualActivity historyItemManualActivity = new HistoryItemManualActivity(id, fkActivityId, timestamp, duration, acquiredRewardi);
                        listAdapterActivities.addItem(historyItemManualActivity);
                        listAdapterActivities.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("ManAct", "Error = %s" + e.toString());
                }
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
                        int fkToDoId = dataObj.get("fkToDoId").getAsInt();
                        String timestamp = dataObj.get("timestamp").getAsString();

                        HistoryItemTodoListPoint historyItemTodoListPoint = new HistoryItemTodoListPoint(id, fkToDoId, timestamp);
                        listAdapterTodoList.addItem(historyItemTodoListPoint);
                        listAdapterTodoList.notifyDataSetChanged();
                    }

                }
                else{
                    Log.d("TodoList", "Error = %s" + e.toString());
                }
            }
        };

        appState = ((Globals)getApplicationContext());
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
        menuItemGadgetHistory = menu.findItem(R.id.show_gadget_history);
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
        );

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
        } else if (id == R.id.nav_fitbit) {
            Intent intent = new Intent(this, Fitbit.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_gadgets) {
            Intent intent = new Intent(this, Gadgets.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
