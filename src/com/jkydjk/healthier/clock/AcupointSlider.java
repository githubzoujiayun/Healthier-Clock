package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import org.json.JSONArray;

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
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Acupoint;
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

public class AcupointSlider extends FragmentActivity implements OnClickListener, OnPageChangeListener {

  ArrayList<Integer> acupointIds;

  ViewPager pager;

  ArrayList<View> pages;

  AcupointFragmentPagerAdapter pagerAdapter;

  View numberBar;

  ImageView[] imageViews;
  LinearLayout numbersView;

  Button back;

  DatabaseHelper helper;
  Dao<Acupoint, Integer> acupointDao;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acupoint_slider);

    acupointIds = getIntent().getIntegerArrayListExtra("acupoints");

    if (acupointIds == null)
      finish();

    pager = (ViewPager) findViewById(R.id.pager);

    back = (Button) findViewById(R.id.back);
    back.setOnClickListener(this);

    helper = new DatabaseHelper(AcupointSlider.this);

    try {
      acupointDao = helper.getAcupointDao();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    numberBar = findViewById(R.id.number_bar);
    imageViews = new ImageView[acupointIds.size()];

    numbersView = (LinearLayout) findViewById(R.id.numbers);

    for (int i = 0; i < acupointIds.size(); i++) {
      ImageView imageView = new ImageView(this);
      imageView.setLayoutParams(new LayoutParams(20, 20));
      imageView.setPadding(20, 0, 20, 0);
      imageViews[i] = imageView;
      if (i == 0) {
        // 默认选中第一张图片
        imageViews[i].setBackgroundResource(R.drawable.page_indicator_black_focused);
      } else {
        imageViews[i].setBackgroundResource(R.drawable.page_indicator_black);
      }
      numbersView.addView(imageViews[i]);
    }

    if (acupointIds.size() > 1) {
      numberBar.setVisibility(View.VISIBLE);
    }

    pagerAdapter = new AcupointFragmentPagerAdapter(getSupportFragmentManager());
    pagerAdapter.acupointIds = acupointIds;
    pager.setAdapter(pagerAdapter);
    pager.setOnPageChangeListener(this);
  }

  /**
   * 
   * @author miclle
   * 
   */
  public static class AcupointFragmentPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Integer> acupointIds;

    public AcupointFragmentPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return acupointIds.size();
    }

    @Override
    public Fragment getItem(int position) {
      return AcupointFragment.newInstance(acupointIds.get(position));
    }
  }

  /**
   * 
   * @author miclle
   * 
   */
  public static class AcupointFragment extends Fragment implements ViewFactory, OnClickListener {

    Integer acupointId;
    Acupoint acupoint;

    View loading;
    ScrollView contentScrollView;
    LinearLayout contentLayout;
    TextView titleTextView;

    ImageSwitcher imageSwitcher;

    List<String> imageSources = new ArrayList<String>();

    View forward;
    View next;

    int index = 0;

    ImageLoader imageLoader;
    DisplayImageOptions options;
    ImageSize minImageSize;

    static AcupointFragment newInstance(Integer acupointId) {
      AcupointFragment fragment = new AcupointFragment();
      fragment.acupointId = acupointId;
      return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.acupoint, container, false);
      loading = view.findViewById(R.id.loading);
      contentScrollView = (ScrollView) view.findViewById(R.id.content_scroll_view);
      contentLayout = (LinearLayout) view.findViewById(R.id.content);
      titleTextView = (TextView) view.findViewById(R.id.title);

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

      try {
        acupoint = ((AcupointSlider) getActivity()).acupointDao.queryForId(acupointId);
      } catch (SQLException e) {
        e.printStackTrace();
      }

      titleTextView.setText(acupoint.getName());
      
      if(acupoint.getMeridian()==null){
        ActivityHelper.generateContentItem(contentLayout, "所在经络", "经外奇穴");
      }else{
        ActivityHelper.generateContentItem(contentLayout, "所在经络", acupoint.getMeridian().getName());
      }
      
      ActivityHelper.generateContentItem(contentLayout, "准确定位", acupoint.getPosition());
      ActivityHelper.generateContentItem(contentLayout, "取穴技巧", acupoint.getLocateSkill());

      loading.setVisibility(View.GONE);
      contentScrollView.setVisibility(View.VISIBLE);

      imageLoader = ImageLoader.getInstance();

      options = new DisplayImageOptions.Builder().showStubImage(R.drawable.image_preview_large).showImageForEmptyUri(R.drawable.image_preview_large).resetViewBeforeLoading().cacheInMemory()
          .cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888).delayBeforeLoading(1000).displayer(new RoundedBitmapDisplayer(5)).build();

      Display display = getActivity().getWindowManager().getDefaultDisplay();

      int width = display.getWidth(); // deprecated
      int height = display.getHeight(); // deprecated

      minImageSize = new ImageSize(width - 20, 80);

      new FragmentTask().execute();
    }

    class FragmentTask extends AsyncTask<String, Integer, String> {

      @Override
      protected String doInBackground(String... params) {
        try {
          HttpClientManager connect = new HttpClientManager(getActivity(), RequestRoute.acupointImages(acupoint.getId()));

          connect.execute(ResuestMethod.GET);

          JSONArray images = new JSONArray(connect.getResponse());

          if (images != null) {
            for (int i = 0; i < images.length(); i++) {
              imageSources.add(images.getString(i));
            }
          }

        } catch (Exception e) {
          e.printStackTrace();
        }

        return null;
      }

      @Override
      protected void onPostExecute(String result) {
        if (imageSources != null && imageSources.size() > 0) {
          setImageSwitcherImage(imageSources.get(index));
        }
        super.onPostExecute(result);
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
     * @param source
     */
    public void setImageSwitcherImage(String source) {
      if (source == null)
        return;
      
      imageLoader.loadImage(getActivity(), RequestRoute.IMAGE_REQUEST_PATH + source, minImageSize, options, new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(Bitmap loadedImage) {
          imageSwitcher.setImageDrawable(new BitmapDrawable(loadedImage));

          if (imageSources.size() > 1) {
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
          index = imageSources.size() - 1;
        }
        setImageSwitcherImage(imageSources.get(index));
        break;

      case R.id.next:
        index++;
        if (index >= imageSources.size()) {
          index = 0;
        }
        setImageSwitcherImage(imageSources.get(index));
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
    for (int i = 0; i < imageViews.length; i++) {
      imageViews[position].setBackgroundResource(R.drawable.page_indicator_black_focused);
      if (position != i) {
        imageViews[i].setBackgroundResource(R.drawable.page_indicator_black);
      }
    }
  }

  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();
    EasyTracker.getInstance().activityStart(this);
  }

  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    EasyTracker.getInstance().activityStop(this); // Add this method.
  }

}
