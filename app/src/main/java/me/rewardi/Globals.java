package me.rewardi;

// Rewardi - Global variables to share data across activities
// Version: V01_000
// Last Modified: 04.12.2018
// Author: HN


import android.app.Application;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Globals extends Application {

    enum messageID {GET_BOXES, GET_SOCKETS, LOCK_BOX, ACTIVATE_SOCKET, DEACTIVATE_SOCKET};

    private String sessionToken;
    public JsonObject test;

    public String getSessionToken(){
        return sessionToken;
    }
    public void setSessionToken(String token){
        sessionToken = token;
    }

    public void sendMessageToServer(messageID msgID, JsonObject sendObj, FutureCallback<String> callBack){
        String endpoint;
        String method;
        switch(msgID){
            case GET_BOXES:{
                endpoint = "https://37.60.168.102:443/api/Boxes";
                method = "GET";
                break;
            }
            case GET_SOCKETS:{
                endpoint = "https://37.60.168.102:443/api/Sockets";
                method = "GET";
                break;
            }
            case LOCK_BOX:{
                endpoint = "https://37.60.168.102:443/api/Boxes/3/lock";
                method = "POST";
                break;
            }
            case ACTIVATE_SOCKET:{
                endpoint = "https://37.60.168.102:443/api/Sockets/1/start";
                method = "POST";
                break;
            }
            case DEACTIVATE_SOCKET:{
                endpoint = "https://37.60.168.102:443/api/Sockets/1/stop";
                method = "POST";
                break;
            }
            default:{
                return;
            }
        }
        Log.d("Globals", "Token = " + sessionToken);

        if(sendObj != null) {
            Log.d("Globals", "Send Object = " + sendObj.toString());
            Ion.with(this)
                    .load(method, endpoint)
                    .setHeader("Authorization", "Bearer " + sessionToken)
                    .setJsonObjectBody(sendObj)
                    .asString() // answer will be delivered to callback as string
                    .setCallback(callBack);
        }
        else{
            Ion.with(this)
                    .load(method, endpoint)
                    .setHeader("Authorization", "Bearer " + sessionToken)
                    .asString() // answer will be delivered to callback as string
                    .setCallback(callBack);
        }
    }

}