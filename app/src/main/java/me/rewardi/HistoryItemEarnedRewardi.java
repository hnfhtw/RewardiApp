/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : HistoryItemEarnedRewardi.java
 * Purpose    : Base class for history items for earned Rewardi (Activities and TodoList Points)
 ********************************************************************************************/

package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)  // to serialize HistoryItemEarnedRewardi object for passing it between activities via intents
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
    public int compareTo(HistoryItemEarnedRewardi historyItemEarnedRewardi) {   // used to list the history items ordered by timestamp
        return this.timestamp.compareTo(historyItemEarnedRewardi.getTimestamp());
    }
}
