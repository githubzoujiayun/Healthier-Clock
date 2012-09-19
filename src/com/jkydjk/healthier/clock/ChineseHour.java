package com.jkydjk.healthier.clock;

import java.util.ArrayList;

import com.jkydjk.healthier.clock.util.Log;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
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
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;

public class ChineseHour extends FragmentActivity implements OnPageChangeListener {

  static final int NUM_ITEMS = 10;

  private boolean isFullScreen = false;

  private LayoutInflater inflater;
  private ViewPager pager;
  private ArrayList<View> pages;

  private SolutionFragmentPagerAdapter pagerAdapter;

  private Activity main;
  private View titleBar;
  private View titleBarShadow;
  private View tabs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chinese_hour);

    pager = (ViewPager) findViewById(R.id.pager);

    pagerAdapter = new SolutionFragmentPagerAdapter(getSupportFragmentManager());

    pager.setAdapter(pagerAdapter);

    inflater = getLayoutInflater();

    pager.setOnPageChangeListener(this);

    main = getParent();
    titleBar = main.findViewById(R.id.title_bar);
    titleBarShadow = main.findViewById(R.id.title_bar_shadow);
    tabs = main.findViewById(android.R.id.tabs);
  }

  private void titlebarSlideUp(){
//    Animation titlebarSlideUp = AnimationUtils.loadAnimation(this, R.anim.titlebar_slide_up);
//    titlebarSlideUp.setAnimationListener(new AnimationListener() {
//      public void onAnimationStart(Animation animation) {
//
//      }
//
//      public void onAnimationRepeat(Animation animation) {
//        
//      }
//
//      public void onAnimationEnd(Animation animation) {
//        titleBar.setVisibility(View.GONE);
//      }
//    });
//
//    titleBar.startAnimation(titlebarSlideUp);
  }
  
  private void toggleFullScreen(int type) {
    switch (type) {
    case Healthier.FULL_SCREEN_NO:
      tabs.setVisibility(View.GONE);
      titlebarSlideUp();
      titleBarShadow.setVisibility(View.GONE);
      break;

    case Healthier.FULL_SCREEN_YES:
      tabs.setVisibility(View.VISIBLE);
      titleBar.setVisibility(View.VISIBLE);
      titleBarShadow.setVisibility(View.VISIBLE);
      break;

    case Healthier.FULL_SCREEN_AUTO:
      tabs.setVisibility(tabs.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
      
      if(titleBar.getVisibility() == View.VISIBLE){
        titlebarSlideUp();
      }else{
        titleBar.setVisibility(View.VISIBLE);
      }
      
      
      titleBarShadow.setVisibility(titleBarShadow.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
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
    // toggleFullScreen(Healthier.FULL_SCREEN_YES);
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

      View view = inflater.inflate(R.layout.hour_solution, container, false);

      final ChineseHour activity = (ChineseHour) getActivity();

      View content = view.findViewById(R.id.content);

      content.setOnTouchListener(new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

          switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            activity.isFullScreen = true;
            break;

          case MotionEvent.ACTION_MOVE:
            activity.isFullScreen = false;
            activity.toggleFullScreen(Healthier.FULL_SCREEN_NO);
            break;

          case MotionEvent.ACTION_UP:
            activity.toggleFullScreen(activity.isFullScreen == true ? Healthier.FULL_SCREEN_AUTO : Healthier.FULL_SCREEN_NO);
            break;

          default:
            break;
          }

          return false;
        }
      });

      return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

    }

  }

}