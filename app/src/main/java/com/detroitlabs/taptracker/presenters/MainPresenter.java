package com.detroitlabs.taptracker.presenters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.detroitlabs.taptracker.views.NewTaskActivity;
import com.detroitlabs.taptracker.models.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class MainPresenter {

    public interface View {
        void startNewTaskActivity(int requestCode);

        void update(@NonNull Task task);

        void insert(@NonNull Task task);

        void showEmptyTaskErrorDialog();

        void showDetailsToast(@NonNull String details);
    }

    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;

    private static final int MAX_HISTORY = 10;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd h:mm a", Locale.US);

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
        view.showDetailsToast(formatTaskHistory(task));
    }

    @VisibleForTesting
    String formatTaskHistory(@NonNull Task task) {
        StringBuilder builder = new StringBuilder();
        if (task.getHistory().isEmpty()) {
            builder.append("No history available for \'");
            builder.append(task.getTask());
            builder.append("\'");
        }
        else {
            builder.append("History for \'");
            builder.append(task.getTask());
            builder.append("\':\n\n");
            int i = 0;
            for(Date d : task.getHistory()) {
                if (i >= MAX_HISTORY) {
                    break;
                }
                builder.append(timeFormat.format(d));
                builder.append("\n");
                ++i;
            }
        }
        return builder.toString();
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
