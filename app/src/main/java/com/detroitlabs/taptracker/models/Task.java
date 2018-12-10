package com.detroitlabs.taptracker.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "task")
    private String task;

    @ColumnInfo(name = "last_completed")
    private Date lastCompletedTime;

    @ColumnInfo(name = "history")
    private List<Date> history = new ArrayList<>();

    public Task(@NonNull String task) {
        this.task = task;
    }

    @NonNull
    public String getTask() {
        return task;
    }

    public Date getLastCompletedTime() {
        return lastCompletedTime;
    }

    public void setLastCompletedTime(Date lastCompletedTime) {
        this.lastCompletedTime = lastCompletedTime;
    }

    public List<Date> getHistory() {
        return history;
    }

    public void setHistory(List<Date> history) {
        this.history = history;
    }

    public void touch() {
        if (lastCompletedTime != null) {
            history.add(lastCompletedTime);
        }
        lastCompletedTime = new Date();
    }

    // for testing
    private static final String TAG = Task.class.getName();
    public void DEBUG__logHistory() {
        Log.d(TAG, String.format("History for \'%s\'", task));
        for (Date d : history) {
            Log.d(TAG, "\t> " + d.toString());
        }
    }
}
