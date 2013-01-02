package com.jkydjk.healthier.clock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

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
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jkydjk.healthier.clock.database.SolutionDatabaseHelper;
import com.jkydjk.healthier.clock.entity.Hour;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;

public class ChineseHour extends BaseActivity implements OnClickListener, OnTouchListener {

  ScrollView contentScrollView;
  TextView updatedAtTextView;
  Button concernButton;
  TextView hourNameTextView;
  TextView hourTimeIntervalTextView;
  View hourRemind;
  TextView appropriateTextView;
  TextView tabooTextView;

  LinearLayout solutionContentView;

  View actionsToolbar;
  View todoButton;
  View actionsLayout;

  ImageButton favoriteImageButton;
  ImageButton alarmImageButton;
  ImageButton processImageButton;
  ImageButton evaluateImageButton;
  ImageButton forwardingImageButton;

  LinearLayout loading;

  public Hour hour;
  public int hourID;

  SharedPreferences sharedPreferences;

  long solutionID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.chinese_hour);

    sharedPreferences = getSharedPreferences("chinese_hour", Context.MODE_PRIVATE);

    Time time = new Time();
    time.setToNow();

    hourID = Hour.from_time_hour(time.hour);

    hour = Hour.find(ChineseHour.this, hourID);

    contentScrollView = (ScrollView) findViewById(R.id.content_scroll_view);
    contentScrollView.setOnTouchListener(this);

    concernButton = (Button) findViewById(R.id.concern_button);
    setConcernIcon(sharedPreferences.getBoolean("concern_" + hourID, false));
    concernButton.setOnClickListener(this);

    updatedAtTextView = (TextView) findViewById(R.id.updated_at);

    hourNameTextView = (TextView) findViewById(R.id.hour_name_text_view);
    hourTimeIntervalTextView = (TextView) findViewById(R.id.hour_time_interval_text_view);
    appropriateTextView = (TextView) findViewById(R.id.appropriate_text_view);
    tabooTextView = (TextView) findViewById(R.id.taboo_text_view);

    hourNameTextView.setText(hour.getName());
    hourTimeIntervalTextView.setText(hour.getTimeInterval());
    appropriateTextView.setText("宜：" + hour.getAppropriate());
    tabooTextView.setText("忌：" + hour.getTaboo());

    hourRemind = findViewById(R.id.hour_remind);
    hourRemind.setOnClickListener(this);

    solutionContentView = (LinearLayout) findViewById(R.id.solution_content);

    actionsToolbar = findViewById(R.id.action_toolbar);

    todoButton = findViewById(R.id.todo_button);
    todoButton.setOnClickListener(this);

    actionsLayout = findViewById(R.id.actions_layout);

    favoriteImageButton = (ImageButton) findViewById(R.id.favorite);
    favoriteImageButton.setOnClickListener(this);

    alarmImageButton = (ImageButton) findViewById(R.id.alarm);
    alarmImageButton.setOnClickListener(this);

    processImageButton = (ImageButton) findViewById(R.id.process);
    processImageButton.setOnClickListener(this);

    evaluateImageButton = (ImageButton) findViewById(R.id.evaluate);
    evaluateImageButton.setOnClickListener(this);

    forwardingImageButton = (ImageButton) findViewById(R.id.forwarding);
    forwardingImageButton.setOnClickListener(this);

    loading = (LinearLayout) findViewById(R.id.loading);

    new Task().execute();
  }

  /**
   * 
   * @author miclle
   * 
   */
  class Task extends AsyncTask<String, Integer, String> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {

      solutionID = sharedPreferences.getLong("hour_solution_" + hourID, 0);

      if (solutionID == 0) {

        if (!ActivityHelper.networkConnected(ChineseHour.this)) {
          return "网络未连接！";
        }

        SQLiteDatabase database = new SolutionDatabaseHelper(ChineseHour.this).getWritableDatabase();

        HttpClientManager connect = new HttpClientManager(ChineseHour.this, HttpClientManager.REQUEST_PATH + RequestRoute.SOLUTION_HOUR);

        connect.addParam("hour", hourID + "");

        try {
          database.beginTransaction();

          connect.execute(ResuestMethod.GET);

          JSONObject json = new JSONObject(connect.getResponse());

          JSONObject solutionJSON = json.getJSONObject("solution");

          String[] whereArgs = { solutionJSON.getLong("id") + "" };

          Cursor cursor = database.rawQuery("select solution_id, version from solutions where solution_id = ?", whereArgs);

          if (cursor.getCount() <= 0) {
            database.insert("solutions", null, Solution.jsonObjectToContentValues("hour", hourID, solutionJSON));

            JSONArray stepsArray = solutionJSON.getJSONArray("steps");

            for (int i = 0; i < stepsArray.length(); i++) {
              database.insert("steps", null, SolutionStep.jsonObjectToContentValues((JSONObject) stepsArray.get(i)));
            }

          } else {
            database.update("solutions", Solution.jsonObjectToContentValues("hour", hourID, solutionJSON), "solution_id = ?", whereArgs);

            JSONArray stepsArray = solutionJSON.getJSONArray("steps");

            for (int i = 0; i < stepsArray.length(); i++) {
              ContentValues cvs = SolutionStep.jsonObjectToContentValues((JSONObject) stepsArray.get(i));
              database.update("steps", cvs, "step_id = ?", new String[] { cvs.getAsString("step_id") });
            }

          }

          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
          Calendar calendar = Calendar.getInstance();
          String today = dateFormat.format(calendar.getTime());

          Editor editor = sharedPreferences.edit();

          editor.putLong("hour_solution_" + hourID, solutionJSON.getLong("id"));
          editor.putString("hour_solution_" + hourID + "_updated_at", today);
          editor.commit();

          database.setTransactionSuccessful();
          database.endTransaction();
          database.close();

        } catch (Exception e) { // errorMessage = "网络访问异常";
          Log.v("Insert failed!");
          database.endTransaction();
          database.close();
          return "网络访问异常!";
        }
      }

      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      String updatedAt = sharedPreferences.getString("hour_solution_" + hourID + "_updated_at", null);

      if (updatedAt != null) {
        updatedAtTextView.setText("更新于" + updatedAt);
        updatedAtTextView.setVisibility(View.VISIBLE);
      }

      solutionID = sharedPreferences.getLong("hour_solution_" + hourID, 0);

      Solution solution = Solution.find(ChineseHour.this, solutionID);

      LayoutInflater layoutInflater = ChineseHour.this.getLayoutInflater();

      if (solution != null) {

        loading.setVisibility(View.GONE);

        View solutionView = layoutInflater.inflate(R.layout.hour_solution, null);

        TextView titleTextView = (TextView) solutionView.findViewById(R.id.title);
        titleTextView.setText(solution.getTitle());

        TextView efficacy = (TextView) solutionView.findViewById(R.id.efficacy);
        efficacy.setText(solution.getEffect());

        TextView tips = (TextView) solutionView.findViewById(R.id.tips);
        tips.setText(solution.getNote());

        LinearLayout stepsView = (LinearLayout) solutionView.findViewById(R.id.steps_view);

        ArrayList<SolutionStep> steps = solution.getSteps();
        if (steps != null && steps.size() > 0) {
          for (int i = 0; i < steps.size(); i++) {
            SolutionStep step = steps.get(i);
            View stepView = layoutInflater.inflate(R.layout.solution_step, null);

            TextView stepNoTextView = (TextView) stepView.findViewById(R.id.step_no);
            stepNoTextView.setText(step.getNo() + "");

            TextView stepContentTextView = (TextView) stepView.findViewById(R.id.step_content);
            stepContentTextView.setText(step.getContent());

            stepView.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                Log.v("Setp On Click");
              }
            });

            stepsView.addView(stepView);
          }
        }

        solutionContentView.removeAllViews();
        solutionContentView.addView(solutionView);
        solutionContentView.setVisibility(View.VISIBLE);

        actionsToolbar.setVisibility(View.VISIBLE);

        favoriteImageButton.setImageResource(solution.getFavorited() == 0 ? R.drawable.action_favorite : R.drawable.action_favorite_on);

      } else {
        loading.findViewById(R.id.loading_icon).setVisibility(View.GONE);
        TextView loadingTextView = (TextView) loading.findViewById(R.id.loading_text);
        if (result != null)
          loadingTextView.setText(result);
      }
    }

  }

  /**
   * 点击事件处理
   */
  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.concern_button: // 特别关注
      boolean isConcern = sharedPreferences.getBoolean("concern_" + hourID, false);
      Editor editor = sharedPreferences.edit();
      editor.putBoolean("concern_" + hourID, !isConcern);
      if (editor.commit()) {
        setConcernIcon(!isConcern);
      }

      break;

    case R.id.hour_remind:
      Intent intent = new Intent(this, HourRemind.class);
      intent.putExtra("hourID", hourID);
      startActivity(intent);
      break;

    case R.id.todo_button:
      v.setVisibility(View.GONE);
      actionsLayout.setVisibility(View.VISIBLE);
      break;

    case R.id.favorite:
      Solution solution = Solution.find(this, solutionID);
      if (solution.getFavorited() == 0) {
        if (Solution.favorite(this, solutionID))
          favoriteImageButton.setImageResource(R.drawable.action_favorite_on);
      } else {
        if (Solution.unfavorite(this, solutionID))
          favoriteImageButton.setImageResource(R.drawable.action_favorite);
      }
      break;

    case R.id.alarm:

      break;

    case R.id.process:
      startActivity(new Intent(this, Process.class));
      break;

    case R.id.evaluate:
      startActivity(new Intent(this, SolutionEvaluate.class));
      break;

    case R.id.forwarding:

      break;

    default:
      break;
    }

  }

  /**
   * 触摸事件处理
   */
  public boolean onTouch(View v, MotionEvent event) {
    todoButton.setVisibility(View.VISIBLE);
    actionsLayout.setVisibility(View.INVISIBLE);
    return false;
  }

  /**
   * 设置'特别关注'图标
   * 
   * @param isConcern
   */
  public void setConcernIcon(boolean isConcern) {
    int concernResourceID = isConcern ? R.drawable.icon_heart_small_red : R.drawable.icon_heart_small_white;
    Drawable concernIcon = getResources().getDrawable(concernResourceID);
    concernIcon.setBounds(0, 0, concernIcon.getMinimumWidth(), concernIcon.getMinimumHeight());
    concernButton.setCompoundDrawables(concernIcon, null, null, null);
  }

}