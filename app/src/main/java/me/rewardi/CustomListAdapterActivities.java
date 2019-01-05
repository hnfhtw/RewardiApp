package me.rewardi;

import android.content.Context;
import android.os.CountDownTimer;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class CustomListAdapterActivities extends BaseAdapter {
    private List<ManualActivity> listActivitiesSelected;//keep track of selected objects
    private List<View> listSelectedRows;//keep track of selected rows
    private List<ManualActivity> listActivities;
    private Context context;
    Globals appState;
    private int layoutResId;
    FutureCallback<Response<String>> startStopActivityCallback;

    public CustomListAdapterActivities(Context context, List<ManualActivity> listActivities, int layoutResId) {
        this.listActivities = listActivities;
        this.context = context;
        listActivitiesSelected = new ArrayList<>();
        listSelectedRows = new ArrayList<>();
        this.layoutResId = layoutResId;
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView textViewActive;
        final CountDownTimer activityTimer;

        convertView = LayoutInflater.from(context).inflate(layoutResId, null);
        final ManualActivity activity = listActivities.get(position);
        TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView textViewRewardi = (TextView) convertView.findViewById(R.id.textViewRewardi);

        if(layoutResId == R.layout.custom_row_activities){
            textViewActive = (TextView) convertView.findViewById(R.id.textViewActive);
        }
        final ToggleButton btnStartStop = (ToggleButton) convertView.findViewById(R.id.btnStartStop);

        if(activity.getIsActive()){
            btnStartStop.setChecked(true);
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
        textViewRewardi.setText("Earn: "+Integer.toString(activity.getRewardiPerHour())+" Rewardi per Hour");
        if(layoutResId == R.layout.custom_row_activities) {
            if (activity.getIsActive()) {
                textViewActive.setText("Active since: " + activity.getActiveSince());
            } else {
                textViewActive.setText("Not active");
            }
        }

        startStopActivityCallback = new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                if (e == null && (result.getHeaders().code() == 201 || result.getHeaders().code() == 204 || result.getHeaders().code() == 200) ) {
                    JsonElement element = new JsonParser().parse(result.getResult());
                    JsonObject obj = element.getAsJsonObject();
                    Log.d("ManAct", "Response Object = " + obj.toString());
                    Log.d("ManAct", "Clicked Object = " + activity.toString());

                    if(obj.has("fkActivity")){      // response to stop activity contains activity history object with embedded activity object
                        obj = obj.getAsJsonObject("fkActivity");
                    }

                    int id = obj.get("id").getAsInt();
                    String activityName = obj.get("name").getAsString();
                    int rewardiPerHour = obj.get("rewardiPerHour").getAsInt();
                    boolean isActive = false;
                    String activeSince = null;
                    if(obj.get("activeSince").isJsonNull() == false){
                        isActive = true;
                        activeSince = obj.get("activeSince").getAsString();
                    }
                    ManualActivity manualActivity = new ManualActivity(id, activityName, rewardiPerHour, isActive, activeSince);
                    setItem(manualActivity);

                    if(layoutResId == R.layout.custom_row_activities) {
                        if (manualActivity.getIsActive()) {       // true if activity was started by button click
                            textViewActive.setText("Active since: " + activity.getActiveSince());
                            activityTimer.start();
                        } else {
                            textViewActive.setText("Not active");
                            activityTimer.cancel();
                        }
                    }

                    notifyDataSetChanged();
                }
                else{
                    Log.d("ManAct", "Response Code = " + Integer.toString(result.getHeaders().code()));
                }
            }
        };

        final long totalSeconds = 6000000;
        final long intervalSeconds = 1;

        activityTimer = new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Date elapsedTime = new Date();

            public void onTick(long millisUntilFinished) {
                //Log.d("seconds elapsed: " , (totalSeconds * 1000 - millisUntilFinished) / 1000);
                elapsedTime.setTime(totalSeconds * 1000 - millisUntilFinished);
                textViewActive.setText("Active since: " + formatter.format(elapsedTime));
            }

            public void onFinish() {
                //Log.d( "done!", "Time's up!");
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
            listActivities.remove(listActivitiesSelected.get(i));
            listActivitiesSelected.remove(i);
            listSelectedRows.get(i).setBackgroundResource(R.color.colorWhite);
            listSelectedRows.remove(i);
        }
    }

    public void setItem(ManualActivity act){
        int i = 0;
        boolean activityFound = false;
        for(i = 0; i<listActivities.size(); ++i){
            if(listActivities.get(i).getId() == act.getId()){
                activityFound = true;
                break;
            }
        }
        if(activityFound == false){
            return;
        }
        else{
            listActivities.set(i, act);
        }
    }
}