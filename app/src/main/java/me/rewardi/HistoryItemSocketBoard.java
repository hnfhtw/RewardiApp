package me.rewardi;

import com.google.gson.JsonObject;

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

    public static HistoryItemSocketBoard parseObject(JsonObject obj) {
        int id = obj.get("id").getAsInt();
        String timestamp = obj.get("timestamp").getAsString();
        if(obj.has("fkSocket")) {        // SocketBoard
            JsonObject socketObj = obj.get("fkSocket").getAsJsonObject();
            int sockId = socketObj.get("id").getAsInt();
            String trustNum = socketObj.get("trustNo").getAsString();
            String name = socketObj.get("name").getAsString();
            int rewardiPerHour = socketObj.get("rewardiPerHour").getAsInt();
            int maxTime = socketObj.get("maxTime").getAsInt();

            SocketBoard socket = new SocketBoard(sockId, trustNum, name, rewardiPerHour, maxTime, false, null);
            int duration = obj.get("duration").getAsInt();
            boolean timeout = obj.get("timeout").getAsBoolean();
            double usedRewardi = obj.get("usedRewardi").getAsDouble();
            HistoryItemSocketBoard historyItemSocketBoard = new HistoryItemSocketBoard(id, socket, timestamp, duration, timeout, usedRewardi);
            return historyItemSocketBoard;
        }
        else{
            return null;
        }
    }
}
