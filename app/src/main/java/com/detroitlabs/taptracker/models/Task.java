/*
 * Copyright 2018 Jason Petterson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.detroitlabs.taptracker.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "task_table",
        indices = {@Index(value = {"task"}, unique = true)})
public class Task implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    private int id;

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

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTask(@NonNull String task) {
        this.task = task;
    }

    @NonNull
    public String getTask() {
        return task;
    }

    public void setLastCompletedTime(@NonNull Date lastCompletedTime) {
        this.lastCompletedTime = lastCompletedTime;
        this.history.add(0, lastCompletedTime);
    }

    public Date getLastCompletedTime() {
        return lastCompletedTime;
    }

    public void setHistory(@NonNull List<Date> history) {
        this.history = history;
    }

    public List<Date> getHistory() {
        return history;
    }

    public void touch() {
        setLastCompletedTime(new Date());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task1 = (Task) o;
        return Objects.equals(task, task1.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(task);
        out.writeLong(lastCompletedTime.getTime());

        long[] historyArray = new long[history.size()];
        for (int i = 0; i < history.size(); ++i) {
            historyArray[i] = history.get(i).getTime();
        }
        out.writeLongArray(historyArray);
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    private Task(Parcel in) {
        this.id = in.readInt();
        this.task = Objects.requireNonNull(in.readString());
        this.lastCompletedTime = new Date(in.readLong());

        history.clear();
        long[] historyArray = in.createLongArray();
        for (long time : Objects.requireNonNull(historyArray)) {
            history.add(new Date(time));
        }
    }
}
