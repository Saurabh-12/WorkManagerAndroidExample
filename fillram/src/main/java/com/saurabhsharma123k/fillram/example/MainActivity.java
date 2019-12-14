package com.saurabhsharma123k.fillram.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.saurabhsharma123k.fillram.example.component.fragment.MemoryFragment;
import com.saurabhsharma123k.fillram.example.view.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {
    ViewPager mViewPager;
    SlidingTabLayout mTabLayout;
    Handler mHandler;
    Runnable mRunnable;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
        mViewPager.setAdapter(new PagerAdapter(MainActivity.this, getSupportFragmentManager()));
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native int sendData(int data_size, int count);





    public static enum NavItem {
        MEMORY(R.string.nav_memory);

        public int titleRes;

        NavItem(int resId) {
            titleRes = resId;
        }
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private static final NavItem[] ITEMS = NavItem.values();

        private Context mContext;

        public PagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (ITEMS[position]) {
                case MEMORY:
                    return MemoryFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return ITEMS.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getString(ITEMS[position].titleRes).toUpperCase();
        }
    }
}