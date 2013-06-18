package com.jkydjk.healthier.clock;

import java.sql.SQLException;

import com.google.analytics.tracking.android.EasyTracker;
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
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.GenericSolution;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

@SuppressLint("SimpleDateFormat")
public class RecipeActivity extends OrmLiteBaseActivity<DatabaseHelper> implements OnClickListener {

  String solutionId;

  View back;
  View close;

  View loading;
  ScrollView contentScrollView;
  LinearLayout page;

  RelativeLayout pictureWrapper;
  ImageView picture;
  View loadingIcon;

  TextView titleTextView;

  LinearLayout stepsView;

  LinearLayout ingredientsView;
  LinearLayout accessoriesView;
  LinearLayout seasoningsView;

  View actionsToolbar;

  ImageButton favoriteImageButton;
  ImageButton evaluateImageButton;
  ImageButton forwardingImageButton;

  LayoutInflater layoutInflater;

  DatabaseHelper helper;
  Dao<GenericSolution, String> genericSolutionStringDao;

  GenericSolution genericSolution;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.recipe);

    solutionId = getIntent().getStringExtra("generic_solution_id");

    if (solutionId == null)
      finish();

    back = findViewById(R.id.back);
    back.setOnClickListener(this);

    if(getIntent().getBooleanExtra("notification_to_enter", false)){
      back.setVisibility(View.VISIBLE);
    }

    close = findViewById(R.id.close);
    close.setOnClickListener(this);

    contentScrollView = (ScrollView) findViewById(R.id.content_scroll_view);
    page = (LinearLayout)findViewById(R.id.page);

    pictureWrapper = (RelativeLayout) findViewById(R.id.picture_wrapper);
    picture = (ImageView) findViewById(R.id.picture);
    loadingIcon = findViewById(R.id.loading_icon);

    titleTextView = (TextView) findViewById(R.id.title);

    ingredientsView = (LinearLayout)findViewById(R.id.ingredients_view);
    accessoriesView = (LinearLayout)findViewById(R.id.accessories_view);
    seasoningsView = (LinearLayout)findViewById(R.id.seasonings_view);

    stepsView = (LinearLayout) findViewById(R.id.steps_view);

    actionsToolbar = findViewById(R.id.action_toolbar);

    favoriteImageButton = (ImageButton) findViewById(R.id.favorite);
    favoriteImageButton.setOnClickListener(this);

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
      layoutInflater = RecipeActivity.this.getLayoutInflater();
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
        RecipeActivity.this.finish();
        Toast.makeText(RecipeActivity.this, R.string.this_solution_can_not_be_found, Toast.LENGTH_SHORT).show();
        return;
      }

      loading.setVisibility(View.GONE);
      contentScrollView.setVisibility(View.VISIBLE);
      titleTextView.setText(genericSolution.getTitle());

      try {
        JSONObject json = new JSONObject(genericSolution.getData());

        if (json.has("ingredients"))
          addMaterials(ingredientsView, json.getJSONArray("ingredients"));

        if (json.has("accessories"))
          addMaterials(accessoriesView, json.getJSONArray("accessories"));

        if (json.has("seasonings"))
          addMaterials(seasoningsView,  json.getJSONArray("seasonings"));

        JSONArray steps = json.getJSONArray("steps");
        for (int i = 0; i < steps.length(); i++) {
          View stepView = layoutInflater.inflate(R.layout.solution_step, null);
          TextView stepNoTextView = (TextView) stepView.findViewById(R.id.step_no);
          stepNoTextView.setText(i + 1 + "");
          TextView stepContentTextView = (TextView) stepView.findViewById(R.id.step_content);
          stepContentTextView.setText((String)steps.get(i));
          stepsView.addView(stepView);
        }

        JSONArray descriptions = json.getJSONArray("descriptions");
        for (int i = 0; i < descriptions.length(); i++) {
          JSONArray field = (JSONArray) descriptions.get(i);
          String title = (String) field.get(0);
          String content = (String) field.get(1);
          if (!StringUtil.isEmpty(content)) {
            View contentItemView = layoutInflater.inflate(R.layout.content_item, null, false);
            TextView titleTextView = (TextView) contentItemView.findViewById(R.id.title);
            titleTextView.setText(title);
            TextView contentTextView = (TextView) contentItemView.findViewById(R.id.content);
            contentTextView.setText(content);
            page.addView(contentItemView);
          }
        }

      } catch (JSONException e) {
        e.printStackTrace();
      }

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

      actionsToolbar.setVisibility(View.VISIBLE);

      favoriteImageButton.setImageResource(genericSolution.isFavorited() ? R.drawable.action_favorite_on : R.drawable.action_favorite);
    }
  }

  /**
   * 添加材料
   * @param materials
   */
  private void addMaterials(LinearLayout layout, JSONArray materials) throws JSONException{
    if (materials.length() > 0)
      layout.setVisibility(View.VISIBLE);

    for (int i = 0; i < materials.length(); i++) {

      View materialView = layoutInflater.inflate(R.layout.recipe_material, null);

      TextView nameTextView = (TextView) materialView.findViewById(R.id.text_view_name);

      TextView weightTextView = (TextView) materialView.findViewById(R.id.text_view_intro);

      JSONObject materail = (JSONObject)materials.get(i);

      nameTextView.setText(materail.getString("name"));
      weightTextView.setText(materail.getString("weight"));

      final int foodId = materail.getInt("id");

      materialView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(RecipeActivity.this, FoodActivity.class);
          intent.putExtra("food_id", foodId);
          startActivity(intent);
        }
      });

      layout.addView(materialView);
    }
  }

  /**
   * 点击事件处理
   */
  public void onClick(View v) {
    switch (v.getId()) {

      case R.id.back:{
        Intent intent = new Intent(this, Healthier.class);
        startActivity(intent);
        finish();
        break;
      }

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