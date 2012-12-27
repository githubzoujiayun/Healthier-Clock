package com.jkydjk.healthier.clock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkydjk.healthier.clock.database.DatabaseManager;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;

public class ChineseHour extends FragmentActivity {

  private ViewPager pager;

  private SolutionFragmentPagerAdapter pagerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chinese_hour);
    pager = (ViewPager) findViewById(R.id.pager);
    pagerAdapter = new SolutionFragmentPagerAdapter(getSupportFragmentManager());
    pager.setAdapter(pagerAdapter);
    // pager.setOnPageChangeListener(this);

    // Time time = new Time();
    // time.setToNow();
    // Log.v("Time.hour: " + time.hour);

    // pager.setCurrentItem(11);
    // pager.setCurrentItem(11, true);
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
      return 12;
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
    Button concernButton;
    TextView hourNameTextView;
    TextView hourTimeIntervalTextView;
    View hourRemind;
    TextView appropriateTextView;
    TextView tabooTextView;
    View actions;

    public int hour;

    SharedPreferences sharedPreferences;

    String hourName;
    String hourTimeInterval;
    String hourAppropriate;
    String hourTaboo;

    static SolutionFragment newInstance(int position) {
      SolutionFragment fragment = new SolutionFragment();
      Bundle bundle = new Bundle();
      bundle.putInt("hour", position + 1);
      fragment.setArguments(bundle);
      return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      hour = getArguments().getInt("hour");
      sharedPreferences = getActivity().getSharedPreferences("configure", Context.MODE_PRIVATE);
      SQLiteDatabase database = DatabaseManager.openDatabase(this.getActivity());
      Cursor cursor = database.rawQuery("select * from hours where _id = ?", new String[] { hour + "" });
      if (cursor != null && cursor.moveToFirst()) {
        hourName = cursor.getString(cursor.getColumnIndex("name"));
        hourTimeInterval = cursor.getString(cursor.getColumnIndex("interval"));
        hourAppropriate = cursor.getString(cursor.getColumnIndex("appropriate"));
        hourTaboo = cursor.getString(cursor.getColumnIndex("taboo"));
        cursor.close();
      }
      database.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.hour, container, false);

      contentScrollView = (ScrollView) view.findViewById(R.id.content_scroll_view);
      concernButton = (Button) view.findViewById(R.id.concern_button);
      int concernResourceID = sharedPreferences.getBoolean("concern_" + hour, false) ? R.drawable.icon_heart_small_red : R.drawable.icon_heart_small_white;
      Drawable concernIcon = getResources().getDrawable(concernResourceID);
      concernIcon.setBounds(0, 0, concernIcon.getMinimumWidth(), concernIcon.getMinimumHeight());
      concernButton.setCompoundDrawables(concernIcon, null, null, null);
      concernButton.setOnClickListener(this);

      hourNameTextView = (TextView) view.findViewById(R.id.hour_name_text_view);
      hourTimeIntervalTextView = (TextView) view.findViewById(R.id.hour_time_interval_text_view);
      appropriateTextView = (TextView) view.findViewById(R.id.appropriate_text_view);
      tabooTextView = (TextView) view.findViewById(R.id.taboo_text_view);
      actions = view.findViewById(R.id.actions);
      hourRemind = view.findViewById(R.id.hour_remind);
      hourRemind.setOnClickListener(this);

      hourNameTextView.setText(hourName);
      hourTimeIntervalTextView.setText(hourTimeInterval);
      appropriateTextView.setText("宜：" + hourAppropriate);
      tabooTextView.setText("忌：" + hourTaboo);
      return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      SolutionTask task = new SolutionTask();
      task.setFragment(this);
      task.execute("");
    }

    // @Override
    // public void onDestroy() {
    // super.onDestroy();
    // Log.v("Fragment onDestory()" + hour);
    // }
    //
    // @Override
    // public void onPause() {
    // super.onPause();
    // Log.v("Fragment onPause()" + hour);
    // }
    //
    // @Override
    // public void onResume() {
    // super.onResume();
    // Log.v("Fragment onResume()" + hour);
    // }

    public void onClick(View v) {
      switch (v.getId()) {
      case R.id.concern_button: // 特别关注
        boolean isConcern = sharedPreferences.getBoolean("concern_" + hour, false);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean("concern_" + hour, !isConcern);
        if (editor.commit()) {
          int concernResourceID = !isConcern ? R.drawable.icon_heart_small_red : R.drawable.icon_heart_small_white;
          Drawable concernIcon = getResources().getDrawable(concernResourceID);
          concernIcon.setBounds(0, 0, concernIcon.getMinimumWidth(), concernIcon.getMinimumHeight());
          concernButton.setCompoundDrawables(concernIcon, null, null, null);
        }

        break;

      case R.id.hour_remind:
        Intent intent = new Intent(getActivity(), HourRemind.class);
        intent.putExtra("hour", hour);
        startActivity(intent);
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

    class SolutionTask extends AsyncTask<String, Integer, String> {

      Fragment fragment;

      public void setFragment(Fragment fragment) {
        this.fragment = fragment;
      }

      @Override
      protected String doInBackground(String... params) {

        int h = hour;

        Log.v("hour: " + hour);

        // SQLiteDatabase database =
        // DatabaseManager.openDatabase(fragment.getActivity());
        // HttpClientManager connect = new
        // HttpClientManager(fragment.getActivity(),
        // HttpClientManager.REQUEST_PATH + RequestRoute.SOLUTION_HOUR);
        // connect.addParam("hour", "");
        // try {
        // connect.execute(ResuestMethod.GET);
        // JSONObject json = new JSONObject(connect.getResponse());
        // JSONObject hourJSONObject = json.getJSONObject("hour");
        // JSONObject solutionJSONObject = json.getJSONObject("solution");
        // ContentValues cvs = new ContentValues();
        // // cvs.put("updated_at", version);
        // database.update("constitutions", cvs, "type = ?", new String[] {});
        // database.close();
        // return null;
        // } catch (Exception e) { // errorMessage = "网络访问异常";
        // if (database != null)
        // database.close();
        // }
        return null;
      }

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
      }

      @Override
      protected void onPostExecute(String result) {
        // fa.findViewById(id)
      }

    }

  }
}