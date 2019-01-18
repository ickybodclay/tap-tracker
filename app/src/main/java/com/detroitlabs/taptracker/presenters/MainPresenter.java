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

package com.detroitlabs.taptracker.presenters;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.applandeo.materialcalendarview.EventDay;
import com.detroitlabs.taptracker.R;
import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.utils.DateFormatUtil;
import com.detroitlabs.taptracker.views.EditTaskActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class MainPresenter {

    public interface View {
        void startNewTaskActivity(int requestCode);

        void insert(@NonNull Task task);

        void update(@NonNull Task task);

        void delete(@NonNull Task task);

        void showEmptyTaskErrorDialog();

        void showTaskDetailsDialog(@NonNull Task task);

        void showDateToast(String formattedDate);

        Context getContext();

        void showNotificationForTask(Task item);

        void showTaskActionsPopup(@NonNull android.view.View taskView, @NonNull Task task);
    }

    private static final String TAG = MainPresenter.class.getName();

    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;
    public static final String CHANNEL_ID = "Reminders";
    public static final String TRACK_ACTION = "com.detroitlabs.taptracker.TRACK_ACTION";
    public static final String REMINDER_ACTION = "com.detroitlabs.taptracker.REMINDER_ACTION";

    private View view;
    private DateFormatUtil dateFormatUtil;
    private AlarmManager alarmManager;

    public MainPresenter() {
    }

    public void setView(View view) {
        this.view = view;
        this.dateFormatUtil = new DateFormatUtil(view.getContext());
        this.alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = view.getContext().getString(R.string.channel_name);
            String description = view.getContext().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = view.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void onNewTaskButtonClicked() {
        view.startNewTaskActivity(NEW_TASK_ACTIVITY_REQUEST_CODE);
    }

    public void onTaskItemClicked(@NonNull Task item) {
        view.showTaskDetailsDialog(item);
    }

    /**
     * @noinspection UnusedDeclaration
     */
    public void onTaskItemLongClicked(@NonNull Task item) {
        // do nothing

        // FIXME for testing only
        //view.showNotificationForTask(item);
        scheduleReminder(item);
    }

    public void onTrackButtonClicked(@NonNull Task item) {
        trackTaskCompleted(item);
    }

    private void trackTaskCompleted(@NonNull Task item) {
        item.touch();
        view.update(item);
    }

    public void onViewMoreButtonClicked(@NonNull android.view.View taskView, @NonNull Task item) {
        view.showTaskActionsPopup(taskView, item);
    }

    public void onHistoryDateSelected(@NonNull Task task, @NonNull EventDay eventDay) {
        Log.d(TAG, "Date selected = " + eventDay.getCalendar().toString());
        StringBuilder builder = new StringBuilder();
        List<Date> dayHistory = getTaskHistoryForDay(task, eventDay);
        if (dayHistory.isEmpty()) {
            builder.append("No task history for this date");
        } else {
            boolean first = true;
            for (Date date : dayHistory) {
                if (first) {
                    first = false;
                } else {
                    builder.append('\n');
                }

                builder.append(dateFormatUtil.formatDate(date));
            }
        }
        view.showDateToast(builder.toString());
    }

    /*
     * This method is needed because for some reason EventDay zeros out time part of the calendar.
     */
    private List<Date> getTaskHistoryForDay(@NonNull Task task, @NonNull EventDay eventDay) {
        // FIXME simplify this
        // TODO limit entries to fit inside a toast message
        List<Date> datesOnEventDay = new ArrayList<>();
        Calendar dayCal = eventDay.getCalendar();
        for (Date d : task.getHistory()) {
            Calendar taskCal = Calendar.getInstance();
            taskCal.setTime(d);
            if (taskCal.get(Calendar.DAY_OF_MONTH) == dayCal.get(Calendar.DAY_OF_MONTH) &&
                    taskCal.get(Calendar.MONTH) == dayCal.get(Calendar.MONTH) &&
                    taskCal.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR)) {
                datesOnEventDay.add(d);
            }
        }
        return datesOnEventDay;
    }

    public void onEditTaskClicked(@NonNull Task task) {
        Log.d(TAG, "Edit task clicked: " + task.getTask());
        // TODO show EditTask with edit flag
    }

    public void onDeleteTaskClicked(@NonNull Task task) {
        Log.d(TAG, "Delete task clicked: " + task.getTask());
        view.delete(task);
    }

    public void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Task task = new Task(data.getStringExtra(EditTaskActivity.EXTRA_REPLY));
            view.insert(task);
        } else {
            view.showEmptyTaskErrorDialog();
        }
    }

    public void handleActions(@NonNull Intent intent) {
        if (TRACK_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Task task = extras.getParcelable("task");

                Log.d(TAG, "Task notification clicked = " + Objects.requireNonNull(task).getTask());
            }
        } else if (REMINDER_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                byte[] taskBytes = extras.getByteArray("task");
                Task task = unmarshall(taskBytes, Task.CREATOR);

                Log.d(TAG, "> Task reminder alarm triggered = " + task.getTask());
                view.showNotificationForTask(task);
            }
        }
    }

    public boolean onTaskActionItemClicked(MenuItem item, Task task) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                onEditTaskClicked(task);
                return true;
            default:
                return false;
        }
    }

    // TODO add data class to handle frequency
    public void scheduleReminder(@NonNull Task task) {
        Log.d(TAG, "> Scheduled alarm for " + task.getTask());

        final int ALARM_DELAY_MILLIS = 3000;

        alarmManager.set(AlarmManager.RTC, SystemClock.elapsedRealtime() + ALARM_DELAY_MILLIS, getReminderPendingIntent(task));
    }

    public void cancelReminder(@NonNull Task task) {
        alarmManager.cancel(getReminderPendingIntent(task));
    }

    public PendingIntent getTrackPendingIntent(@NonNull Task task) {
        Intent trackIntent = new Intent(TRACK_ACTION);
        trackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        trackIntent.putExtra("task", task);
        return PendingIntent.getActivity(view.getContext(), task.hashCode(), trackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent getReminderPendingIntent(@NonNull Task task) {
        Intent trackIntent = new Intent(REMINDER_ACTION);
        trackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        trackIntent.putExtra("task", marshall(task));
        return PendingIntent.getActivity(view.getContext(), task.hashCode(), trackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static byte[] marshall(Parcelable parcelable) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return parcel;
    }

    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }
}
