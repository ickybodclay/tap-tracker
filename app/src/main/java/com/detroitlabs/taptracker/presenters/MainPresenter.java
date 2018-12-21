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

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.applandeo.materialcalendarview.EventDay;
import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.views.NewTaskActivity;

import static android.app.Activity.RESULT_OK;

public class MainPresenter {

    public interface View {
        void startNewTaskActivity(int requestCode);

        void insert(@NonNull Task task);

        void update(@NonNull Task task);

        void delete(@NonNull Task task);

        void showEmptyTaskErrorDialog();

        void showTaskDetailsDialog(@NonNull Task task);
    }

    private static final String TAG = MainPresenter.class.getName();

    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;

    private View view;

    public MainPresenter() {
    }

    public void setView(View view) {
        this.view = view;
    }

    public void onNewTaskButtonClicked() {
        view.startNewTaskActivity(NEW_TASK_ACTIVITY_REQUEST_CODE);
    }

    public void onTaskItemClicked(@NonNull Task task) {
        view.showTaskDetailsDialog(task);
    }

    public void onTaskItemLongClicked(@NonNull Task item) {
        item.touch();
        view.update(item);
    }

    public void onHistoryDateSelected(EventDay day) {
        Log.d(TAG, "Date selected = " + day.getCalendar().toString());
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
