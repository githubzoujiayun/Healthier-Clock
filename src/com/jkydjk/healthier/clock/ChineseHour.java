package com.jkydjk.healthier.clock;

import java.util.ArrayList;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class ChineseHour extends FragmentActivity implements OnPageChangeListener {

    static final int NUM_ITEMS = 10;

    private LayoutInflater inflater;
    private ViewPager pager;
    private ArrayList<View> pages;

    private MyAdapter mAdapter;

    private Activity main;
    private View titleBar;
    private View tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chinese_hour);

        pager = (ViewPager) findViewById(R.id.pager);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        pager.setAdapter(mAdapter);
        
        inflater = getLayoutInflater();

        pager.setOnPageChangeListener(this);

        main = getParent();
        tabs = main.findViewById(android.R.id.tabs);
        titleBar = main.findViewById(R.id.title_bar);
        
        
    }

    private void toggleFullScreen(int type) {
        switch (type) {
        case Healthier.FULL_SCREEN_NO:
            tabs.setVisibility(View.GONE);
            titleBar.setVisibility(View.GONE);
            break;

        case Healthier.FULL_SCREEN_YES:
            tabs.setVisibility(View.VISIBLE);
            titleBar.setVisibility(View.VISIBLE);
            break;

        case Healthier.FULL_SCREEN_AUTO:
            tabs.setVisibility(tabs.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            titleBar.setVisibility(titleBar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            break;

        default:
            break;
        }
    }

    // 指引页面更改事件监听器
    public void onPageScrollStateChanged(int state) {
        // TODO Auto-generated method stub
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        tabs.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);
    }

    public void onPageSelected(int position) {
        // for (int i = 0; i < imageViews.length; i++) {
        // imageViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
        //
        // if (position != i) {
        // imageViews[i].setBackgroundResource(R.drawable.page_indicator);
        // }
        // }
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return SolutionFragment.newInstance(position);
        }
    }

    public static class SolutionFragment extends Fragment {

        int mNum;

        static SolutionFragment newInstance(int num) {
            SolutionFragment fragment = new SolutionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("num", num);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.hour_solution, container, false);
            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            FragmentActivity activity = getActivity();
            
//            activity.setContentView(layoutResID)
            
            View content = activity.findViewById(R.id.content);
            content.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Log.v("solution Fragment onTouch");
                    return false;
                }
            });
            
        }

    }

}