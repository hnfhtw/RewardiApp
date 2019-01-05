package me.rewardi;

import com.google.gson.JsonObject;

public class Message {

    enum messageTypes {TODO_HISTORY_GRANT_REQUEST, ACTIVITY_HISTORY_GRANT_REQUEST, SUPERVISOR_LINK_REQUEST, SUPERVISOR_UNLINK_REQUEST};

    private messageTypes messageType;
    private String messageTitle;
    private String messageText;
    private JsonObject rawData;
    private int endpointAnswerId;

    public Message(){
        this.messageType = Message.messageTypes.TODO_HISTORY_GRANT_REQUEST;
        this.messageTitle = "";
        this.messageText = "";
        this.rawData = null;
        this.endpointAnswerId = 0;
    }

    public Message(Message.messageTypes messageType, String messageTitle, String messageText, JsonObject rawData, int endpointAnswerId){
        this.messageType = messageType;
        this.messageTitle = messageTitle;
        this.messageText = messageText;
        this.rawData = rawData;
        this.endpointAnswerId = endpointAnswerId;
    }

    public messageTypes getMessageType() { return messageType; }

    public void setMessageType(messageTypes messageType) { this.messageType = messageType; }

    public String getMessageTitle() { return messageTitle; }

    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }

    public String getMessageText() { return messageText; }

    public void setMessageText(String messageText) { this.messageText = messageText; }

    public JsonObject getRawData() { return rawData; }

    public void setRawData(JsonObject rawData) { this.rawData = rawData; }

    public int getEndpointAnswerId() { return endpointAnswerId; }

    public void setEndpointAnswerId(int endpointAnswerId) { this.endpointAnswerId = endpointAnswerId; }
}
