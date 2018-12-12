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

public class NewTaskPresenter {
    public interface View {

        void setResultCanceledAndFinish();

        void setResultOkAndFinish(String taskName);
    }

    private View view;

    public NewTaskPresenter() {
    }

    public void setView(@NonNull View view) {
        this.view = view;
    }

    public void onSaveButtonClicked(String taskName) {
        if (taskName == null || taskName.equals("")) {
            view.setResultCanceledAndFinish();
        } else {
            view.setResultOkAndFinish(taskName);
        }
    }
}
