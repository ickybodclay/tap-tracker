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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.detroitlabs.taptracker.R;
import com.detroitlabs.taptracker.presenters.NewTaskPresenter;

public class NewTaskActivity extends AppCompatActivity implements NewTaskPresenter.View {

    public static final String EXTRA_REPLY = "com.detroitlabs.taptracker.REPLY";

    private EditText mEditTaskView;

    private NewTaskPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupPresenter();
        setupViews();
    }

    private void setupPresenter() {
        presenter = new NewTaskPresenter();
        presenter.setView(this);
    }

    private void setupViews() {
        setContentView(R.layout.activity_new_task);
        mEditTaskView = findViewById(R.id.edit_task);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                presenter.onSaveButtonClicked(mEditTaskView.getText().toString());
            }
        });
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
}
