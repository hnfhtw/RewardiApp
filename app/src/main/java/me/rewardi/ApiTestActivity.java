package me.rewardi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;

public class ApiTestActivity extends AppCompatActivity implements View.OnClickListener {

    FutureCallback<String> myCallback;
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

        debugText = (TextView) findViewById(R.id.debugText);

        appState = ((Globals)getApplicationContext());


       myCallback = new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if(e == null){
                    debugText.setText(result);
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
                appState.sendMessageToServer(Globals.messageID.GET_BOXES, null, myCallback);
                break;

            case R.id.buttonGetSockets:
                appState.sendMessageToServer(Globals.messageID.GET_SOCKETS, null, myCallback);
                break;

            case R.id.buttonLockBox:
                JsonObject emptyObj = new JsonObject();
                appState.sendMessageToServer(Globals.messageID.LOCK_BOX, emptyObj, myCallback);
                break;

            case R.id.buttonActivateSocket:
                JsonObject dataObj = new JsonObject();
                dataObj.addProperty("maxTime", 3600);
                appState.sendMessageToServer(Globals.messageID.DEACTIVATE_SOCKET, dataObj, myCallback);
                break;

            case R.id.buttonDeactivateSocket:
                appState.sendMessageToServer(Globals.messageID.DEACTIVATE_SOCKET, null, myCallback);
                break;

            default:
                break;
        }
    }
}
