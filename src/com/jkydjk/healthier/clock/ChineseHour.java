package com.jkydjk.healthier.clock;

import java.util.ArrayList;

import com.jkydjk.healthier.clock.util.Log;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;

public class ChineseHour extends FragmentActivity implements OnPageChangeListener {

  static final int NUM_ITEMS = 12;

  private LayoutInflater inflater;
  private ViewPager pager;
  private ArrayList<View> pages;

  private SolutionFragmentPagerAdapter pagerAdapter;

  private Activity main;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chinese_hour);

    pager = (ViewPager) findViewById(R.id.pager);
    pagerAdapter = new SolutionFragmentPagerAdapter(getSupportFragmentManager());
    pager.setAdapter(pagerAdapter);
    inflater = getLayoutInflater();
    pager.setOnPageChangeListener(this);
  }

  // 指引页面更改事件监听器
  public void onPageScrollStateChanged(int state) {

  }

  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//    Log.v("position: " + position);
  }

  public void onPageSelected(int position) {

  }

  public static class SolutionFragmentPagerAdapter extends FragmentPagerAdapter {
    public SolutionFragmentPagerAdapter(FragmentManager fm) {
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
      // mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//      View view = inflater.inflate(R.layout.loading_page, container, false);
//      return view;

       return buildSolutionPage(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      Log.v("onActivityCreated:" + this);
    }

    public View buildSolutionPage(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.hour_solution, container, false);

      final Healthier activity = (Healthier) getActivity().getParent();

      View content = view.findViewById(R.id.content);
      final View actionsLayout = view.findViewById(R.id.actions);

      Button favorite = (Button) view.findViewById(R.id.favorite);
      favorite.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          Log.v("favorite on click");
        }
      });

      return view;
    }

  }

}