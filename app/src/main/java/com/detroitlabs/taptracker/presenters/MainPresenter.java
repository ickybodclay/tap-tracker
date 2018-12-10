package com.detroitlabs.taptracker.presenters;

import android.content.Intent;

import com.detroitlabs.taptracker.views.NewTaskActivity;
import com.detroitlabs.taptracker.models.Task;

import static android.app.Activity.RESULT_OK;

public class MainPresenter {
    public interface View {
        void startNewTaskActivity(int requestCode);

        void update(Task item);

        void insert(Task task);

        void showEmptyTaskErrorDialog();
    }

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

    public void onTaskItemClicked(Task item) {
        item.touch();
        item.DEBUG__logHistory();
        view.update(item);
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
