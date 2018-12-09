package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class TodoListPoint {
    private String name;
    private int rewardi;
    private int id;
    private boolean done;

    public TodoListPoint() {}

    @ParcelConstructor
    public TodoListPoint(int id, String name, int rewardi, boolean done){
        this.id = id;
        this.name = name;
        this.rewardi = rewardi;
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRewardi() {
        return rewardi;
    }

    public void setRewardi(int rewardi) {
        this.rewardi = rewardi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getDone() { return done; }

    public void setDone(boolean done) { this.done = done; }
}
