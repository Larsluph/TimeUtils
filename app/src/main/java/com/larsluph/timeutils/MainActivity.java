package com.larsluph.timeutils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.larsluph.timeutils.chrono.ChronoActivity;
import com.larsluph.timeutils.clock.ClockActivity;
import com.larsluph.timeutils.timer.TimerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button clock = findViewById(R.id.button_clock);
//        final Button alarms = findViewById(R.id.button_alarms);
        final Button chrono = findViewById(R.id.button_chrono);
        final Button timer = findViewById(R.id.button_timer);

        clock.setOnClickListener(v -> {
            // Clock button handler
            Intent intent = new Intent(MainActivity.this, ClockActivity.class);
            startActivity(intent);
        });
//        alarms.setOnClickListener(v -> {
//            // Alarms button handler
//            Intent intent = new Intent(MainActivity.this, AlarmsActivity.class);
//            startActivity(intent);
//        });
        chrono.setOnClickListener(v -> {
            // Chrono button handler
            Intent intent = new Intent(MainActivity.this, ChronoActivity.class);
            startActivity(intent);
        });
        timer.setOnClickListener(v -> {
            // Timer button handler
            Intent intent = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(intent);
        });
    }
}
