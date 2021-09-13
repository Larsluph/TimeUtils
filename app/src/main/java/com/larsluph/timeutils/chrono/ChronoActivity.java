package com.larsluph.timeutils.chrono;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.larsluph.timeutils.R;

import java.util.Locale;

public class ChronoActivity extends AppCompatActivity {

    enum ChronoStatus {
        not_started,
        started,
        stopped
    }

    private Button start, lap, stop, reset;
    private ScrollView scrollView;
    private TextView displayChrono, displayLap;
    private Chronometer chronometer;
    private Thread chronoThread;
    private int lapCount = 0;
    private Time lastLap = Time.empty();
    private long lastStop = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrono);

        start = findViewById(R.id.start);
        lap = findViewById(R.id.lap);
        stop = findViewById(R.id.stop);
        reset = findViewById(R.id.reset);
        displayChrono = findViewById(R.id.displayChrono);
        displayLap = findViewById(R.id.displayLap);
        scrollView = findViewById(R.id.scroll_lap);

        findViewById(R.id.chrono_exit_button).setOnClickListener(v -> finish());

        start.setOnClickListener(v -> startChrono());
        lap.setOnClickListener(v -> saveLap());
        stop.setOnClickListener(v -> stopChrono());
        reset.setOnClickListener(v -> resetChrono());

        toggleButtonVisibility(ChronoStatus.not_started);
    }

    private void toggleButtonVisibility(ChronoStatus status) {
        switch (status) {
            case not_started:
                start.setVisibility(View.VISIBLE);
                start.setText(R.string.chrono_start);
                lap.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.INVISIBLE);
                break;
            case started:
                start.setVisibility(View.INVISIBLE);
                lap.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
                reset.setVisibility(View.INVISIBLE);
                break;
            case stopped:
                start.setVisibility(View.VISIBLE);
                start.setText(R.string.chrono_resume);
                lap.setVisibility(View.VISIBLE);
                stop.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void updateChronoView(Time time) {
        runOnUiThread(() -> displayChrono.setText(time.toString()));
    }

    public void updateLapView(String data) {
        CharSequence text = displayLap.getText();
        if (lapCount != 0) { text += "\n"; } // add newline if not the first lap
        text += data;
        displayLap.setText(text);
    }

    public void resetLapView() {
        lapCount = 0;
        displayLap.setText("");
    }

    private void startChrono() {
        if (chronometer != null) return;
        toggleButtonVisibility(ChronoStatus.started);

        if (lastStop == 0) chronometer = new Chronometer(this);
        else chronometer = new Chronometer(this, lastStop);

        chronoThread = new Thread(chronometer);
        chronometer.start();
        chronoThread.start();
    }

    private void stopChrono() {
        if (chronometer == null) return;

        chronometer.stop();
        toggleButtonVisibility(ChronoStatus.stopped);
        lastStop = chronometer.stopTime.toLong();

        chronometer = null;
        chronoThread = null;
    }

    private void resetChrono() {
        updateChronoView(Time.empty());
        toggleButtonVisibility(ChronoStatus.not_started);
        lastStop = 0;
        resetLapView();
    }

    private void saveLap() {
        if (chronometer == null) return;

        // update lap display
        Time currentChrono = chronometer.getElapsedTime();
        Time elapsed = currentChrono.sub(lastLap);
        updateLapView(String.format(Locale.getDefault(), "LAP %s: %s (+%s)", ++lapCount, currentChrono, elapsed));
        // scroll to bottom
        scrollView.post(() -> scrollView.smoothScrollTo(0, displayLap.getBottom()));
        lastLap = currentChrono;
    }
}
