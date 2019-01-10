package me.rewardi;

import com.google.gson.JsonObject;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
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

    public static HistoryItemBox parseObject(JsonObject obj) {
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
