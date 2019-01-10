package me.rewardi;

import com.google.gson.JsonObject;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemManualActivity extends HistoryItemEarnedRewardi {
    private ManualActivity activity;
    private int duration;
    private double acquiredRewardi;

    public HistoryItemManualActivity(){}

    @ParcelConstructor
    public HistoryItemManualActivity(int id, ManualActivity activity, String timestamp, int duration, double acquiredRewardi, boolean granted, String supervisorMessage, String supervisorName){
        super(id, timestamp, granted, supervisorMessage, supervisorName);
        this.activity = activity;
        this.duration = duration;
        this.acquiredRewardi = acquiredRewardi;
    }

    public ManualActivity getActivity() {
        return activity;
    }
    public void setActivity(ManualActivity activity) {
        this.activity = activity;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public double getAcquiredRewardi() {
        return acquiredRewardi;
    }
    public void setAcquiredRewardi(double acquiredRewardi) { this.acquiredRewardi = acquiredRewardi; }

    public static HistoryItemManualActivity parseObject(JsonObject obj) {
        int id = obj.get("id").getAsInt();
        JsonObject activityObj = obj.get("fkActivity").getAsJsonObject();
        int activityId = activityObj.get("id").getAsInt();
        String name = activityObj.get("name").getAsString();
        int rewardiPerHour = activityObj.get("rewardiPerHour").getAsInt();
        ManualActivity act = new ManualActivity(activityId, name, rewardiPerHour, false, null);
        String timestamp = obj.get("timestamp").getAsString();
        int duration = obj.get("duration").getAsInt();
        double acquiredRewardi = obj.get("acquiredRewardi").getAsDouble();

        boolean supervised = false;
        if(!obj.get("fkSupervisorId").isJsonNull()){    // user has a supervisor
            supervised = true;
        }

        boolean granted = true;
        String supervisorMessage = "";
        String supervisorName = "";
        if(supervised){
            if(!obj.get("granted").isJsonNull()){
                granted = obj.get("granted").getAsBoolean();
            }else{
                granted = false;
            }
            if(!obj.get("remark").isJsonNull()){
                supervisorMessage = obj.get("remark").getAsString();
            }
            supervisorName = obj.get("fkSupervisor").getAsJsonObject().get("fkAspNetUsers").getAsJsonObject().get("userName").getAsString();
        }

        HistoryItemManualActivity historyItemManualActivity = new HistoryItemManualActivity(id, act, timestamp, duration, acquiredRewardi, granted, supervisorMessage, supervisorName);
        return historyItemManualActivity;
    }
}
