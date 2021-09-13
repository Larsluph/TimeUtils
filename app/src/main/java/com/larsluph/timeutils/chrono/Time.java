package com.larsluph.timeutils.chrono;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Time {
    public static final long MILLIS_TO_SECONDS = 1000;
    public static final long MILLIS_TO_MINUTES = 60 * MILLIS_TO_SECONDS;
    public static final long MILLS_TO_HOURS = 60 * MILLIS_TO_MINUTES;

    public int hours;
    public int minutes;
    public int seconds;
    public int millis;

    public Time(int h, int m, int s, int ms) {
        hours = h;
        minutes = m;
        seconds = s;
        millis = ms;
    }

    @NonNull
    public static Time empty() {
        return new Time(0, 0, 0, 0);
    }

    @NonNull
    public static Time fromLong(final long time) {
        Time result = Time.empty();

        result.millis = (int) time % 1000; //the last 3 digits of millisecs
        result.seconds = (int) ((time / MILLIS_TO_SECONDS) % 60);
        result.minutes = (int) ((time / MILLIS_TO_MINUTES) % 60);
        result.hours = (int) (time / MILLS_TO_HOURS);

        return result;
    }

    public long toLong() {
        return hours * MILLS_TO_HOURS + minutes * MILLIS_TO_MINUTES + seconds * MILLIS_TO_SECONDS + millis;
    }

    @NonNull
    public String toString() {
        if (hours == 0)
            if (minutes == 0) return String.format(Locale.getDefault(), "%02d.%03d", seconds, millis);
            else return String.format(Locale.getDefault(), "%02d:%02d.%03d", minutes, seconds, millis);
        else
            return String.format(Locale.getDefault(), "%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }

    public Time add(Time other) {
        return Time.fromLong(toLong() + other.toLong());
    }

    public Time sub(Time other) {
        return Time.fromLong(toLong() - other.toLong());
    }
}
