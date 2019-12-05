package com.zebra.ecrt.saurabh.anrtest;

import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Saurabh ANRTest";
    private final Object _mutex = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called !!!!");
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called !!!!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called !!!!");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called !!!!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called !!!!");
    }

    public void createANR(View view) {
        Log.i(TAG, "clicked on DeadLock");
        deadLock();
    }

    public void createANRLoop(View view) {
        Log.i(TAG, "clicked on ANR Loop");
        InfiniteLoop();
    }

    public void createANRSleep(View view) {
        Log.i(TAG, "clicked on Sleep Loop");
        Sleep();
    }


    private void deadLock() {
        new LockerThread().start();

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                synchronized (_mutex) {
                    Log.e(TAG, "ANR-Failed There should be a dead lock before this message");
                }
            }
        }, 1000);
    }

    public class LockerThread extends Thread {

        LockerThread() {
            setName("APP: Locker");
        }

        @Override
        public void run() {
            synchronized (_mutex) {
                //noinspection InfiniteLoopStatement
                while (true)
                    Sleep();
            }
        }
    }

    private static void Sleep() {
        try {
            Thread.sleep(8 * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void InfiniteLoop() {
        int i = 0;
        //noinspection InfiniteLoopStatement
        while (true) {
            i++;
        }
    }
}
