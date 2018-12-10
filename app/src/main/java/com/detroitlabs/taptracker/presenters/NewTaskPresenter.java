package com.detroitlabs.taptracker.presenters;

import android.text.TextUtils;

public class NewTaskPresenter {
    public interface View {

        void setResultCanceledAndFinish();

        void setResultOkAndFinish(String taskName);
    }

    private View view;

    public NewTaskPresenter() {
    }

    public void setView(View view) {
        this.view = view;
    }

    public void onSaveButtonClicked(String taskName) {
        if (TextUtils.isEmpty(taskName)) {
            view.setResultCanceledAndFinish();
        } else {
            view.setResultOkAndFinish(taskName);
        }
    }
}
