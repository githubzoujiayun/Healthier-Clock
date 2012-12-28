package com.jkydjk.healthier.clock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkydjk.healthier.clock.database.DatabaseManager;
import com.jkydjk.healthier.clock.database.SolutionDatabaseHelper;
import com.jkydjk.healthier.clock.entity.Hour;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.Log;

public class ChineseHour extends FragmentActivity implements OnPageChangeListener {

  private ViewPager pager;

  private SolutionFragmentPagerAdapter pagerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chinese_hour);

    pager = (ViewPager) findViewById(R.id.pager);
    pagerAdapter = new SolutionFragmentPagerAdapter(getSupportFragmentManager());
    pager.setAdapter(pagerAdapter);
    pager.setOnPageChangeListener(this);
    Time time = new Time();
    time.setToNow();
    int hour = Hour.from_time_hour(time.hour);
    pager.setCurrentItem(hour == 12 ? 0 : hour);
  }

  public void onPageScrollStateChanged(int arg0) {
    // TODO Auto-generated method stub

  }

  public void onPageScrolled(int arg0, float arg1, int arg2) {
    // TODO Auto-generated method stub

  }

  public void onPageSelected(int position) {
    Fragment fragment = pagerAdapter.getItem(position);
    FragmentActivity fa = fragment.getActivity();
    ScrollView sv = (ScrollView) fa.findViewById(R.id.content_scroll_view);
    sv.scrollTo(0, 0);
  }

  /**
   * 
   * @author miclle
   * 
   */
  public static class SolutionFragmentPagerAdapter extends FragmentPagerAdapter {

    Map<Integer, SolutionFragment> fragments = new HashMap<Integer, ChineseHour.SolutionFragment>();

    public SolutionFragmentPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return 12;
    }

    @Override
    public Fragment getItem(int position) {
      SolutionFragment fragment = fragments.get(position);
      if (fragment == null) {
        fragment = SolutionFragment.newInstance(position);
        fragments.put(position, fragment);
      }
      return fragment;
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

    LinearLayout solutionContentView;

    View actions;

    View loading;

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

      solutionContentView = (LinearLayout) view.findViewById(R.id.solution_content);

      loading = view.findViewById(R.id.loading);
      return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      SolutionTask task = new SolutionTask();
      task.execute();
    }

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

      SQLiteDatabase database;

      @Override
      protected String doInBackground(String... params) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        String today = dateFormat.format(calendar.getTime());

        database = new SolutionDatabaseHelper(getActivity()).getWritableDatabase();

        Cursor cursor = database.rawQuery("select * from solutions where ableon_type = ? and ableon_id = ?", new String[] { "hour", hour + "" });

        if (cursor == null || cursor.getCount() == 0) {

          HttpClientManager connect = new HttpClientManager(getActivity(), HttpClientManager.REQUEST_PATH + RequestRoute.SOLUTION_HOUR);

          connect.addParam("hour", hour + "");

          try {
            database.beginTransaction();

            connect.execute(ResuestMethod.GET);

            JSONObject json = new JSONObject(connect.getResponse());

            JSONObject solutionJSON = json.getJSONObject("solution");

            // database.update("hours", cvs, "id = ?", null });

            database.insert("solutions", null, Solution.jsonObjectToContentValues("hour", hour, solutionJSON));

            JSONArray stepsArray = solutionJSON.getJSONArray("steps");

            for (int i = 0; i < stepsArray.length(); i++) {
              database.insert("steps", null, SolutionStep.jsonObjectToContentValues((JSONObject) stepsArray.get(i)));
            }

            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();

          } catch (Exception e) { // errorMessage = "网络访问异常";
            Log.v("Insert failed!");
            database.endTransaction();
            database.close();
          }
        }

        database.close();

        return null;
      }

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
      }

      @Override
      protected void onPostExecute(String result) {
        Solution solution = Solution.getSolution(getActivity(), "hour", hour);
        if (solution != null) {
          loading.setVisibility(View.GONE);
          View solutionView = getLayoutInflater(null).inflate(R.layout.hour_solution, null);

          TextView titleTextView = (TextView) solutionView.findViewById(R.id.title);
          titleTextView.setText(solution.getTitle());

          TextView efficacy = (TextView) solutionView.findViewById(R.id.efficacy);
          efficacy.setText(solution.getEffect());

          TextView tips = (TextView) solutionView.findViewById(R.id.tips);
          tips.setText(solution.getNote());

          solutionContentView.removeAllViews();
          solutionContentView.addView(solutionView);
          solutionContentView.setVisibility(View.VISIBLE);
        }
      }

    }
  }

}