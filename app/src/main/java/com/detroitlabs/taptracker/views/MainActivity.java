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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.detroitlabs.taptracker.R;
import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.models.TaskViewModel;
import com.detroitlabs.taptracker.presenters.MainPresenter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainPresenter.View {

    private TaskViewModel mTaskViewModel;
    private TaskListAdapter mAdapter;

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
    }

    private void setupViews() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new TaskListAdapter(this);
        mTaskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        mAdapter.setOnItemClickListener(new TaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task item) {
                presenter.onTaskItemClicked(item);
            }

            @Override
            public boolean onItemLongClick(Task item) {
                presenter.onTaskItemLongClicked(item);
                return true;
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);

        mTaskViewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> tasks) {
                // Update the cached copy of the words in the adapter.
                mAdapter.setTasks(tasks);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshAdapterOnInterval();
    }

    private void refreshAdapterOnInterval() {
        final long intervalInMs = 1000L;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                handler.postDelayed(this, intervalInMs);
            }
        }, intervalInMs);
    }

    @Override
    public void startNewTaskActivity(int requestCode) {
        Intent intent = new Intent(this, NewTaskActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void update(@NonNull Task item) {
        mTaskViewModel.update(item);
    }

    @Override
    public void insert(@NonNull Task task) {
        mTaskViewModel.insert(task);
    }

    @Override
    public void showEmptyTaskErrorDialog() {
        Toast.makeText(
                getApplicationContext(),
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showHistoryDialog(@NonNull Task task) {
        List<Calendar> selectedDays = getSelectedDays(task.getHistory());

        DatePickerBuilder builder = new DatePickerBuilder(this, calendar -> presenter.onHistoryDateSelected(calendar))
                .date((selectedDays.isEmpty()) ? null : selectedDays.get(0))
                .headerColor(R.color.colorPrimaryDark)
                .todayLabelColor(R.color.colorPrimary)
                .pickerType(CalendarView.MANY_DAYS_PICKER)
                .selectedDays(selectedDays);

        DatePicker datePicker = builder.build();
        datePicker.show();
    }

    @Override
    public void showNoHistoryDialog(@NonNull Task task) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(task.getTask())
                .setMessage(R.string.no_task_history)
                .create();

        dialog.show();
    }

    private List<Calendar> getSelectedDays(@NonNull List<Date> dateList) {
        List<Calendar> calendars = new ArrayList<>();

        Log.d(MainActivity.class.getName(), "Selected Days: ");
        for (Date date : dateList) {
            Log.d(MainActivity.class.getName(), "> " + date.toString());
            Calendar calendar = DateUtils.getCalendar();
            calendar.setTime(date);
            calendars.add(calendar);
        }

        return calendars;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
}
