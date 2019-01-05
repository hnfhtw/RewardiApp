package me.rewardi;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel(Parcel.Serialization.BEAN)
public class HistoryItemTodoListPoint extends HistoryItemEarnedRewardi {
    private TodoListPoint todoListPoint;
    private int acquiredRewardi;

    public HistoryItemTodoListPoint() {}

    @ParcelConstructor
    public HistoryItemTodoListPoint(int id, TodoListPoint todoListPoint, String timestamp, int acquiredRewardi, boolean granted, String supervisorMessage, String supervisorName){
        super(id, timestamp, granted, supervisorMessage, supervisorName);
        this.todoListPoint = todoListPoint;
        this.acquiredRewardi = acquiredRewardi;
    }

    public TodoListPoint getTodoListPoint() {
        return todoListPoint;
    }

    public void setTodoListPoint(TodoListPoint todoListPoint) {
        this.todoListPoint = todoListPoint;
    }

    public int getAcquiredRewardi() {
        return acquiredRewardi;
    }

    public void setAcquiredRewardi(int acquiredRewardi) {
        this.acquiredRewardi = acquiredRewardi;
    }
}
