package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.Acupoint;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Alarms;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

@SuppressLint("SimpleDateFormat")
public class SolutionActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener {

  String solutionId;

  View close;

  View loading;
  ScrollView contentScrollView;

  RelativeLayout pictureWrapper;
  ImageView picture;
  View loadingIcon;

  TextView titleTextView;
  TextView efficacy;

  LinearLayout stepsView;
  LinearLayout solutionView;

  View actionsToolbar;

  ImageButton favoriteImageButton;
  ImageButton alarmImageButton;
  ImageButton processImageButton;
  ImageButton evaluateImageButton;
  ImageButton forwardingImageButton;

  LayoutInflater layoutInflater;

  DatabaseHelper helper;
  Dao<GenericSolution, String> genericSolutionStringDao;
  GenericSolution genericSolution;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solution);

    solutionId = getIntent().getStringExtra("generic_solution_id");

    if (solutionId == null)
      finish();

    close = findViewById(R.id.close);
    close.setOnClickListener(this);

    contentScrollView = (ScrollView) findViewById(R.id.content_scroll_view);

    pictureWrapper = (RelativeLayout) findViewById(R.id.picture_wrapper);
    picture = (ImageView) findViewById(R.id.picture);
    loadingIcon = findViewById(R.id.loading_icon);

    solutionView = (LinearLayout) findViewById(R.id.solution_view);

    titleTextView = (TextView) findViewById(R.id.title);

    efficacy = (TextView) findViewById(R.id.efficacy);

    stepsView = (LinearLayout) findViewById(R.id.steps_view);

    actionsToolbar = findViewById(R.id.action_toolbar);

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

    loading = findViewById(R.id.loading);

    new Task().execute();
  }

  @Override
  protected void onResume() {
    if (genericSolution != null) {
      try {
        genericSolutionStringDao.refresh(genericSolution);
        favoriteImageButton.setImageResource(genericSolution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    super.onResume();
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

      layoutInflater = SolutionActivity.this.getLayoutInflater();

      helper = getHelper();
      try {
        genericSolutionStringDao = helper.getGenericSolutionStringDao();
        genericSolution = genericSolutionStringDao.queryForId(solutionId);
      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      if (genericSolution == null) {
        SolutionActivity.this.finish();
        Toast.makeText(SolutionActivity.this, R.string.this_solution_can_not_be_found, Toast.LENGTH_SHORT).show();
        return;
      }

      loading.setVisibility(View.GONE);
      contentScrollView.setVisibility(View.VISIBLE);

      titleTextView.setText(genericSolution.getTitle());

      efficacy.setText(genericSolution.getIntro());

      ImageLoader imageLoader = ImageLoader.getInstance();

      DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.image_preview_large).showImageForEmptyUri(R.drawable.image_preview_large).resetViewBeforeLoading()
          .cacheInMemory().cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888).delayBeforeLoading(1000).displayer(new RoundedBitmapDisplayer(5))
          .build();

      imageLoader.displayImage(RequestRoute.REQUEST_PATH + genericSolution.getLargeImage(), picture, options, new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(Bitmap loadedImage) {
          loadingIcon.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingFailed(FailReason failReason) {
          Log.v("onLoadingFailed: " + failReason);
          super.onLoadingFailed(failReason);
        }
      });

      try {
        JSONObject solutionJSON = new JSONObject(genericSolution.getData());

        JSONArray stepsArray = solutionJSON.getJSONArray("steps");

        for (int i = 0; i < stepsArray.length(); i++) {

          final SolutionStep step = SolutionStep.parseJsonObject((JSONObject) stepsArray.get(i));

          View stepView = layoutInflater.inflate(R.layout.solution_step, null);

          TextView stepNoTextView = (TextView) stepView.findViewById(R.id.step_no);
          stepNoTextView.setText(step.getNo() + "");

          TextView stepContentTextView = (TextView) stepView.findViewById(R.id.step_content);
          stepContentTextView.setText(step.getContent());

          stepContentTextView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              Intent intent = new Intent(SolutionActivity.this, SolutionStepSlider.class);
              intent.putExtra("solutionId", genericSolution.getId());
              intent.putExtra("stepNo", step.getNo());
              startActivity(intent);
            }
          });

          if (step.getAcupointIds().size() > 0) {
            View acupointLayout = stepView.findViewById(R.id.acupoint_layout);
            acupointLayout.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                Intent intent = new Intent(SolutionActivity.this, AcupointSlider.class);
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

      actionsToolbar.setVisibility(View.VISIBLE);

      favoriteImageButton.setImageResource(genericSolution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);

      if (genericSolution.isAlarm(SolutionActivity.this)) {
        alarmImageButton.setImageResource(R.drawable.action_alarm_on);
      }

    }
  }

  /**
   * 点击事件处理
   */
  public void onClick(View v) {
    switch (v.getId()) {

    // 返回
    case R.id.close:
      finish();
      break;

    // 收藏
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
      if (!genericSolution.isAlarm(this)) {
        long time = Alarms.addSolutionAlarm(this, genericSolution);
        if (time > 0){
          alarmImageButton.setImageResource(R.drawable.action_alarm_on);
          Alarms.popAlarmSetToast(this, time);
        }else{
          Toast.makeText(this, R.string.alarm_is_not_created_successfully, Toast.LENGTH_SHORT).show();
        }
      }
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
      intent.putExtra("generic_solution_id", genericSolution.getId());
      intent.putExtra("generic_solution_type_id", genericSolution.getTypeId());
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
      intent.putExtra("generic_solution_id", genericSolution.getId());
      intent.putExtra("generic_solution_type_id", genericSolution.getTypeId());
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

}