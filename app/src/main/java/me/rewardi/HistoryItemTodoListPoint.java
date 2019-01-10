package me.rewardi;

import com.google.gson.JsonObject;

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
    public void setTodoListPoint(TodoListPoint todoListPoint) { this.todoListPoint = todoListPoint; }
    public int getAcquiredRewardi() {
        return acquiredRewardi;
    }
    public void setAcquiredRewardi(int acquiredRewardi) {
        this.acquiredRewardi = acquiredRewardi;
    }

    public static HistoryItemTodoListPoint parseObject(JsonObject obj) {
        int id = obj.get("id").getAsInt();
        JsonObject todoListPointObj = obj.get("fkToDo").getAsJsonObject();
        String name = todoListPointObj.get("name").getAsString();
        int todoListPointId = todoListPointObj.get("id").getAsInt();
        int rewardi = todoListPointObj.get("rewardi").getAsInt();
        TodoListPoint todoListPoint = new TodoListPoint(todoListPointId, name, rewardi, true);
        String timestamp = obj.get("timestamp").getAsString();
        int acquiredRewardi = obj.get("acquiredRewardi").getAsInt();

        boolean supervised = false;
        if(!obj.get("fkSupervisorId").isJsonNull()){    // user has a supervisor
            supervised = true;
        }

        boolean granted = true;
        String supervisorMessage = "";
        String supervisorName = "";
        if(supervised){
            if(!obj.get("granted").isJsonNull()){
                granted = obj.get("granted").getAsBoolean();
            }else{
                granted = false;
            }
            if(!obj.get("remark").isJsonNull()){
                supervisorMessage = obj.get("remark").getAsString();
            }
            supervisorName = obj.get("fkSupervisor").getAsJsonObject().get("fkAspNetUsers").getAsJsonObject().get("userName").getAsString();
        }

        HistoryItemTodoListPoint historyItemTodoListPoint = new HistoryItemTodoListPoint(id, todoListPoint, timestamp, acquiredRewardi, granted, supervisorMessage, supervisorName);
        return historyItemTodoListPoint;
    }
}
