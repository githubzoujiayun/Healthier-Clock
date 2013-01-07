package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Acupoint;
import com.jkydjk.healthier.clock.entity.Meridian;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;

public class AcupointSlider extends FragmentActivity implements OnClickListener {

  ArrayList<Integer> acupointIds;

  ViewPager pager;
  ArrayList<View> pages;

  AcupointFragmentPagerAdapter pagerAdapter;

  View numberBar;
  View loading;
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
    numberBar = findViewById(R.id.number_bar);
    loading = findViewById(R.id.loading);

    back = (Button) findViewById(R.id.back);
    back.setOnClickListener(this);

    helper = new DatabaseHelper(AcupointSlider.this);
    try {
      acupointDao = helper.getAcupointDao();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    pagerAdapter = new AcupointFragmentPagerAdapter(getSupportFragmentManager());
    pagerAdapter.acupointIds = acupointIds;
    pager.setAdapter(pagerAdapter);
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
  public static class AcupointFragment extends Fragment {

    Integer acupointId;
    Acupoint acupoint;

    View loading;
    ScrollView contentScrollView;
    LinearLayout contentLayout;
    TextView titleTextView;

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
      return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

      AcupointSlider activity = (AcupointSlider) getActivity();

      try {
        acupoint = activity.acupointDao.queryForId(acupointId);
        Log.v("acupoint: " + acupoint);
      } catch (SQLException e) {
        e.printStackTrace();
      }

      titleTextView.setText(acupoint.getName());

      ActivityHelper.generateContentItem(contentLayout, "所在经络", acupoint.getMeridian().getName());
      
      ActivityHelper.generateContentItem(contentLayout, "准确定位", acupoint.getPosition());
      
      ActivityHelper.generateContentItem(contentLayout, "取穴技巧", acupoint.getLocateSkill());

      loading.setVisibility(View.GONE);
      contentScrollView.setVisibility(View.VISIBLE);
    }

    class FragmentTask extends AsyncTask<String, Integer, String> {

      @Override
      protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        return null;
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
}