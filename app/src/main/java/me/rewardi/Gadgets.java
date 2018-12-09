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

public class Gadgets extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem menuItemDelete;
    private CustomListAdapterGadgets listAdapter;
    private FloatingActionButton floatingActionButtonAdd;
    FutureCallback<Response<String>> serverResponseCallback;
    Globals appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gadgets);
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
                        Gadget item = (Gadget) parent.getItemAtPosition(position);
                        Toast.makeText(Gadgets.this, item.getName(), Toast.LENGTH_LONG).show();
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

        serverResponseCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                Log.d("Gadgets", "Result Header = " + result.getHeaders().toString());
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Gadgets", "Element = " + element.toString());
                    JsonArray array = element.getAsJsonArray();
                    int nrOfGadgets = array.size();
                    Log.d("Gadgets", "Number of Gadgets = " + nrOfGadgets);
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
                            try {
                                String usedSince = gadget.get("usedSince").getAsString();
                                if(usedSince != null){
                                    isActive = true;
                                }
                            }catch(Exception ex){

                            }
                            SocketBoard socketBoard = new SocketBoard(id, trustNumber, name, rewardiPerHour, maxTime);
                            listAdapter.addItem(socketBoard);
                            listAdapter.notifyDataSetChanged();
                        }
                        else if(trustNumber.charAt(0) == '1') {   // Box
                            int rewardiPerOpen = gadget.get("rewardiPerOpen").getAsInt();
                            boolean isLocked = gadget.get("isLocked").getAsBoolean();
                            Box box = new Box(id, trustNumber, name, rewardiPerOpen, isLocked);
                            listAdapter.addItem(box);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
                else{
                    Log.d("Gadgets", "Error = %s" + e.toString());
                }
            }
        };

        appState = ((Globals)getApplicationContext());
        appState.sendMessageToServer(Globals.messageID.BOX_GET_ALL, 0,null, serverResponseCallback);
        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_GET_ALL, 0,null, serverResponseCallback);
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
                        listAdapter.removeSelectedGadgets();
                        listAdapter.notifyDataSetChanged();
                        showDeleteMenu(false);
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

            String type = data.getStringExtra("gadgetType");
            if(type.equals("SocketBoard")){
                Gadget gadget = new SocketBoard(3, data.getStringExtra("trustNumber"), data.getStringExtra("name"), data.getIntExtra("rewardiPerHour", 0), data.getIntExtra("maxTimeSec", 0));
                listAdapter.addItem(gadget);
                listAdapter.notifyDataSetChanged();
            }
            else if(type.equals("Box")){
                Gadget gadget = new Box(3, data.getStringExtra("trustNumber"), data.getStringExtra("name"), data.getIntExtra("rewardiPerOpen", 0), false);
                listAdapter.addItem(gadget);
                listAdapter.notifyDataSetChanged();
            }
        }
    }
}
