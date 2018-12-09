package me.rewardi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

public class ApiTestActivity extends AppCompatActivity implements View.OnClickListener {

    FutureCallback<Response<String>> myCallback;
    Globals appState;
    TextView debugText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);

        Button btnGetBoxes = (Button) findViewById(R.id.buttonGetBoxes);
        btnGetBoxes.setOnClickListener(this); // calling onClick() method
        Button btnGetSockets = (Button) findViewById(R.id.buttonGetSockets);
        btnGetSockets.setOnClickListener(this);
        Button btnLockBox = (Button) findViewById(R.id.buttonLockBox);
        btnLockBox.setOnClickListener(this);
        Button btnActivateSocket = (Button) findViewById(R.id.buttonActivateSocket);
        btnActivateSocket.setOnClickListener(this);
        Button btnDeactivateSocket = (Button) findViewById(R.id.buttonDeactivateSocket);
        btnDeactivateSocket.setOnClickListener(this);
        Button btnHome = (Button) findViewById(R.id.buttonHome);
        btnHome.setOnClickListener(this);

        debugText = (TextView) findViewById(R.id.debugText);

        appState = ((Globals)getApplicationContext());


       myCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if(e == null){
                    debugText.setText(result.getHeaders().toString() + "\n\n" + result.getResult().toString());
                    Log.d("ApiTestActivity", "Result = %s" + result);
                }
                else{
                    Log.d("ApiTestActivity", "Error = %s" + e.toString());
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        // default method for handling onClick Events..

        switch (v.getId()) {

            case R.id.buttonGetBoxes:
                appState.sendMessageToServer(Globals.messageID.BOX_GET_ALL, 0,null, myCallback);
                break;

            case R.id.buttonGetSockets:
                appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_GET_ALL, 0, null, myCallback);
                break;

            case R.id.buttonLockBox:
                appState.sendMessageToServer(Globals.messageID.BOX_LOCK, 3, null, myCallback);
                break;

            case R.id.buttonActivateSocket:
                JsonObject dataObj = new JsonObject();
                dataObj.addProperty("maxTime", 3600);
                appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_STOP, 1, dataObj, myCallback);
                break;

            case R.id.buttonDeactivateSocket:
                appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_START, 1, null, myCallback);
                break;

            case R.id.buttonHome:
                Intent intent = new Intent(this, Home.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
