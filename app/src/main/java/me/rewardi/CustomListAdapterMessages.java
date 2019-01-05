package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.JsonObject;
import java.util.List;

class CustomListAdapterMessages extends BaseAdapter {
    private List<Message> listMessages;
    private Context context;
    Globals appState;
    private int layoutResId;

    public CustomListAdapterMessages(Context context, List<Message> listMessages, int layoutResId) {
        this.listMessages = listMessages;
        this.context = context;
        this.layoutResId = layoutResId;
    }

    @Override
    public int getCount() {
         return listMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return listMessages.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(Message msg){
        listMessages.add(msg);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(layoutResId, null);
        final Message msg = listMessages.get(position);
        TextView textViewMessageTitle = (TextView) convertView.findViewById(R.id.textViewMessageTitle);
        TextView textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);

        final Button buttonDeleteMessage = (Button) convertView.findViewById(R.id.buttonDeleteMessage);
        final Button buttonDenyRequest = (Button) convertView.findViewById(R.id.buttonDenyRequest);
        final Button buttonConfirmRequest = (Button) convertView.findViewById(R.id.buttonConfirmRequest);

        textViewMessageTitle.setText(msg.getMessageTitle());
        textViewMessage.setText(msg.getMessageText());

        appState = ((Globals)context.getApplicationContext());

        Message.messageTypes messageType = msg.getMessageType();
        if( (messageType == Message.messageTypes.TODO_HISTORY_GRANT_REQUEST) || (messageType == Message.messageTypes.ACTIVITY_HISTORY_GRANT_REQUEST)
                || (messageType == Message.messageTypes.SUPERVISOR_LINK_REQUEST) || (messageType == Message.messageTypes.SUPERVISOR_UNLINK_REQUEST) ){
            buttonDeleteMessage.setEnabled(false);
        }


        buttonDeleteMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // do something
            }
        });

        final int idx = position;

        buttonDenyRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JsonObject sendObj = new JsonObject();
                sendObj.addProperty("granted", false);
                sendObj.addProperty("remark", "supervisor denied");

                if(msg.getMessageType() == Message.messageTypes.TODO_HISTORY_GRANT_REQUEST){
                    appState.sendMessageToServer(Globals.messageID.SUPERVISOR_TODO_HISTORY_GRANT_REQUEST, msg.getEndpointAnswerId(),sendObj, null);
                }
                else if(msg.getMessageType() == Message.messageTypes.ACTIVITY_HISTORY_GRANT_REQUEST){

                    appState.sendMessageToServer(Globals.messageID.SUPERVISOR_ACTIVITY_HISTORY_GRANT_REQUEST, msg.getEndpointAnswerId(),sendObj, null);
                }
                else if(msg.getMessageType() == Message.messageTypes.SUPERVISOR_LINK_REQUEST){
                    // send link response -> deny
                    appState.sendMessageToServer(Globals.messageID.SUPERVISOR_LINK_REQUEST_REPLY, msg.getEndpointAnswerId(),sendObj, null);
                }
                else if(msg.getMessageType() == Message.messageTypes.SUPERVISOR_UNLINK_REQUEST){
                    // send unlink response -> deny
                    appState.sendMessageToServer(Globals.messageID.SUPERVISOR_UNLINK_REQUEST_REPLY, msg.getEndpointAnswerId(),sendObj, null);
                }
                listMessages.remove(idx);
                notifyDataSetChanged();
            }
        });

        buttonConfirmRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JsonObject sendObj = new JsonObject();
                sendObj.addProperty("granted", true);
                sendObj.addProperty("remark", "supervisor confirmed");

                if(msg.getMessageType() == Message.messageTypes.TODO_HISTORY_GRANT_REQUEST){
                    appState.sendMessageToServer(Globals.messageID.SUPERVISOR_TODO_HISTORY_GRANT_REQUEST, msg.getEndpointAnswerId(),sendObj, null);
                }
                else if(msg.getMessageType() == Message.messageTypes.ACTIVITY_HISTORY_GRANT_REQUEST){

                    appState.sendMessageToServer(Globals.messageID.SUPERVISOR_ACTIVITY_HISTORY_GRANT_REQUEST, msg.getEndpointAnswerId(),sendObj, null);
                }
                else if(msg.getMessageType() == Message.messageTypes.SUPERVISOR_LINK_REQUEST){
                    // send link response -> confirm
                    appState.sendMessageToServer(Globals.messageID.SUPERVISOR_LINK_REQUEST_REPLY, msg.getEndpointAnswerId(),sendObj, null);
                }
                else if(msg.getMessageType() == Message.messageTypes.SUPERVISOR_UNLINK_REQUEST){
                    // send unlink response -> confirm
                    appState.sendMessageToServer(Globals.messageID.SUPERVISOR_UNLINK_REQUEST_REPLY, msg.getEndpointAnswerId(),sendObj, null);
                }
                listMessages.remove(idx);
                notifyDataSetChanged();
            }
        });


        return convertView;
    }
}
