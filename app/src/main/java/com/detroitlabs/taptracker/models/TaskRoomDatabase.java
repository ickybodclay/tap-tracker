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
import android.util.Log;

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
                            //.addCallback(fillDemoDatabaseCallback) // FOR TESTING ONLY TODO add warning toast when this is enabled
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
            Log.w(TaskRoomDatabase.class.getName(), "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            Log.w(TaskRoomDatabase.class.getName(), "!!! FILL DEMO DATABASE CALLBACK IS ACTIVE !!!");
            Log.w(TaskRoomDatabase.class.getName(), "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            // NOTE: when adding new data to demo, keep in mind to add dates in ascending order
            // TODO convert this test task db setup to config file that can be more easily updated

            mDao.deleteAll();

            Date now = new Date();

            Task task = new Task("Drink Water");
            task.setLastCompletedTime(new Date(now.toInstant().minus(30, ChronoUnit.MINUTES).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(25, ChronoUnit.MINUTES).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(20, ChronoUnit.MINUTES).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(15, ChronoUnit.MINUTES).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(10, ChronoUnit.MINUTES).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(61, ChronoUnit.MINUTES).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Take Daily Vitamin");
            task.setLastCompletedTime(new Date(now.toInstant().minus(4, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(3, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(2, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(1, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(5, ChronoUnit.MINUTES).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Cut Lawn");
            task.setLastCompletedTime(new Date(now.toInstant().minus(21, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(14, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(7, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(1, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Water Succulents");
            task.setLastCompletedTime(new Date(now.toInstant().minus(3, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Washed Car");
            task.setLastCompletedTime(new Date(now.toInstant().minus(8, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Clean Bathroom");
            task.setLastCompletedTime(new Date(now.toInstant().minus(15, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Vacuum Home Office");
            task.setLastCompletedTime(new Date(now.toInstant().minus(35, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Ate Turkey");
            task.setLastCompletedTime(new Date(now.toInstant().minus(70, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Hosted a Party");
            task.setLastCompletedTime(new Date(now.toInstant().minus(400, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Renewed Plates");
            task.setLastCompletedTime(new Date(now.toInstant().minus(800, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Renewed License");
            task.setLastCompletedTime(new Date(now.toInstant().minus(1200, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Get Oil Change");
            task.setLastCompletedTime(new Date(now.toInstant().minus(22 * 30, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(16 * 30, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(12 * 30, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(5 * 30, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            task = new Task("Change Water Filter");
            task.setLastCompletedTime(new Date(now.toInstant().minus(5 * 30, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(4 * 30, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(3 * 30, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(2 * 30, ChronoUnit.DAYS).toEpochMilli()));
            task.setLastCompletedTime(new Date(now.toInstant().minus(30, ChronoUnit.DAYS).toEpochMilli()));
            mDao.insert(task);

            return null;
        }
    }
}
