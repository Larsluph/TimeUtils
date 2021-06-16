package com.larsluph.timeutils.chrono;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.larsluph.timeutils.R;

public class ChronoActivity extends AppCompatActivity {

    Button start, lap, pause, reset;
    ScrollView scrollView;
    TextView displayChrono, displayLap;
    Chronometer chronometer;
    Thread chronoThread;
    String lapViewContent = "";
    int lapCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrono);

        start = findViewById(R.id.start);
        lap = findViewById(R.id.lap);
        pause = findViewById(R.id.pause);
        reset = findViewById(R.id.stop);
        displayChrono = findViewById(R.id.displayChrono);
        displayLap = findViewById(R.id.displayLap);
        scrollView = findViewById(R.id.scroll_lap);

        final Button buttonExit = findViewById(R.id.chrono_exit_button);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChrono();
            }
        });

        lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLap();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseChrono();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChrono();
            }
        });
    }

    private void toggleButtonVisibility(boolean isStart) {
        if (isStart) {
            start.setVisibility(View.VISIBLE);
            lap.setVisibility(View.INVISIBLE);
            // pause.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
        } else {
            start.setVisibility(View.INVISIBLE);
            lap.setVisibility(View.VISIBLE);
            // pause.setVisibility(View.VISIBLE);
            reset.setVisibility(View.VISIBLE);
        }
    }

    public void updateChronoView(final int h, final int m, final int s, final int millis) {
        runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                String data;
                if (h == 0) {
                    if (m == 0) {
                        data = String.format("%02d.%03d", s, millis);
                    } else {
                        data = String.format("%02d:%02d.%03d", m, s, millis);
                    }
                } else {
                    data = String.format("%02d:%02d:%02d.%03d", h, m, s, millis);
                }

                displayChrono.setText(data);
            }
        });
    }

    public void updateLapView() {
        displayLap.setText(lapViewContent);
    }

    private void startChrono() {
        toggleButtonVisibility(false);
        lapCount = 0;
        lapViewContent = "";
        updateLapView();
        chronometer = new Chronometer(this);
        chronoThread = new Thread(chronometer);
        chronoThread.start();
        chronometer.start();
    }

    private void pauseChrono() {

    }

    private void resetChrono() {
        toggleButtonVisibility(true);

        if (chronometer != null) {
            chronometer.stop();
            chronoThread.interrupt();
            chronoThread = null;
            chronometer = null;
        }
    }

    private void saveLap() {
        if (chronometer != null) {
            // update lap display
            if (lapCount != 0) { lapViewContent += "\n";} // add newline if not the first lap
            lapViewContent += String.format("LAP %s: %s", ++lapCount, displayChrono.getText());
            updateLapView();
            // scroll to bottom
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, displayLap.getBottom());
                }
            });
        }
    }
}