package com.jkydjk.healthier.clock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.jkydjk.healthier.clock.database.DatabaseHelper;
import com.jkydjk.healthier.clock.entity.HealthTip;

import java.sql.SQLException;

/**
 * Created by miclle on 13-6-7.
 */
public class HealthTipActivity  extends OrmLiteBaseActivity<DatabaseHelper> implements View.OnClickListener {

  int id;
  DatabaseHelper helper;

  ImageButton close;
  TextView contentTextView;
  Button positive;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.health_tip);

    helper = new DatabaseHelper(getApplicationContext());

    id = getIntent().getIntExtra("id", -1);

    close = (ImageButton)findViewById(R.id.close);
    close.setOnClickListener(this);

    contentTextView = (TextView)findViewById(R.id.text_view_content);

    positive = (Button)findViewById(R.id.positive);
    positive.setOnClickListener(this);

    if (id == -1)
      finish();

    try {
      Dao<HealthTip, Integer> healthTipIntegerDao = helper.getHealthTipIntegerDao();

      HealthTip tip = healthTipIntegerDao.queryForId(id);

      contentTextView.setText(tip.content);

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  /**
   * 点击事件处理
   */
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.close:
        finish();
        break;

      case R.id.positive:
        Intent intent = new Intent(this, Healthier.class);
        startActivity(intent);
        finish();
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