package com.detroitlabs.taptracker.utils;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public final class DateFormatUtil {
    @VisibleForTesting
    static final SimpleDateFormat currentDayTimeFormat = new SimpleDateFormat("h:mm a", Locale.US);
    @VisibleForTesting
    static final SimpleDateFormat pastDaytimeFormat = new SimpleDateFormat("MM-dd h:mm a", Locale.US);

    private static Clock clock = Clock.systemDefaultZone();

    private DateFormatUtil() {
    }

    @VisibleForTesting
    private static Clock getClock() {
        return clock;
    }

    @VisibleForTesting
    static void setClock(Clock clock) {
        DateFormatUtil.clock = clock;
    }

    public static String formatDate(@NonNull Date dateToFormat) {
        Instant then = dateToFormat.toInstant();
        Instant now = Instant.now(getClock());
        if (now.isBefore(then.plusSeconds(60))) {
            long gapSeconds = ChronoUnit.SECONDS.between(then, now);
            return gapSeconds + " secs ago";
        } else if (now.isBefore(then.plus(60, ChronoUnit.MINUTES))) {
            long gapMinutes = ChronoUnit.MINUTES.between(then, now);
            return gapMinutes + " mins ago";
        } else if (now.isBefore(then.plus(24, ChronoUnit.HOURS))) {
            return "Today " + currentDayTimeFormat.format(dateToFormat);
        }
        return pastDaytimeFormat.format(dateToFormat);
    }
}
