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

import android.support.annotation.NonNull;

import com.detroitlabs.taptracker.models.Task;

public class EditTaskPresenter {
    public interface View {

        void setResultCanceledAndFinish();

        void setResultOkAndFinish(String taskName);

        void setResultOkAndFinish(Task task);
    }

    private View view;

    private Task editTask;

    public EditTaskPresenter() {
    }

    public void setView(@NonNull View view) {
        this.view = view;
    }

    public void onSaveButtonClicked(String taskName) {
        if (taskName == null || taskName.equals("")) {
            view.setResultCanceledAndFinish();
        } else if (isEditMode()) {
            editTask.setTask(taskName);
            view.setResultOkAndFinish(editTask);
        } else {
            view.setResultOkAndFinish(taskName);
        }
    }

    public void setEditTask(Task existingTask) {
        this.editTask = existingTask;
    }

    public boolean isEditMode() {
        return editTask != null;
    }
}
