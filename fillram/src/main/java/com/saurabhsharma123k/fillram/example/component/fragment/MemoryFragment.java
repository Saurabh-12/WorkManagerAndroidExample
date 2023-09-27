package com.saurabhsharma123k.fillram.example.component.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.saurabhsharma123k.fillram.example.MainActivity;
import com.saurabhsharma123k.fillram.example.R;
import com.saurabhsharma123k.fillram.example.utils.ByteArrayWrapper;
import com.saurabhsharma123k.fillram.example.utils.MemoryUtils;
import com.saurabhsharma123k.fillram.example.utils.Utils;
import com.saurabhsharma123k.fillram.example.view.ArcView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MemoryFragment extends BaseFragment {
    public static final String TAG = "ECRT_MemoryFragment";

    @BindView(R.id.memory_total_value) TextView mTextTotal;
    @BindView(R.id.memory_free_value) TextView mTextFree;
    //@InjectView(R.id.memory_total_value) TextView mTextCurrent;
    @BindView(R.id.memory_low_value) TextView mTextLow;
    @BindView(R.id.fill_ram_rate_tv) TextView mTextRate;
    @BindView(R.id.memory_arc) ArcView mArc;

    @BindView(R.id.button_memory) Button mButtonFill;

    private MemoryUtils mMemoryUtils;
    private Handler mHandler = new Handler();
    private Handler memoryHandler = new Handler();
    private Runnable mRunnable;
    private ArrayList<ByteArrayWrapper> mAllocations = new ArrayList<>();

    private boolean mUpdate;

    public static MemoryFragment newInstance() {
        return new MemoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMemoryUtils = MemoryUtils.get(mActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_memory, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mArc.setTextBottom("RAM");
    }

    @Override
    public void onResume() {
        super.onResume();
        mUpdate = true;
        mHandler.post(new UpdateRunnable());
    }

    @Override
    public void onPause() {
        super.onPause();
        mUpdate = false;
    }

    @OnClick(R.id.button_memory)
    public void onClickFill() {
     if (mButtonFill.getTag() == null) {
         String delay = Utils.getDelayIntervalFromSDCard();
         Log.d("ECRT ", "Delay msg: "+delay);
         if(delay == null || delay.equals(""))
         {
             //Toast.makeText(mActivity, "Please grant SDcard Read permission " +
               //      "by going Setting >> app>>Permission", Toast.LENGTH_SHORT).show();
         }else if(delay.equals("NoFile")){
             Log.d("ECRT ", "No config file for Delay ! going with byDefaultValue");
             //Toast.makeText(mActivity, "Please grant SDcard Read permission " +
                //     "by going Setting >> app>>Permission", Toast.LENGTH_SHORT).show();
         }else{
             String [] spiltData = delay.split("=");
             if(spiltData[4].trim() != null && spiltData[5].trim() != null)
             {
                 Log.d("ECRT ", " Spilt Data 4 : "+spiltData[4]+" Spilt data 5 : "+spiltData[5]);
                 Utils.DELAY_IN_MILLI = 1000*Integer.valueOf(spiltData[5].trim());
                 mTextRate.setText("30 mb/"+spiltData[5].trim()+"sec");
             }else{
                 Log.d("ECRT ", " Spilt Data 1 : "+spiltData[0]+" Spilt data 2 : "+spiltData[1]);
                 Utils.DELAY_IN_MILLI = 1000*Integer.valueOf(spiltData[1].trim());
                 mTextRate.setText("30 mb/"+spiltData[1].trim()+"sec");
             }
             Log.d("ECRT ", " Utils.DELAY_IN_MILLI "+Utils.DELAY_IN_MILLI);
         }
            //App.getServiceHolder().startServices(mActivity);
            startFillRAM();
            mButtonFill.setText(R.string.memory_fill_stop);
            mButtonFill.setTag("started");
        } else {
            ActivityManager am = (ActivityManager) mActivity.getSystemService(Activity.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(999);
            String packageName = mActivity.getPackageName();
            for (ActivityManager.RunningServiceInfo service : services) {
                if (service.process.startsWith(packageName)) {
                    Log.d(TAG,"Killing " + service.process);
                    Process.sendSignal(service.pid, Process.SIGNAL_KILL);
                }
            }
            //App.getServiceHolder().stopServices();
            stopFillRAM();
            mButtonFill.setText(R.string.memory_fill_start);
            mButtonFill.setTag(null);
        }
    }

    class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            if (mUpdate) {
                mMemoryUtils.update();
                mTextTotal.setText(mMemoryUtils.getRamSize() + "");
                mTextFree.setText(mMemoryUtils.getAvailableMemory() + "");
                mTextLow.setText(String.valueOf(mMemoryUtils.isLowMemory()).toUpperCase());
                mArc.setProgress(mMemoryUtils.getFreeMemoryPercentage());
                mMemoryUtils.getMemoryThreshold();

                mHandler.postDelayed(this, Utils.UPDATE_UI_IN_MILLI);
            } else {
                mHandler.removeCallbacks(this);
            }
        }
    }
    int count = 1;
    byte[] bytes;
    private void startFillRAM()
    {

        memoryHandler.postDelayed( mRunnable = new Runnable() {
            public void run() {
                //Calling native method to fill RAM
                Log.d(TAG,"Attempting Allocation...");
               // if (MemoryUtils.isMemoryAvailable()) {
                    int success = ((MainActivity)getActivity()).sendData(Utils.MEMORY_2MB, count);
                    if (success == 0) {
                        Log.v("ECRT_JAVA" , "Failed to allocate native memory : size " + Utils.MEMORY_2MB * count);
                        Log.v("ECRT_JAVA" , "Native free heap memory : getNativeHeapFreeSize: " + Debug.getNativeHeapFreeSize());
                    }else{
                        Log.d(TAG,"Allocated new block native size: " + Utils.MEMORY_2MB * count);
                    }
                    //mAllocations.add(new ByteArrayWrapper(bytes));
                //}
                count++;
                memoryHandler.postDelayed(mRunnable, Utils.DELAY_IN_MILLI);
            }
        }, Utils.DELAY_IN_MILLI);
    }

    private void stopFillRAM()
    {
       // ((MainActivity)getActivity()).freeMalloc();
        memoryHandler.removeCallbacks(mRunnable); //stop handler
    }


}
