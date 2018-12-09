package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class ManualActivity {
    private int id;
    private String name;
    private int rewardiPerHour;
    private boolean isActive;

    public ManualActivity(){}

    @ParcelConstructor
    public ManualActivity(int id, String name, int rewardiPerHour, boolean isActive){
        this.id = id;
        this.name = name;
        this.rewardiPerHour = rewardiPerHour;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRewardiPerHour() {
        return rewardiPerHour;
    }

    public void setRewardiPerHour(int rewardiPerHour) {
        this.rewardiPerHour = rewardiPerHour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsActive() { return isActive; }

    public void setIsActive(boolean active) { isActive = active; }
}
