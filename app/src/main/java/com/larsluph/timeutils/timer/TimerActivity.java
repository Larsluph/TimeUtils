package com.larsluph.timeutils.timer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.larsluph.timeutils.R;

public class TimerActivity extends AppCompatActivity {

    enum NotifID {
        Timer,
        TimerRing
    }
    enum ChannelID {
        Timer,
        TimerEnd
    }

    CountDownTimer timer = null;
    NumberPicker hoursPicker, minutesPicker, secondsPicker;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        createNotificationChannel(ChannelID.Timer.toString(), "Timer", "Channel used for ongoing timers", NotificationManager.IMPORTANCE_LOW);
        createNotificationChannel(ChannelID.TimerEnd.toString(), "Timer Ended", "Channel used to notify ended timers", NotificationManager.IMPORTANCE_HIGH);

        hoursPicker = findViewById(R.id.hours);
        minutesPicker = findViewById(R.id.minutes);
        secondsPicker = findViewById(R.id.seconds);

        initNumberPickers();

        status = findViewById(R.id.textStatus);

        final Button start = findViewById(R.id.selectData);
        start.setOnClickListener(v -> {
            cancelTimer();
            startTimer();
        });

        final Button cancel = findViewById(R.id.buttonCancel);
        cancel.setOnClickListener(v -> cancelTimer());

        final Button button = findViewById(R.id.timer_exit_button);
        button.setOnClickListener(v -> finish());
    }

    private void initNumberPickers() {
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(24);

        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);

        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);

        hoursPicker.setTextSize(30.0F);
        minutesPicker.setTextSize(30.0F);
        secondsPicker.setTextSize(30.0F);
    }

    private void notifyOngoingTimer(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelID.Timer.toString())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NotifID.Timer.ordinal(), builder.build());
    }

    private void notifyEndedTimer(String title, String content) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelID.TimerEnd.toString())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSound(alarmSound);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NotifID.TimerRing.ordinal(), builder.build());
    }

    private void delNotif(NotifID id) {
        getSystemService(NotificationManager.class).cancel(id.ordinal());
    }

    private void createNotificationChannel(String channelID, String name, String description, int importance) {
        NotificationChannel channel = new NotificationChannel(channelID, name, importance);
        channel.setDescription(description);

        NotificationManager notifMgr = getSystemService(NotificationManager.class);
        notifMgr.createNotificationChannel(channel);
    }

    private String formatData(int data) {
        if (data < 10) {
            return "0" + data;
        } else {
            return String.valueOf(data);
        }
    }

    public int[] formatTime(int seconds) {
        int hour, min, sec;

        min = seconds / 60;
        sec = seconds % 60;

        hour = min / 60;
        min %= 60;

        return new int[]{hour, min, sec};
    }

    private void startTimer() {
        int seconds = getTimeData();
        timer = new CountDownTimer(seconds*1000L, 1000) {

            public void onTick(long millisUntilFinished) {
                int total = (int) millisUntilFinished / 1000 + 1;

                int[] time = formatTime(total);

                String hours = formatData(time[0]);
                String mins = formatData(time[1]);
                String secs = formatData(time[2]);

                notifyOngoingTimer(getString(R.string.tag_timer), String.format("%s:%s:%s", hours, mins, secs));
                status.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_green_dark, null));
                status.setText(String.format("%s:%s:%s", hours, mins, secs));
            }

            public void onFinish() {
                delNotif(NotifID.Timer);
                status.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.holo_red_dark, null));
                status.setText(R.string.timer_done);
                notifyEndedTimer(getString(R.string.tag_timer), getString(R.string.timer_done));
            }
        };
        timer.start();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            status.setText("");
            delNotif(NotifID.Timer);
            delNotif(NotifID.TimerRing);
        }
    }

    private int getTimeData() {
        return hoursPicker.getValue()*360 + minutesPicker.getValue()*60 + secondsPicker.getValue();
    }
}
