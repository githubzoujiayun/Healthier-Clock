package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.animation.Cycling;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Acupoint;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.entity.Hour;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;

@SuppressLint("SimpleDateFormat")
public class ChineseHour extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener {

  ScrollView contentScrollView;

  View updatedAtWrapper;
  TextView updatedAtTextView;
  ImageView cyclingLoading;

  Button concernButton;

  ImageView picture;

  View hourRemind;
  TextView appropriateTextView;
  TextView tabooTextView;

  LinearLayout solutionContentView;

  View actionsToolbar;
  View actionsLayout;

  ImageButton favoriteImageButton;
  ImageButton alarmImageButton;
  ImageButton processImageButton;
  ImageButton evaluateImageButton;
  ImageButton forwardingImageButton;

  LinearLayout loading;

  LayoutInflater layoutInflater;
  SharedPreferences sharedPreferences;

  DatabaseHelper helper;
  Dao<Acupoint, Integer> acupointDao;

  Dao<GenericSolution, String> genericSolutionStringDao;
  GenericSolution genericSolution;

  Hour hour;
  int hourID;
  int solutionId;

  boolean isUpdatIng = false;

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

    picture = (ImageView) findViewById(R.id.picture);
    picture.setImageResource(ActivityHelper.getImageResourceID(this, "hour_" + hourID));

    concernButton = (Button) findViewById(R.id.concern_button);
    setConcernIcon(sharedPreferences.getBoolean("concern_" + hourID, false));
    concernButton.setOnClickListener(this);

    updatedAtWrapper = findViewById(R.id.updated_at_wrapper);
    updatedAtWrapper.setOnClickListener(this);

    updatedAtTextView = (TextView) findViewById(R.id.updated_at);
    cyclingLoading = (ImageView) findViewById(R.id.cycling_loading);

    appropriateTextView = (TextView) findViewById(R.id.appropriate_text_view);
    tabooTextView = (TextView) findViewById(R.id.taboo_text_view);

    appropriateTextView.setText("宜: " + hour.getAppropriate());
    tabooTextView.setText("忌: " + hour.getTaboo());

    hourRemind = findViewById(R.id.hour_remind);
    hourRemind.setOnClickListener(this);

    solutionContentView = (LinearLayout) findViewById(R.id.solution_content);

    actionsToolbar = findViewById(R.id.action_toolbar);

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

  @Override
  protected void onResume() {
//    if (solution != null) {
//      alarmImageButton.setImageResource(solution.isAlarm(this) ? R.drawable.action_alarm_on : R.drawable.action_alarm);
//
//      try {
//        favoriteImageButton.setImageResource(solution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);
//      } catch (SQLException e) {
//        e.printStackTrace();
//      }
//    }
    super.onResume();
  }

  /**
   * 
   * @author miclle
   * 
   */
  class Task extends AsyncTask<String, Integer, String> {

    private boolean force = false;

    /**
     * Set before execute() methods
     * 
     * @param force
     * @return
     */
    public Task setForceUpdate(boolean force) {
      this.force = force;
      return this;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      isUpdatIng = true;
      Cycling.start(cyclingLoading);
    }

