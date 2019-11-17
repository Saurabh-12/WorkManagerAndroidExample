package com.sks.example.workmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RebootWorker extends Worker {
    public Context mcontext;
    public Activity myActivity;

    public RebootWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mcontext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            showToastMsg();
            return Result.success();
        }catch (Exception e)
        {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private void showToastMsg() {
            Log.i(MainActivity.TAG, "showToastMsg 1");

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(mcontext, "Toast Msg from Worker!!", Toast.LENGTH_LONG).show();
            }
        });

        Log.i(MainActivity.TAG, "showToastMsg 2");
    }
}
