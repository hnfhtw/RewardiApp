package me.rewardi;

public class Box extends Gadget {
    private int rewardiPerOpen;
    private boolean isLocked;

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

    public boolean isLocked() { return isLocked; }

    public void setLocked(boolean locked) { isLocked = locked; }
}
