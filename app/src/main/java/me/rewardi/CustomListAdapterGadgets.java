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

import java.net.Socket;
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
    private List<ActivityTimer> listTimers;
    private Context context;
    Globals appState;
    FutureCallback<Response<String>> startStopSocketBoardCallback;
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
    }

    public void addItem(SocketBoard socketBoard){
        listGadgets.add(socketBoard);

        long startValueMilis = 0;
        if(socketBoard.getIsActive()){
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
            SocketBoard socketBoard = (SocketBoard) gadget;
            timer = getTimer(gadget.getId());
            if(timer != null){
                timer.setOutputText(textViewActive);
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
                        //((SocketBoard)gadget).setIsActive(true);
                    }
                    else{
                        appState = ((Globals)context.getApplicationContext());
                        appState.sendMessageToServer(Globals.messageID.SOCKETBOARD_STOP, ((SocketBoard)gadget).getId(),null, startStopSocketBoardCallback);
                        //((SocketBoard)gadget).setIsActive(false);
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

                    if (element.isJsonNull()) {   // no data in STOP SOCKETBOARD message -> stop timer and set TextView appropriately - then return.
                        appState.requestUserDataUpdate();
                        ((SocketBoard) gadget).setIsActive(false);
                        textViewActive.setText("Socket Board switched OFF");
                        ActivityTimer tim1 = getTimer(((SocketBoard) gadget).getId());
                        if (tim1 != null) {
                            tim1.cancel();
                            tim1.setStartValueMilis(0);
                        }
                        return;
                    } else {                       // if response contains payload it is a START SOCKETBOARD message -> parse object values (not really necessary actually), update object, start timer and set TextView appropriately
                        JsonObject obj = element.getAsJsonObject();
                        Log.d("SocketBoard", "startStopSocketBoardCallback Response Object = " + obj.toString());

                        int id = obj.get("id").getAsInt();
                        String trustNumber = obj.get("trustNo").getAsString();
                        String socketBoardName = obj.get("name").getAsString();
                        int rewardiPerHour = obj.get("rewardiPerHour").getAsInt();
                        int maxTime = obj.get("maxTime").getAsInt();

                        boolean isActive = false;
                        String activeSince = null;
                        if (obj.get("usedSince").isJsonNull() == false) {
                            isActive = true;
                            activeSince = obj.get("usedSince").getAsString();
                        }
                        SocketBoard socketBoard = new SocketBoard(id, trustNumber, socketBoardName, rewardiPerHour, maxTime, isActive, activeSince);
                        setItem((Gadget) socketBoard);
                        ActivityTimer tim = getTimer(id);

                        if (socketBoard.getIsActive()) {
                            String actSince = activeSince.substring(0, 19);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            long startValueMilis = 0;
                            try {
                                Date date = format.parse(actSince);
                                Date currentTime = Calendar.getInstance().getTime();
                                startValueMilis = currentTime.getTime() - date.getTime();
                                Log.d("SocketBoard", "ActSince = " + actSince);
                                Log.d("SocketBoard", "Current Time = " + currentTime);
                                Log.d("SocketBoard", "Start Value in ms = " + startValueMilis);
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            if (startValueMilis < 0) {
                                startValueMilis = 0;
                            }

                            if (tim != null) {
                                tim.setStartValueMilis(startValueMilis);
                                tim.start();
                            }

                        } else {
                            textViewActive.setText("Socket Board switched OFF");
                            if (tim != null) {
                                tim.cancel();
                                tim.setStartValueMilis(0);
                            }
                        }
                        notifyDataSetChanged();
                    }
                }
                else{
                    //Log.d("SocketBoard", "startStopSocketBoardCallback Response Header = " + result.getHeaders().code());
                }
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
