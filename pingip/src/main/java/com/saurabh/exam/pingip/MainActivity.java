package com.saurabh.exam.pingip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "Saurabh";
    public static final String MyPREFERENCES = "MyPingCountPrefs" ;
    public static final String PingCount = "PingCount";
    SharedPreferences sharedpreferences;
    private EditText ipAddress;
    private EditText pingCountEtx;
    PendingIntent pintent;
    AlarmManager alarm;

    public String   s_dns1 ;
    public String   s_dns2;
    public String   s_gateway;
    public String   s_ipAddress;
    public String   s_leaseDuration;
    public String   s_netmask;
    public String   s_serverAddress;
    TextView info;
    DhcpInfo d;
    WifiManager wifii;
    int pingCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipAddress = (EditText)findViewById(R.id.ping_ip_address_etx);
        pingCountEtx = (EditText)findViewById(R.id.ping_count_etx);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.d("ECRT", bundle == null ? "Bundle is null" : "Bundle is not null");

        if(bundle !=null)
            pingCount = bundle.getInt("PingCount");
        Log.d("ECRT","PingCount "+pingCount);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(PingCount, pingCount);
        editor.commit();

/*        PackageManager p = getPackageManager();
        // activity which is first time open in manifiest file which is declare as
        // <category android:name="android.intent.category.LAUNCHER" />
        ComponentName componentName = new ComponentName(this, com.zebra.ecrt.sks.pingip.MainActivity.class);
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);*/

/*        Intent serviceIntent = new Intent(MainActivity.this, MyPingService.class);
        serviceIntent.putExtra("inputExtra", "Saurabh");
        startService(serviceIntent);*/

/*        Intent ishintent = new Intent(MainActivity.this, MyPingService.class);
        PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, ishintent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1000, pintent);*/


/*        mWorkManager = WorkManager.getInstance();
        pingRequest = new PeriodicWorkRequest.Builder(MyPingWorker.class,
                REPEAT_INTERVAL, TimeUnit.MILLISECONDS,PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                TimeUnit.MILLISECONDS)
                .addTag("Periodic_Ping")
                .build();


        mWorkManager.enqueueUniquePeriodicWork("Periodic_Ping",
                ExistingPeriodicWorkPolicy.REPLACE, pingRequest);

        //Callback method to get Info for scheduled ping work
        mWorkManager.getWorkInfoByIdLiveData(pingRequest.getId()).observe(this,
                new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        if (workInfo != null) {
                            WorkInfo.State state = workInfo.getState();
                            Log.i("ECRT", "OneTimeWorkRequest Status: "+state.toString());
                        }
                    }
                });*/
        //finish();

    }


    @Override
    protected void onStart() {
        super.onStart();
        //finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MyPingService.class);
        pintent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        wifii = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        d = wifii.getDhcpInfo();

        s_dns1 = "DNS 1: " + intToIp(d.dns1);
        s_dns2 = "DNS 2: " + intToIp(d.dns2);
        s_gateway = "Default Gateway: " + intToIp(d.gateway);
        s_ipAddress = "IP Address: " + intToIp(d.ipAddress);
        s_leaseDuration = "Lease Time: " + intToIp(d.leaseDuration);
        s_netmask = "Subnet Mask: " + intToIp(d.netmask);
        s_serverAddress = "Server IP: " + intToIp(d.serverAddress);

        //dispaly them
        info = (TextView) findViewById(R.id.wifi_detail_txt);
        info.setText("Network Info\n" + s_dns1 + "\n" + s_dns2 + "\n" + s_gateway + "\n" + s_ipAddress
                + "\n" + s_leaseDuration + "\n" + s_netmask + "\n" + s_serverAddress);

        ipAddress.setText(intToIp(d.gateway));
    }


    public void startPing(View view) {
        String hostIp = ipAddress.getText().toString().trim();
        int pingCountEditText = Integer.valueOf(pingCountEtx.getText().toString().trim());
        boolean isIpAddress = Patterns.IP_ADDRESS.matcher(hostIp).matches();
        if(isIpAddress){
            hostIp = ipAddress.getText().toString().trim();
        }else{
            hostIp = intToIp(d.gateway);
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(PingCount, pingCountEditText);
        editor.commit();

        Intent serviceIntent = new Intent(MainActivity.this, MyPingService.class);
        serviceIntent.putExtra("hostIp", hostIp);
/*
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);

        //1 sec =1000 miliseconds
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                1000, pintent);*/

        //startService(new Intent(this, MyPingService .class));
        startService(serviceIntent);

    }

    public void stopPing(View view) {
        //alarm.cancel(pintent);
        stopService(new Intent(this, MyPingService .class));

    }

    public String intToIp(int i) {
        return ( i & 0xFF)+"."+((i >> 8 ) & 0xFF)+"."
                +((i >> 16 ) & 0xFF)+"."+((i >> 24 ) & 0xFF );
    }
}
