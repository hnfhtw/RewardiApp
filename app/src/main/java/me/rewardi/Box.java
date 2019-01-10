package me.rewardi;

import com.google.gson.JsonObject;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class Box extends Gadget {
    private int rewardiPerOpen;
    private boolean isLocked;

    public Box() {}

    @ParcelConstructor
    public Box(int id, String trustNumber, String name, int rewardiPerOpen, boolean isLocked){
        super(id, trustNumber, name);
        this.rewardiPerOpen = rewardiPerOpen;
        this.isLocked = isLocked;
    }

    public int getRewardiPerOpen() {
        return rewardiPerOpen;
    }
    public void setRewardiPerOpen(int rewardiPerOpen) {
        this.rewardiPerOpen = rewardiPerOpen;
    }
    public boolean getIsLocked() { return isLocked; }
    public void setIsLocked(boolean locked) { isLocked = locked; }

    public static Box parseObject(JsonObject obj) {
        int id = obj.get("id").getAsInt();
        String trustNumber = obj.get("trustNo").getAsString();
        String name = obj.get("name").getAsString();
        if(trustNumber.charAt(0) == '1') {   // Box
            int rewardiPerOpen = obj.get("rewardiPerOpen").getAsInt();
            boolean isLocked = obj.get("isLocked").getAsBoolean();
            Box box = new Box(id, trustNumber, name, rewardiPerOpen, isLocked);
            return box;
        } else {
            return null;
        }
    }
}
