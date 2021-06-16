package com.larsluph.timeutils.chrono;

import android.content.Context;

public class Chronometer implements Runnable {

    //Some constants for milliseconds to hours, minutes, and seconds conversion
    public static final long MILLIS_TO_SECONDS = 1000;
    public static final long MILLIS_TO_MINUTES = 60000;
    public static final long MILLS_TO_HOURS = 3600000;

    private final Context context; // instance of class
    private long startTime; // start time
    public boolean isRunning; // loop controller

    public Chronometer(Context ctx) {
        context = ctx;
    }
    public Chronometer(Context ctx, long startData) {
        this(ctx);
        startTime = startData;
    }

    public void start() {
        if(startTime == 0) { //if the start time was not set before! e.g. by second constructor
            startTime = System.currentTimeMillis();
        }
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
    }


    @Override
    public void run() {
        while(isRunning) {
            //We do not call ConvertTimeToString here because it will add some overhead
            //therefore we do the calculation without any function calls!

            //Here we calculate the difference of starting time and current time
            long since = System.currentTimeMillis() - startTime;

            //convert the resulted time difference into hours, minutes, seconds and milliseconds
            int millis = (int) since % 1000; //the last 3 digits of millisecs
            int seconds = (int) (since / MILLIS_TO_SECONDS) % 60;
            int minutes = (int) ((since / (MILLIS_TO_MINUTES)) % 60);
            int hours = (int) ((since / (MILLS_TO_HOURS)));

            ((ChronoActivity) context).updateChronoView(hours, minutes, seconds, millis);

            //Sleep the thread for a short amount, to prevent high CPU usage!
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}