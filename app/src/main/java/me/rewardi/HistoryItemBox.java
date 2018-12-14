package me.rewardi;

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
}
