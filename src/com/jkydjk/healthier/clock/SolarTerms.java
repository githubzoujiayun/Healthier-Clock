package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;
import com.jkydjk.healthier.clock.widget.TextViewVertical;

import android.graphics.Typeface;
import android.os.Bundle;

public class SolarTerms extends BaseActivity {

  private TextViewVertical tv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solar_terms);

  }
}