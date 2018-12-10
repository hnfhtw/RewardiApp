package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemBox {
    private int id;
    private int fkBoxId;
    private String timestamp;
    private int usedRewardi;

    public HistoryItemBox() {}

    @ParcelConstructor
    public HistoryItemBox(int id, int fkBoxId, String timestamp, int usedRewardi){
        this.id = id;
        this.fkBoxId = fkBoxId;
        this.timestamp = timestamp;
        this.usedRewardi = usedRewardi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFkBoxId() {
        return fkBoxId;
    }

    public void setFkBoxId(int fkBoxId) {
        this.fkBoxId = fkBoxId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getUsedRewardi() {
        return usedRewardi;
    }

    public void setUsedRewardi(int usedRewardi) {
        this.usedRewardi = usedRewardi;
    }
}
