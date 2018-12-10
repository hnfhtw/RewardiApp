package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemTodoListPoint {
    private int id;
    private int fkToDoId;
    private String timestamp;

    public HistoryItemTodoListPoint() {}

    @ParcelConstructor
    public HistoryItemTodoListPoint(int id, int fkToDoId, String timestamp){
        this.id = id;
        this.fkToDoId = fkToDoId;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFkToDoId() {
        return fkToDoId;
    }

    public void setFkToDoId(int fkToDoId) {
        this.fkToDoId = fkToDoId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
