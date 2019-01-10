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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.applandeo.materialcalendarview.EventDay;
import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.utils.DateFormatUtil;
import com.detroitlabs.taptracker.views.NewTaskActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    }

    private static final String TAG = MainPresenter.class.getName();

    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;

    private View view;
    private DateFormatUtil dateFormatUtil;

    public MainPresenter() {
    }

    public void setView(View view) {
        this.view = view;
        this.dateFormatUtil = new DateFormatUtil(view.getContext());
    }

    public void onNewTaskButtonClicked() {
        view.startNewTaskActivity(NEW_TASK_ACTIVITY_REQUEST_CODE);
    }

    public void onTaskItemClicked(@NonNull Task item) {
        view.showTaskDetailsDialog(item);
    }

    /** @noinspection UnusedDeclaration */
    public void onTaskItemLongClicked(@NonNull Task item) {
        // do nothing
    }

    public void onTrackButtonClicked(@NonNull Task item) {
        trackTaskCompleted(item);
    }

    private void trackTaskCompleted(@NonNull Task item) {
        item.touch();
        view.update(item);
    }

    public void onViewMoreButtonClicked(@NonNull Task item) {
        view.showTaskDetailsDialog(item);
    }

    public void onHistoryDateSelected(@NonNull Task task, @NonNull EventDay eventDay) {
        Log.d(TAG, "Date selected = " + eventDay.getCalendar().toString());
        StringBuilder builder = new StringBuilder();
        List<Date> dayHistory = getTaskHistoryForDay(task, eventDay);
        if (dayHistory.isEmpty()) {
            builder.append("No task history for this date");
        }
        else {
            boolean first = true;
            for (Date date : dayHistory) {
                if (first) {
                    first = false;
                }
                else {
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

    public void onDeleteTaskClicked(@NonNull Task task) {
        Log.d(TAG, "Delete task clicked: " + task.getTask());
        view.delete(task);
    }

    public void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Task task = new Task(data.getStringExtra(NewTaskActivity.EXTRA_REPLY));
            view.insert(task);
        } else {
            view.showEmptyTaskErrorDialog();
        }
    }
}
