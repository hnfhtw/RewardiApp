package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemTodoListPoint {
    private int id;
    private TodoListPoint todoListPoint;
    private String timestamp;
    private int acquiredRewardi;

    public HistoryItemTodoListPoint() {}

    @ParcelConstructor
    public HistoryItemTodoListPoint(int id, TodoListPoint todoListPoint, String timestamp, int acquiredRewardi){
        this.id = id;
        this.todoListPoint = todoListPoint;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TodoListPoint getTodoListPoint() {
        return todoListPoint;
    }

    public void setTodoListPoint(TodoListPoint todoListPoint) {
        this.todoListPoint = todoListPoint;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getAcquiredRewardi() {
        return acquiredRewardi;
    }

    public void setAcquiredRewardi(int acquiredRewardi) {
        this.acquiredRewardi = acquiredRewardi;
    }
}
