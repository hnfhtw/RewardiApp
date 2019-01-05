package me.rewardi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class CustomListAdapterGadgets extends BaseAdapter {
    private List<Gadget> listGadgetsSelected;//keep track of selected objects
    private List<View> listSelectedRows;//keep track of selected rows
    private List<Gadget> listGadgets;
    private Context context;
    Globals appState;
    FutureCallback<Response<String>> startStopSocketBoardCallback;

    public CustomListAdapterGadgets(Context context, List<Gadget> listGadgets) {
        this.listGadgets = listGadgets;
        this.context = context;
        listGadgetsSelected = new ArrayList<>();
        listSelectedRows = new ArrayList<>();
    }

    @Override
    public int getCount() {
         return listGadgets.size();
    }

    @Override
    public Object getItem(int position) {
        return listGadgets.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(Gadget gadget){ listGadgets.add(gadget); }

    public void addItem(Box box){
        listGadgets.add(box);
    }

    public void addItem(SocketBoard socketBoard){
        listGadgets.add(socketBoard);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_gadgets, null);
        final Gadget gadget = listGadgets.get(position);
        TextView textViewGadgetName = (TextView) convertView.findViewById(R.id.textViewGadgetName);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);
        TextView textViewActive = (TextView) convertView.findViewById(R.id.textViewActive);
        final ToggleButton btnStartStop = (ToggleButton) convertView.findViewById(R.id.btnStartStop);
        if(gadget instanceof Box) {
            btnStartStop.setText("Lock Box");
            btnStartStop.setTextOff("Lock Box");
            btnStartStop.setTextOn("Box Locked");
            textViewRewardi.setText("Cost: " + Integer.toString(((Box)gadget).getRewardiPerOpen()) + " Rewardi to Open Box");

            if(((Box)gadget).getIsLocked()){
                btnStartStop.setChecked(true);
                btnStartStop.setEnabled(false);
                textViewActive.setText("Box locked!");
            }
            else{
                textViewActive.setText("Box unlocked!");
            }
        }
        else if(gadget instanceof SocketBoard){
            btnStartStop.setText("Switch On");
            btnStartStop.setTextOff("Switch On");
            btnStartStop.setTextOn("Switch Off");
            textViewRewardi.setText("Cost: " + Integer.toString(((SocketBoard)gadget).getRewardiPerHour()) + " Rewardi / Hour");
            if(((SocketBoard)gadget).getIsActive()){
                btnStartStop.setChecked(true);
                textViewActive.setText("Socket Board switched ON");
            }
            else{
                textViewActive.setText("Socket Board switched OFF");
            }
        }

        appState = ((Globals)context.getApplicationContext());
        btnStartStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(gadget instanceof Box) {
                    if(btnStartStop.isChecked()){
                        btnStartStop.setEnabled(false);
                        appState.sendMessageToServer(Globals.messageID.BOX_LOCK, ((Box)gadget).getId(),null, null);
                        ((Box)gadget).setIsLocked(true);
                    }
                }
                else if(gadget instanceof SocketBoard){
                    if(btnStartStop.isChecked()){
                        JsonObject data = new JsonObject();
                        data.addProperty("maxTime", ((SocketBoard)gadget).getMaxTimeSec());
                        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_START, ((SocketBoard)gadget).getId(),data, startStopSocketBoardCallback);
                        ((SocketBoard)gadget).setIsActive(true);
                    }
                    else{
                        appState = ((Globals)context.getApplicationContext());
                        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_STOP, ((SocketBoard)gadget).getId(),null, startStopSocketBoardCallback);
                        ((SocketBoard)gadget).setIsActive(false);
                    }
                }
                notifyDataSetChanged();
            }
        });

        textViewGadgetName.setText(gadget.getName());

        startStopSocketBoardCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && (result.getHeaders().code() == 201 || result.getHeaders().code() == 204 || result.getHeaders().code() == 200) ) {
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonObject obj = element.getAsJsonObject();
                    if(!obj.has("maxTime")){    // this is a response to "stop socketboard" if no field "maxTime" present
                        appState.requestUserDataUpdate();
                    }
                }
                else{}
            }
        };

        return convertView;
    }

    public void handleLongPress(int position, View view){
        if(listSelectedRows.contains(view)){
            listSelectedRows.remove(view);
            listGadgetsSelected.remove(listGadgets.get(position));
            view.setBackgroundResource(R.color.colorWhite);
        }else{
            listGadgetsSelected.add(listGadgets.get(position));
            listSelectedRows.add(view);
            view.setBackgroundResource(R.color.colorDarkGray);
        }
    }

    public List<Gadget> getListGadgetsSelected() {
        return listGadgetsSelected;
    }

    public void setListGadgetsSelected(List<Gadget> listGadgetsSelected) {
        this.listGadgetsSelected = listGadgetsSelected;
    }

    public void removeSelectedGadgets(){
        listGadgets.removeAll(listGadgetsSelected);
        listGadgetsSelected.clear();
        for(View view : listSelectedRows)
            view.setBackgroundResource(R.color.colorWhite);
        listSelectedRows.clear();
    }

    public void removeGadget(int gadgetId){
        boolean gadgetFound = false;
        int i = 0;
        for(i = 0; i<listGadgetsSelected.size(); ++i){
            if(listGadgetsSelected.get(i).getId() == gadgetId){
                gadgetFound = true;
                break;
            }
        }
        if(gadgetFound == false){
            return;
        }
        else{
            listGadgets.remove(listGadgetsSelected.get(i));
            listGadgetsSelected.remove(i);
            listSelectedRows.get(i).setBackgroundResource(R.color.colorWhite);
            listSelectedRows.remove(i);
        }
    }

    public void setItem(Gadget gadget){
        int i = 0;
        boolean gadgetFound = false;
        for(i = 0; i<listGadgets.size(); ++i){
            if(listGadgets.get(i).getId() == gadget.getId()){
                gadgetFound = true;
                break;
            }
        }
        if(gadgetFound == false){
            return;
        }
        else{
            listGadgets.set(i, gadget);
        }
    }
}
