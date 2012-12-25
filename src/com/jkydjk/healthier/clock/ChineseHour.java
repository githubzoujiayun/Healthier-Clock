package com.jkydjk.healthier.clock;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.jkydjk.healthier.clock.util.Log;

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
    // Log.v("position: " + position);
  }

  public void onPageSelected(int position) {

  }

  /**
   * 
   * @author miclle
   *
   */
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

  /**
   * 
   * @author miclle
   * 
   */
  public static class SolutionFragment extends Fragment implements OnClickListener {

    ScrollView contentScrollView;
    View hourRemind;
    View actions;

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
      
      View view = inflater.inflate(R.layout.hour, container, false);

      contentScrollView = (ScrollView) view.findViewById(R.id.content);
      actions = view.findViewById(R.id.actions);

      hourRemind = view.findViewById(R.id.hour_remind);
      hourRemind.setOnClickListener(this);

//      final ImageButton todo = (ImageButton) view.findViewById(R.id.todo);
//      todo.setOnClickListener(this);
//
//      ImageButton favorite = (ImageButton) view.findViewById(R.id.favorite);
//      favorite.setOnClickListener(this);
//
//      ImageButton alarm = (ImageButton) view.findViewById(R.id.alarm);
//      alarm.setOnClickListener(this);
//
//      ImageButton process = (ImageButton) view.findViewById(R.id.process);
//      process.setOnClickListener(this);
//
//      ImageButton evaluate = (ImageButton) view.findViewById(R.id.evaluate);
//      evaluate.setOnClickListener(this);
//
//      ImageButton forwarding = (ImageButton) view.findViewById(R.id.forwarding);
//      forwarding.setOnClickListener(this);

//      content.setOnTouchListener(new OnTouchListener() {
//        public boolean onTouch(View v, MotionEvent event) {
//          todo.setVisibility(View.VISIBLE);
//          actions.setVisibility(View.INVISIBLE);
//          return false;
//        }
//      });

      return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      Log.v("onActivityCreated:" + this);
    }

    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.hour_remind:
        startActivity(new Intent(getActivity(), HourRemind.class));
        break;

      case R.id.todo:
        v.setVisibility(View.GONE);
        actions.setVisibility(View.VISIBLE);
        break;

      case R.id.favorite:

        break;

      case R.id.alarm:

        break;

      case R.id.process:
        startActivity(new Intent(getActivity(), Process.class));
        break;

      case R.id.evaluate:
        startActivity(new Intent(getActivity(), SolutionEvaluate.class));
        break;

      case R.id.forwarding:

        break;
        
      default:
        break;
      }
      
    }

  }

}