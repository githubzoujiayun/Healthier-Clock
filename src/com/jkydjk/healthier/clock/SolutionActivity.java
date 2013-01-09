package com.jkydjk.healthier.clock;

import java.sql.SQLException;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
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
import com.jkydjk.healthier.clock.entity.Solution;
import com.jkydjk.healthier.clock.entity.SolutionStep;
import com.jkydjk.healthier.clock.util.ImageLoaderUtil;
import com.jkydjk.healthier.clock.util.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FakeBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

@SuppressLint("SimpleDateFormat")
public class SolutionActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener {

  int solutionId;

  View back;

  View loading;
  ScrollView contentScrollView;

  RelativeLayout pictureWrapper;
  ImageView picture;
  View loadingIcon;

  TextView titleTextView;
  TextView efficacy;
  TextView tips;

  LinearLayout stepsView;

  View actionsToolbar;

  ImageButton favoriteImageButton;
  ImageButton alarmImageButton;
  ImageButton processImageButton;
  ImageButton evaluateImageButton;
  ImageButton forwardingImageButton;

  LayoutInflater layoutInflater;

  DatabaseHelper helper;
  Dao<Solution, Integer> solutionDao;
  Dao<SolutionStep, Integer> solutionStepDao;

  Solution solution;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solution);

    solutionId = getIntent().getIntExtra("solutionId", -1);
    if (solutionId == -1)
      finish();

    back = findViewById(R.id.back);
    back.setOnClickListener(this);

    contentScrollView = (ScrollView) findViewById(R.id.content_scroll_view);

    pictureWrapper = (RelativeLayout) findViewById(R.id.picture_wrapper);
    picture = (ImageView) findViewById(R.id.picture);
    loadingIcon = findViewById(R.id.loading_icon);

    titleTextView = (TextView) findViewById(R.id.title);

    efficacy = (TextView) findViewById(R.id.efficacy);

    tips = (TextView) findViewById(R.id.tips);

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
        solutionDao = helper.getSolutionDao();
        solutionStepDao = helper.getSolutionStepDao();

        solution = solutionDao.queryForId(solutionId);
      } catch (SQLException e) {
        e.printStackTrace();
      }

      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      if (solution == null) {
        SolutionActivity.this.finish();
        Toast.makeText(SolutionActivity.this, R.string.this_solution_can_not_be_found, Toast.LENGTH_SHORT).show();
        return;
      }

      loading.setVisibility(View.GONE);
      contentScrollView.setVisibility(View.VISIBLE);

      titleTextView.setText(solution.getTitle());
      efficacy.setText(solution.getEffect());
      tips.setText(solution.getNote());

      // "http://site.com/image.jpg" Or "file:///mnt/sdcard/images/image.jpg"
      String imageUrl = "http://dribbble.s3.amazonaws.com/users/4737/screenshots/881241/attachments/95183/Big-Photo.png";

      ImageLoader imageLoader = ImageLoader.getInstance();

      DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.image_preview_large).showImageForEmptyUri(R.drawable.image_preview_large).resetViewBeforeLoading()
          .cacheInMemory().cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888).delayBeforeLoading(1000).displayer(new RoundedBitmapDisplayer(5))
          .build();

      imageLoader.displayImage(imageUrl, picture, options, new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(Bitmap loadedImage) {
          loadingIcon.setVisibility(View.GONE);
        }
      });

      // Display display = getWindowManager().getDefaultDisplay();
      //
      // int width = display.getWidth(); // deprecated
      // int height = display.getHeight(); // deprecated
      //
      // Log.v("width: " + width);
      //
      // ImageSize minImageSize = new ImageSize(width - 20, 80);
      //
      // imageLoader.loadImage(SolutionActivity.this, imageUrl, minImageSize,
      // options, new SimpleImageLoadingListener() {
      // @Override
      // public void onLoadingComplete(Bitmap loadedImage) {
      // picture.setImageBitmap(loadedImage);
      // // picture.setVisibility(View.GONE);
      // loadingIcon.setVisibility(View.GONE);
      //
      // // ImageView image = new ImageView(SolutionActivity.this);
      // // image.setBackgroundColor(0xFFFF0000);
      // // image.setImageBitmap(loadedImage);
      // // pictureWrapper.addView(image);
      // }
      // });

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

          View acupointLayout = stepView.findViewById(R.id.acupoint_layout);

          acupointLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              Intent intent = new Intent(SolutionActivity.this, AcupointSlider.class);
              intent.putExtra("acupoints", step.getAcupointIds());
              startActivity(intent);
            }
          });

          stepsView.addView(stepView);
        }
      }

      actionsToolbar.setVisibility(View.VISIBLE);

      favoriteImageButton.setImageResource(solution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);
    }
  }

  /**
   * 点击事件处理
   */
  public void onClick(View v) {
    switch (v.getId()) {

    // 返回
    case R.id.back:
      finish();
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

}