    @Override
    protected String doInBackground(String... params) {

      layoutInflater = ChineseHour.this.getLayoutInflater();

      String genericSolutionId = sharedPreferences.getString("hour_" + hourID, "");

      helper = getHelper();

      try {

        genericSolutionStringDao = helper.getGenericSolutionStringDao();

        acupointDao = helper.getAcupointDao();

        genericSolution = genericSolutionStringDao.queryForId(genericSolutionId);

        if (force || genericSolution == null) {

          if (!ActivityHelper.networkIsConnected(ChineseHour.this)) {
            return "网络未连接！";
          }

          HttpClientManager connect = new HttpClientManager(ChineseHour.this, RequestRoute.REQUEST_PATH + RequestRoute.SOLUTION_HOUR);
          connect.addParam("hour", hourID + "");

          connect.execute(ResuestMethod.GET);

          JSONObject json = new JSONObject(connect.getResponse());

          JSONObject solutionJSON = json.getJSONObject("solution");

          boolean isFavorited = (genericSolution != null && genericSolution.isFavorited()) ? true : false;

          genericSolution = GenericSolution.parseJsonObject(solutionJSON);

          genericSolution.setFavorited(isFavorited);

          genericSolutionStringDao.createOrUpdate(genericSolution);

          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
          Calendar calendar = Calendar.getInstance();
          String today = dateFormat.format(calendar.getTime());

          Editor editor = sharedPreferences.edit();
          editor.putString("hour_" + hourID, genericSolution.getId());
          editor.putString("hour_" + hourID + "_updated_at", today);
          editor.commit();
        }

      } catch (Exception e) {
        e.printStackTrace();
        return "网络访问异常!";
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      Cycling.stop(cyclingLoading);
      isUpdatIng = false;

      String updatedAt = sharedPreferences.getString("hour_" + hourID + "_updated_at", null);

      if (updatedAt != null) {
        updatedAtTextView.setText("更新于" + updatedAt);
        updatedAtTextView.setVisibility(View.VISIBLE);
      }

      if (genericSolution != null) {

        loading.setVisibility(View.GONE);

        solutionContentView.removeAllViews();

        LinearLayout solutionView = (LinearLayout) layoutInflater.inflate(R.layout.hour_solution, null);

        TextView titleTextView = (TextView) solutionView.findViewById(R.id.title);
        titleTextView.setText(genericSolution.getTitle());

        TextView efficacy = (TextView) solutionView.findViewById(R.id.efficacy);
        efficacy.setText(genericSolution.getIntro());

        try {
          JSONObject solutionJSON = new JSONObject(genericSolution.getData());

          JSONArray stepsArray = solutionJSON.getJSONArray("steps");

          LinearLayout stepsView = (LinearLayout) solutionView.findViewById(R.id.steps_view);

          for (int i = 0; i < stepsArray.length(); i++) {

            final SolutionStep step = SolutionStep.parseJsonObject((JSONObject) stepsArray.get(i));

            View stepView = layoutInflater.inflate(R.layout.solution_step, null);

            TextView stepNoTextView = (TextView) stepView.findViewById(R.id.step_no);
            stepNoTextView.setText(step.getNo() + "");

            TextView stepContentTextView = (TextView) stepView.findViewById(R.id.step_content);
            stepContentTextView.setText(step.getContent());

            stepContentTextView.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                Intent intent = new Intent(ChineseHour.this, SolutionStepSlider.class);
                intent.putExtra("solutionId", genericSolution.getId());
                intent.putExtra("stepNo", step.getNo());
                startActivity(intent);
              }
            });

            if (step.getAcupointIds().size() > 0) {
              View acupointLayout = stepView.findViewById(R.id.acupoint_layout);
              acupointLayout.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                  Intent intent = new Intent(ChineseHour.this, AcupointSlider.class);
                  intent.putExtra("acupoints", step.getAcupointIds());
                  startActivity(intent);
                }
              });
              acupointLayout.setVisibility(View.VISIBLE);
            }
            stepsView.addView(stepView);
          }

          JSONArray descriptions = solutionJSON.getJSONArray("descriptions");
          for (int i = 0; i < descriptions.length(); i++) {
            JSONArray field = (JSONArray) descriptions.get(i);
            String title = (String) field.get(0);
            String content = (String) field.get(1);
            if (!StringUtil.isEmpty(content)) {
              View contentItemView = layoutInflater.inflate(R.layout.content_item, null, false);
              TextView itemTitleTextView = (TextView) contentItemView.findViewById(R.id.title);
              itemTitleTextView.setText(title);
              TextView contentTextView = (TextView) contentItemView.findViewById(R.id.content);
              contentTextView.setText(content);
              solutionView.addView(contentItemView);
            }
          }

        } catch (JSONException e) {
          e.printStackTrace();
        }

        solutionContentView.addView(solutionView);
        solutionContentView.setVisibility(View.VISIBLE);

        actionsToolbar.setVisibility(View.VISIBLE);

        favoriteImageButton.setImageResource(genericSolution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);

//        if (solution.isAlarm(ChineseHour.this)) {
//          alarmImageButton.setImageResource(R.drawable.action_alarm_on);
//        }

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

    case R.id.updated_at_wrapper:
      if (!isUpdatIng)
        new Task().setForceUpdate(true).execute();
      break;

    case R.id.hour_remind: {
      Intent intent = new Intent(this, HourRemind.class);
      intent.putExtra("hourID", hourID);
      startActivity(intent);
      break;
    }

    case R.id.favorite:
      try {
        genericSolution.setFavorited(!genericSolution.isFavorited());
        genericSolutionStringDao.update(genericSolution);
        favoriteImageButton.setImageResource(genericSolution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      break;

    case R.id.alarm: {
//      if (!genericSolution.isAlarm(this)) {
//        long time = Alarms.addSolutionAlarm(this, genericSolution);
//        if (time > 0)
//          alarmImageButton.setImageResource(R.drawable.action_alarm_on);
//        Alarms.popAlarmSetToast(this, time);
//      }
      break;
    }

    case R.id.process: {

      if (!ActivityHelper.networkIsConnected(this)) {
        Toast.makeText(this, R.string.network_is_not_connected, Toast.LENGTH_SHORT).show();
        return;
      }

      if (!ActivityHelper.isLogged(this)) {
        Toast.makeText(this, R.string.you_are_not_logged_in_can_not_do_the_process_management, Toast.LENGTH_SHORT).show();
        return;
      }

      Intent intent = new Intent(this, Process.class);
      intent.putExtra("solutionId", genericSolution.getId());
      startActivity(intent);
      break;
    }

    case R.id.evaluate: {
      if (!ActivityHelper.networkIsConnected(this)) {
        Toast.makeText(this, R.string.network_is_not_connected, Toast.LENGTH_SHORT).show();
        return;
      }

      if (!ActivityHelper.isLogged(this)) {
        Toast.makeText(this, R.string.you_are_not_logged_in_can_not_be_evaluated, Toast.LENGTH_SHORT).show();
        return;
      }

      Intent intent = new Intent(this, SolutionEvaluate.class);
      intent.putExtra("solutionId", genericSolution.getId());
      startActivity(intent);
      break;
    }

    case R.id.forwarding: {
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("text/plain");
      intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
      intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_solution), genericSolution.getTitle(), genericSolution.getIntro()));
      startActivity(Intent.createChooser(intent, getTitle()));
      break;
    }

    default:
      break;
    }

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