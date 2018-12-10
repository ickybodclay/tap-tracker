package com.detroitlabs.taptracker.presenters;

import android.app.Activity;
import android.content.Intent;

import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.views.NewTaskActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public void onTaskItemClicked_should_handle_no_history() {
        String testTaskName = "test task";
        Task test = new Task(testTaskName);
        subject.onTaskItemClicked(test);

        verify(mockView).showDetailsToast(eq(subject.formatTaskHistory(test)));
    }

    @Test
    public void onTaskItemClicked_should_handle_not_display_more_than_max_history() {
        String testTaskName = "test task";
        List<Date> testHistory = new ArrayList<>();
        Date testDate1 = Date.from(Instant.EPOCH);
        Date testDate2 = Date.from(testDate1.toInstant().plus(Period.ofDays(1)));
        Date testDate3 = Date.from(testDate1.toInstant().plus(Period.ofDays(2)));

        testHistory.add(testDate3);
        testHistory.add(testDate2);
        testHistory.add(testDate1);

        Task test = mock(Task.class);
        when(test.getTask()).thenReturn(testTaskName);
        when(test.getHistory()).thenReturn(testHistory);

        subject.onTaskItemClicked(test);

        verify(mockView).showDetailsToast(eq(subject.formatTaskHistory(test)));
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
