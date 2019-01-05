package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemGadget implements Comparable<HistoryItemGadget>{
    private int id;
    private String timestamp;

    public HistoryItemGadget() {}

    @ParcelConstructor
    public HistoryItemGadget(int id, String timestamp){
        this.id = id;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(HistoryItemGadget historyItemGadget) {
        return this.timestamp.compareTo(historyItemGadget.getTimestamp());
    }
}
