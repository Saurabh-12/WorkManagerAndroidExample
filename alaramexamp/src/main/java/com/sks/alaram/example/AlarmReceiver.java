package com.sks.alaram.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "Saurabh";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AlarmReceiver onReceive: "+intent.getAction());
        //Do your work here
        Toast.makeText(context, "Alarm!! Alarm!!! Triggered", Toast.LENGTH_SHORT).show();
    }
}
