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
import com.koushikdutta.ion.Response;

public class Globals extends Application {

    enum messageID {ACTIVITY_GET_ALL, ACTIVITY_GET, ACTIVITY_CREATE, ACTIVITY_EDIT, ACTIVITY_DELETE, ACTIVITY_START, ACTIVITY_STOP,
        ACTIVITY_HISTORY_GET_ALL, BOX_GET_ALL, BOX_GET, BOX_CREATE, BOX_EDIT, BOX_DELETE, BOX_LOCK, BOX_UNLOCK, BOX_HISTORY_GET_ALL,
        SOCKETBOARD_GET_ALL, SOCKETBOARD_GET, SOCKETBOARD_CREATE, SOCKETBOARD_EDIT, SOCKETBOARD_DELETE, SOCKETBOARD_START, SOCKETBOARD_STOP, SOCKETBOARD_RESET, SOCKETBOARD_HISSTORY_GET_ALL,
        TODO_GET_ALL, TODO_GET, TODO_CREATE, TODO_EDIT, TODO_DELETE, TODO_DONE, TODO_HISTORY_GET_ALL, USER_GET, USER_EDIT, USER_SET_SUPERVISOR, USER_REMOVE_SUPERVISOR,
        SOCKETBOARD_EXTEND_MAXTIME, SUPERVISOR_LINK_REQUEST_REPLY, SUPERVISOR_UNLINK_REQUEST_REPLY, SUPERVISOR_ACTIVITY_HISTORY_GRANT_REQUEST, SUPERVISOR_TODO_HISTORY_GRANT_REQUEST,
        SUPERVISOR_TODO_HISTORY_PENDING_GET_ALL, SUPERVISOR_ACTIVITY_HISTORY_PENDING_GET_ALL, SUPERVISOR_LINK_REQUEST_PENDING_GET_ALL, SUPERVISOR_UNLINK_REQUEST_PENDING_GET_ALL,
        SUPERVISOR_SUBORDINATES_GET_ALL};    // messages for response to FCM messages obtained from server

    private String sessionToken;
    private User user;

