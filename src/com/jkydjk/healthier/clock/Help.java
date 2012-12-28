package com.jkydjk.healthier.clock;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class Help extends BaseActivity implements OnPageChangeListener {

  private ArrayList<View> pages;

  private ViewPager pager;
  private View number;
  private ImageView[] imageViews;
  private ViewGroup group;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help);

    pager = (ViewPager) findViewById(R.id.pager);
    number = findViewById(R.id.number);

    LayoutInflater inflater = getLayoutInflater();
    View page01 = inflater.inflate(R.layout.help01, null);
    View page02 = inflater.inflate(R.layout.help02, null);
    View page03 = inflater.inflate(R.layout.help03, null);
    View page04 = inflater.inflate(R.layout.help04, null);
    View page05 = inflater.inflate(R.layout.transparent, null);

    pages = new ArrayList<View>();
    pages.add(page01);
    pages.add(page02);
    pages.add(page03);
    pages.add(page04);
    pages.add(page05);

    imageViews = new ImageView[pages.size()];

    group = (ViewGroup) findViewById(R.id.view_group);

    for (int i = 0; i < pages.size(); i++) {
      ImageView imageView = new ImageView(this);
      imageView.setLayoutParams(new LayoutParams(20, 20));
      imageView.setPadding(20, 0, 20, 0);
      imageViews[i] = imageView;

      if (i == 0) {
        // 默认选中第一张图片
        imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
      } else {
        imageViews[i].setBackgroundResource(R.drawable.page_indicator);
      }

      group.addView(imageViews[i]);
    }

    pager.setAdapter(new GuidePageAdapter());
    pager.setOnPageChangeListener(this);

  }

  // 指引页面更改事件监听器
  public void onPageScrollStateChanged(int state) {
    // TODO Auto-generated method stub
  }

  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    if (position == pages.size() - 1) {
      number.setVisibility(View.GONE);
      finish();
    }
  }

  public void onPageSelected(int position) {
    for (int i = 0; i < imageViews.length; i++) {
      imageViews[position].setBackgroundResource(R.drawable.page_indicator_focused);

      if (position != i) {
        imageViews[i].setBackgroundResource(R.drawable.page_indicator);
      }
    }
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