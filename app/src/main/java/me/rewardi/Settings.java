package me.rewardi;

import android.content.BroadcastReceiver;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

public class Settings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UpdateUserdata {

    Globals appState;
    FutureCallback<Response<String>> changePasswordCallback;
    FutureCallback<Response<String>> setSupervisorCallback;
    FutureCallback<Response<String>> removeSupervisorCallback;
    private TextView toolbarRewardi;
    private BroadcastReceiver currentActivityReceiver;
    private EditText editTextPartnerUserName;
    private TextView textViewPartner;
    private Button buttonSetPartner;
    private Button buttonRemovePartner;
    private TextView textViewSupervisorTitle;
    private TextView textViewSupervisorStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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

        appState = ((Globals)getApplicationContext());
        appState.setUserDataListener(this);
        appState.requestUserDataUpdate();
        //toolbarRewardi.setText(Double.toString(appState.getUser().getTotalRewardi()));

        editTextPartnerUserName = (EditText) findViewById(R.id.editTextPartnerUserName);
        textViewPartner = (TextView) findViewById(R.id.textViewPartner);
        buttonSetPartner = (Button) findViewById(R.id.buttonSetPartner);
        buttonRemovePartner = (Button) findViewById(R.id.buttonRemovePartner);
        textViewSupervisorTitle = (TextView) findViewById(R.id.textViewSupervisorTitle);
        textViewSupervisorStatus = (TextView) findViewById(R.id.textViewSupervisorStatus);

        String partnerUserName = appState.getUser().getSupervisorName();
        String partnerMailAddress = appState.getUser().getSupervisorMailAddress();
        User.supervisorStatusTypes supervisorStatus = appState.getUser().getSupervisorStatus();

        if(supervisorStatus == User.supervisorStatusTypes.NONE){        // user has no supervisor
            editTextPartnerUserName.setText("Enter new supervisor mail address");
            buttonSetPartner.setEnabled(true);
            buttonRemovePartner.setEnabled(false);
            textViewSupervisorTitle.setText("Request new Rewardi Supervisor");
            textViewPartner.setText("not set");
            textViewSupervisorStatus.setText("Current supervisor: ");
        }
        else if(supervisorStatus == User.supervisorStatusTypes.LINKED){  // user has a linked supervisor
            editTextPartnerUserName.setText(partnerMailAddress);
            buttonSetPartner.setEnabled(false);
            buttonRemovePartner.setEnabled(true);
            textViewSupervisorTitle.setText("Remove current Supervisor to set a new one");
            textViewPartner.setText(partnerUserName + " / " +  partnerMailAddress);
            textViewSupervisorStatus.setText("Current supervisor: ");
        }
        else if(supervisorStatus == User.supervisorStatusTypes.LINK_PENDING){   // desired supervisor has not yet confirmed
            editTextPartnerUserName.setText(partnerMailAddress);
            buttonSetPartner.setEnabled(false);
            buttonRemovePartner.setEnabled(true);
            textViewSupervisorTitle.setText("Cancel pending link request to set a new Supervisor");
            textViewPartner.setText(partnerUserName + " / " +  partnerMailAddress);
            textViewSupervisorStatus.setText("Supervisor (pending): ");
        }
        else if(supervisorStatus == User.supervisorStatusTypes.UNLINK_PENDING){   // desired supervisor has not yet confirmed
            editTextPartnerUserName.setText(partnerMailAddress);
            buttonSetPartner.setEnabled(false);
            buttonRemovePartner.setEnabled(true);
            textViewSupervisorTitle.setText("Cancel pending unlink request");
            textViewPartner.setText(partnerUserName + " / " +  partnerMailAddress);
            textViewSupervisorStatus.setText("Supervisor (unlink pending): ");
        }

