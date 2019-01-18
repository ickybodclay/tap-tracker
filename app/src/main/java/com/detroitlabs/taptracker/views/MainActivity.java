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

package com.detroitlabs.taptracker.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.detroitlabs.taptracker.R;
import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.models.TaskViewModel;
import com.detroitlabs.taptracker.presenters.MainPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.detroitlabs.taptracker.presenters.MainPresenter.CHANNEL_ID;

public class MainActivity extends AppCompatActivity implements MainPresenter.View {
    private static final String TAG = MainActivity.class.getName();

    private TaskViewModel taskViewModel;
    private RecyclerView taskRecyclerView;
    private TaskListAdapter adapter;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupPresenter();
        setupViews();
    }

    private void setupPresenter() {
        presenter = new MainPresenter();
        presenter.setView(this);
        presenter.handleActions(getIntent());
    }

    private void setupViews() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        taskRecyclerView = findViewById(R.id.recyclerview_task_list);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        adapter = new TaskListAdapter(this);
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        adapter.setOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task item) {
                presenter.onTaskItemClicked(item);
            }

            @Override
            public boolean onItemLongClick(Task item) {
                presenter.onTaskItemLongClicked(item);
                return true;
            }

            @Override
            public void onTrackButtonClicked(Task item) {
                presenter.onTrackButtonClicked(item);
            }

            @Override
            public void onViewMoreButtonClicked(View taskView, Task item) {
                presenter.onViewMoreButtonClicked(taskView, item);
            }
        });
        taskRecyclerView.setAdapter(adapter);
        taskRecyclerView.setLayoutManager(layoutManager);
        registerForContextMenu(taskRecyclerView);

        taskViewModel.getAllTasks().observe(this, tasks -> adapter.setTasks(tasks));
    }

    @Override
    public void startNewTaskActivity(int requestCode) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void startEditTaskActivity(int requestCode, @NonNull Task task) {
        Intent intent = new Intent(this, EditTaskActivity.class);
        intent.putExtra("task", task);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void insert(@NonNull Task task) {
        taskViewModel.insert(task);
    }

    @Override
    public void update(@NonNull Task item) {
        taskViewModel.update(item);
    }

    @Override
    public void delete(@NonNull Task task) {
        taskViewModel.delete(task);
    }

    @Override
    public void showEmptyTaskErrorDialog() {
        Toast.makeText(
                getApplicationContext(),
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showTaskDetailsDialog(@NonNull Task task) {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getBaseContext().getString(R.string.task_details_title, task.getTask()))
                .setView(R.layout.dialog_task_details)
                .create();
        dialog.show();

        List<EventDay> events = getEventDays(task.getHistory());

        CalendarView calendarView = dialog.findViewById(R.id.calendar_view);
        AppCompatButton deleteButton = dialog.findViewById(R.id.delete_button);
        AppCompatButton closeButton = dialog.findViewById(R.id.close_button);

        if (task.getHistory().isEmpty()) {
            try {
                Objects.requireNonNull(calendarView).setDate(new Date());
            } catch (OutOfDateRangeException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            try {
                Objects.requireNonNull(calendarView).setDate(events.get(0).getCalendar());
            } catch (OutOfDateRangeException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            Objects.requireNonNull(calendarView).setOnDayClickListener(eventDay -> {
                presenter.onHistoryDateSelected(task, eventDay);
            });
            Objects.requireNonNull(calendarView).setEvents(events);
        }

        Objects.requireNonNull(deleteButton).setOnClickListener(view -> {
            presenter.onDeleteTaskClicked(task);
            dialog.cancel();
        });
        Objects.requireNonNull(closeButton).setOnClickListener(view -> dialog.cancel());
    }

    private List<EventDay> getEventDays(@NonNull List<Date> dateList) {
        List<EventDay> events = new ArrayList<>();

        Log.d(MainActivity.class.getName(), "Selected Days: ");
        for (Date date : dateList) {
            Log.d(MainActivity.class.getName(), "> " + date.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.d(MainActivity.class.getName(), "~ " + calendar.toString());
            EventDay day = new EventDay(calendar, R.drawable.ic_check_green_24dp);
            // NOTE: EventDay constructor zeroes out the time part of the calendar
            events.add(day);
        }

        return events;
    }

    @Override
    public void showDateToast(String formattedDate) {
        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showNotificationForTask(@NonNull Task item) {
        int notificationId = item.hashCode();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check_box_white_24dp)
                .setContentTitle(String.format("Reminder: \'%s\'", item.getTask()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .addAction(0, getString(R.string.track), presenter.getTrackPendingIntent(item))
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_task) {
            presenter.onNewTaskButtonClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        presenter.handleOnActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Context getContext() {
        return getBaseContext();
    }

    @Override
    public void showTaskActionsPopup(@NonNull View view, @NonNull final Task task) {
        Log.d(TAG, "> Show task actions for task: " + task.getTask());
        PopupMenu popup = new PopupMenu(this, view, Gravity.BOTTOM | Gravity.START);
        popup.setOnMenuItemClickListener(item -> {
            Log.d(TAG, ">> Task menu item clicked = " + item.toString() + " for " + task.getTask());

            return presenter.onTaskActionItemClicked(item, task);
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.task_actions, popup.getMenu());
        popup.show();
    }
}
