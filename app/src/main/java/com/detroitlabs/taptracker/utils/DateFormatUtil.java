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
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.detroitlabs.taptracker.R;

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
    static final SimpleDateFormat timestampFormat = new SimpleDateFormat("MM-dd-yyyy h:mm a", Locale.US);
    @VisibleForTesting
    static final SimpleDateFormat monthDayFormat = new SimpleDateFormat("(MMM d)", Locale.US);
    @VisibleForTesting
    static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("(MMM yyyy)", Locale.US);

    private Clock clock = Clock.systemDefaultZone();
    private Context context;

    public DateFormatUtil(Context context) {
        this.context = context;
    }

    @VisibleForTesting
    void setClock(@NonNull Clock clock) {
        this.clock = clock;
    }

    public String formatDateWithTimeSinceNow(@NonNull Date dateToFormat) {
        Instant then = dateToFormat.toInstant();
        Instant now = Instant.now(clock);
        LocalDateTime localThen = LocalDateTime.ofInstant(then, ZoneId.systemDefault());
        LocalDateTime localNow = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        long gap;
        if (now.isBefore(then.plusSeconds(60))) {
            gap = ChronoUnit.SECONDS.between(then, now);
            return context.getString(R.string.df_seconds, gap);
        } else if (now.isBefore(then.plus(60, ChronoUnit.MINUTES))) {
            gap = ChronoUnit.MINUTES.between(then, now);
            return context.getString(R.string.df_minutes, gap);
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS))) {
            return context.getString(R.string.df_today, currentDayTimeFormat.format(dateToFormat));
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(2, ChronoUnit.DAYS))) {
            return context.getString(R.string.df_yesterday, currentDayTimeFormat.format(dateToFormat));
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(7, ChronoUnit.DAYS))) {
            gap = ChronoUnit.DAYS.between(then, now);
            return context.getString(R.string.df_days, gap);
        } else if (now.isBefore(then.truncatedTo(ChronoUnit.DAYS).plus(14, ChronoUnit.DAYS))) {
            return context.getString(R.string.df_last_week, monthDayFormat.format(dateToFormat));
        } else if (localNow.isBefore(localThen.plusMonths(1))) {
            gap = ChronoUnit.DAYS.between(then, now);
            return context.getString(R.string.df_days, gap);
        } else if (localNow.isBefore(localThen.plusMonths(2))) {
            return context.getString(R.string.df_last_month, monthDayFormat.format(dateToFormat));
        } else if (localNow.isBefore(localThen.plusYears(1))) {
            gap = localThen.until(localNow, ChronoUnit.MONTHS);
            return context.getString(R.string.df_months, gap);
        } else if (localNow.isBefore(localThen.plusYears(2))) {
            return context.getString(R.string.df_last_year, monthYearFormat.format(dateToFormat));
        } else if (localNow.isBefore(localThen.plusYears(9))) {
            gap = localThen.until(localNow, ChronoUnit.YEARS);
            return context.getString(R.string.df_years, gap, monthYearFormat.format(dateToFormat));
        }
        return timestampFormat.format(dateToFormat);
    }

    public String formatDate(@NonNull Date dateToFormat) {
        return timestampFormat.format(dateToFormat);
    }
}
