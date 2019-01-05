package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemEarnedRewardi implements Comparable<HistoryItemEarnedRewardi>{
    private int id;
    private String timestamp;
    private boolean granted;
    private String supervisorMessage;
    private String supervisorName;

    public HistoryItemEarnedRewardi() {}

    @ParcelConstructor
    public HistoryItemEarnedRewardi(int id, String timestamp, boolean granted, String supervisorMessage, String supervisorName){
        this.id = id;
        this.timestamp = timestamp;
        this.granted = granted;
        this.supervisorMessage = supervisorMessage;
        this.supervisorName = supervisorName;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public boolean getGranted() { return granted; }
    public void setGranted(boolean granted) { this.granted = granted; }
    public String getSupervisorMessage() { return supervisorMessage; }
    public void setSupervisorMessage(String supervisorMessage) { this.supervisorMessage = supervisorMessage; }
    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }

    @Override
    public int compareTo(HistoryItemEarnedRewardi historyItemEarnedRewardi) {
        return this.timestamp.compareTo(historyItemEarnedRewardi.getTimestamp());
    }
}
