package com.detroitlabs.taptracker.data;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<Date> fromTimestampCsv(String dateCsv) {
        List<Date> dateList = new ArrayList<>();
        if (!dateCsv.isEmpty()) {
            String[] timestamps = dateCsv.split(",");
            for (String t : timestamps) {
                Long value = Long.parseLong(t);
                dateList.add(new Date(value));
            }
        }
        return dateList;
    }

    @TypeConverter
    public static String dateListToString(List<Date> dateList) {
        StringBuilder builder = new StringBuilder();
        for (Date d : dateList) {
            builder.append(d.getTime());
            builder.append(',');
        }
        return builder.toString();
    }
}