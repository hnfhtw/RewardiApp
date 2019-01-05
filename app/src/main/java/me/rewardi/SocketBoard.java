package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class SocketBoard extends Gadget {
    private int rewardiPerHour;
    private int maxTimeSec;
    private boolean isActive;

    public SocketBoard() {}

    @ParcelConstructor
    public SocketBoard(int id, String trustNumber, String name, int rewardiPerHour, int maxTimeSec, boolean isActive){
        super(id, trustNumber, name);
        this.rewardiPerHour = rewardiPerHour;
        this.maxTimeSec = maxTimeSec;
        this.isActive = isActive;
    }

    public int getRewardiPerHour() { return rewardiPerHour; }
    public void setRewardiPerHour(int rewardiPerHour) { this.rewardiPerHour = rewardiPerHour; }
    public int getMaxTimeSec() { return maxTimeSec; }
    public void setMaxTimeSec(int maxTimeSec) { this.maxTimeSec = maxTimeSec; }
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean active) { isActive = active; }
}
