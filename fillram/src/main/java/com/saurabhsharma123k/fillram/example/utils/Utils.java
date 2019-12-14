package com.saurabhsharma123k.fillram.example.utils;

import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

    public static int DELAY_IN_MILLI = 1000*10;
    public static final int UPDATE_UI_IN_MILLI = 1000;
    public static final int START_SERVICE_DELAY_IN_MILLI = 1000;
    public static final int MEMORY_2MB = 1024*1024*30;

    public static int dpToPixels(Resources resources, float pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, resources.getDisplayMetrics());
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static String getDelayIntervalFromSDCard()
    {
        StringBuffer stringBuffer = new StringBuffer();
        String aDataRow = "";
        String aBuffer = "";
        if(isExternalStorageAvailable()){
            Log.d("ECRT_FillRamm1"," Extenal SDcard avilable");
            try {
                File myFile = new File("/sdcard/FillRamDelay.txt");
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(
                        new InputStreamReader(fIn));
                while ((aDataRow = myReader.readLine()) != null) {
                    aBuffer += aDataRow + "\n";
                }
                myReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                aBuffer = "NoFile";
            }
        }else{
            Log.d("ECRT_FillRamm1"," Extenal SDcard NOT avilable");
        }

        return aBuffer;
    }
}
