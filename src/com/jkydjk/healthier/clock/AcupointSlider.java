package com.jkydjk.healthier.clock;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jkydjk.healthier.clock.util.Log;

public class AcupointSlider extends BaseActivity implements OnClickListener {

  private Button back;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.acupoint);

    Intent intent = getIntent();

    ArrayList<Integer> ids = intent.getIntegerArrayListExtra("acupoints");

    if (ids == null)
      finish();

    Log.v("ids: " + ids);

    back = (Button) findViewById(R.id.back);
    back.setOnClickListener(this);

  }

  public void onClick(View v) {
    switch (v.getId()) {
    case R.id.back:
      finish();
      break;

    default:
      break;
    }

  }

}
