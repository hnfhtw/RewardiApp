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
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem menuItemDelete;
    private CustomListAdapterTodoList listAdapter;
    private FloatingActionButton floatingActionButtonAdd;
    FutureCallback<Response<String>> getAllTodoListPointsCallback;
    FutureCallback<Response<String>> createTodoListPointCallback;
    FutureCallback<Response<String>> deleteTodoListPointCallback;
    FutureCallback<Response<String>> editTodoListPointCallback;
    Globals appState;
    private TodoListPoint editTodoListPoint;    // server does not send whole object as payload if the todo list point is edited with PUT request -> so store the object that is to be edited here until server confirms with HTTP STATUS 204

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarRewardi = (TextView) toolbar.findViewById(R.id.textViewRewardiAccountBalanceHeader);

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
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
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

        getAllTodoListPointsCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("TodoList", "Element = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfTodoListPoints = array.size();
                    Log.d("TodoList", "Number of Todo List Points = " + nrOfTodoListPoints);
                    JsonObject dataObj = null;
                    for (int i = 0; i < nrOfTodoListPoints; ++i) {
                        dataObj = array.get(i).getAsJsonObject();
                        Log.d("TodoList", "Todo List Point " + i + " = " + dataObj.toString());
                        int id = dataObj.get("id").getAsInt();
                        String pointName = dataObj.get("name").getAsString();
                        int rewardi = dataObj.get("rewardi").getAsInt();
                        boolean done = dataObj.get("done").getAsBoolean();

                        TodoListPoint todoListPoint = new TodoListPoint(id, pointName, rewardi, done);
                        listAdapter.addItem(todoListPoint);
                        listAdapter.notifyDataSetChanged();
                    }

                }
                else{
                    Log.d("TodoList", "Error = %s" + e.toString());
                }
            }
        };

        createTodoListPointCallback = new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                Log.d("TodoList", "createTodoListPointCallback called!");
                Log.d("TodoList", "Server Response Code = " + res.getHeaders().code());
                if(e == null){
                    JsonElement element = new JsonParser().parse(res.getResult());
                    Log.d("TodoList", "Element = " + element.toString());
                    JsonObject dataObj = element.getAsJsonObject();

                    int id = dataObj.get("id").getAsInt();
                    String pointName = dataObj.get("name").getAsString();
                    int rewardi = dataObj.get("rewardi").getAsInt();
                    boolean isDone = dataObj.get("done").getAsBoolean();

                    TodoListPoint todoListPoint = new TodoListPoint(id, pointName, rewardi, isDone);
                    listAdapter.addItem(todoListPoint);
                    listAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d("TodoList", "Error = %s" + e.toString());
                }
            }
        };

        deleteTodoListPointCallback = new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                Log.d("TodoList", "deleteTodoListPointCallback called!");
                Log.d("TodoList", "Server Response = " + res.toString());
                if(e == null){
                    // HN-CHECK -> check if response is 200 -> then remove todo list point from list
                    JsonElement element = new JsonParser().parse(res.getResult());
                    Log.d("TodoList", "Element = " + element.toString());
                    JsonObject dataObj = element.getAsJsonObject();

                    listAdapter.removeTodoListPoint(dataObj.get("id").getAsInt());
                    listAdapter.notifyDataSetChanged();
                    showDeleteMenu(false);
                }
                else{
                    Log.d("TodoList", "Error = %s" + e.toString());
                }
            }
        };

        editTodoListPointCallback = new FutureCallback<Response<String>>() {

            @Override
            public void onCompleted(Exception e, Response<String> res) {
                Log.d("TodoList", "editTodoListPointCallback called!");
                Log.d("TodoList", "Server Response = " + res.toString());
                if(e == null){
                    if(res.getHeaders().code() == 204){         // edit list item if server confirms the change with HTTP STATUS 204
                        listAdapter.setItem(editTodoListPoint);
                        listAdapter.notifyDataSetChanged();
                    }
                }
                else{
                    Log.d("TodoList", "Error = %s" + e.toString());
                }
            }
        };

        appState = ((Globals)getApplicationContext());
        appState.sendMessageToServer(Globals.messageID.TODO_GET_ALL, 0,null, getAllTodoListPointsCallback);
        toolbarRewardi.setText(Double.toString(appState.getUser().getTotalRewardi()));
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
                    public boolean onMenuItemClick(MenuItem menuItem) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
}
