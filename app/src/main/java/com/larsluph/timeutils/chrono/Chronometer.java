package com.larsluph.timeutils.chrono;

public class Chronometer implements Runnable {

    //Some constants for milliseconds to hours, minutes, and seconds conversion
    public static final int delay = 10;

    private final ChronoActivity context; // instance of activity
    private Time startTime; // start time
    public Time stopTime; // stop time
    private Time offset; // offset time
    private boolean isRunning; // loop controller

    public Chronometer(ChronoActivity ctx) {
        context = ctx;
        stopTime = Time.empty();
        offset = Time.empty();
    }
    public Chronometer(ChronoActivity ctx, Time var_offset) {
        this(ctx);
        offset = var_offset;
    }
    public Chronometer(ChronoActivity ctx, long var_offset) {
        this(ctx);
        offset = Time.fromLong(var_offset);
    }

    public void start() {
        startTime = Time.fromLong(System.currentTimeMillis());
        isRunning = true;
    }

    public void stop() {
        stopTime = getElapsedTime();
        isRunning = false;
    }

    public Time getElapsedTime() {
        return Time.fromLong(System.currentTimeMillis() - startTime.toLong() + offset.toLong());
    }

    @Override
    public void run() {
        while(isRunning) {
            context.updateChronoView(getElapsedTime());

            //Sleep the thread for a short amount, to prevent high CPU usage!
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                stop();
                e.printStackTrace();
            }
        }
    }
}
