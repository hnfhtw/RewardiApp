package me.rewardi;

import com.google.gson.JsonObject;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class ManualActivity {
    private int id;
    private String name;
    private int rewardiPerHour;
    private boolean isActive;
    private String activeSince;

    public ManualActivity(){}

    @ParcelConstructor
    public ManualActivity(int id, String name, int rewardiPerHour, boolean isActive, String activeSince){
        this.id = id;
        this.name = name;
        this.rewardiPerHour = rewardiPerHour;
        this.isActive = isActive;
        this.activeSince = activeSince;
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
    public String getActiveSince() {
        return activeSince;
    }
    public void setActiveSince(String activeSince) {
        this.activeSince = activeSince;
    }

    public static ManualActivity parseObject(JsonObject obj) {
        int id = obj.get("id").getAsInt();
        String activityName = obj.get("name").getAsString();
        int rewardiPerHour = obj.get("rewardiPerHour").getAsInt();

        boolean isActive = false;
        String activeSince = null;
        if(obj.get("activeSince").isJsonNull() == false){
            isActive = true;
            activeSince = obj.get("activeSince").getAsString();

        }
        ManualActivity manualActivity = new ManualActivity(id, activityName, rewardiPerHour, isActive, activeSince);
        return manualActivity;
    }
}
