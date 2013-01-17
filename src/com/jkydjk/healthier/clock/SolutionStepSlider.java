package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Acupoint;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

@SuppressLint("UseSparseArrays")
public class SolutionStepSlider extends FragmentActivity implements OnClickListener, OnPageChangeListener {

  int solutionId;
  int stepNo;

  String title;

  TextView titleTextView;

  ViewPager pager;

  AcupointFragmentPagerAdapter pagerAdapter;

  View numberBar;

  ImageView[] imageViews;
  LinearLayout numbersView;

  Button back;

  Solution solution;
  List<SolutionStep> steps;

  DatabaseHelper helper;

  Dao<Solution, Integer> solutionDao;
  Dao<SolutionStep, Integer> solutionStepDao;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solution_step_slider);

    solutionId = getIntent().getIntExtra("solutionId", -1);
    stepNo = getIntent().getIntExtra("stepNo", -1);

    if (solutionId == -1 || stepNo == -1)
      finish();

    titleTextView = (TextView) findViewById(R.id.title_text);

    title = getString(R.string.steps);

    pager = (ViewPager) findViewById(R.id.pager);

    back = (Button) findViewById(R.id.back);
    back.setOnClickListener(this);

    numberBar = findViewById(R.id.number_bar);

    new Task().execute();
  }

  class Task extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
      try {
        helper = new DatabaseHelper(SolutionStepSlider.this);
        solutionStepDao = helper.getSolutionStepDao();
        steps = solutionStepDao.queryForEq("solution_id", solutionId);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      if (steps == null || steps.size() == 0)
        return;

      if (steps.size() > 1) {
        imageViews = new ImageView[steps.size()];
        numbersView = (LinearLayout) findViewById(R.id.numbers);

        for (int i = 0; i < steps.size(); i++) {
          ImageView imageView = new ImageView(SolutionStepSlider.this);
          imageView.setLayoutParams(new LayoutParams(20, 20));
          imageView.setPadding(20, 0, 20, 0);
          imageViews[i] = imageView;
          if (i == 0) {
            imageViews[i].setBackgroundResource(R.drawable.page_indicator_black_focused);
          } else {
            imageViews[i].setBackgroundResource(R.drawable.page_indicator_black);
          }
          numbersView.addView(imageViews[i]);
        }
        numberBar.setVisibility(View.VISIBLE);
      }

      pagerAdapter = new AcupointFragmentPagerAdapter(getSupportFragmentManager());
      pagerAdapter.steps = steps;
      pager.setAdapter(pagerAdapter);
      pager.setOnPageChangeListener(SolutionStepSlider.this);

      pager.setCurrentItem(stepNo - 1, true);

      titleTextView.setText(title + " " + (pager.getCurrentItem() + 1) + "/" + steps.size());

      super.onPostExecute(result);
    }

  }

  /**
   * 
   * @author miclle
   * 
   */
  public static class AcupointFragmentPagerAdapter extends FragmentPagerAdapter {

    List<SolutionStep> steps;

    public AcupointFragmentPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return steps.size();
    }

    @Override
    public Fragment getItem(int position) {
      return AcupointFragment.newInstance(steps.get(position));
    }
  }

  /**
   * 
   * @author miclle
   * 
   */
  public static class AcupointFragment extends Fragment implements ViewFactory {

    SolutionStep step;

    TextView contentTextView;

    View imageWrapper;
    ImageSwitcher imageSwitcher;

    List<String> imageSources = new ArrayList<String>();

    View forward;
    View next;

    int index = 0;

    static AcupointFragment newInstance(SolutionStep step) {
      AcupointFragment fragment = new AcupointFragment();
      fragment.step = step;
      return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.step, container, false);

      contentTextView = (TextView) view.findViewById(R.id.content);

      imageWrapper = view.findViewById(R.id.image_wrapper);

      imageSwitcher = (ImageSwitcher) view.findViewById(R.id.images);
      imageSwitcher.setFactory(AcupointFragment.this);

      forward = view.findViewById(R.id.forward);
      next = view.findViewById(R.id.next);

      return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

      contentTextView.setText(step.getContent());
    }

    public View makeView() {
      ImageView imageView = new ImageView(getActivity());
      imageView.setAdjustViewBounds(true);
      return imageView;
    }

  }

  /**
   * 点击处理
   */
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    default:
      break;
    }
  }

  /**
   * 页面滑动
   */
  // 指引页面更改事件监听器
  public void onPageScrollStateChanged(int state) {
  }

  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
  }

  public void onPageSelected(int position) {

    titleTextView.setText(title + " " + (position + 1) + "/" + steps.size());

    for (int i = 0; i < imageViews.length; i++) {
      imageViews[position].setBackgroundResource(R.drawable.page_indicator_black_focused);
      if (position != i) {
        imageViews[i].setBackgroundResource(R.drawable.page_indicator_black);
      }
    }
  }

}
