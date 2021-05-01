package com.saurabh.exam.pingip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class MyBootReceiver extends BroadcastReceiver {
    public static final String LOG_TAG = "Saurabh";
    SharedPreferences sharedPref;
    int defaultValue = 2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String message = "BootDeviceReceiver onReceive, action is " + action;
        Log.d(LOG_TAG, message);

        sharedPref = context.getSharedPreferences("MyPingCountPrefs",Context.MODE_PRIVATE);
        defaultValue = sharedPref.getInt("PingCount",2);
        Log.d(LOG_TAG, "Saved PingCount Value: "+defaultValue);

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            //Intent service = new Intent(context, MyPingService.class);
            // context.startService(service);
            startServiceByAlarm(context,defaultValue);
            //startServiceDirectly(context,defaultValue);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startServiceByAlarm(Context context, int pingValue)
    {
        Intent serviceIntent = new Intent(context, MyPingService.class);
        serviceIntent.putExtra("hostIp", getGatewayIP(context));

/*        // Get alarm manager.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        // Create intent to invoke the background service.
        Intent intent = new Intent(context, MyPingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Log.d(LOG_TAG, "BR StartService: PingCount : "+pingValue);
        long startTime = System.currentTimeMillis();
        int intervalTime = 30*1000;
        if(pingValue<=10){
            intervalTime = (60/pingValue)*1000;
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, intervalTime);
        Log.d(LOG_TAG, "BR StartService: Ping intervalTime : "+intervalTime +"ms");

        String message = "Start service use repeat alarm. ";
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, message);

        // Create repeat alarm.
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime+intervalTime, pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalTime, pendingIntent);*/
        //start service
        context.startForegroundService(serviceIntent);
    }

    private String getGatewayIP(Context context){
        String   s_gateway = null;
        WifiManager wifii = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo d = wifii.getDhcpInfo();
        s_gateway = intToIp(d.gateway);
        return s_gateway;
    }

    public String intToIp(int i) {
        return ( i & 0xFF)+"."+((i >> 8 ) & 0xFF)+"."
                +((i >> 16 ) & 0xFF)+"."+((i >> 24 ) & 0xFF );
    }


    /* Start RunAfterBootService service directly and invoke the service every 10 seconds. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startServiceDirectly(Context context, int pingValue)
    {
        Log.d(LOG_TAG, "BR StartService: PingCount : "+pingValue);
        int intervalTime = 30*1000;
        if(pingValue<=10){
            intervalTime = (60/pingValue)*1000;
        }

        try {
            while (true) {
                String message = "BootDeviceReceiver onReceive start service directly.";
                Log.d(LOG_TAG, message);

                // This intent is used to start background service. The same service will be invoked for each invoke in the loop.
                Intent serviceIntent = new Intent(context, MyPingService.class);
                serviceIntent.putExtra("hostIp", getGatewayIP(context));
                context.startForegroundService(serviceIntent);
                Log.d(LOG_TAG, "BR StartService: Ping intervalTime : "+intervalTime +"ms");


                // Current thread sleep time in milli second.
                Thread.sleep(intervalTime);
            }
        }catch(InterruptedException ex)
        {
            Log.e(LOG_TAG, ex.getMessage(), ex);
        }
    }
}
