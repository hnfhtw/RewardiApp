package me.rewardi;

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
}
