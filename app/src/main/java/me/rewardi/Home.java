package me.rewardi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
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
        implements NavigationView.OnNavigationItemSelectedListener, UpdateUserdata, SocketBoardDialogFragment.SocketBoardDialogListener {

    Globals appState;
    private CustomListAdapterTodoList listAdapterTodoList;
    private CustomListAdapterActivities listAdapterActivities;
    private CustomGridViewActivity adapterViewAndroid;
    FutureCallback<Response<String>> getAllGadgetsCallback;
    FutureCallback<Response<String>> getAllTodoListPointsCallback;
    FutureCallback<Response<String>> getAllActivitiesCallback;
    private TextView toolbarRewardi;
    private TextView textViewRewardiAccountBalance;
    private BroadcastReceiver currentActivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarRewardi  = (TextView) toolbar.findViewById(R.id.textViewRewardiAccountBalanceHeader);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textViewRewardiAccountBalance = (TextView) findViewById(R.id.textViewRewardiAccountBalance);

        final List<Gadget> gadgetItems = new ArrayList<Gadget>();
        adapterViewAndroid = new CustomGridViewActivity(this, gadgetItems);
        GridView androidGridView = (GridView)findViewById(R.id.grid_view_image_text);
        androidGridView.setAdapter(adapterViewAndroid);
        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Gadget gadget = gadgetItems.get(i);
                if(gadget instanceof SocketBoard){
                    SocketBoard socketBoard = (SocketBoard)gadget;
                    showSocketBoardDialogFragment(socketBoard);
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

        getAllGadgetsCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Home", "getAllGadgetsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfGadgets = array.size();
                    Log.d("Home", "getAllGadgetsCallback Server Response Number of Gadgets = " + nrOfGadgets);
                    JsonObject obj = null;
                    for(int i = 0; i<nrOfGadgets; ++i){
                        obj = array.get(i).getAsJsonObject();
                        Log.d("Gadgets", "Gadget"+i+" = " + obj.toString());

                        SocketBoard socketBoard = SocketBoard.parseObject(obj);
                        if(socketBoard != null) {
                            adapterViewAndroid.addItem(socketBoard);
                        }
                        else {
                            Box box = Box.parseObject(obj);
                            if(box != null) {
                                adapterViewAndroid.addItem(box);
                            }
                        }
                        adapterViewAndroid.notifyDataSetChanged();
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
                    JsonObject obj = null;
                    for (int i = 0; i < nrOfTodoListPoints; ++i) {
                        obj = array.get(i).getAsJsonObject();
                        Log.d("TodoList", "Todo List Point " + i + " = " + obj.toString());
                        TodoListPoint todoListPoint = TodoListPoint.parseObject(obj);
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
                    JsonObject obj = null;
                    for (int i = 0; i < nrOfActivities; ++i) {
                        obj = array.get(i).getAsJsonObject();
                        Log.d("ManAct", "Activities" + i + " = " + obj.toString());
                        ManualActivity manualActivity = ManualActivity.parseObject(obj);
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

        appState.setUserDataListener(this);
        appState.requestUserDataUpdate();
        appState.sendMessageToServer(Globals.messageID.BOX_GET_ALL, 0,null, getAllGadgetsCallback);
        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_GET_ALL, 0,null, getAllGadgetsCallback);
        appState.sendMessageToServer(Globals.messageID.TODO_GET_ALL, 0,null, getAllTodoListPointsCallback);
        appState.sendMessageToServer(Globals.messageID.ACTIVITY_GET_ALL, 0,null, getAllActivitiesCallback);
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
        ((SocketBoardDialogFragment)newFragment).setListener(this);
        newFragment.show(getSupportFragmentManager(), "socketboarddialog");
    }

    public void showBoxDialogFragment(Box box) {
        DialogFragment newFragment = new BoxDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("box", Parcels.wrap(box));
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "boxdialog");
    }

    @Override
    public void onUserDataUpdate(User user) {
        if(toolbarRewardi != null){
            toolbarRewardi.setText(Double.toString(user.getTotalRewardi()));
        }
        if(textViewRewardiAccountBalance != null){
            textViewRewardiAccountBalance.setText(Double.toString(user.getTotalRewardi()));
        }
    }

    @Override
    public void onFinishSocketBoardDialog(SocketBoard socketBoard) {
        adapterViewAndroid.setItem(socketBoard);
        adapterViewAndroid.notifyDataSetChanged();
    }
}
