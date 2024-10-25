package com.example.periodtracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TrackResultActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_result);

        resultTextView = findViewById(R.id.result);

        // Get the selected date and cycle length from the intent
        long selectedDateMillis = getIntent().getLongExtra("selectedDate", 0);
        int cycleLength = getIntent().getIntExtra("cycleLength", 28); // Default cycle length is 28 days

        if (selectedDateMillis != 0) {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selectedDateMillis);

            // Display the results based on the user-provided cycle length
            displayTrackingResults(selectedDate, cycleLength);
        } else {
            resultTextView.setText("No date selected.");
        }
    }

    private void displayTrackingResults(Calendar selectedDate, int cycleLength) {
        // Ovulation typically occurs 14 days before the next period
        int ovulationDay = cycleLength - 14;

        // Calculate the current date
        Calendar currentDate = Calendar.getInstance();

        // Calculate the next period date (cycleLength days after the selected date)
        Calendar nextPeriod = (Calendar) selectedDate.clone();
        nextPeriod.add(Calendar.DAY_OF_MONTH, cycleLength);

        // Calculate ovulation day (cycleLength - 14 days after the last period)
        Calendar ovulationDate = (Calendar) selectedDate.clone();
        ovulationDate.add(Calendar.DAY_OF_MONTH, ovulationDay);

        // Calculate fertile window (5 days before ovulation to 1 day after)
        Calendar fertileStart = (Calendar) ovulationDate.clone();
        fertileStart.add(Calendar.DAY_OF_MONTH, -5); // Fertility starts 5 days before ovulation
        Calendar fertileEnd = (Calendar) ovulationDate.clone();
        fertileEnd.add(Calendar.DAY_OF_MONTH, 1); // Fertility ends 1 day after ovulation

        // Calculate safe days (before fertile window and after)
        Calendar safeStart = (Calendar) selectedDate.clone();
        safeStart.add(Calendar.DAY_OF_MONTH, cycleLength + 5); // Safe days start 5 days after the next period

        Calendar safeEnd = (Calendar) nextPeriod.clone();
        safeEnd.add(Calendar.DAY_OF_MONTH, -5); // Safe days end 5 days before the next period

        // Format date to display
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String result;

        // Predict this month's period
        if (nextPeriod.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                nextPeriod.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
            result = "This month's expected period starts on: " + sdf.format(nextPeriod.getTime()) + "\n"+"\n"+"\n";
        }
        else {
            result = "This month's expected period has already passed.\n\n";
        }

        result+="📅 Selected Date: " + sdf.format(selectedDate.getTime()) +"\n"+"\n"+
                "🩸 Next Period (Predicted): " + sdf.format(nextPeriod.getTime()) +"\n"+"\n"+
                "🔵 Ovulation Day: " + sdf.format(ovulationDate.getTime()) +"\n"+"\n"+
                "🌸 Fertile Days: " + sdf.format(fertileStart.getTime()) + " to " + sdf.format(fertileEnd.getTime()) +"\n"+"\n"+
                "🛡️ Safe Days: " + sdf.format(safeStart.getTime()) + " to " + sdf.format(safeEnd.getTime());

        resultTextView.setText(result);

        showNextPeriodNotification(nextPeriod);
    }

    private void showNextPeriodNotification(Calendar nextPeriod) {
        String channelId = "period_tracker_channel";
        String channelName = "Period Tracker Notifications";

        // Create NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a NotificationChannel for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an Intent that will be shown when the user clicks on the notification
        Intent intent = new Intent(this, TrackResultActivity.class);
        intent.putExtra("selectedDate", nextPeriod.getTimeInMillis());
        intent.putExtra("cycleLength", 28); // Default cycle length for intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.img_10) // Set your own notification icon
                .setContentTitle("Period Tracker Reminder")
                .setContentText("Your next period is predicted to start on: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(nextPeriod.getTime()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }
}

