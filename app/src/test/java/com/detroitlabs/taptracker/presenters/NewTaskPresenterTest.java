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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class NewTaskPresenterTest {
    private NewTaskPresenter subject;

    @Mock
    private NewTaskPresenter.View mockView;

    @Before
    public void setup() {
        initMocks(this);
        subject = new NewTaskPresenter();
        subject.setView(mockView);
    }

    @Test
    public void onSaveButtonClicked_with_empty_task_sets_cancelled_result() {
        subject.onSaveButtonClicked("");
        verify(mockView).setResultCanceledAndFinish();
    }

    @Test
    public void onSaveButtonClicked_with_valid_task_sets_ok_result() {
        String testTaskName = "test task";
        subject.onSaveButtonClicked(testTaskName);
        verify(mockView).setResultOkAndFinish(eq(testTaskName));
    }
}
