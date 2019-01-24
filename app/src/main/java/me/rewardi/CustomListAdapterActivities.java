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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class CustomListAdapterActivities extends BaseAdapter {
    private List<ManualActivity> listActivitiesSelected;//keep track of selected objects
    private List<View> listSelectedRows;//keep track of selected rows
    private List<ManualActivity> listActivities;
    private List<ActivityTimer> listTimers;
    private Context context;
    Globals appState;
    private int layoutResId;
    FutureCallback<Response<String>> startStopActivityCallback;
    TextView textViewActive;

    public CustomListAdapterActivities(Context context, List<ManualActivity> listActivities, int layoutResId) {
        this.listActivities = listActivities;
        this.context = context;
        listActivitiesSelected = new ArrayList<>();
        listSelectedRows = new ArrayList<>();
        this.layoutResId = layoutResId;
        this.listTimers = new ArrayList<ActivityTimer>();
    }

    @Override
    public int getCount() {
        return listActivities.size();
    }

    @Override
    public Object getItem(int position) {
        return listActivities.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addItem(ManualActivity activity){
        listActivities.add(activity);

        long startValueMilis = 0;
        if(activity.getIsActive()){
            String activeSince = activity.getActiveSince().substring(0,18);
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
        listTimers.add(new ActivityTimer(6000000,1, startValueMilis, activity.getId()));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(layoutResId, null);
        final ManualActivity activity = listActivities.get(position);
        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);

        textViewActive = (TextView) convertView.findViewById(R.id.textViewActive);
        final ActivityTimer timer = getTimer(activity.getId());
        if(timer != null){
            timer.setOutputTextView(textViewActive);
        }

        final ToggleButton btnStartStop = (ToggleButton) convertView.findViewById(R.id.btnStartStop);

        if(activity.getIsActive()){
            btnStartStop.setChecked(true);

            String activeSince = activity.getActiveSince().substring(0,19);
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
            timer.start();       // start timer as activity is already running!
        }
        else{
            textViewActive.setText("Not active");
        }

        appState = ((Globals)context.getApplicationContext());
        btnStartStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(btnStartStop.isChecked()){
                    appState.sendMessageToServer(Globals.messageID.ACTIVITY_START,activity.getId(),null, startStopActivityCallback);
                    activity.setIsActive(true);
                }
                else{
                    appState.sendMessageToServer(Globals.messageID.ACTIVITY_STOP, activity.getId(),null, startStopActivityCallback);
                    activity.setIsActive(false);
                }
            }
        });

        textViewName.setText(activity.getName());
        textViewRewardi.setText("Earn: "+Integer.toString(activity.getRewardiPerHour())+" Rewardi / Hour");

        startStopActivityCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && (result.getHeaders().code() == 201 || result.getHeaders().code() == 204 || result.getHeaders().code() == 200) ) {
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonObject obj = element.getAsJsonObject();
                    Log.d("ManAct", "Response Object = " + obj.toString());

                    if(obj.has("fkActivity")){      // response to stop activity contains activity history object with embedded activity object
                        obj = obj.getAsJsonObject("fkActivity");
                        appState.requestUserDataUpdate();
                    }

                    ManualActivity manualActivity = ManualActivity.parseObject(obj);
                    int idx = setItem(manualActivity);
                    ActivityTimer tim = getTimer(manualActivity.getId());

                    if (manualActivity.getIsActive()) {       // true if activity was started by button click

                        String actSince = manualActivity.getActiveSince().substring(0,19);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        long startValueMilis = 0;
                        try {
                            Date date = format.parse(actSince);
                            Date currentTime = Calendar.getInstance().getTime();
                            startValueMilis = currentTime.getTime() - date.getTime();
                            Log.d("ManAct", "ActSince = " + actSince);
                            Log.d("ManAct", "Current Time = " + currentTime);
                            Log.d("ManAct", "Start Value in ms = " + startValueMilis);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                        if(startValueMilis < 0){
                            startValueMilis = 0;
                        }

                        if(tim != null){
                            tim.setStartValueMilis(startValueMilis);
                            tim.start();
                        }

                    } else {
                        textViewActive.setText("Not active");
                        if(tim != null){
                            tim.cancel();
                            tim.setStartValueMilis(0);
                        }

                    }
                    notifyDataSetChanged();
                }
                else{
                    Log.d("ManAct", "Response Code = " + Integer.toString(result.getHeaders().code()));
                }
            }
        };

        return convertView;
    }

    public void handleLongPress(int position, View view){
        if(listSelectedRows.contains(view)){
            listSelectedRows.remove(view);
            listActivitiesSelected.remove(listActivities.get(position));
            view.setBackgroundResource(R.color.colorWhite);
        }else{
            listActivitiesSelected.add(listActivities.get(position));
            listSelectedRows.add(view);
            view.setBackgroundResource(R.color.colorDarkGray);
        }

    }

    public List<ManualActivity> getListActivitiesSelected() {
        return listActivitiesSelected;
    }

    public void setListActivitiesSelected(List<ManualActivity> listActivitiesSelected) {
        this.listActivitiesSelected = listActivitiesSelected;
    }

    public void removeSelectedActivities(){
        listActivities.removeAll(listActivitiesSelected);
        listActivitiesSelected.clear();
        for(View view : listSelectedRows)
            view.setBackgroundResource(R.color.colorWhite);
        listSelectedRows.clear();
    }

    public void removeActivity(int activityId){
        boolean activityFound = false;
        int i = 0;
        for(i = 0; i<listActivitiesSelected.size(); ++i){
            if(listActivitiesSelected.get(i).getId() == activityId){
                activityFound = true;
                break;
            }
        }
        if(activityFound == false){
            return;
        }
        else{
            int idx = listActivities.indexOf(listActivitiesSelected.get(i));
            listActivities.remove(listActivitiesSelected.get(i));
            removeTimer(listActivitiesSelected.get(i).getId());
            listActivitiesSelected.remove(i);
            listSelectedRows.get(i).setBackgroundResource(R.color.colorWhite);
            listSelectedRows.remove(i);
        }
    }

    public int setItem(ManualActivity act){
        int i = 0;
        boolean activityFound = false;
        for(i = 0; i<listActivities.size(); ++i){
            if(listActivities.get(i).getId() == act.getId()){
                activityFound = true;
                break;
            }
        }
        if(activityFound == false){
            return -1;
        }
        else{
            listActivities.set(i, act);
            return i;
        }
    }

    public ActivityTimer getTimer(int idOfActivity){
        int i = 0;
        boolean timerFound = false;
        for(i = 0; i<listTimers.size(); ++i){
            if(listTimers.get(i).getIdOfActivity() == idOfActivity){
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

    public boolean removeTimer(int idOfActivity){
        int i = 0;
        boolean timerFound = false;
        for(i = 0; i<listTimers.size(); ++i){
            if(listTimers.get(i).getIdOfActivity() == idOfActivity){
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