    public String getSessionToken(){
        return sessionToken;
    }
    public void setSessionToken(String token){
        sessionToken = token;
    }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public void sendMessageToServer(messageID msgID, int deviceId, JsonObject sendObj, FutureCallback<Response<String>> callBack){
        String endpoint;
        String method;
        switch(msgID){
            case ACTIVITY_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/Activities";
                method = "GET";
                break;
            }
            case ACTIVITY_GET:{
                endpoint = "https://37.60.168.102:443/api/Activities/" + deviceId;
                method = "GET";
                break;
            }
            case ACTIVITY_CREATE:{
                endpoint = "https://37.60.168.102:443/api/Activities";
                method = "POST";
                break;
            }
            case ACTIVITY_EDIT:{
                endpoint = "https://37.60.168.102:443/api/Activities/" + deviceId;
                method = "PUT";
                break;
            }
            case ACTIVITY_DELETE:{
                endpoint = "https://37.60.168.102:443/api/Activities/" + deviceId;
                method = "DELETE";
                break;
            }
            case ACTIVITY_START:{
                endpoint = "https://37.60.168.102:443/api/Activities/" + deviceId + "/start";
                method = "PUT";
                break;
            }
            case ACTIVITY_STOP:{
                endpoint = "https://37.60.168.102:443/api/Activities/" + deviceId + "/stop";
                method = "PUT";
                break;
            }
            case ACTIVITY_HISTORY_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/ActivityHistories";
                method = "GET";
                break;
            }
            case BOX_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/Boxes";
                method = "GET";
                break;
            }
            case BOX_GET:{
                endpoint = "https://37.60.168.102:443/api/Boxes/" + deviceId;
                method = "GET";
                break;
            }
            case BOX_CREATE:{
                endpoint = "https://37.60.168.102:443/api/Boxes";
                method = "POST";
                break;
            }
            case BOX_EDIT:{
                endpoint = "https://37.60.168.102:443/api/Boxes/" + deviceId;
                method = "PUT";
                break;
            }
            case BOX_DELETE:{
                endpoint = "https://37.60.168.102:443/api/Boxes/" + deviceId;
                method = "DELETE";
                break;
            }
            case BOX_LOCK:{
                endpoint = "https://37.60.168.102:443/api/Boxes/" + deviceId + "/lock";
                method = "PUT";
                break;
            }
            case BOX_UNLOCK:{
                endpoint = "https://37.60.168.102:443/api/Boxes/" + deviceId + "/reset";
                method = "PUT";
                break;
            }
            case BOX_HISTORY_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/BoxHistories";
                method = "GET";
                break;
            }
            case SOCKETBOARD_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/Sockets";
                method = "GET";
                break;
            }
            case SOCKETBOARD_GET:{
                endpoint = "https://37.60.168.102:443/api/Sockets/" + deviceId;
                method = "GET";
                break;
            }
            case SOCKETBOARD_CREATE:{
                endpoint = "https://37.60.168.102:443/api/Sockets";
                method = "POST";
                break;
            }
            case SOCKETBOARD_EDIT:{
                endpoint = "https://37.60.168.102:443/api/Sockets/" + deviceId;
                method = "PUT";
                break;
            }
            case SOCKETBOARD_DELETE:{
                endpoint = "https://37.60.168.102:443/api/Sockets/" + deviceId;
                method = "DELETE";
                break;
            }
            case SOCKETBOARD_START:{
                endpoint = "https://37.60.168.102:443/api/Sockets/" + deviceId + "/start";
                method = "PUT";
                break;
            }
            case SOCKETBOARD_STOP:{
                endpoint = "https://37.60.168.102:443/api/Sockets/" + deviceId + "/stop";
                method = "PUT";
                break;
            }
            case SOCKETBOARD_RESET:{
                endpoint = "https://37.60.168.102:443/api/Sockets/" + deviceId + "/reset";
                method = "PUT";
                break;
            }
            case SOCKETBOARD_HISSTORY_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/SocketHistories";
                method = "GET";
                break;
            }
            case TODO_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/ToDoes";
                method = "GET";
                break;
            }
            case TODO_GET:{
                endpoint = "https://37.60.168.102:443/api/ToDoes/" + deviceId;
                method = "GET";
                break;
            }
            case TODO_CREATE:{
                endpoint = "https://37.60.168.102:443/api/ToDoes";
                method = "POST";
                break;
            }
            case TODO_EDIT:{
                endpoint = "https://37.60.168.102:443/api/ToDoes/" + deviceId;
                method = "PUT";
                break;
            }
            case TODO_DELETE:{
                endpoint = "https://37.60.168.102:443/api/ToDoes/" + deviceId;
                method = "DELETE";
                break;
            }
            case TODO_DONE:{
                endpoint = "https://37.60.168.102:443/api/ToDoes/" + deviceId + "/done";
                method = "PUT";
                break;
            }
            case TODO_HISTORY_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/ToDoHistories";
                method = "GET";
                break;
            }
            case USER_GET:{
                endpoint = "https://37.60.168.102:443/api/User";
                method = "GET";
                break;
            }
            case USER_EDIT:{
                endpoint = "https://37.60.168.102:443/api/User";
                method = "PUT";
                break;
            }
            case USER_SET_SUPERVISOR:{
                endpoint = "https://37.60.168.102:443/api/User/supervisor";
                method = "POST";
                break;
            }
            case USER_REMOVE_SUPERVISOR:{
                endpoint = "https://37.60.168.102:443/api/User/supervisor";
                method = "DELETE";
                break;
            }
            case SOCKETBOARD_EXTEND_MAXTIME:{
                endpoint = "https://37.60.168.102:443/api/Sockets/" + deviceId + "/extend";
                method = "PUT";
                break;
            }
            case SUPERVISOR_LINK_REQUEST_REPLY:{
                endpoint = "https://37.60.168.102:443/api/Supervisor/" + deviceId;
                method = "PUT";
                break;
            }
            case SUPERVISOR_UNLINK_REQUEST_REPLY:{
                endpoint = "https://37.60.168.102:443/api/Supervisor/" + deviceId;
                method = "DELETE";
                break;
            }
            case SUPERVISOR_ACTIVITY_HISTORY_GRANT_REQUEST:{
                endpoint = "https://37.60.168.102:443/api/Supervisor/ActivityHistory/" + deviceId;
                method = "PUT";
                break;
            }
            case SUPERVISOR_TODO_HISTORY_GRANT_REQUEST:{
                endpoint = "https://37.60.168.102:443/api/Supervisor/ToDoHistory/" + deviceId;
                method = "PUT";
                break;
            }
            case SUPERVISOR_TODO_HISTORY_PENDING_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/Supervisor/ToDoHistories?pending=true";
                method = "GET";
                break;
            }
            case SUPERVISOR_ACTIVITY_HISTORY_PENDING_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/Supervisor/ActivityHistories?pending=true";
                method = "GET";
                break;
            }
            case SUPERVISOR_LINK_REQUEST_PENDING_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/Supervisor?status=1";     // status = 1 -> LINK_PENDING
                method = "GET";
                break;
            }
            case SUPERVISOR_UNLINK_REQUEST_PENDING_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/Supervisor?status=3";     // status = 3 -> UNLINK_PENDING
                method = "GET";
                break;
            }
            case SUPERVISOR_SUBORDINATES_GET_ALL:{
                endpoint = "https://37.60.168.102:443/api/Supervisor?pending=false";
                method = "GET";
                break;
            }
            default:{
                return;
            }
        }
        Log.d("Globals", "Token = " + sessionToken);
        Log.d("Globals", "Endpoint = " + endpoint);
        Log.d("Globals", "Method = " + method);

        if(sendObj != null) {
            Log.d("Globals", "Send Object = " + sendObj.toString());
            Ion.with(this)
                    .load(method, endpoint)
                    .setHeader("Authorization", "Bearer " + sessionToken)
                    .setLogging("ION_VERBOSE_LOGGING", Log.VERBOSE)
                    .setJsonObjectBody(sendObj)
                    .asString() // answer will be delivered to callback as string
                    .withResponse()
                    .setCallback(callBack);
        }
        else{
            Ion.with(this)
                    .load(method, endpoint)
                    .setHeader("Authorization", "Bearer " + sessionToken)
                    .setHeader("Content-Length", "0")
                    .setLogging("ION_VERBOSE_LOGGING", Log.VERBOSE)
                    .asString() // answer will be delivered to callback as string
                    .withResponse()
                    .setCallback(callBack);
        }
    }

}