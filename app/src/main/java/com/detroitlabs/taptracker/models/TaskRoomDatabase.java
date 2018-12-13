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

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.time.temporal.ChronoUnit;
import java.util.Date;

@Database(entities = {Task.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class TaskRoomDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

    private static volatile TaskRoomDatabase INSTANCE;

    static TaskRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskRoomDatabase.class, "task_database")
                            //.addCallback(fillDemoDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     *  This callback is for populating a fresh install with demo data.  It's main use is as
     *  convenience method for updating promo videos and marketing materials.
     */
    @SuppressWarnings("unused")
    private static RoomDatabase.Callback fillDemoDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDemoDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDemoDbAsync extends AsyncTask<Void, Void, Void> {
        private final TaskDao mDao;

        PopulateDemoDbAsync(TaskRoomDatabase db) {
            mDao = db.taskDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDao.deleteAll();

            Date now = new Date();

            Task task = new Task("Take Daily Vitamin");
            task.setLastCompletedTime(new Date(now.toInstant().minus(5, ChronoUnit.MINUTES).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Get oil change");
            task.setLastCompletedTime(new Date(now.toInstant().minus(5, ChronoUnit.MONTHS).toEpochMilli()));
            mDao.insert(task);

            return null;
        }
    }
}
