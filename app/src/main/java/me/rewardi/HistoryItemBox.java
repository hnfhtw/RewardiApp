/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : HistoryItemBox.java
 * Purpose    : Representation of a Box History item (information when a certain box was opened,
 *              how many Rewardi were spent, etc...);
 *              Inherits from super class HistoryItemGadget (ID and timestamp of history item)
 ********************************************************************************************/

package me.rewardi;

import com.google.gson.JsonObject;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)  // to serialize HistoryItemBox object for passing it between activities via intents
public class HistoryItemBox extends HistoryItemGadget{
    private Box box;
    private int usedRewardi;

    public HistoryItemBox() {}

    @ParcelConstructor
    public HistoryItemBox(int id, Box box, String timestamp, int usedRewardi){
        super(id, timestamp);
        this.box = box;
        this.usedRewardi = usedRewardi;
    }

    public Box getBox() {
        return box;
    }
    public void setBox(Box box) {
        this.box = box;
    }
    public int getUsedRewardi() {
        return usedRewardi;
    }
    public void setUsedRewardi(int usedRewardi) {
        this.usedRewardi = usedRewardi;
    }

    public static HistoryItemBox parseObject(JsonObject obj) {  // parse a JsonObject received from the server to a HistoryItemBox object
        int id = obj.get("id").getAsInt();
        String timestamp = obj.get("timestamp").getAsString();
        if(obj.has("fkBox")) {   // Box
            JsonObject boxObj = obj.get("fkBox").getAsJsonObject();
            int boxId = boxObj.get("id").getAsInt();
            String trustNum = boxObj.get("trustNo").getAsString();
            String name = boxObj.get("name").getAsString();
            int rewardiPerOpen = boxObj.get("rewardiPerOpen").getAsInt();
            boolean isLocked = boxObj.get("isLocked").getAsBoolean();

            Box box = new Box(boxId, trustNum, name, rewardiPerOpen, isLocked);
            int usedRewardi = obj.get("usedRewardi").getAsInt();
            HistoryItemBox historyItemBox = new HistoryItemBox(id, box, timestamp, usedRewardi);
            return historyItemBox;
        }
        else{
            return null;
        }
    }
}
