package com.sks.alaram.example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Saurabh";
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    int uniqueInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, uniqueInt, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public void startAlarm(View view) {
        setAlarm();
    }

    public void stopAlarm(View view) {
        cancelAlarm();
    }


    private void setAlarm() {
        Log.d(TAG, "AlarmReceiver set");
        // Set the alarm to start at approximately 11:59 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);


        // Enable BootReceiver Component
        setBootReceiverEnabled(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    private void cancelAlarm() {
        Log.d(TAG, "AlarmReceiver cancelled");
        alarmManager.cancel(pendingIntent);

        // Disable BootReceiver Component
        setBootReceiverEnabled(PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    private void setBootReceiverEnabled(int componentEnabledState) {
        ComponentName componentName = new ComponentName(this, BootReceiver.class);
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,
                componentEnabledState,
                PackageManager.DONT_KILL_APP);
    }
}
