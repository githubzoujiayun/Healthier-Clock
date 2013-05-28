package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("UseSparseArrays")
public class SolutionStepSlider extends FragmentActivity implements OnClickListener, OnPageChangeListener {

  String solutionId;
  int stepNo;

  String title;

  TextView titleTextView;

  ViewPager pager;

  AcupointFragmentPagerAdapter pagerAdapter;

  View numberBar;

  ImageView[] imageViews;
  LinearLayout numbersView;

  Button back;

  DatabaseHelper helper;
  Dao<GenericSolution, String> genericSolutionStringDao;
  GenericSolution genericSolution;
  List<SolutionStep> steps;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solution_step_slider);

    solutionId = getIntent().getStringExtra("solutionId");

    stepNo = getIntent().getIntExtra("stepNo", -1);

    if (solutionId == null || stepNo == -1)
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
        genericSolutionStringDao = helper.getGenericSolutionStringDao();
        genericSolution = genericSolutionStringDao.queryForId(solutionId);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      if (genericSolution == null)
        return;

      try {
        JSONObject solutionJSON = new JSONObject(genericSolution.getData());

        JSONArray stepsArray = solutionJSON.getJSONArray("steps");

        steps = new ArrayList<SolutionStep>();
        
        for (int i = 0; i < stepsArray.length(); i++) {
          steps.add(SolutionStep.parseJsonObject((JSONObject) stepsArray.get(i)));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }

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

      pager.setCurrentItem(stepNo - 1, false);

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
  public static class AcupointFragment extends Fragment implements ViewFactory, OnClickListener {

    SolutionStep step;

    TextView contentTextView;

    ImageSwitcher imageSwitcher;

    View forward;
    View next;

    int index = 0;

    ImageLoader imageLoader;
    DisplayImageOptions options;
    ImageSize minImageSize;

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

      imageSwitcher = (ImageSwitcher) view.findViewById(R.id.images);
      imageSwitcher.setFactory(AcupointFragment.this);

      imageSwitcher.setImageResource(R.drawable.image_preview_large);

      forward = view.findViewById(R.id.forward);
      forward.setOnClickListener(this);

      next = view.findViewById(R.id.next);
      next.setOnClickListener(this);

      return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      contentTextView.setText(step.getContent());

      imageLoader = ImageLoader.getInstance();

      options = new DisplayImageOptions.Builder().showStubImage(R.drawable.image_preview_large).showImageForEmptyUri(R.drawable.image_preview_large).resetViewBeforeLoading().cacheInMemory()
          .cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888).delayBeforeLoading(1000).displayer(new RoundedBitmapDisplayer(5)).build();

      Display display = getActivity().getWindowManager().getDefaultDisplay();

      int width = display.getWidth(); // deprecated
//      int height = display.getHeight(); // deprecated

      minImageSize = new ImageSize(width - 20, 80);

      if (step.getAcupointIds() != null && step.getAcupointIds().size() > 0) {
        setImageSwitcherImage(step.getAcupointIds().get(index));
      }

    }

    public View makeView() {
      ImageView imageView = new ImageView(getActivity());
      imageView.setAdjustViewBounds(true);
      return imageView;
    }

    /**
     * 设置ImageSwitcher图片
     * 
     * @param acupointId
     */
    public void setImageSwitcherImage(int acupointId) {

      imageLoader.loadImage(getActivity(), RequestRoute.acupointImage(acupointId), options, new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(Bitmap loadedImage) {
          imageSwitcher.setImageDrawable(new BitmapDrawable(loadedImage));

          if (step.getAcupointIds().size() > 1) {
            forward.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
          }

        }
      });
    }

    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.forward:
        index--;
        if (index < 0) {
          index = step.getAcupointIds().size() - 1;
        }
        setImageSwitcherImage(step.getAcupointIds().get(index));
        break;

      case R.id.next:
        index++;
        if (index >= step.getAcupointIds().size()) {
          index = 0;
        }
        setImageSwitcherImage(step.getAcupointIds().get(index));
        break;

      default:
        break;
      }
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
