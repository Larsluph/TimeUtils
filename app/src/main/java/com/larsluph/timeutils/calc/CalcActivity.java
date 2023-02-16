package com.larsluph.timeutils.calc;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
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
    /**
     * Pattern to match valid date.
     * Matches:
     * - DD-MM
     * - DD/MM
     * - DD-MM-YYYY
     * - DD/MM-YYYY
     * - DD-MM/YYYY
     * - DD/MM/YYYY
     */
    private static final Pattern pDate = Pattern.compile("^(\\d{1,2})[/-](\\d{1,2})(?:[/-](\\d{4}))?$");

    /**
     * Pattern to match valid time
     * Matches:
     * - hh:mm
     * - hh:mm:ss
     */
    private static final Pattern pTime = Pattern.compile("^(2[0-3]|[01]?[0-9]):([0-5]?[0-9])(?::([0-5]?[0-9]))?$");

    private static final Pattern pDays = Pattern.compile("(-?\\d+)d");
    private static final Pattern pHours = Pattern.compile("(-?\\d+)h");
    private static final Pattern pMinutes = Pattern.compile("(-?\\d+)m");
    private static final Pattern pSeconds = Pattern.compile("(-?\\d+)s");

    private EditText textDate;
    private EditText textTime;
    private EditText textDelta;
    private TextView result;

    private boolean dispDate;
    private boolean dispYear;
    private boolean dispTime;
    private boolean dispSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        textDate = findViewById(R.id.baseDate);
        textTime = findViewById(R.id.baseTime);
        textDelta = findViewById(R.id.deltaDateTime);
        result = findViewById(R.id.calcResult);

        // When "Enter" key is pressed on deltaDateTime
        textDelta.setOnEditorActionListener((v, id, e) -> {
            calcResult();
            return true;
        });
        findViewById(R.id.buttonCalc).setOnClickListener(v -> calcResult());
    }

    /**
     * Hide keyboard if {token} is focused
     * @param token Token
     */
    private void hideKeyboard(IBinder token) {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    /**
     * Takes a string as input and check if it's a valid date
     * @param input input string
     */
    public static boolean validateDate(String input) {
        Matcher date = pDate.matcher(input);
        return date.matches();
    }

    /**
     * Takes a string as input and check if it's a valid time
     * @param input input string
     */
    public static boolean validateTime(String input) {
        Matcher time = pTime.matcher(input);
        return time.matches();
    }

    /**
     * Takes a string as input and converts it to a LocalDate object
     * @param input input string
     */
    public LocalDate detectDate(String input) {
        LocalDate date = LocalDate.now();
        if (input.isEmpty()) {
            // no input, return current date
            dispDate = false;
        } else if (validateDate(input)) {
            // input is valid date
            String[] matchs = input.replace("/", "-").split("-");
            switch (matchs.length) {
                case 3:
                    date = date.withYear(Integer.parseInt(matchs[2]));
                    dispYear = true;
                case 2:
                    date = date.withMonth(Integer.parseInt(matchs[1]))
                            .withDayOfMonth(Integer.parseInt(matchs[0]));
                    break;
                default:
                    throw new NumberFormatException("Invalid Date Format");
            }
        } else {
            // input is invalid date, display field
            Log.w("date parsing", "invalid date");
            dispYear = true;
        }
        return date;
    }

    /**
     * Takes a string as input and converts it to a LocalTime object
     * @param input input string
     */
    public LocalTime detectTime(String input) {
        LocalTime time = LocalTime.now();
        if (input.isEmpty()) {
            // no input, return current time
            dispTime = false;
            return time;
        } else if (validateTime(input)) {
            // input is valid time
            String[] matchs = input.split(":");
            switch (matchs.length) {
                case 3:
                    time = time.withSecond(Integer.parseInt(matchs[2]));
                    dispSeconds = true;
                case 2:
                    time = time.withHour(Integer.parseInt(matchs[0]))
                            .withMinute(Integer.parseInt(matchs[1]));
                    break;
                default:
                    throw new NumberFormatException("Invalid Time Format");
            }
        } else {
            // input is invalid time, display field
            Log.w("time parsing", "invalid time");
            dispSeconds = true;
        }
        return time;
    }

    /**
     * Takes a string as input and converts it to a TimeSpan object
     * @param input input string
     */
    public TimeSpan detectDelta(String input) {
        // apply regex
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

        // return formatted delta
        return new TimeSpan(days, hours, minutes, seconds);
    }

    /**
     * Add a TimeSpan to a LocalDateTime
     * @param base base datetime
     * @param delta delta to add to base
     */
    public LocalDateTime processDelta(LocalDateTime base, TimeSpan delta) {
        return base
                .plusDays(delta.days)
                .plusHours(delta.hours)
                .plusMinutes(delta.minutes)
                .plusSeconds(delta.seconds);
    }

    /**
     * Render processed delta to result text box
     * @param finalDateTime processed delta to display
     */
    private void renderResult(LocalDateTime finalDateTime) {
        result.setTextColor(getResources().getColor(android.R.color.tab_indicator_text, null));
        StringBuilder pattern = new StringBuilder();
        if (dispDate) {
            pattern.append("dd/MM");
            if (dispYear) pattern.append("/yyyy");
        }
        if (dispTime) {
            pattern.append(" HH:mm");
            if (dispSeconds) pattern.append(":ss");
        }
        if (pattern.length() < 1) {
            result.setText(R.string.calc_error);
            result.setTextColor(getColor(R.color.calc_error));
        } else
            result.setText(finalDateTime.format(DateTimeFormatter.ofPattern(pattern.toString().trim())));
    }

    /**
     * Function bound to button
     */
    private void calcResult() {
        // hide keyboard if open
        hideKeyboard(textDelta.getWindowToken());

        // By default, display date without year and time without seconds
        dispDate = true;
        dispYear = false;
        dispTime = true;
        dispSeconds = false;

        // base date detection
        LocalDate date = detectDate(textDate.getText().toString());
        // base time detection
        LocalTime time = detectTime(textTime.getText().toString());

        // combine date and time into a single variable
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // delta detection
        TimeSpan delta = detectDelta(textDelta.getText().toString());

        // sum delta with dateTime
        LocalDateTime finalDateTime = processDelta(dateTime, delta);

        // render with correct format
        renderResult(finalDateTime);
    }
}
