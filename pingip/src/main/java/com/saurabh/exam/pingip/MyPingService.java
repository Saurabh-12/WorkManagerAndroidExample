package com.saurabh.exam.pingip;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MyPingService extends Service {
    public static final String LOG_TAG = "Saurabh";
    public static final String CHANNEL_ID = "pingServiceChannel";
    public static String IP_Address = null;
    int count = 0;
    SharedPreferences sharedPref;
    int defaultValue = 2;
    boolean stopHandler = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        createNotificationChannel();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPref = getSharedPreferences("MyPingCountPrefs", Context.MODE_PRIVATE);
        defaultValue = sharedPref.getInt("PingCount",2);
        Log.d(LOG_TAG, "MyPingService: Saved PingCount Value: "+defaultValue);

        String input = intent.getStringExtra("hostIp");
        if(input != null && !input.isEmpty())
            IP_Address = input;

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Ping Service")
                .setContentText(input)
                .setNotificationSilent()
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1001,notification);
        //do heavy work on a background thread
        startAlarm(defaultValue);
        //stopSelf();




        return START_REDELIVER_INTENT;//START_STICKY;
    }


    @Override
    public void onDestroy() {
        stopHandler = true;
        stopSelf();
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public boolean pingToServer(String host) {
        //host = "192.168.1.65";
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + host);
            int exitValue = ipProcess.waitFor();
            Log.d(LOG_TAG, "ping host: "+host+" exitValue: "+exitValue);
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void startAlarm(int pingValue) {
        final Handler h = new Handler();
        Log.d(LOG_TAG, "MyPingService: PingCount : "+pingValue);
        long startTime = System.currentTimeMillis();
        int intervalTime = 30*1000;
        if(pingValue<=10){
            intervalTime = (60/pingValue)*1000;
        }
        final int delay = intervalTime; //milliseconds
        Log.d(LOG_TAG, "MyPingService: Ping interval Time : "+delay);
        h.postDelayed(new Runnable(){
            public void run(){
                //send Ping
                if(IP_Address!=null){
                    if (pingToServer(IP_Address))
                        Log.d(LOG_TAG, "ping is reachable");
                    else
                        Log.d(LOG_TAG, "ping is not reachable");
                }else {
                    if (pingToServer(getGatewayIP()))
                        Log.d(LOG_TAG, "ping is reachable");
                    else
                        Log.d(LOG_TAG, "ping is not reachable");
                }
                if (!stopHandler) {
                    h.postDelayed(this, delay);
                }
            }
        }, intervalTime);
    }

    private String getGatewayIP(){
        String   s_gateway = null;
        WifiManager wifii = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo d = wifii.getDhcpInfo();
        s_gateway = intToIp(d.gateway);
        return s_gateway;
    }

    public String intToIp(int i) {
        return ( i & 0xFF)+"."+((i >> 8 ) & 0xFF)+"."
                +((i >> 16 ) & 0xFF)+"."+((i >> 24 ) & 0xFF );
    }

}
