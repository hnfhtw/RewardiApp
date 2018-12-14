package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemManualActivity {
    private int id;
    private ManualActivity activity;
    private String timestamp;
    private int duration;
    private double acquiredRewardi;

    public HistoryItemManualActivity(){}

    @ParcelConstructor
    public HistoryItemManualActivity(int id, ManualActivity activity, String timestamp, int duration, double acquiredRewardi){
        this.id = id;
        this.activity = activity;
        this.timestamp = timestamp;
        this.duration = duration;
        this.acquiredRewardi = acquiredRewardi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ManualActivity getActivity() {
        return activity;
    }

    public void setActivity(ManualActivity activity) {
        this.activity = activity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public void setAcquiredRewardi(double acquiredRewardi) {
        this.acquiredRewardi = acquiredRewardi;
    }
}
