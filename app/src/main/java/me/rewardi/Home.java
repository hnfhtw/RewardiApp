package me.rewardi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Globals appState;
    private CustomListAdapterTodoList listAdapterTodoList;
    private CustomListAdapterActivities listAdapterActivities;
    private CustomGridViewActivity adapterViewAndroid;
    FutureCallback<Response<String>> getUserDataCallback;
    FutureCallback<Response<String>> getAllGadgetsCallback;
    FutureCallback<Response<String>> getAllTodoListPointsCallback;
    FutureCallback<Response<String>> getAllActivitiesCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

        final TextView textViewRewardiAccountBalance = (TextView) findViewById(R.id.textViewRewardiAccountBalance);

        final List<Gadget> gadgetItems = new ArrayList<Gadget>();
        adapterViewAndroid = new CustomGridViewActivity(this, gadgetItems);
        GridView androidGridView = (GridView)findViewById(R.id.grid_view_image_text);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Gadget gadget = gadgetItems.get(i);
                if(gadget instanceof SocketBoard){
                    showSocketBoardDialogFragment((SocketBoard)gadget);
                }
                else if(gadget instanceof Box){
                    showBoxDialogFragment((Box)gadget);
                }
            }
        });

        List<TodoListPoint> todoListItems = new ArrayList<TodoListPoint>();
        listAdapterTodoList = new CustomListAdapterTodoList(this, todoListItems, R.layout.custom_row_todolist_home);
        ListView listViewTodoList = (ListView) findViewById(R.id.listviewTodoList);
        listViewTodoList.setAdapter(listAdapterTodoList);

        List<ManualActivity> activityItems = new ArrayList<ManualActivity>();
        listAdapterActivities = new CustomListAdapterActivities(this, activityItems, R.layout.custom_row_activities_home);
        ListView listViewActivities = (ListView) findViewById(R.id.listviewActivities);
        listViewActivities.setAdapter(listAdapterActivities);

        getUserDataCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Home", "getUserDataCallback Server Response = " + element.toString());
                    JsonObject object = element.getAsJsonObject();

                    int userId = object.get("id").getAsInt();
                    String firebaseInstanceId = object.get("instanceId").getAsString();
                    double rewardi = object.get("totalRewardi").getAsDouble();
                    int fkPartnerUserId = 0;
                    String partnerUserName = "";
                    String partnerMailAddress = "";
                    User.supervisorStatusTypes supervisorStatus = User.supervisorStatusTypes.NONE;
                    if(object.get("fkSupervisorUserId").isJsonNull() == false){
                        fkPartnerUserId = object.get("fkSupervisorUserId").getAsInt();
                        partnerUserName = object.get("fkSupervisorUser").getAsJsonObject().get("fkAspNetUsers").getAsJsonObject().get("userName").getAsString();
                        partnerMailAddress = object.get("fkSupervisorUser").getAsJsonObject().get("fkAspNetUsers").getAsJsonObject().get("email").getAsString();
                        int status = object.get("supervisorStatus").getAsInt();
                        switch(status){
                            case 1: { supervisorStatus = User.supervisorStatusTypes.LINK_PENDING; break; }
                            case 2: { supervisorStatus = User.supervisorStatusTypes.LINKED; break; }
                            case 3: { supervisorStatus = User.supervisorStatusTypes.UNLINK_PENDING; break; }
                            default:{ supervisorStatus = User.supervisorStatusTypes.NONE; break; }
                        }
                    }
                    String userName = object.get("fkAspNetUsers").getAsJsonObject().get("userName").getAsString();
                    String email = object.get("fkAspNetUsers").getAsJsonObject().get("email").getAsString();

                    User user = new User(userId, firebaseInstanceId, rewardi,fkPartnerUserId, userName, email, partnerUserName, partnerMailAddress, supervisorStatus);
                    appState.setUser(user);
                    textViewRewardiAccountBalance.setText(Double.toString(user.getTotalRewardi()));
                    toolbarRewardi.setText(Double.toString(user.getTotalRewardi()));
                  }
                else{
                    Log.d("Home", "getUserDataCallback Server Response Error = " + e.toString());
                }
            }
        };

        getAllGadgetsCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Home", "getAllGadgetsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfGadgets = array.size();
                    Log.d("Home", "getAllGadgetsCallback Server Response Number of Gadgets = " + nrOfGadgets);
                    JsonObject gadget = null;
                    for(int i = 0; i<nrOfGadgets; ++i){
                        gadget = array.get(i).getAsJsonObject();
                        Log.d("Gadgets", "Gadget"+i+" = " + gadget.toString());
                        int id = gadget.get("id").getAsInt();
                        String trustNumber = gadget.get("trustNo").getAsString();
                        String name = gadget.get("name").getAsString();
                        if(trustNumber.charAt(0) == '2') {        // SocketBoard
                            int rewardiPerHour = gadget.get("rewardiPerHour").getAsInt();
                            int maxTime = gadget.get("maxTime").getAsInt();
                            boolean isActive = false;
                            if(gadget.get("usedSince").isJsonNull() == false){
                                isActive = true;
                            }

                            SocketBoard socketBoard = new SocketBoard(id, trustNumber, name, rewardiPerHour, maxTime, isActive);
                            adapterViewAndroid.addItem(socketBoard);
                            adapterViewAndroid.notifyDataSetChanged();
                        }
                        else if(trustNumber.charAt(0) == '1') {   // Box
                            int rewardiPerOpen = gadget.get("rewardiPerOpen").getAsInt();
                            boolean isLocked = gadget.get("isLocked").getAsBoolean();

                            Box box = new Box(id, trustNumber, name, rewardiPerOpen, isLocked);
                            adapterViewAndroid.addItem(box);
                            adapterViewAndroid.notifyDataSetChanged();
                        }
                    }
                }
                else{
                    Log.d("Home", "getAllGadgetsCallback Server Response Error = " + e.toString());
                }
            }
        };

        getAllTodoListPointsCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Home", "getAllTodoListPointsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfTodoListPoints = array.size();
                    Log.d("Home", "getAllTodoListPointsCallback Server Response Number of Todo List Points = " + nrOfTodoListPoints);
                    JsonObject dataObj = null;
                    for (int i = 0; i < nrOfTodoListPoints; ++i) {
                        dataObj = array.get(i).getAsJsonObject();
                        Log.d("TodoList", "Todo List Point " + i + " = " + dataObj.toString());
                        int id = dataObj.get("id").getAsInt();
                        String pointName = dataObj.get("name").getAsString();
                        int rewardi = dataObj.get("rewardi").getAsInt();
                        boolean done = dataObj.get("done").getAsBoolean();

                        TodoListPoint todoListPoint = new TodoListPoint(id, pointName, rewardi, done);
                        listAdapterTodoList.addItem(todoListPoint);
                        listAdapterTodoList.notifyDataSetChanged();
                    }

                }
                else{
                    Log.d("Home", "getAllTodoListPointsCallback Server Response Error = " + e.toString());
                }
            }
        };

        getAllActivitiesCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Home", "getAllActivitiesCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfActivities = array.size();
                    Log.d("Home", "getAllActivitiesCallback Server Response Number of Activities = " + nrOfActivities);
                    JsonObject activity = null;
                    for (int i = 0; i < nrOfActivities; ++i) {
                        activity = array.get(i).getAsJsonObject();
                        Log.d("ManAct", "Activities" + i + " = " + activity.toString());
                        int id = activity.get("id").getAsInt();
                        String activityName = activity.get("name").getAsString();
                        int rewardiPerHour = activity.get("rewardiPerHour").getAsInt();

                        ManualActivity manualActivity;
                        boolean isActive = false;
                        if(activity.get("activeSince").isJsonNull() == false){
                            isActive = true;
                            String activeSince = activity.get("activeSince").getAsString();
                            manualActivity = new ManualActivity(id, activityName, rewardiPerHour, isActive, activeSince);
                        }
                        else{
                            manualActivity = new ManualActivity(id, activityName, rewardiPerHour, isActive, null);
                        }
                        listAdapterActivities.addItem(manualActivity);
                        listAdapterActivities.notifyDataSetChanged();
                    }

                }
                else{
                    Log.d("Home", "getAllActivitiesCallback Server Response Error = " + e.toString());
                }
            }
        };

        appState = ((Globals)getApplicationContext());

        // FCM Messaging Stuff
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "fcm_default_channel";
            String channelName = "Rewardi";
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("Home", "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Home", "Firebase getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and send firebase token (instanceID) to server
                        JsonObject dataObj = new JsonObject();
                        dataObj.addProperty("instanceId", token);           // firebase instance ID
                        appState.sendMessageToServer(Globals.messageID.USER_EDIT, 0,dataObj, null);
                        Log.d("Home", "Firebase Token (Instance ID) = " + token);
                    }
                });

        appState.sendMessageToServer(Globals.messageID.USER_GET, 0,null, getUserDataCallback);
        appState.sendMessageToServer(Globals.messageID.BOX_GET_ALL, 0,null, getAllGadgetsCallback);
        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_GET_ALL, 0,null, getAllGadgetsCallback);
        appState.sendMessageToServer(Globals.messageID.TODO_GET_ALL, 0,null, getAllTodoListPointsCallback);
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
        getMenuInflater().inflate(R.menu.home, menu);
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

    public void showSocketBoardDialogFragment(SocketBoard socketBoard) {
        DialogFragment newFragment = new SocketBoardDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("socketboard", Parcels.wrap(socketBoard));
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "socketboarddialog");
    }

    public void showBoxDialogFragment(Box box) {
        DialogFragment newFragment = new BoxDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("box", Parcels.wrap(box));
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "boxdialog");
    }
}
