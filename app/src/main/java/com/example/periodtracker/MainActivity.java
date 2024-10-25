package com.example.periodtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button trackButton;
    private EditText cycleLengthInput;
    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        cycleLengthInput = findViewById(R.id.cycleLength);
        trackButton = findViewById(R.id.trackButton);

        // Listen for date selection
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                selectedDate = selectedCalendar.getTimeInMillis();
            }
        });

        // Set button click listener
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cycleLengthStr = cycleLengthInput.getText().toString();
                if (selectedDate == 0) {
                    Toast.makeText(MainActivity.this, "Please select a date.", Toast.LENGTH_SHORT).show();
                } else if (cycleLengthStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter cycle length.", Toast.LENGTH_SHORT).show();
                } else {
                    int cycleLength = Integer.parseInt(cycleLengthStr);

                    // Start the tracking result activity and pass the date and cycle length
                    Intent intent = new Intent(MainActivity.this, TrackResultActivity.class);
                    intent.putExtra("selectedDate", selectedDate);
                    intent.putExtra("cycleLength", cycleLength);
                    startActivity(intent);
                }
            }
        });
    }
}
