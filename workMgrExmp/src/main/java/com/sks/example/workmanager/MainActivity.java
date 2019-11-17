package com.sks.example.workmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "Saurabh";
    PeriodicWorkRequest workRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "inside oncreate 1");

        PeriodicWorkRequest.Builder myWorkBuilder =
                new PeriodicWorkRequest.Builder(RebootWorker.class, 5, TimeUnit.MINUTES);
        workRequest = myWorkBuilder.build();

        Log.i(TAG, "inside oncreate 2");
    }

    public void startWork(View view) {
        Log.i(TAG, "startWork 1");
        WorkManager.getInstance(MainActivity.this).
                enqueueUniquePeriodicWork("RebotWorkTag",
                        ExistingPeriodicWorkPolicy.KEEP, workRequest);
        Log.i(TAG, "startWork 2");

    }


    public void stopWork(View view) {
        Log.i(TAG, "stopWork 1");
        WorkManager.getInstance(MainActivity.this).cancelAllWork();
        Log.i(TAG, "stopWork 2");
    }
}
