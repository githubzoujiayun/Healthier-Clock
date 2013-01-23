package com.jkydjk.healthier.clock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Process extends BaseActivity implements OnClickListener {

  View back;
  View enter;

  int solutionId;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.process);

    Intent intent = getIntent();
    solutionId = intent.getIntExtra("solutionId", 0);

    if (solutionId == 0)
      finish();

    back = findViewById(R.id.back);
    back.setOnClickListener(this);

    enter = findViewById(R.id.enter);
    enter.setOnClickListener(this);

    // RadioButton

  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    case R.id.enter:
      finish();
      break;

    default:
      break;
    }

  }

}
