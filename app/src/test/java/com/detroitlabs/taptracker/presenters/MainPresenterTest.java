package com.detroitlabs.taptracker.presenters;

import android.app.Activity;
import android.content.Intent;

import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.views.NewTaskActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.detroitlabs.taptracker.presenters.MainPresenter.NEW_TASK_ACTIVITY_REQUEST_CODE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MainPresenterTest {
    private MainPresenter subject;

    @Mock
    private MainPresenter.View mockView;

    @Before
    public void setup() {
        initMocks(this);
        subject = new MainPresenter();
        subject.setView(mockView);
    }

    @Test
    public void handleOnActivityResult_should_insert_task_if_result_ok() {
        String testTaskName = "test task";
        Task test = new Task(testTaskName);
        Intent data = mock(Intent.class);
        when(data.getStringExtra(NewTaskActivity.EXTRA_REPLY)).thenReturn(testTaskName);
        subject.handleOnActivityResult(NEW_TASK_ACTIVITY_REQUEST_CODE, Activity.RESULT_OK, data);
        verify(mockView).insert(eq(test));
    }

    @Test
    public void handleOnActivityResult_should_show_error_if_result_cancelled() {
        subject.handleOnActivityResult(NEW_TASK_ACTIVITY_REQUEST_CODE, Activity.RESULT_CANCELED, null);
        verify(mockView).showEmptyTaskErrorDialog();
    }
}
