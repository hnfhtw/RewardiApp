/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : SocketBoard.java
 * Purpose    : Representation of a Rewardi SocketBoard;
 *              Inherits (properties ID, trustNumber and Name) from super class Gadget
 ********************************************************************************************/

package me.rewardi;

import android.util.Log;

import com.google.gson.JsonObject;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class SocketBoard extends Gadget {
    private int rewardiPerHour;
    private int maxTimeSec;
    private boolean isActive;
    private String activeSince;

    public SocketBoard() {}

    @ParcelConstructor
    public SocketBoard(int id, String trustNumber, String name, int rewardiPerHour, int maxTimeSec, boolean isActive, String activeSince){
        super(id, trustNumber, name);
        this.rewardiPerHour = rewardiPerHour;
        this.maxTimeSec = maxTimeSec;
        this.isActive = isActive;
        this.activeSince = activeSince;
        Log.d("SocketBoard", "SocketBoard constructor: ID = " + id + ", trustNumber = " + trustNumber + ", name = " + name + ", rewardiPerHour = " + rewardiPerHour + ", maxTime = " + maxTimeSec + ", isActive = " + isActive + ", activeSince = " + activeSince);
    }

    public int getRewardiPerHour() { return rewardiPerHour; }
    public void setRewardiPerHour(int rewardiPerHour) { this.rewardiPerHour = rewardiPerHour; }
    public int getMaxTimeSec() { return maxTimeSec; }
    public void setMaxTimeSec(int maxTimeSec) { this.maxTimeSec = maxTimeSec; }
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean active) { isActive = active; }
    public String getActiveSince() { return activeSince; }
    public void setActiveSince(String activeSince) { this.activeSince = activeSince; }

    public static SocketBoard parseObject(JsonObject obj) { // parse a JsonObject received from the server to a SocketBoard object
        int id = obj.get("id").getAsInt();
        String trustNumber = obj.get("trustNo").getAsString();
        String name = obj.get("name").getAsString();
        if (trustNumber.charAt(0) == '2') {        // SocketBoard
            int rewardiPerHour = obj.get("rewardiPerHour").getAsInt();
            int maxTime = obj.get("maxTime").getAsInt();
            boolean isActive = false;
            String activeSince = null;
            if (obj.get("usedSince").isJsonNull() == false) {
                isActive = true;
                activeSince = obj.get("usedSince").getAsString();
            }

            SocketBoard socketBoard = new SocketBoard(id, trustNumber, name, rewardiPerHour, maxTime, isActive, activeSince);
            return socketBoard;
        } else {
            return null;
        }
    }
}
