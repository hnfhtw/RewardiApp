package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemManualActivity {
    private int id;
    private int fkActivityId;
    private String timestamp;
    private int duration;
    private int acquiredRewardi;

    public HistoryItemManualActivity(){}

    @ParcelConstructor
    public HistoryItemManualActivity(int id, int fkActivityId, String timestamp, int duration, int acquiredRewardi){
        this.id = id;
        this.fkActivityId = fkActivityId;
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

    public int getFkActivityId() {
        return fkActivityId;
    }

    public void setFkActivityId(int fkActivityId) {
        this.fkActivityId = fkActivityId;
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

    public int getAcquiredRewardi() {
        return acquiredRewardi;
    }

    public void setAcquiredRewardi(int acquiredRewardi) {
        this.acquiredRewardi = acquiredRewardi;
    }
}
