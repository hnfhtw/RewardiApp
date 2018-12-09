package me.rewardi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

class CustomListAdapterActivities extends BaseAdapter {
    private List<ManualActivity> listActivitiesSelected;//keep track of selected objects
    private List<View> listSelectedRows;//keep track of selected rows
    private List<ManualActivity> listActivities;
    private Context context;
    Globals appState;

    public CustomListAdapterActivities(Context context, List<ManualActivity> listActivities) {
        this.listActivities = listActivities;
        this.context = context;
        listActivitiesSelected = new ArrayList<>();
        listSelectedRows = new ArrayList<>();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.custom_row_activities, null);
        final ManualActivity activity = listActivities.get(position);
        TextView text1 = (TextView) convertView.findViewById(R.id.text1);
        final ToggleButton btnStartStop = (ToggleButton) convertView.findViewById(R.id.btnStartStop);

        if(activity.getIsActive()){
            btnStartStop.setChecked(true);
        }

        appState = ((Globals)context.getApplicationContext());
        btnStartStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(btnStartStop.isChecked()){
                    appState.sendMessageToServer(Globals.messageID.ACTIVITY_START,activity.getId(),null, null);
                    activity.setIsActive(true);
                }
                else{
                    appState.sendMessageToServer(Globals.messageID.ACTIVITY_STOP, activity.getId(),null, null);
                    activity.setIsActive(false);
                }
            }
        });

        text1.setText(activity.getName());

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
