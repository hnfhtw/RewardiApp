package me.rewardi;

public class SocketBoard extends Gadget {
    private int rewardiPerHour;
    private int maxTimeSec;
    private boolean isActive;

    public SocketBoard(int id, String trustNumber, String name, int rewardiPerHour, int maxTimeSec){
        super(id, trustNumber, name);
        this.rewardiPerHour = rewardiPerHour;
        this.maxTimeSec = maxTimeSec;
    }

    public int getRewardiPerHour() { return rewardiPerHour; }

    public void setRewardiPerHour(int rewardiPerHour) { this.rewardiPerHour = rewardiPerHour; }

    public int getMaxTimeSec() { return maxTimeSec; }

    public void setMaxTimeSec(int maxTimeSec) { this.maxTimeSec = maxTimeSec; }

    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { isActive = active; }
}
