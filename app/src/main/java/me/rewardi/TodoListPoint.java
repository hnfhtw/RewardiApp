/********************************************************************************************
 * Project    : Rewardi
 * Created on : 12/2018 - 01/2019
 * Author     : Harald Netzer
 * Version    : 001
 *
 * File       : TodoListPoint.java
 * Purpose    : Representation of a Rewardi TodoList Point;
 ********************************************************************************************/

package me.rewardi;

import com.google.gson.JsonObject;

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

    public static TodoListPoint parseObject(JsonObject obj) {   // parse a JsonObject received from the server to a TodoListPoint object
        int id = obj.get("id").getAsInt();
        String pointName = obj.get("name").getAsString();
        int rewardi = obj.get("rewardi").getAsInt();
        boolean done = obj.get("done").getAsBoolean();

        TodoListPoint todoListPoint = new TodoListPoint(id, pointName, rewardi, done);
        return todoListPoint;
    }
}
