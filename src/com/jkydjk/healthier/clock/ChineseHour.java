package com.jkydjk.healthier.clock;

import java.util.ArrayList;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class ChineseHour extends Activity implements OnClickListener, OnPageChangeListener {

    private LayoutInflater inflater;
    private ViewPager pager;
    private ArrayList<View> pages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chinese_hour);

        pager = (ViewPager) findViewById(R.id.pager);

        inflater = getLayoutInflater();
        
        View page01 = inflater.inflate(R.layout.hour_solution, null);
        View page02 = inflater.inflate(R.layout.hour_solution, null);

        pages = new ArrayList<View>();
        pages.add(page01);
        pages.add(page02);

        pager.setAdapter(new GuidePageAdapter());
        pager.setOnPageChangeListener(this);
        
        Log.v(getCurrentFocus()+"");
        
//        try {
//            Context context = createPackageContext("com.jkydjk.healthier.clock", CONTEXT_INCLUDE_CODE | CONTEXT_IGNORE_SECURITY);
//            Class clazz = context.getClassLoader().loadClass("com.jkydjk.healthier.clock.Healthier");
//            BaseActivity owner = (BaseActivity)clazz.newInstance();
//            Log.v(owner+"");
//            
////            this.getCurrentActivity().findViewById(id)
////            owner.getCurrentFocus().findViewById(id)
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.back:
//            finish();
            break;
        }
    }

    // 指引页面更改事件监听器
    public void onPageScrollStateChanged(int state) {
        // TODO Auto-generated method stub
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // TODO Auto-generated method stub
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

    // 指引页面数据适配器
    private class GuidePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(pages.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(pages.get(position));
            return pages.get(position);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            // TODO Auto-generated method stub
        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View container) {
            // TODO Auto-generated method stub
        }

        @Override
        public void finishUpdate(View container) {
            // TODO Auto-generated method stub
        }
    }

}