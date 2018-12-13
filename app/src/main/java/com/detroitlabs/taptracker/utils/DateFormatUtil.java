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
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS))) {
            return "Today " + currentDayTimeFormat.format(dateToFormat);
        }
        return pastDaytimeFormat.format(dateToFormat);
    }
}
