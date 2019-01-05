package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemSocketBoard extends HistoryItemGadget{
    private SocketBoard socketBoard;
    private int duration;
    private boolean timeout;
    private double usedRewardi;

    public HistoryItemSocketBoard() {}

    @ParcelConstructor
    public HistoryItemSocketBoard(int id, SocketBoard socketBoard, String timestamp, int duration, boolean timeout, double usedRewardi){
        super(id, timestamp);
        this.socketBoard = socketBoard;
        this.duration = duration;
        this.timeout = timeout;
        this.usedRewardi = usedRewardi;
    }

    public SocketBoard getSocketBoard() {
        return socketBoard;
    }
    public void setSocketBoard(SocketBoard socketBoard) {
        this.socketBoard = socketBoard;
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
    public double getUsedRewardi() {
        return usedRewardi;
    }
    public void setUsedRewardi(double usedRewardi) {
        this.usedRewardi = usedRewardi;
    }
}
