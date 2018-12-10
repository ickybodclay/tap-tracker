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
