package com.detroitlabs.taptracker.presenters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.utils.DateFormatUtil;
import com.detroitlabs.taptracker.views.NewTaskActivity;

import static android.app.Activity.RESULT_OK;

public class MainPresenter {

    public interface View {
        void startNewTaskActivity(int requestCode);

        void update(@NonNull Task task);

        void insert(@NonNull Task task);

        void showEmptyTaskErrorDialog();

        void showDetailsDialog(@NonNull String task, @NonNull String[] formattedDates);
    }

    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;

    private static final int MAX_HISTORY = 10;

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
        view.showDetailsDialog(task.getTask(), formatHistory(task));
    }

    @VisibleForTesting
    String[] formatHistory(@NonNull Task task) {
        if (task.getHistory().isEmpty()) {
            return new String[]{"No recent history"};
        }

        String[] formattedHistory = new String[
                task.getHistory().size() > MAX_HISTORY ? MAX_HISTORY : task.getHistory().size()];
        for (int i = 0; i < formattedHistory.length; ++i) {
            formattedHistory[i] = DateFormatUtil.formatDate(task.getHistory().get(i));
        }
        return formattedHistory;
    }

    public void onTaskItemLongClicked(@NonNull Task item) {
        item.touch();
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
