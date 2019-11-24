package com.sks.alaram.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "Saurabh";
    int uniqueInt;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive - Intent Action: " + intent.getAction());
        uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAlarm(context);
        }else{
                //do nothing
                Log.i(TAG, "intent Action:  not supported");
            }

    }

    public void setAlarm(Context context) {
        // Set the alarm to start at approximately 11:59 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueInt, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    public void cancelAlarm(Context context) {
        // Set the alarm to start at approximately 11:59 p.m.

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
        // Disable BootReceiver Component
        setBootReceiverEnabled(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, context);
    }

    private void setBootReceiverEnabled(int componentEnabledState, Context context) {
        ComponentName componentName = new ComponentName(context, BootReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,
                componentEnabledState,
                PackageManager.DONT_KILL_APP);
    }
}
