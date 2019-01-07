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

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public final class DateFormatUtil {
    @VisibleForTesting
    static final SimpleDateFormat currentDayTimeFormat = new SimpleDateFormat("h:mm a", Locale.US);
    @VisibleForTesting
    static final SimpleDateFormat pastDaytimeFormat = new SimpleDateFormat("MM-dd h:mm a", Locale.US);
    @VisibleForTesting
    static final SimpleDateFormat monthDayFormat = new SimpleDateFormat("(MMM d)", Locale.US);
    @VisibleForTesting
    static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("(MMM yyyy)", Locale.US);

    private static Clock clock = Clock.systemDefaultZone();

    private DateFormatUtil() {
    }

    @VisibleForTesting
    static void setClock(@NonNull Clock clock) {
        DateFormatUtil.clock = clock;
    }

    public static String formatDate(@NonNull Date dateToFormat) {
        Instant then = dateToFormat.toInstant();
        Instant now = Instant.now(clock);
        LocalDateTime localThen = LocalDateTime.ofInstant(then, ZoneId.systemDefault());
        LocalDateTime localNow = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        long gap;
        if (now.isBefore(then.plusSeconds(60))) {
            long gapSeconds = ChronoUnit.SECONDS.between(then, now);
            return gapSeconds + " secs ago";
        } else if (now.isBefore(then.plus(60, ChronoUnit.MINUTES))) {
            gap = ChronoUnit.MINUTES.between(then, now);
            return gap + " mins ago";
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS))) {
            return "Today " + currentDayTimeFormat.format(dateToFormat);
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(2, ChronoUnit.DAYS))) {
            return "Yesterday " + currentDayTimeFormat.format(dateToFormat);
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(7, ChronoUnit.DAYS))) {
            gap = ChronoUnit.DAYS.between(then, now);
            return gap + " days ago";
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(14, ChronoUnit.DAYS))) {
            return "Last week " + monthDayFormat.format(dateToFormat);
        } else if (localNow.isBefore(localThen.plusMonths(1))) {
            gap = ChronoUnit.DAYS.between(then, now);
            return gap + " days ago";
        } else if (localNow.isBefore(localThen.plusMonths(2))) {
            return "Last month " + monthDayFormat.format(dateToFormat);
        } else if (localNow.isBefore(localThen.plusYears(1))) {
            gap = localThen.until(localNow, ChronoUnit.MONTHS);
            return gap + " months ago";
        } else if (localNow.isBefore(localThen.plusYears(2))) {
            return "Last year " + monthYearFormat.format(dateToFormat);
        } else if (localNow.isBefore(localThen.plusYears(9))) {
            gap = localThen.until(localNow, ChronoUnit.YEARS);
            return gap + " years ago " + monthYearFormat.format(dateToFormat);
        }
        return pastDaytimeFormat.format(dateToFormat);
    }
}
