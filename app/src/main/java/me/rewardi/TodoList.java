/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : TodoList.java
 * Purpose    : List the todo list points of the current user;
 *              add/edit/delete/finish todo list points
 ********************************************************************************************/

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

public class TodoList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UpdateUserdata {

    private Globals appState;
    private MenuItem menuItemDelete;
    private CustomListAdapterTodoList listAdapter;
    private FloatingActionButton floatingActionButtonAdd;
    FutureCallback<Response<String>> getAllTodoListPointsCallback;
    FutureCallback<Response<String>> createTodoListPointCallback;
    FutureCallback<Response<String>> deleteTodoListPointCallback;
    FutureCallback<Response<String>> editTodoListPointCallback;
    private TodoListPoint editTodoListPoint;    // server does not send whole object as payload if the todo list point is edited with PUT request -> so store the object that is to be edited here until server confirms with HTTP STATUS 204
    private TextView toolbarRewardi;
    private BroadcastReceiver currentActivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
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

        floatingActionButtonAdd.setOnClickListener( // button to add new TodoList point -> start TodoListPointAdd activity on click
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), TodoListPointAdd.class);
                        startActivityForResult(intent, 101);
                    }
                });

        List<TodoListPoint> items = new ArrayList<TodoListPoint>();

        // ListAdapter is resonsible for conversion between java code and list items that can be used
        listAdapter = new CustomListAdapterTodoList(this, items, R.layout.custom_row_todolist);
        ListView listview1 = (ListView) findViewById(R.id.listview1);
        listview1.setAdapter(listAdapter);

        // set up an onitemclicklistener that something should be done if an item is clicked
        listview1.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  // if a ListView entry (row) is clicked, the corresponding Rewardi TodoListPoint can be edit -> put the object to an intent, start the TodoListPointAdd activity and pass the intent
                        TodoListPoint item = (TodoListPoint) parent.getItemAtPosition(position);
                        Intent intent = new Intent(view.getContext(), TodoListPointAdd.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("todoListPoint", Parcels.wrap(item));
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 102);
                    }
                }
        );

        listview1.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {   // on a long click on a ListView entry (row) the Delete button is shown -> the Rewardi TodoListPoint can be deleted
                        listAdapter.handleLongPress(position,view);
                        if(listAdapter.getListTodoListPointsSelected().size() > 0){
                            showDeleteMenu(true);
                        }else{
                            showDeleteMenu(false);
                        }
                        return true;
                    }
                }
        );

        getAllTodoListPointsCallback = new FutureCallback<Response<String>>() { // callback function that is called on server response to the request "get all Rewardi TodoListPoints of the current user"
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("TodoList", "getAllTodoListPointsCallback Server Response = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfTodoListPoints = array.size();
                    Log.d("TodoList", "getAllTodoListPointsCallback Server Response Number of Todo List Points = " + nrOfTodoListPoints);
                    JsonObject obj = null;
                    for (int i = 0; i < nrOfTodoListPoints; ++i) {
                        obj = array.get(i).getAsJsonObject();
                        Log.d("TodoList", "getAllTodoListPointsCallback Server Response Todo List Point " + i + " = " + obj.toString());
                        TodoListPoint todoListPoint = TodoListPoint.parseObject(obj);

                        listAdapter.addItem(todoListPoint);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("TodoList", "getAllTodoListPointsCallback Server Response Error = " + e.toString());
                }
            }
        };

        createTodoListPointCallback = new FutureCallback<Response<String>>() {  // callback function that is called on server response to the request "create a new Rewardi TodoListPoint for the current user"

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(res.getResult());
                    Log.d("TodoList", "createTodoListPointCallback Server Response = " + element.toString());
                    JsonObject obj = element.getAsJsonObject();
                    TodoListPoint todoListPoint = TodoListPoint.parseObject(obj);

                    listAdapter.addItem(todoListPoint);
                    listAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d("TodoList", "createTodoListPointCallback Server Response Error = " + e.toString());
                }
            }
        };

        deleteTodoListPointCallback = new FutureCallback<Response<String>>() {  // callback function that is called on server response to the request "delete a Rewardi TodoListPoint of the current user"

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                if(e == null){
                    // HN-CHECK -> check if response is 200 -> then remove todo list point from list
                    JsonElement element = new JsonParser().parse(res.getResult());
                    Log.d("TodoList", "deleteTodoListPointCallback Server Response = " + element.toString());
                    JsonObject obj = element.getAsJsonObject();

                    listAdapter.removeTodoListPoint(obj.get("id").getAsInt());
                    listAdapter.notifyDataSetChanged();
                    showDeleteMenu(false);
                }
                else{
                    Log.d("TodoList", "deleteTodoListPointCallback Server Response Error = " + e.toString());
                }
            }
        };

        editTodoListPointCallback = new FutureCallback<Response<String>>() {    // callback function that is called on server response to the request "edit a Rewardi TodoListPoint of the current user"

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                if(e == null){
                    if(res.getHeaders().code() == 204){         // edit list item if server confirms the change with HTTP STATUS 204
                        listAdapter.setItem(editTodoListPoint);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("TodoList", "editTodoListPointCallback Server Response Error = " + e.toString());
                }
            }
        };

        appState = ((Globals)getApplicationContext());
        appState.setUserDataListener(this); // ensure that this activity is informed when new user data is received from the server
        appState.requestUserDataUpdate();   // request new user data from the server
        appState.sendMessageToServer(Globals.messageID.TODO_GET_ALL, 0,null, getAllTodoListPointsCallback); // send request to server: "get all Rewardi TodoListPoints of the current user"
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
        getMenuInflater().inflate(R.menu.todo_list, menu);
        menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemDelete.setVisible(false);//initially hidden
        menuItemDelete.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) { // several ListView rows (=Rewardi TodoListPoints) can be highlighted and then deleted by clicking the Delete button -> send requests to the server to delete all these Rewardi TodoListPoints
                        List<TodoListPoint> deleteList = listAdapter.getListTodoListPointsSelected();
                        for(int i = 0; i<deleteList.size(); ++i){
                            appState.sendMessageToServer(Globals.messageID.TODO_DELETE, deleteList.get(i).getId(),null, deleteTodoListPointCallback);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // called when TodoListPointAdd finishes -> either wenn new Rewardi TodoListPoint was added (requestCode == 101) or when an existing Rewardi TodoListPoint was edited (requestCode == 102)
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            TodoListPoint point = Parcels.unwrap(bundle.getParcelable("todoListPoint"));
            JsonObject dataObj = new JsonObject();
            dataObj.addProperty("name", point.getName());
            dataObj.addProperty("rewardi",point.getRewardi());
            if(requestCode == 101){     // 101 = RESULT_ADD -> add new todo list point
                appState.sendMessageToServer(Globals.messageID.TODO_CREATE, 0,dataObj, createTodoListPointCallback);
            }
            else if(requestCode == 102){    // 102 = RESULT_EDIT -> edit existing todo list point
                appState.sendMessageToServer(Globals.messageID.TODO_EDIT, point.getId(),dataObj, editTodoListPointCallback);
                editTodoListPoint = point;
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
