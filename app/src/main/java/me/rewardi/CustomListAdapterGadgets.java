/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : CustomListAdapterGadgets.java
 * Purpose    : The activity Gadgets lists all Gadgets of the current user in a ListView.
 *              This ListView ist managed by a CustomListAdapterGadgets.
 ********************************************************************************************/

package me.rewardi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class CustomListAdapterGadgets extends BaseAdapter {
    private List<Gadget> listGadgetsSelected;   // keep track of selected Gadget objects
    private List<View> listSelectedRows;        // keep track of selected rows
    private List<Gadget> listGadgets;           // list containing all Gadget objects of the current user
    private List<ActivityTimer> listTimers;     // list containing an Activity timer for each switched-on SocketBoard
    private Context context;
    Globals appState;
    TextView textViewActive;
    ActivityTimer timer;

    public CustomListAdapterGadgets(Context context, List<Gadget> listGadgets) {
        this.listGadgets = listGadgets;
        this.context = context;
        listGadgetsSelected = new ArrayList<>();
        listSelectedRows = new ArrayList<>();
        this.listTimers = new ArrayList<ActivityTimer>();
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
    }   // add a Box to the ListView

    public void addItem(SocketBoard socketBoard){   // add a SocketBoard to the ListView
        listGadgets.add(socketBoard);

        long startValueMilis = 0;
        if(socketBoard.getIsActive()){  // check if the SocketBoard is currently switched on -> if yes, preload and start the ActivityTimer to show the run-time in the corresponding UI element
            String activeSince = socketBoard.getActiveSince().substring(0,18);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            try {
                Date date = format.parse(activeSince);
                Date currentTime = Calendar.getInstance().getTime();
                startValueMilis = currentTime.getTime() - date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(startValueMilis < 0){
                startValueMilis = 0;
            }
        }
        listTimers.add(new ActivityTimer(6000000,1, startValueMilis, socketBoard.getId()));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_gadgets, null);
        final Gadget gadget = listGadgets.get(position);
        TextView textViewGadgetName = (TextView) convertView.findViewById(R.id.textViewGadgetName);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);
        textViewActive = (TextView) convertView.findViewById(R.id.textViewActive);
        final ToggleButton btnStartStop = (ToggleButton) convertView.findViewById(R.id.btnStartStop);
        Log.d("getViewSocketBoard", "getView gadget name = " + gadget.getName());
        if(gadget instanceof Box) { // send UI elements correctly depending on type of Gadget
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
            SocketBoard socketBoard = (SocketBoard) gadget;
            timer = getTimer(gadget.getId());
            if(timer != null){
                timer.setOutputTextView(textViewActive);
            }

            btnStartStop.setText("Switch On");
            btnStartStop.setTextOff("Switch On");
            btnStartStop.setTextOn("Switch Off");
            textViewRewardi.setText("Cost: " + Integer.toString(((SocketBoard)gadget).getRewardiPerHour()) + " Rewardi / Hour");
            if(socketBoard.getIsActive()){
                btnStartStop.setChecked(true);

                String activeSince = socketBoard.getActiveSince().substring(0,19);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                long startValueMilis = 0;
                try {
                    Date date = format.parse(activeSince);
                    Date currentTime = Calendar.getInstance().getTime();
                    startValueMilis = currentTime.getTime() - date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(startValueMilis < 0){
                    startValueMilis = 0;
                }
                timer.setStartValueMilis(startValueMilis);
                timer.start();       // start timer as socket board already switched ON
            }
            else{
                textViewActive.setText("Socket Board switched OFF");
            }
        }

        appState = ((Globals)context.getApplicationContext());
        btnStartStop.setOnClickListener(new GadgetOnButtonClickListener(this,gadget,textViewActive, btnStartStop, appState));

        textViewGadgetName.setText(gadget.getName());

        return convertView;
    }

    public void handleLongPress(int position, View view){   // on a long press the ListView rows are highlighted -> the can then either be unselected with another long press or deleted by the Delete button
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
            removeTimer(listGadgetsSelected.get(i).getId());
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

    public Gadget getGadget(int id){
        int i = 0;
        boolean gadgetFound = false;
        for(i = 0; i<listGadgets.size(); ++i){
            if(listGadgets.get(i).getId() == id){
                gadgetFound = true;
                break;
            }
        }
        if(gadgetFound == false){
            return null;
        }
        else{
            return listGadgets.get(i);
        }
    }

    public ActivityTimer getTimer(int idOfSocketBoard){
        int i = 0;
        boolean timerFound = false;
        for(i = 0; i<listTimers.size(); ++i){
            if(listTimers.get(i).getIdOfActivity() == idOfSocketBoard){
                timerFound = true;
                break;
            }
        }
        if(timerFound == false){
            return null;
        }
        else{
            return listTimers.get(i);
        }
    }

    public boolean removeTimer(int idOfSocketBoard){
        int i = 0;
        boolean timerFound = false;
        for(i = 0; i<listTimers.size(); ++i){
            if(listTimers.get(i).getIdOfActivity() == idOfSocketBoard){
                timerFound = true;
                break;
            }
        }
        if(timerFound == false){
            return false;
        }
        else{
            listTimers.remove(i);
            return true;
        }
    }
}
