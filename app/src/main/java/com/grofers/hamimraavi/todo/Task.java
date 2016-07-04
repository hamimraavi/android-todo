package com.grofers.hamimraavi.todo;

/**
 * Created by Hamim on 01/07/16.
 */
public class Task {
    private String description, scheduled_time;
    private int id;
    Boolean pending;

    public Task() {
    }

    public Task(int id, String description, String scheduled_time, Boolean pending) {
        this.id = id;
        this.description = description;
        this.scheduled_time = scheduled_time;
        this.pending = pending;
    }

    public int getId(){
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String name) {
        this.description = name;
    }

    public String getPending() {
        if(this.pending == false)
            return "Done";
        else
            return "Pending";
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public String getScheduledTime() {
        //return scheduled_time;
        String time = this.scheduled_time;
        time = time.substring(0, 10) + " " + time.substring(11, 16);
        return time;
    }

    public void setScheduledTime(String scheduled_time) {
        this.scheduled_time = scheduled_time;
    }
}
