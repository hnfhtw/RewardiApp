package me.rewardi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

public class Activities extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem menuItemDelete;
    private CustomListAdapterActivities listAdapter;
    private FloatingActionButton floatingActionButtonAdd;
    FutureCallback<Response<String>> getAllActivitiesCallback;
    FutureCallback<Response<String>> createActivityCallback;
    FutureCallback<Response<String>> deleteActivityCallback;
    Globals appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        floatingActionButtonAdd = findViewById(R.id.floatingActionButtonAdd);

        floatingActionButtonAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), ManualActivityAdd.class);
                        startActivityForResult(intent, 101);
                    }
                });

        List<ManualActivity> items = new ArrayList<ManualActivity>();

        // ListAdapter is resonsible for conversion between java code and list items that can be used
        listAdapter = new CustomListAdapterActivities(this, items);
        ListView listview1 = (ListView) findViewById(R.id.listview1);
        listview1.setAdapter(listAdapter);

        // set up an onitemclicklistener that something should be done if an item is clicked
        listview1.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ManualActivity item = (ManualActivity) parent.getItemAtPosition(position);
                        Toast.makeText(Activities.this, item.getName(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        listview1.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                        listAdapter.handleLongPress(position,view);
                        if(listAdapter.getListActivitiesSelected().size() > 0){
                            showDeleteMenu(true);
                        }else{
                            showDeleteMenu(false);
                        }
                        return true;
                    }
                }
        );

        getAllActivitiesCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                Log.d("ManAct", "Result Header = " + result.getHeaders().toString());
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("ManAct", "Element = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfActivities = array.size();
                    Log.d("ManAct", "Number of Activities = " + nrOfActivities);
                    JsonObject activity = null;
                    for (int i = 0; i < nrOfActivities; ++i) {
                        activity = array.get(i).getAsJsonObject();
                        Log.d("ManAct", "Activities" + i + " = " + activity.toString());
                        int id = activity.get("id").getAsInt();
                        String activityName = activity.get("name").getAsString();
                        int rewardiPerHour = activity.get("rewardiPerHour").getAsInt();

                        boolean isActive = false;
                        try {
                            String activeSince = activity.get("activeSince").getAsString();
                            if (activeSince != null) {
                                isActive = true;
                            }
                        } catch (Exception ex) { }

                        ManualActivity manualActivity = new ManualActivity(id, activityName, rewardiPerHour, isActive);
                        listAdapter.addItem(manualActivity);
                        listAdapter.notifyDataSetChanged();
                    }

                }
                else{
                    Log.d("ManAct", "Error = %s" + e.toString());
                }
            }
        };

        createActivityCallback = new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                Log.d("ManAct", "createActivityCallback called!");
                Log.d("ManAct", "Server Response = " + res.toString());
                if(e == null){
                    JsonElement element = new JsonParser().parse(res.getResult());
                    Log.d("ManAct", "Element = " + element.toString());
                    JsonObject activityObj = element.getAsJsonObject();

                    int id = activityObj.get("id").getAsInt();
                    String activityName = activityObj.get("name").getAsString();
                    int rewardiPerHour = activityObj.get("rewardiPerHour").getAsInt();
                    boolean isActive = false;
                    try {
                        String activeSince = activityObj.get("activeSince").getAsString();
                        if(activeSince != null){
                            isActive = true;
                        }
                    }catch(Exception ex){ }

                    ManualActivity act = new ManualActivity(id, activityName, rewardiPerHour, isActive);
                    listAdapter.addItem(act);
                    listAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d("ManAct", "Error = %s" + e.toString());
                }
            }
        };

        deleteActivityCallback = new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                Log.d("ManAct", "deleteActivityCallback called!");
                Log.d("ManAct", "Server Response = " + res.toString());
                if(e == null){
                   // HN-CHECK -> check if response is 200 -> when remove activity from list
                    JsonElement element = new JsonParser().parse(res.getResult());
                    Log.d("ManAct", "Element = " + element.toString());
                    JsonObject activityObj = element.getAsJsonObject();

                    listAdapter.removeActivity(activityObj.get("id").getAsInt());
                    listAdapter.notifyDataSetChanged();
                    showDeleteMenu(false);
                }
                else{
                    Log.d("ManAct", "Error = %s" + e.toString());
                }
            }
        };

        appState = ((Globals)getApplicationContext());
        appState.sendMessageToServer(Globals.messageID.ACTIVITY_GET_ALL, 0,null, getAllActivitiesCallback);
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
        getMenuInflater().inflate(R.menu.activities, menu);
        menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemDelete.setVisible(false);//initially hidden
        menuItemDelete.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        List<ManualActivity> deleteList = listAdapter.getListActivitiesSelected();
                        for(int i = 0; i<deleteList.size(); ++i){
                            appState.sendMessageToServer(Globals.messageID.ACTIVITY_DELETE, deleteList.get(i).getId(),null, deleteActivityCallback);
                        }

                        //listAdapter.removeSelectedActivities();
                        //listAdapter.notifyDataSetChanged();
                        //showDeleteMenu(false);
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

    private void showDeleteMenu(boolean show){
        menuItemDelete.setVisible(show);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK){
            JsonObject dataObj = new JsonObject();
            dataObj.addProperty("name", data.getStringExtra("name"));
            dataObj.addProperty("rewardiPerHour",data.getStringExtra("rewardiPerHour"));
            appState.sendMessageToServer(Globals.messageID.ACTIVITY_CREATE, 0,dataObj, createActivityCallback);
        }
    }
}
