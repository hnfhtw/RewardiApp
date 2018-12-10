package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemSocketBoard {
    private int id;
    private int fkSocketId;
    private String timestamp;
    private int duration;
    private boolean timeout;
    private int usedRewardi;

    public HistoryItemSocketBoard() {}

    @ParcelConstructor
    public HistoryItemSocketBoard(int id, int fkSocketId, String timestamp, int duration, boolean timeout, int usedRewardi){
        this.id = id;
        this.fkSocketId = fkSocketId;
        this.timestamp = timestamp;
        this.duration = duration;
        this.timeout = timeout;
        this.usedRewardi = usedRewardi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFkSocketId() {
        return fkSocketId;
    }

    public void setFkSocketId(int fkSocketId) {
        this.fkSocketId = fkSocketId;
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

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public int getUsedRewardi() {
        return usedRewardi;
    }

    public void setUsedRewardi(int usedRewardi) {
        this.usedRewardi = usedRewardi;
    }
}
