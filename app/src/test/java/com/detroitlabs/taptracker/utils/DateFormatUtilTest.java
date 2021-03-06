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

package com.detroitlabs.taptracker.utils;

import android.content.Context;

import com.detroitlabs.taptracker.R;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DateFormatUtilTest {

    @Mock
    private Context mockContext;

    private DateFormatUtil dateFormatUtil;

    @Before
    public void setup() {
        initMocks(this);

        dateFormatUtil = new DateFormatUtil(mockContext);
    }

    @Test
    public void formatDate_should_return_seconds_if_less_than_1_min_passed() {
        when(mockContext.getString(R.string.df_seconds, 5L)).thenReturn("5 secs ago");

        Date now = mockNow();
        Date then = new Date(now.toInstant().minusSeconds(5).toEpochMilli());
        String formattedDate = dateFormatUtil.formatDateWithTimeSinceNow(then);

        assertEquals("5 secs ago", formattedDate);
    }

    @Test
    public void formatDate_should_return_minutes_if_less_than_1_hour_passed() {
        when(mockContext.getString(R.string.df_minutes, 5L)).thenReturn("5 mins ago");

        Date now = mockNow();
        Date then = new Date(now.toInstant().minus(5, ChronoUnit.MINUTES).toEpochMilli());
        String formattedDate = dateFormatUtil.formatDateWithTimeSinceNow(then);

        assertEquals("5 mins ago", formattedDate);
    }

    @Test
    public void formatDate_should_return_yesterday_if_previous_day() {
        //when(mockContext.getString(R.string.df_yesterday, 5L)).thenReturn("");

        Date now = mockNow();
        Date then = new Date(now.toInstant().minus(5, ChronoUnit.HOURS).toEpochMilli());
        String actual = dateFormatUtil.formatDateWithTimeSinceNow(then);

        String expected = "Yesterday " + DateFormatUtil.currentDayTimeFormat.format(then);
        assertEquals(expected, actual);
    }

    @Test
    public void formatDate_should_return_today_time_if_same_day() {
        Date now = mockNow(12);
        Date then = new Date(now.toInstant().minus(5, ChronoUnit.HOURS).toEpochMilli());
        String actual = dateFormatUtil.formatDateWithTimeSinceNow(then);

        String expected = "Today " + DateFormatUtil.currentDayTimeFormat.format(then);
        assertEquals(expected, actual);
    }

    @Test
    public void formatDate_should_return_past_day_time_if_greater_than_1_day_passed() {
        Date now = mockNow();
        Date then = new Date(now.toInstant().minus(25, ChronoUnit.HOURS).toEpochMilli());
        String actual = dateFormatUtil.formatDateWithTimeSinceNow(then);

        //String expected = DateFormatUtil.timestampFormat.format(then);
        assertEquals("1 days ago", actual);
    }

    private Date mockNow() {
        return mockNow(0);
    }

    private Date mockNow(int hourShift) {
        Instant mockInstant = Instant.EPOCH.plus(6 * 30, ChronoUnit.DAYS).plus(hourShift, ChronoUnit.HOURS);
        dateFormatUtil.setClock(Clock.fixed(mockInstant, ZoneId.systemDefault()));
        return new Date(mockInstant.toEpochMilli());
    }
}
