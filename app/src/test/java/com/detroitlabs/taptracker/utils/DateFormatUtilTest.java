package com.detroitlabs.taptracker.utils;

import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateFormatUtilTest {
    @Before
    public void setup() {
        DateFormatUtil.setClock(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
    }

    @Test
    public void formatDate_should_return_seconds_if_less_than_1_min_passed() {
        Date now = new Date();
        Date then = new Date(now.toInstant().minusSeconds(5).toEpochMilli());
        String formattedDate = DateFormatUtil.formatDate(then);

        assertEquals("5 secs ago", formattedDate);
    }

    @Test
    public void formatDate_should_return_minutes_if_less_than_1_hour_passed() {
        Date now = new Date();
        Date then = new Date(now.toInstant().minus(5, ChronoUnit.MINUTES).toEpochMilli());
        String formattedDate = DateFormatUtil.formatDate(then);

        assertEquals("5 mins ago", formattedDate);
    }

    @Test
    public void formatDate_should_return_today_time_if_less_than_1_day_passed() {
        Date now = new Date();
        Date then = new Date(now.toInstant().minus(5, ChronoUnit.HOURS).toEpochMilli());
        String actual = DateFormatUtil.formatDate(then);

        String expected = "Today " + DateFormatUtil.currentDayTimeFormat.format(then);
        assertEquals(expected, actual);
    }

    @Test
    public void formatDate_should_return_past_day_time_if_greater_than_1_day_passed() {
        Date now = new Date();
        Date then = new Date(now.toInstant().minus(25, ChronoUnit.HOURS).toEpochMilli());
        String actual = DateFormatUtil.formatDate(then);

        String expected = DateFormatUtil.pastDaytimeFormat.format(then);
        assertEquals(expected, actual);
    }
}