        buttonSetPartner.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(editTextPartnerUserName.length() > 0){
                            JsonObject dataObj = new JsonObject();
                            dataObj.addProperty("eMailAddress", editTextPartnerUserName.getText().toString());
                            appState.sendMessageToServer(Globals.messageID.USER_SET_SUPERVISOR, 0,dataObj, setSupervisorCallback);
                        }
                    }
                });

        buttonRemovePartner.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        appState.sendMessageToServer(Globals.messageID.USER_REMOVE_SUPERVISOR, 0,null, removeSupervisorCallback);
                    }
                });

        EditText editTextOldPassword = (EditText) findViewById(R.id.editTextOldPassword);
        final EditText editTextNewPassword1 = (EditText) findViewById(R.id.editTextNewPassword1);
        final EditText editTextNewPassword2 = (EditText) findViewById(R.id.editTextNewPassword2);

        Button buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newPassword = editTextNewPassword1.getText().toString();
                        if(newPassword.length() > 0 && newPassword.equals(editTextNewPassword2.getText().toString()) ){
                            // new password entered twice -> send it to server
                            JsonObject dataObj = new JsonObject();
                            dataObj.addProperty("userName", appState.getUser().getUserName());
                            dataObj.addProperty("emailAddress", appState.getUser().getEmail());
                            dataObj.addProperty("password",newPassword);
                            dataObj.addProperty("deviceID", appState.getUser().getDeviceId());
                            appState.sendMessageToServer(Globals.messageID.USER_EDIT, 0,dataObj, changePasswordCallback);
                        }
                    }
                });


        changePasswordCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    JsonElement element = new JsonParser().parse(result.getResult());
                    Log.d("Settings", "changePasswordCallback Server Response = " + element.toString());
                }
                else{
                    Log.d("Settings", "changePasswordCallback Server Response Error = " + e.toString());
                }
            }
        };

        setSupervisorCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && result.getHeaders().code() == 204) {
                    String partnerMailAddress = editTextPartnerUserName.getText().toString();
                    buttonSetPartner.setEnabled(false);
                    buttonRemovePartner.setEnabled(true);
                    textViewSupervisorTitle.setText("Cancel pending link request to set a new Supervisor");
                    textViewPartner.setText(partnerMailAddress);
                    textViewSupervisorStatus.setText("Supervisor (pending): ");
                }
                else{
                    Log.d("Settings", "setSupervisorCallback Server Response Status Code = " + Integer.toString(result.getHeaders().code()));
                }
            }
        };

        removeSupervisorCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && result.getHeaders().code() == 204) {
                    //editTextPartnerUserName.setText("Enter new supervisor mail address");
                    buttonSetPartner.setEnabled(false);
                    buttonRemovePartner.setEnabled(true);
                    textViewSupervisorTitle.setText("Cancel pending unlink request");
                    //textViewPartner.setText("not set");
                    textViewSupervisorStatus.setText("Supervisor (unlink pending): ");
                }
                else{
                    Log.d("Settings", "removeSupervisorCallback Server Response Status Code = " + Integer.toString(result.getHeaders().code()));
                }
            }
        };
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
        getMenuInflater().inflate(R.menu.settings, menu);
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

        String partnerUserName = appState.getUser().getSupervisorName();
        String partnerMailAddress = appState.getUser().getSupervisorMailAddress();
        User.supervisorStatusTypes supervisorStatus = appState.getUser().getSupervisorStatus();

        if(supervisorStatus == User.supervisorStatusTypes.NONE){        // user has no supervisor
            editTextPartnerUserName.setText("Enter new supervisor mail address");
            buttonSetPartner.setEnabled(true);
            buttonRemovePartner.setEnabled(false);
            textViewSupervisorTitle.setText("Request new Rewardi Supervisor");
            textViewPartner.setText("not set");
            textViewSupervisorStatus.setText("Current supervisor: ");
        }
        else if(supervisorStatus == User.supervisorStatusTypes.LINKED){  // user has a linked supervisor
            editTextPartnerUserName.setText(partnerMailAddress);
            buttonSetPartner.setEnabled(false);
            buttonRemovePartner.setEnabled(true);
            textViewSupervisorTitle.setText("Remove current Supervisor to set a new one");
            textViewPartner.setText(partnerUserName + " / " +  partnerMailAddress);
            textViewSupervisorStatus.setText("Current supervisor: ");
        }
        else if(supervisorStatus == User.supervisorStatusTypes.LINK_PENDING){   // desired supervisor has not yet confirmed
            editTextPartnerUserName.setText(partnerMailAddress);
            buttonSetPartner.setEnabled(false);
            buttonRemovePartner.setEnabled(true);
            textViewSupervisorTitle.setText("Cancel pending link request to set a new Supervisor");
            textViewPartner.setText(partnerUserName + " / " +  partnerMailAddress);
            textViewSupervisorStatus.setText("Supervisor (pending): ");
        }
        else if(supervisorStatus == User.supervisorStatusTypes.UNLINK_PENDING){   // desired supervisor has not yet confirmed
            editTextPartnerUserName.setText(partnerMailAddress);
            buttonSetPartner.setEnabled(false);
            buttonRemovePartner.setEnabled(true);
            textViewSupervisorTitle.setText("Cancel pending unlink request");
            textViewPartner.setText(partnerUserName + " / " +  partnerMailAddress);
            textViewSupervisorStatus.setText("Supervisor (unlink pending): ");
        }
    }
}
