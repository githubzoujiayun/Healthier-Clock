package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.jkydjk.healthier.clock.animation.Cycling;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Acupoint;
import com.jkydjk.healthier.clock.entity.Alarm;
import com.jkydjk.healthier.clock.entity.Hour;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.entity.Alarm.DaysOfWeek;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.AlarmUtil;
import com.jkydjk.healthier.clock.util.StringUtil;

@SuppressLint("SimpleDateFormat")
public class ChineseHour extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener, OnTouchListener {

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
  View todoButton;
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
  Dao<Solution, Integer> solutionDao;
  Dao<SolutionStep, Integer> solutionStepDao;
  Dao<Acupoint, Integer> acupointDao;

  Hour hour;
  int hourID;
  int solutionId;
  Solution solution;

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
    contentScrollView.setOnTouchListener(this);

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

      solutionId = sharedPreferences.getInt("hour_" + hourID, -1);

      try {
        helper = getHelper();

        solutionDao = helper.getSolutionDao();
        solutionStepDao = helper.getSolutionStepDao();
        acupointDao = helper.getAcupointDao();

        if (force || solutionId == -1) {
          solution = null;
        } else {
          solution = solutionDao.queryForId(solutionId);
        }

        if (solution == null) {
          if (!ActivityHelper.networkConnected(ChineseHour.this)) {
            return "网络未连接！";
          }

          HttpClientManager connect = new HttpClientManager(ChineseHour.this, RequestRoute.REQUEST_PATH + RequestRoute.SOLUTION_HOUR);
          connect.addParam("hour", hourID + "");

          connect.execute(ResuestMethod.GET);

          JSONObject json = new JSONObject(connect.getResponse());

          JSONObject solutionJSON = json.getJSONObject("solution");

          solution = Solution.parseJsonObject(solutionJSON);

          solutionDao.createIfNotExists(solution);
          solutionDao.refresh(solution);

          JSONArray stepsArray = solutionJSON.getJSONArray("steps");

          ForeignCollection<SolutionStep> steps = solutionDao.getEmptyForeignCollection("steps");

          for (int i = 0; i < stepsArray.length(); i++) {
            SolutionStep step = SolutionStep.parseJsonObject((JSONObject) stepsArray.get(i));
            step.setSolution(solution);
            solutionStepDao.delete(step);
            // solutionStepDao.createOrUpdate(step);
            steps.add(step);
          }

          solution.setSteps(steps);

          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
          Calendar calendar = Calendar.getInstance();
          String today = dateFormat.format(calendar.getTime());

          Editor editor = sharedPreferences.edit();

          editor.putInt("hour_" + hourID, solution.getId());
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

      if (solution != null) {

        loading.setVisibility(View.GONE);

        View solutionView = layoutInflater.inflate(R.layout.hour_solution, null);

        TextView titleTextView = (TextView) solutionView.findViewById(R.id.title);
        titleTextView.setText(solution.getTitle());

        TextView efficacy = (TextView) solutionView.findViewById(R.id.efficacy);
        efficacy.setText(solution.getEffect());

        TextView tips = (TextView) solutionView.findViewById(R.id.tips);
        tips.setText(solution.getNote());

        if (StringUtil.isEmpty(solution.getNote())) {
          tips.setVisibility(View.GONE);
          solutionView.findViewById(R.id.tips_title).setVisibility(View.GONE);
        } else {
          tips.setVisibility(View.VISIBLE);
          solutionView.findViewById(R.id.tips_title).setVisibility(View.VISIBLE);
        }

        LinearLayout stepsView = (LinearLayout) solutionView.findViewById(R.id.steps_view);

        ForeignCollection<SolutionStep> steps = solution.getSteps();

        if (steps != null) {

          Iterator<SolutionStep> it = steps.iterator();

          while (it.hasNext()) {
            final SolutionStep step = (SolutionStep) it.next();

            View stepView = layoutInflater.inflate(R.layout.solution_step, null);

            TextView stepNoTextView = (TextView) stepView.findViewById(R.id.step_no);
            stepNoTextView.setText(step.getNo() + "");

            TextView stepContentTextView = (TextView) stepView.findViewById(R.id.step_content);
            stepContentTextView.setText(step.getContent());

            stepContentTextView.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                Intent intent = new Intent(ChineseHour.this, SolutionStepSlider.class);
                intent.putExtra("solutionId", solution.getId());
                intent.putExtra("stepNo", step.getNo());
                startActivity(intent);
              }
            });

            View acupointLayout = stepView.findViewById(R.id.acupoint_layout);

            acupointLayout.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                Intent intent = new Intent(ChineseHour.this, AcupointSlider.class);
                intent.putExtra("acupoints", step.getAcupointIds());
                startActivity(intent);
              }
            });

            stepsView.addView(stepView);
          }
        }

        solutionContentView.removeAllViews();
        solutionContentView.addView(solutionView);
        solutionContentView.setVisibility(View.VISIBLE);

        actionsToolbar.setVisibility(View.VISIBLE);

        favoriteImageButton.setImageResource(solution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);

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
    case R.id.todo_button:
      v.setVisibility(View.GONE);
      actionsLayout.setVisibility(View.VISIBLE);
      break;

    case R.id.favorite:
      try {
        solution.setFavorited(!solution.isFavorited());
        solutionDao.update(solution);
        favoriteImageButton.setImageResource(solution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      break;

    case R.id.alarm: {
      long time = Alarm.addSolutionAlarm(this, solution);
      AlarmUtil.popAlarmSetToast(this, time);
      break;
    }
    case R.id.process:
      startActivity(new Intent(this, Process.class));
      break;

    case R.id.evaluate: {
      Intent intent = new Intent(this, SolutionEvaluate.class);
      intent.putExtra("solutionId", solution.getId());
      startActivity(intent);
      break;
    }
    case R.id.forwarding: {
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("text/plain");
      intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
      intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_solution), solution.getTitle(), solution.getEffect()));
      startActivity(Intent.createChooser(intent, getTitle()));
      break;
    }
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