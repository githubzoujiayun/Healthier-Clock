package com.jkydjk.healthier.clock;

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
import com.jkydjk.healthier.clock.network.HttpClientManager;
import com.jkydjk.healthier.clock.network.RequestRoute;
import com.jkydjk.healthier.clock.network.ResuestMethod;
import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.util.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

@SuppressLint("SimpleDateFormat")
public class FoodActivity extends BaseActivity implements OnClickListener {

  int foodId;

  View close;

  View loading;
  LinearLayout page;

  RelativeLayout pictureWrapper;
  ImageView picture;
  View loadingIcon;

  TextView titleTextView;

  LayoutInflater layoutInflater;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.food);

    foodId = getIntent().getIntExtra("food_id", -1);

    if (foodId == -1)
      finish();

    if (!ActivityHelper.networkIsConnected(this)) {
      Toast.makeText(this, R.string.network_is_not_connected, Toast.LENGTH_SHORT).show();
      finish();
    }

    close = findViewById(R.id.close);
    close.setOnClickListener(this);

    page = (LinearLayout)findViewById(R.id.page);

    pictureWrapper = (RelativeLayout) findViewById(R.id.picture_wrapper);
    picture = (ImageView) findViewById(R.id.picture);
    loadingIcon = findViewById(R.id.loading_icon);

    titleTextView = (TextView) findViewById(R.id.title);

    loading = findViewById(R.id.loading);

    new Task().execute();
  }

  /**
   * 
   * @author miclle
   * 
   */
  class Task extends AsyncTask<String, Integer, String> {

    JSONObject responseJSON = null;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      loading.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {

      layoutInflater = FoodActivity.this.getLayoutInflater();

      try {
        HttpClientManager connect = new HttpClientManager(FoodActivity.this, RequestRoute.REQUEST_PATH + RequestRoute.SOLUTION_SOLAR_TERM);
        connect.execute(ResuestMethod.GET);
        connect.addParam("id", foodId + "");

        responseJSON = new JSONObject(connect.getResponse());

      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      loading.setVisibility(View.GONE);

      try {
        titleTextView.setText(responseJSON.getString("name"));

        JSONArray descriptions = responseJSON.getJSONArray("descriptions");

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

        ImageLoader imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder().showStubImage(R.drawable.image_preview_large).showImageForEmptyUri(R.drawable.image_preview_large).resetViewBeforeLoading()
            .cacheInMemory().cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.ARGB_8888).delayBeforeLoading(1000).displayer(new RoundedBitmapDisplayer(5))
            .build();

        imageLoader.displayImage(RequestRoute.REQUEST_PATH + responseJSON.getString("large_image"), picture, options, new SimpleImageLoadingListener() {
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


      } catch (JSONException e) {
        e.printStackTrace();
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

    default:
      break;
    }

  }

}