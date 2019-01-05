package me.rewardi;

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
}
