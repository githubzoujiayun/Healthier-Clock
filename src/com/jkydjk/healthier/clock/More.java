package com.jkydjk.healthier.clock;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class More extends BaseActivity implements OnClickListener {

  View backLayout;
  View settingLayout;
  View feedbackLayout;
  View helpLayout;
  View aboutLayout;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

    setContentView(R.layout.more);

    backLayout = findViewById(R.id.back);
    backLayout.setOnClickListener(this);

    settingLayout = findViewById(R.id.setting);
    settingLayout.setOnClickListener(this);

    aboutLayout = findViewById(R.id.layout_about);
    aboutLayout.setOnClickListener(this);

    feedbackLayout = findViewById(R.id.feedback);
    feedbackLayout.setOnClickListener(this);
  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    case R.id.setting:
      startActivity(new Intent(this, SettingsActivity.class));
      overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
      break;

    case R.id.layout_about:
      startActivity(new Intent(this, Help.class));
      finish();
      break;

    case R.id.feedback:
      finish();
      startActivity(new Intent(this, Feedback.class));
      break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    finish();
    return false;
  }

}