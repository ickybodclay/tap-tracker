package com.detroitlabs.taptracker.utils;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public final class DateFormatUtil {
    private static final SimpleDateFormat secondsTimeFormat = new SimpleDateFormat("s", Locale.US);
    private static final SimpleDateFormat minutesTimeFormat = new SimpleDateFormat("m", Locale.US);
    private static final SimpleDateFormat currentDayTimeFormat = new SimpleDateFormat(" h:mm a", Locale.US);
    private static final SimpleDateFormat pastDaytimeFormat = new SimpleDateFormat("MM-dd h:mm a", Locale.US);

    private DateFormatUtil() {
    }

    public static String formatDate(@NonNull Date date) {
        Instant then = date.toInstant();
        Instant now = Instant.now();
        if (now.isBefore(then.plusSeconds(60))) {
            return secondsTimeFormat.format(date) + " secs ago";
        } else if (now.isBefore(then.plus(60, ChronoUnit.MINUTES))) {
            return minutesTimeFormat.format(date) + " mins ago";
        } else if (now.isBefore(then.plus(24, ChronoUnit.HOURS))) {
            return "Today " + currentDayTimeFormat.format(date);
        }
        return pastDaytimeFormat.format(date);
    }
}
