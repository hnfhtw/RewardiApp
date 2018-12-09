package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

class CustomListAdapterGadgets extends BaseAdapter {
    private List<Gadget> listGadgetsSelected;//keep track of selected objects
    private List<View> listSelectedRows;//keep track of selected rows
    private List<Gadget> listGadgets;
    private Context context;
    Globals appState;

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

    public void addItem(Gadget gadget){
        listGadgets.add(gadget);
    }

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
        final ToggleButton btnStartStop = (ToggleButton) convertView.findViewById(R.id.btnStartStop);
        if(gadget.getTrustNumber().charAt(0) == '1') {       // Box
            btnStartStop.setText("Lock Box");
            btnStartStop.setTextOff("Lock Box");
            btnStartStop.setTextOn("Box Locked");
            if(((Box)gadget).isLocked()){
                btnStartStop.setChecked(true);
                btnStartStop.setEnabled(false);
            }
        }
        else if(gadget.getTrustNumber().charAt(0) == '2'){  // SocketBoard
            btnStartStop.setText("Switch On");
            btnStartStop.setTextOff("Switch On");
            btnStartStop.setTextOn("Switch Off");
            if(((SocketBoard)gadget).isActive()){
                btnStartStop.setChecked(true);
            }
        }
        btnStartStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(gadget.getTrustNumber().charAt(0) == '1') {       // Box
                    if(btnStartStop.isChecked()){
                        btnStartStop.setEnabled(false);
                        appState = ((Globals)context.getApplicationContext());
                        appState.sendMessageToServer(Globals.messageID.BOX_LOCK, ((Box)gadget).getId(),null, null);
                        ((Box)gadget).setLocked(true);
                    }
                }
                else if(gadget.getTrustNumber().charAt(0) == '2'){  // SocketBoard
                    if(btnStartStop.isChecked()){
                        JsonObject data = new JsonObject();
                        data.addProperty("maxTime", ((SocketBoard)gadget).getMaxTimeSec());
                        //data.addProperty("maxTime", 3600);
                        appState = ((Globals)context.getApplicationContext());
                        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_START, ((SocketBoard)gadget).getId(),data, null);
                        ((SocketBoard)gadget).setActive(true);
                    }
                    else{
                        appState = ((Globals)context.getApplicationContext());
                        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_STOP, ((SocketBoard)gadget).getId(),null, null);
                        ((SocketBoard)gadget).setActive(false);
                    }
                }
            }
        });

        textViewGadgetName.setText(gadget.getName());
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
}
