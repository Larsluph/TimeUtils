package com.larsluph.timeutils.calc;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.larsluph.timeutils.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcActivity extends AppCompatActivity {
    private final Pattern pDays = Pattern.compile("(\\d+)d");
    private final Pattern pHours = Pattern.compile("(\\d+)h");
    private final Pattern pMinutes = Pattern.compile("(\\d+)m");
    private final Pattern pSeconds = Pattern.compile("(\\d+)s");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        ((EditText)findViewById(R.id.deltaDateTime)).setOnEditorActionListener((v, id, e) -> {
            calcResult();
            return true;
        });
        findViewById(R.id.buttonCalc).setOnClickListener(v -> calcResult());
    }

    private void calcResult() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.deltaDateTime).getWindowToken(), 0);

        // fetch fields from layout
        final EditText textDate = findViewById(R.id.baseDate);
        final EditText textTime = findViewById(R.id.baseTime);
        final EditText deltaDateTime = findViewById(R.id.deltaDateTime);
        final TextView result = findViewById(R.id.calcResult);

        boolean dispDate = true;
        boolean dispYear = false;
        boolean dispTime = true;
        boolean dispSeconds = false;

        // set default datetime
        LocalDate baseDate = LocalDate.now();
        LocalTime baseTime = LocalTime.now();

        // base date detection
        try {
            String[] matchs = textDate.getText().toString()
                                                .replace("/", "-")
                                                .split("-");
            switch (matchs.length) {
                case 3:
                    baseDate = baseDate.withYear(Integer.parseInt(matchs[2]));
                    dispYear = true;
                case 2:
                    baseDate = baseDate.withMonth(Integer.parseInt(matchs[1]))
                                       .withDayOfMonth(Integer.parseInt(matchs[0]));
                    break;
                default:
                    throw new NumberFormatException("Invalid Date Format");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d("date parsing", "invalid date");
            dispDate = false;
        }

        // base time detection
        try {
            String[] matchs = textTime.getText().toString().split(":");
            switch (matchs.length) {
                case 3:
                    baseTime = baseTime.withSecond(Integer.parseInt(matchs[2]));
                    dispSeconds = true;
                case 2:
                    baseTime = baseTime.withHour(Integer.parseInt(matchs[0]))
                                       .withMinute(Integer.parseInt(matchs[1]));
                    break;
                default:
                    throw new NumberFormatException("Invalid Time Format");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.d("time parsing", "invalid time");
            dispTime = false;
        }

        // gen dt from values
        LocalDateTime baseDateTime = LocalDateTime.of(baseDate, baseTime);

        // delta datetime detection
        final String input = deltaDateTime.getText().toString();

        // regex matching
        Matcher mDays = pDays.matcher(input);
        Matcher mHours = pHours.matcher(input);
        Matcher mMinutes = pMinutes.matcher(input);
        Matcher mSeconds = pSeconds.matcher(input);

        // default values
        int days = 0, hours = 0, minutes = 0, seconds = 0;

        // match days
        while (mDays.find()) {
            days += Integer.parseInt(Objects.requireNonNull(mDays.group(1)));
        }
        // match hours
        while (mHours.find()) {
            hours += Integer.parseInt(Objects.requireNonNull(mHours.group(1)));
        }
        // match minutes
        while (mMinutes.find()) {
            minutes += Integer.parseInt(Objects.requireNonNull(mMinutes.group(1)));
        }
        // match seconds
        while (mSeconds.find()) {
            seconds += Integer.parseInt(Objects.requireNonNull(mSeconds.group(1)));
        }

        // compute final dt
        LocalDateTime finalDateTime = baseDateTime.plusDays(days)
            .plusHours(hours)
            .plusMinutes(minutes)
            .plusSeconds(seconds);

        // render with correct format
        result.setTextColor(getResources().getColor(android.R.color.tab_indicator_text, null));
        String pattern = "";
        if (dispDate) {
            pattern += "dd/MM";
            if (dispYear) pattern += "/yyyy";
        }
        if (dispTime) {
            pattern += " HH:mm";
            if (dispSeconds) pattern += ":ss";
        }
        if (pattern.isEmpty()) {
            result.setText(R.string.calc_error);
            result.setTextColor(getColor(R.color.calc_error));
        } else
            result.setText(finalDateTime.format(DateTimeFormatter.ofPattern(pattern.trim())));
    }
}
