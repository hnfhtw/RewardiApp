package me.rewardi;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import org.parceler.Parcels;
import java.util.ArrayList;
import java.util.List;

public class Gadgets extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UpdateUserdata {

    Globals appState;
    private MenuItem menuItemDelete;
    private CustomListAdapterGadgets listAdapter;
    private FloatingActionButton floatingActionButtonAdd;
    FutureCallback<Response<String>> getAllGadgetsCallback;
    FutureCallback<Response<String>> createGadgetCallback;
    FutureCallback<Response<String>> deleteGadgetCallback;
    FutureCallback<Response<String>> editGadgetCallback;
    private Gadget editGadget;    // server does not send whole object as payload if the gadget is edited with PUT request -> so store the object that is to be edited here until server confirms with HTTP STATUS 204
    private TextView toolbarRewardi;
    private BroadcastReceiver currentActivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadgets);
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

        floatingActionButtonAdd = findViewById(R.id.floatingActionButtonAdd);

        floatingActionButtonAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), GadgetAdd.class);
                        startActivityForResult(intent, 101);
                    }
                });

        List<Gadget> items = new ArrayList<Gadget>();

        // ListAdapter is resonsible for conversion between java code and list items that can be used
        listAdapter = new CustomListAdapterGadgets(this, items);
        ListView listview1 = (ListView) findViewById(R.id.listview1);
        listview1.setAdapter(listAdapter);

        // set up an onitemclicklistener that something should be done if an item is clicked
        listview1.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(view.getContext(), GadgetAdd.class);
                        Bundle bundle = new Bundle();

                        Gadget item = (Gadget) parent.getItemAtPosition(position);
                        if(item.getTrustNumber().charAt(0) == '2') {        // SocketBoard
                            SocketBoard socketBoardItem = (SocketBoard) parent.getItemAtPosition(position);
                            bundle.putParcelable("socketBoard", Parcels.wrap(socketBoardItem));
                        }
                        else if(item.getTrustNumber().charAt(0) == '1') {    // Box
                            Box boxItem = (Box) parent.getItemAtPosition(position);
                            bundle.putParcelable("box", Parcels.wrap(boxItem));
                        }
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 102);
                    }
                }
        );

        listview1.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                        listAdapter.handleLongPress(position,view);
                        if(listAdapter.getListGadgetsSelected().size() > 0){
                            showDeleteMenu(true);
                        }else{
                            showDeleteMenu(false);
                        }
                        return true;
                    }
                }
        );

        getAllGadgetsCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Gadgets", "getAllGadgetsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfGadgets = array.size();
                    Log.d("Gadgets", "getAllGadgetsCallback Server Response Number of Gadgets = " + nrOfGadgets);
                    JsonObject obj = null;
                    for(int i = 0; i<nrOfGadgets; ++i){
                        obj = array.get(i).getAsJsonObject();
                        Log.d("Gadgets", "Gadget"+i+" = " + obj.toString());

                        SocketBoard socketBoard = SocketBoard.parseObject(obj);
                        if(socketBoard != null) {
                            listAdapter.addItem(socketBoard);
                        }
                        else {
                            Box box = Box.parseObject(obj);
                            if(box != null) {
                                listAdapter.addItem(box);
                            }
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("Gadgets", "getAllGadgetsCallback Server Response Error = " + e.toString());
                }
            }
        };

        createGadgetCallback = new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(res.getResult());
                    Log.d("Gadgets", "createGadgetCallback Server Response = " + element.toString());
                    JsonObject obj = element.getAsJsonObject();

                    SocketBoard socketBoard = SocketBoard.parseObject(obj);
                    if(socketBoard != null) {
                        listAdapter.addItem(socketBoard);
                    }
                    else {
                        Box box = Box.parseObject(obj);
                        if(box != null) {
                            listAdapter.addItem(box);
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d("Gadgets", "createGadgetCallback Server Response Error = " + e.toString());
                }
            }
        };

        deleteGadgetCallback = new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                if(e == null){
                    // HN-CHECK -> check if response is 200 -> then remove activity from list
                    JsonElement element = new JsonParser().parse(res.getResult());
                    Log.d("Gadgets", "deleteGadgetCallback Server Response = " + element.toString());
                    JsonObject obj = element.getAsJsonObject();

                    listAdapter.removeGadget(obj.get("id").getAsInt());
                    listAdapter.notifyDataSetChanged();
                    showDeleteMenu(false);
                }
                else{
                    Log.d("Gadgets", "deleteGadgetCallback Server Response Error = " + e.toString());
                }
            }
        };

        editGadgetCallback = new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                if(e == null){
                    if(res.getHeaders().code() == 204){         // edit list item if server confirms the change with HTTP STATUS 204
                        listAdapter.setItem(editGadget);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("Gadgets", "editGadgetCallback Server Response Error = " + e.toString());
                }
            }
        };

        appState = ((Globals)getApplicationContext());
        appState.setUserDataListener(this);
        appState.requestUserDataUpdate();
        appState.sendMessageToServer(Globals.messageID.BOX_GET_ALL, 0,null, getAllGadgetsCallback);
        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_GET_ALL, 0,null, getAllGadgetsCallback);
        //toolbarRewardi.setText(Double.toString(appState.getUser().getTotalRewardi()));
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
        getMenuInflater().inflate(R.menu.gadgets, menu);
        menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemDelete.setVisible(false);//initially hidden
        menuItemDelete.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        List<Gadget> deleteList = listAdapter.getListGadgetsSelected();
                        for(int i = 0; i<deleteList.size(); ++i){
                            if(deleteList.get(i).getTrustNumber().charAt(0) == '2') {        // SocketBoard
                                appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_DELETE, deleteList.get(i).getId(), null, deleteGadgetCallback);
                            }
                            else if(deleteList.get(i).getTrustNumber().charAt(0) == '1') {    // Box
                                appState.sendMessageToServer(Globals.messageID.BOX_DELETE, deleteList.get(i).getId(), null, deleteGadgetCallback);
                            }
                        }
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

    private void showDeleteMenu(boolean show){
        menuItemDelete.setVisible(show);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            String type = data.getStringExtra("gadgetType");
            if(type.equals("SocketBoard")) {
                SocketBoard socketBoard = Parcels.unwrap(bundle.getParcelable("socketBoard"));
                JsonObject dataObj = new JsonObject();
                dataObj.addProperty("trustNo", socketBoard.getTrustNumber());
                dataObj.addProperty("name", socketBoard.getName());
                dataObj.addProperty("rewardiPerHour", socketBoard.getRewardiPerHour());
                dataObj.addProperty("maxTime", socketBoard.getMaxTimeSec());
                if (requestCode == 101) {     // 101 = RESULT_ADD -> add new socket board
                    appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_CREATE, 0, dataObj, createGadgetCallback);
                } else if (requestCode == 102) {    // 102 = RESULT_EDIT -> edit existing socket board
                    appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_EDIT, socketBoard.getId(), dataObj, editGadgetCallback);
                    editGadget = socketBoard;
                }
            }
            else if(type.equals("Box")){
                Box box = Parcels.unwrap(bundle.getParcelable("box"));
                JsonObject dataObj = new JsonObject();
                dataObj.addProperty("trustNo", box.getTrustNumber());
                dataObj.addProperty("name", box.getName());
                dataObj.addProperty("rewardiPerOpen", box.getRewardiPerOpen());
                if (requestCode == 101) {     // 101 = RESULT_ADD -> add new box
                    appState.sendMessageToServer(Globals.messageID.BOX_CREATE, 0, dataObj, createGadgetCallback);
                } else if (requestCode == 102) {    // 102 = RESULT_EDIT -> edit existing box
                    appState.sendMessageToServer(Globals.messageID.BOX_EDIT, box.getId(), dataObj, editGadgetCallback);
                    editGadget = box;
                }
            }
        }
    }

    @Override
    public void onUserDataUpdate(User user) {
        if(toolbarRewardi != null){
            toolbarRewardi.setText(Double.toString(user.getTotalRewardi()));
        }
    }
}
