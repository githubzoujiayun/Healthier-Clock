package com.jkydjk.healthier.clock;

import com.jkydjk.healthier.clock.util.ActivityHelper;
import com.jkydjk.healthier.clock.util.Lunar;

import android.os.Bundle;
import android.widget.ImageView;

public class SolarTerms extends BaseActivity {

  ImageView picture;

  int solarTermIndex;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.solar_terms);

    solarTermIndex = Lunar.getCurrentSolarTermIntervalIndex();

    picture = (ImageView) findViewById(R.id.picture);

    setPictureImage();
  }

  @Override
  protected void onResume() {
    super.onResume();
    solarTermIndex = Lunar.getCurrentSolarTermIntervalIndex();
    setPictureImage();
  }

  private void setPictureImage() {
    picture.setImageResource(ActivityHelper.getImageResourceID(this, "solar_terms_" + solarTermIndex));
  }

}
