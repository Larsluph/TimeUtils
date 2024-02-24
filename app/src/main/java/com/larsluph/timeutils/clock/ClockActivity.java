package com.larsluph.timeutils.clock;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;

import androidx.appcompat.app.AppCompatActivity;

import com.larsluph.timeutils.R;

public class ClockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        ((Button) findViewById(R.id.clock_exit_button)).setOnClickListener(v -> finish());

        final TextClock textClock12 = findViewById(R.id.clockWidget12);
        final TextClock textClock24 = findViewById(R.id.clockWidget24);

        // format clocks
        /*if (isScreenPortrait()) {
            textClock12.setFormat12Hour("hh:mm:ss a");
            textClock12.setFormat24Hour(null);
            textClock24.setFormat12Hour(null);
        } else {
            textClock24.setFormat12Hour("hh:mm:ss a");
        }
        textClock24.setFormat24Hour("HH:mm:ss");*/

        // set fullscreen mode if landscape
        if (isScreenLandscape()){
            if (textClock24.is24HourModeEnabled()) {
                textClock24.setTextSize(TypedValue.COMPLEX_UNIT_SP, 185);
            } else {
                textClock24.setTextSize(TypedValue.COMPLEX_UNIT_SP, 135);
            }
        }
    }

    // re-enable fullscreen mode when back on focus
    protected void onResume() {
        super.onResume();
        if (isScreenLandscape()) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private boolean isScreenPortrait() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private boolean isScreenLandscape() {
        return this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}