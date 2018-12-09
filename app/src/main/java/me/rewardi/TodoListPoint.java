package me.rewardi;

public class TodoListPoint {
    private String name;
    private int rewardi;
    private int id;
    private boolean done;

    public TodoListPoint(String name, int rewardi, int id, boolean done){
        this.name = name;
        this.rewardi = rewardi;
        this.id = id;
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

    public boolean isDone() { return done; }

    public void setDone(boolean done) { this.done = done; }
}
