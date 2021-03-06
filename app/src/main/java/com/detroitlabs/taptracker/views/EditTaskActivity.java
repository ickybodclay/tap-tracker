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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.detroitlabs.taptracker.R;
import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.presenters.EditTaskPresenter;

public class EditTaskActivity extends AppCompatActivity implements EditTaskPresenter.View {

    public static final String EXTRA_REPLY = "com.detroitlabs.taptracker.REPLY";

    private EditText mEditTaskView;

    private EditTaskPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupPresenter();
        setupViews();
        setupForEditTask();
    }

    private void setupPresenter() {
        presenter = new EditTaskPresenter();
        presenter.setView(this);
    }

    private void setupViews() {
        setContentView(R.layout.activity_edit_task);
        mEditTaskView = findViewById(R.id.edit_task);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> presenter.onSaveButtonClicked(mEditTaskView.getText().toString()));
    }

    private void setupForEditTask() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("task")) {
            Task existingTask = extras.getParcelable("task");
            presenter.setEditTask(existingTask);
        }
    }

    @Override
    public void setResultCanceledAndFinish() {
        Intent replyIntent = new Intent();
        setResult(RESULT_CANCELED, replyIntent);
        finish();
    }

    @Override
    public void setResultOkAndFinish(String taskName) {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, taskName);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    @Override
    public void setResultOkAndFinish(Task task) {
        Intent replyIntent = new Intent();
        replyIntent.putExtra(EXTRA_REPLY, task);
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}
